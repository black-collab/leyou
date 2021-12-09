package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecParamApi {

    /**
     * 根据分类ID查询参数组，组内包含规格参数
     * @param cid
     * @return
     */
    @GetMapping("{cid}")
    public List<SpecGroup> querySpecByCid(@PathVariable("cid") Long cid);

    /**
     * 根据3级分类ID查询参数分组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public List<SpecGroup> querySpecGroup(@PathVariable("cid") Long cid);

    /**
     * 根据分组gid查询详细参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public List<SpecParam> querySpecParams(
            @RequestParam(name = "gid",required = false) Long gid,
            @RequestParam(name = "cid",required = false) Long cid,
            @RequestParam(name = "generic",required = false) Boolean generic,
            @RequestParam(name = "searching",required = false) Boolean searching
    );
}
