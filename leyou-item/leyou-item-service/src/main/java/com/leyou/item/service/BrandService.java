package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mappers.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BrandService {

    @Resource
    private BrandMapper brandMapper;

    /**
     * 分页查询品牌
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> findBrandByKey(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        PageHelper.startPage(page, rows);
        List<Brand> brandList = brandMapper.findBrandByKey(key, sortBy, desc);
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brandList);
        PageResult<Brand> pageResult = new PageResult<>(
                brandPageInfo.getTotal(),
                brandPageInfo.getPages(),
                brandPageInfo.getList());
        return pageResult;
    }

    /**
     * 创建新的品牌
     *
     * @param name
     * @param image
     * @param cids
     * @param letter
     */
    @Transactional
    public void newBrand(String name, String image, List<Integer> cids, Character letter) {
        brandMapper.newBrand(name, image, letter);
        Long bid = brandMapper.findLastInsertId();
        for (Integer cid : cids) {
            brandMapper.newBrandAndCategory(bid, cid);
        }
    }

    public List<Brand> findBrandBycid(Long cid) {
        return brandMapper.findBrandsBycid(cid);
    }

    public Brand findBrandBycId(long id) {
        return this.brandMapper.findBrandById(id);
    }
}
