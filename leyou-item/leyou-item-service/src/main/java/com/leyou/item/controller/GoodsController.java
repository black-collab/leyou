package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    /**
     * 分页查询Spu
     *
     * @param key      搜索字段
     * @param saleable 筛选条件：true为上架，false为下架，没有则为全部
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "saleable", required = false) Boolean saleable,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "rows", defaultValue = "5") Integer rows
    ) {
        PageResult<SpuBo> spuBos = this.goodsService.querySpuBoByPage(key, saleable, page, rows);
        if (CollectionUtils.isEmpty(spuBos.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuBos);
    }

    /**
     * 新增spu商品
     *
     * @param spuBo 增强的spu实体类
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveSpu(@RequestBody SpuBo spuBo) {
        this.goodsService.saveSpu(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改spu和sku
     *
     * @param spuBo
     * @return
     */
    @PutMapping("goods")
    public ResponseEntity<Void> updateSpu(@RequestBody SpuBo spuBo) {
        this.goodsService.updateSpu(spuBo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 通过spuId查询spuDetail
     *
     * @param sid
     * @return
     */
    @GetMapping("spu/detail/{sid}")
    public ResponseEntity<SpuDetail> querySupDetailBySpuId(@PathVariable("sid") Long sid) {
        SpuDetail spuDetail = goodsService.querySpuDetailBySpuId(sid);
        if (spuDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 通过spuId查询sku
     *
     * @param sid
     * @return
     */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long sid) {
        List<Sku> skus = goodsService.querySkuBySpuId(sid);
        if (CollectionUtils.isEmpty(skus)) {
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }
    @GetMapping("{id}")
    public ResponseEntity<Spu> queryOneSpuById(@PathVariable("id") Long sid) {
        Spu spu = goodsService.queryOneSpuById(sid);
        if (spu == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }
}
