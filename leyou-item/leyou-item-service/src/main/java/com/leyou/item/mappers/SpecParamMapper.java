package com.leyou.item.mappers;

import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpecParamMapper {

    /**
     * 根据不同的条件查询参数集合
     * @param cid
     * @param gid
     * @param generic
     * @param searching
     * @return
     */
    List<SpecParam> querySpecParamById(
            @Param("cid") Long cid,
            @Param("gid") Long gid,
            @Param("generic") Boolean generic,
            @Param("searching") Boolean searching);
}
