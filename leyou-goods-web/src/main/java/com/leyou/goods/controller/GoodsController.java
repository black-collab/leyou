package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@RequestMapping("goods")
public class GoodsController {

    @Resource
    private GoodsService goodsService;

    @Resource
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model){
        Map<String,Object> modelMap = this.goodsService.loadModel(id);
        model.addAllAttributes(modelMap);
        this.goodsHtmlService.asyncExcute(id);
        return "item";
    }
}
