package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.bo.SpuBo;
import com.leyou.commom.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //key
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");
        }
        //saleable
        if(saleable!=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //page
        PageHelper.startPage(page,rows);
        //rows
        //执行查询
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        List<SpuBo> spuBos = new ArrayList<>();
        spus.forEach(spu->{
            SpuBo spuBo = new SpuBo();
            // copy共同属性的值到新的对象
            BeanUtils.copyProperties(spu, spuBo);
            // 查询分类名称
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "/"));
            // 查询品牌的名称
            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            spuBos.add(spuBo);
        });
        //
        return new PageResult<>(pageInfo.getTotal(), spuBos);



    }
    @Transactional
    public void addGoods(SpuBo spuBo) {
        //新增spu

        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        //新增SpuDetial
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        saveSkuAndStock(spuBo);
        sendMsg("insert",spuBo.getId());
    }
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
        sendMsg("update",spuBo.getId());
    }

    public SpuDetail querySpudetail(Long spuId) {
        return  this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    public List<Sku> querySkuBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(sku);
        skus.forEach(s -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }
    @Transactional
    public void updateGoods(SpuBo spu) {
            // 查询以前sku
            List<Sku> skus = this.querySkuBySpuId(spu.getId());
            // 如果以前存在，则删除
            if(!CollectionUtils.isEmpty(skus)) {
                List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
                // 删除以前库存
                Example example = new Example(Stock.class);
                example.createCriteria().andIn("skuId", ids);
                this.stockMapper.deleteByExample(example);

                // 删除以前的sku
                Sku record = new Sku();
                record.setSpuId(spu.getId());
                this.skuMapper.delete(record);

            }
            // 新增sku和库存
            saveSkuAndStock(spu);

            // 更新spu
            spu.setLastUpdateTime(new Date());
            spu.setCreateTime(null);
            spu.setValid(null);
            spu.setSaleable(null);
            this.spuMapper.updateByPrimaryKeySelective(spu);

            // 更新spu详情
            this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

    }


    public List<Category> queryAllByCid3(Long cid3) {
        Category category1 = this.categoryMapper.selectByPrimaryKey(cid3);
        Category category2 = this.categoryMapper.selectByPrimaryKey(category1.getParentId());
        Category category3 = this.categoryMapper.selectByPrimaryKey(category2.getParentId());
        return Arrays.asList(category1,category2,category3);
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }
    public void sendMsg(String str,Long id){
        try {
            this.amqpTemplate.convertAndSend("item."+ str ,id);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Sku querySkuById(Long id) {
        return this.skuMapper.selectByPrimaryKey(id);
    }
}
