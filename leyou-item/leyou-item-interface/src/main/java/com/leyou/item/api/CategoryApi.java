package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("category")
public interface CategoryApi {

    /**
     * 根据Id查询这个Id的子分类
     * @param id
     * @return
     */
    @GetMapping("list")
    public List<Category> findCategoryById(@RequestParam("pid") Long id);

    /**
     * 根据分类id集合查询对应的分类名称集合
     * @param ids
     * @return
     */
    @GetMapping
    List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 根据三级分类ID查询所属的1、2、3级分类集合
     * @param cid3
     * @return
     */
    @GetMapping("all/level")
    public List<Category> queryCategoryListByCid3(@RequestParam("id") Long cid3);
}
