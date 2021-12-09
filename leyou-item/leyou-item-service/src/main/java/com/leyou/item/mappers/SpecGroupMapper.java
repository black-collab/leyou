package com.leyou.item.mappers;

import com.leyou.item.pojo.SpecGroup;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SpecGroupMapper {
    @Select("select * from tb_spec_group where cid = #{cid}")
    List<SpecGroup> querySpecGroupById(Long cid);
}
