package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Resource
    private BrandService brandService;

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
    public ResponseEntity<PageResult<Brand>> findBrandByKey(
            @RequestParam("key") String key,
            @RequestParam("page") Integer page,
            @RequestParam("rows") Integer rows,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc
    ) {
        PageResult<Brand> pageResult = this.brandService.findBrandByKey(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(pageResult.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    @PostMapping
    public ResponseEntity<String> newBrand(
            @RequestParam("name") String name,
            @RequestParam("image") String image,
            @RequestParam("cids") List<Integer> cids,
            @RequestParam("letter") Character letter
    ) {
        this.brandService.newBrand(name, image, cids, letter);
        return ResponseEntity.status(HttpStatus.CREATED).body("新增成功");
    }

    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsBycid(
            @PathVariable("cid") Long cid
    ) {
        List<Brand> brands = brandService.findBrandBycid(cid);
        if(CollectionUtils.isEmpty(brands)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }

    @GetMapping({"{id}"})
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") long id){
        Brand brand = this.brandService.findBrandBycId(id);
        if(brand == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}
