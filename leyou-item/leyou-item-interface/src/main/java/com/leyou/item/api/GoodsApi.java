package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping
public interface GoodsApi {

    @GetMapping("{id}")
    public Spu queryOneSpuById(@PathVariable("id") Long sid);

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
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "saleable", required = false) Boolean saleable,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "rows", defaultValue = "5") Integer rows
    );

    /**
     * 新增spu商品
     *
     * @param spuBo 增强的spu实体类
     * @return
     */
    @PostMapping("goods")
    public Void saveSpu(@RequestBody SpuBo spuBo);

    /**
     * 修改spu和sku
     *
     * @param spuBo
     * @return
     */
    @PutMapping("goods")
    public Void updateSpu(@RequestBody SpuBo spuBo);

    /**
     * 通过spuId查询spuDetail
     *
     * @param sid
     * @return
     */
    @GetMapping("spu/detail/{sid}")
    public SpuDetail querySupDetailBySpuId(@PathVariable("sid") Long sid);

    /**
     * 通过spuId查询sku
     *
     * @param sid
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkuBySpuId(@RequestParam("id") Long sid);
}
