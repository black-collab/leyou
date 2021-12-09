package com.leyou.item.mappers;

import com.leyou.item.pojo.Sku;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SkuMapper {

    void saveSku(Sku sku);

    /**
     * 根据spuId查询sku集合
     * @param sid spuId
     * @return
     */
    @Select("select s.*,st.stock from tb_sku as s inner join tb_stock as st on s.spu_id = #{sid}  and st.sku_id = s.id")
    List<Sku> querySkuBySpuId(Long sid);

    void deleteSkuBySpuId(Long id);
}
