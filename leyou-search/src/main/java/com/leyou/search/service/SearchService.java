package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.leyou.pojo.*;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.reponsitory.GoodsReponsitory;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;

import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GoodsReponsitory goodsReponsitory;

    @Autowired
    private SpecificationClient specificationClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {

        // 创建goods对象
        Goods goods = new Goods();

        // 查询品牌
        Brand brand = this.brandClient.queryById(spu.getBrandId());

        // 查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        // 查询spu下的所有sku
        List<Sku> skus = this.goodsClient.querySkuBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        // 遍历skus，获取价格集合
        skus.forEach(sku ->{
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);
        });

        // 查询出所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);
        // 查询spuDetail。获取规格参数值
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());
        // 获取通用的规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        // 获取特殊的规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });
        // 定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            // 判断是否通用规格参数
            if (param.getGeneric()) {
                // 获取通用规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                // 判断是否是数值类型
                if (param.getNumeric()){
                    // 如果是数值的话，判断该数值落在那个区间
                    value = chooseSegment(value, param);
                }
                // 把参数名和值放入结果集中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });

        // 设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);

        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchResult search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            return null;
        }
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND);
        QueryBuilder boolBasicQuery=buildBooleanQueryBuilder(searchRequest);
        queryBuilder.withQuery(boolBasicQuery);
       //
        queryBuilder.withPageable(PageRequest.of(searchRequest.getPage()-1,searchRequest.getSize()));
       //
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        //添加分类和品牌的聚合
        String categoryAggsName="categories";
        String brandsAggsName="brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggsName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandsAggsName).field("brandId"));
        //执行查询
        AggregatedPage<Goods> goodsPage =(AggregatedPage<Goods>)this.goodsReponsitory.search(queryBuilder.build());

        //

        List<Map<String, Object>> categoriesAggs=getCategoriesAggs(goodsPage.getAggregation(categoryAggsName));

        List<Brand> brandsAggs=getBrandsAggs(goodsPage.getAggregation(brandsAggsName));
        List<Map<String, Object>> specs=null;
        if(!CollectionUtils.isEmpty(categoriesAggs)&&categoriesAggs.size()==1) {
             specs = getAggsSpecParam((Long) categoriesAggs.get(0).get("id"), boolBasicQuery);
        }
        return new SearchResult(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent(),categoriesAggs,brandsAggs,specs);
    }
    /*
     * @Description:构建bool查询
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/22
    */
    private QueryBuilder buildBooleanQueryBuilder(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //添加查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        //添加过滤条件
        if(CollectionUtils.isEmpty(searchRequest.getFilter())){
            return boolQueryBuilder;
        }
        for (Map.Entry<String, Object> entry : searchRequest.getFilter().entrySet()) {
            String key = entry.getKey();
            if(StringUtils.equals("品牌",key)){
                key="brandId";
            }
            else if(StringUtils.equals("分类",key)){
                key="cid3";
            }else {
                key="specs." + key+ ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /*
     * @Description:根据查询条件聚合规格参数
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/22
    */
    private List<Map<String, Object>> getAggsSpecParam(Long id, QueryBuilder basicQuery) {
        // 创建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(basicQuery);
        // 查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, id, null, true);
        // 添加聚合
        params.forEach(param -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });
        // 只需要聚合结果集，不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        // 执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsReponsitory.search(queryBuilder.build());

        // 定义一个集合，收集聚合结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();
        // 解析聚合查询的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            // 放入规格参数名
            map.put("k", entry.getKey());
            // 收集规格参数值
            List<Object> options = new ArrayList<>();
            // 解析每个聚合
            StringTerms terms = (StringTerms)entry.getValue();
            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("options", options);
            paramMapList.add(map);
        }

        return paramMapList;
    }


    private List<Brand> getBrandsAggs(Aggregation aggregation) {
        LongTerms terms=(LongTerms)aggregation;
        List<Brand> brands=new ArrayList<>();
        terms.getBuckets().forEach(bucket -> {
            Brand brand = this.brandClient.queryById(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });
        return brands;
    }

    private List<Map<String, Object>> getCategoriesAggs(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        return  terms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            long id = bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());

    }

    public void creatIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        List<Sku> skus = this.goodsClient.querySkuBySpuId(id);
        this.goodsReponsitory.save(goods);
    }
}

