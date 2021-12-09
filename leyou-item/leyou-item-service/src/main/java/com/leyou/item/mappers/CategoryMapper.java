package com.leyou.item.mappers;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category> {

    @Select("select * from tb_category where parent_id = #{id}")
    @Results(
            value = {
                    @Result(property = "parentId", column = "parent_id"),
                    @Result(property = "isParent", column = "is_parent")
            }
    )
    List<Category> findCategoryById(long id);

    List<String> queryCategoryByIds(@Param("ids") List<Long> ids);

    @Select("select * from tb_category where id = #{cid3}")
    Category queryCategoryById(Long cid3);
}
