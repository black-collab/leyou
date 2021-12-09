package com.leyou.goods.service;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecParamClient;
import com.leyou.item.pojo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class GoodsService {

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private CategoryClient categoryClient;

    @Resource
    private BrandClient brandClient;

    @Resource
    private SpecParamClient specParamClient;

    /**
     * 根据spuId查询Goods信息封装到map返回给前端显示
     * @param id
     * @return
     */
    public Map<String, Object> loadModel(Long id) {
        HashMap<String, Object> map = new HashMap<>();
        //查询spu
        Spu spu = this.goodsClient.queryOneSpuById(id);
        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySupDetailBySpuId(id);
        //查询skus
        List<Sku> skus = this.goodsClient.querySkuBySpuId(id);
        //查询分类
        List<Long> categoryIds = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> categoryNames = this.categoryClient.queryNamesByIds(categoryIds);
        ArrayList<Map<String, Object>> categories = new ArrayList<>();
        //把category的id和name存到map里面，方便前端使用
        for (int i = 0; i < categoryNames.size(); i++) {
            HashMap<String, Object> category = new HashMap<>();
            category.put("id",categoryIds.get(i));
            category.put("name",categoryNames.get(i));
            categories.add(category);
        }
        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //查询规格参数组
        List<SpecGroup> groups = this.specParamClient.querySpecByCid(spu.getCid3());
        //查询特殊规格参数
        List<SpecParam> specParams = this.specParamClient.querySpecParams(null, spu.getCid3(), false, null);
        HashMap<Long, String> paramMap = new HashMap<>();
        specParams.forEach(specParam -> {
            //获取每个param的id和name封装进map,用于前端获取参数名
            paramMap.put(specParam.getId(), specParam.getName());
        });
        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", groups);
        // 查询特殊规格参数
        map.put("paramMap", paramMap);
        return map;
    }
}
