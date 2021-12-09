package com.leyou.item.mappers;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper {

    /**
     * 分页查询
     * @param key
     * @param sortBy
     * @param desc
     * @return
     */
    List<Brand> findBrandByKey(@Param("key") String key, @Param("sortBy") String sortBy, @Param("desc") Boolean desc);

    /**
     * 新增品牌
     * @param name
     * @param image
     * @param letter
     * @return
     */
    int newBrand(@Param("name") String name, @Param("image") String image, @Param("letter") Character letter);

    /**
     *
     * @return 获取当前线程connection最后insert添加的数据的ID，不同线程的connection不会影响
     */
    @Select("select LAST_INSERT_ID()")
    Long findLastInsertId();

    /**
     * 新增中间表数据
     * @param bid
     * @param cid
     */
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void newBrandAndCategory(@Param("bid") Long bid, @Param("cid") Integer cid);

    /**
     * 根据brandId查询brand品牌
     * @param brandId
     * @return
     */
    @Select("select * from tb_brand where id = #{id}")
    Brand findBrandById(Long brandId);

    @Select("SELECT b.* FROM tb_category_brand as cb INNER JOIN tb_brand as b " +
            "ON cb.category_id = #{cid} AND cb.brand_id = b.id")
    List<Brand> findBrandsBycid(Long cid);
}
