package com.leyou.item.mappers;

import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Spu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SpuMapper {
    List<Spu> querySpuByKey(@Param("key") String key, @Param("saleable") Boolean saleable);

    /**
     * 添加spu
     * @param spuBo
     */
    void saveSpu(SpuBo spuBo);

    void updateSpu(SpuBo spuBo);

    @Select("select * from tb_spu where id = #{id}")
    Spu queryOneSpuById(Long id);
}
