package com.leyou.item.service;

import com.leyou.item.mappers.SpecGroupMapper;
import com.leyou.item.mappers.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecGroupAndParamService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    public List<SpecGroup> querySpecGroup(Long cid) {
        return specGroupMapper.querySpecGroupById(cid);
    }

    public List<SpecParam> querySpecParam(Long cid, Long gid, Boolean generic, Boolean searching) {
        return specParamMapper.querySpecParamById(cid, gid, generic, searching);
    }

    /**
     * 根据分类ID查询参数组，组内包含规格参数
     * @param cid
     * @return
     */
    public List<SpecGroup> querySpecByCid(Long cid) {
        //查询分类id所拥有的参数组
        List<SpecGroup> groups = this.specGroupMapper.querySpecGroupById(cid);
        groups.forEach(specGroup -> {
            Long id = specGroup.getId();
            //给每个参数组对象添加对应的组的参数集合
            List<SpecParam> specParams = this.specParamMapper.querySpecParamById(null, id, null, null);
            specGroup.setParams(specParams);
        });
        return groups;
    }
}
