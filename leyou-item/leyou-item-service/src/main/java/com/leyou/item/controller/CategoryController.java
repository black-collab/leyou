package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 根据Id查询这个Id的子分类
     * @param id
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> findCategoryById(@RequestParam("pid") Long id) {
        if (id == null || id < 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Category> categoryList = categoryService.findCategoryById(id);
        if(CollectionUtils.isEmpty(categoryList)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryList);
    }

    /**
     * 根据id集合查询对应的分类名集合
     * @param ids
     * @return
     */
    @GetMapping
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids){
        List<String> names = this.categoryService.findNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    };

    /**
     * 根据三级分类ID查询所属的1、2、3级分类集合
     * @param cid3
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryCategoryListByCid3(@RequestParam("id") Long cid3){
        List<Category> categories = this.categoryService.findCategoryListByCid3(cid3);
        if(CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }
}
