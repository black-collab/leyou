package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("brand")
public interface BrandApi {

    @GetMapping({"{id}"})
    public Brand queryBrandById(@PathVariable("id") long id);

    /**
     * 分页查询品牌
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping(value = "page")
    public PageResult<Brand> findBrandByKey(
            @RequestParam("key") String key,
            @RequestParam("page") Integer page,
            @RequestParam("rows") Integer rows,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc
    );

    @PostMapping
    public String newBrand(
            @RequestParam("name") String name,
            @RequestParam("image") String image,
            @RequestParam("cids") List<Integer> cids,
            @RequestParam("letter") Character letter
    );

    @GetMapping("cid/{cid}")
    public List<Brand> queryBrandsBycid(
            @PathVariable("cid") Long cid
    );
}
