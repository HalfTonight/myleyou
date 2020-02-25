package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller

public class GoodsController {
    @Autowired
    GoodsService goodsService;


    @Autowired
    private GoodsHtmlService goodsHtmlService;
    @GetMapping("item/{id}.html")
    public String goItemPage(Model model, @PathVariable("id") Long id ){
        Map<String, Object> map = this.goodsService.loadData(id);
        model.addAllAttributes(map);
        goodsHtmlService.creatHtml(id);
        return "item";
    }
}
