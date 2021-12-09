package com.leyou.search.bo;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.search.pojo.Goods;

import java.util.List;
import java.util.Map;

public class SearchResult extends PageResult<Goods> {

    private List<Map<String, Object>> categories;
    private List<Brand> brands;
    private List<Map<String, Object>> specs;

    public SearchResult(Long total, Integer totalPageCount, List<Goods> items, List<Map<String, Object>> categories, List<Brand> brands, List<Map<String, Object>> specs) {
        super(total, totalPageCount, items);
        this.categories = categories;
        this.brands = brands;
        this.specs = specs;
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

    public List<Map<String, Object>> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Map<String, Object>> specs) {
        this.specs = specs;
    }
}