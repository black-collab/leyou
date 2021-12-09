package com.leyou.item.service;

import com.leyou.item.mappers.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    public List<Category> findCategoryById(long id){
        return categoryMapper.findCategoryById(id);
    }

    public List<String> findNamesByIds(List<Long> ids) {
        return this.categoryMapper.queryCategoryByIds(ids);
    }

    public List<Category> findCategoryListByCid3(Long cid3) {
        Category category3 = this.categoryMapper.queryCategoryById(cid3);
        Category category2 = this.categoryMapper.queryCategoryById(category3.getParentId());
        Category category1 = this.categoryMapper.queryCategoryById(category2.getParentId());
        return Arrays.asList(category1,category2,category3);
    }
}
