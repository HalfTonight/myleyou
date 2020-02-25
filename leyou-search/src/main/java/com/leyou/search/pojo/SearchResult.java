package com.leyou.search.pojo;

import com.leyou.commom.PageResult;
import com.leyou.pojo.Brand;

import java.util.List;
import java.util.Map;

public class SearchResult extends PageResult<Goods> {

    private List<Map<String, Object>> categories;

    private List<Brand> brands;

    private List<Map<String, Object>> specParam;

    public SearchResult(List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specParam) {
        this.categories = categories;
        this.brands = brands;
        this.specParam = specParam;
    }

    public List<Map<String, Object>> getCategories() {
        return categories;
    }

    public void setCategories(List<Map<String, Object>> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public List<Map<String, Object>> getSpecParam() {
        return specParam;
    }

    public void setSpecParam(List<Map<String, Object>> specParam) {
        this.specParam = specParam;
    }

    public SearchResult(Long total, List<Goods> items, List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specParam) {
        super(total, items);
        this.categories = categories;
        this.brands = brands;
        this.specParam = specParam;
    }

    public SearchResult(Long total, Integer totalPage, List<Goods> items, List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specParam) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
        this.specParam = specParam;
    }

    public SearchResult() {
    }
}
