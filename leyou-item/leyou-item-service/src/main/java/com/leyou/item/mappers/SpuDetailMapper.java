package com.leyou.item.mappers;

import com.leyou.item.pojo.SpuDetail;
import org.apache.ibatis.annotations.Select;

public interface SpuDetailMapper {

    void saveSpuDetail(SpuDetail spuDetail);

    @Select("select * from tb_spu_detail where spu_id = #{sid}")
    SpuDetail querySpuDetailBySpuId(Long sid);

    void updateSpuDetail(SpuDetail spuDetail);
}
