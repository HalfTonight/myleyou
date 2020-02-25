package com.leyou.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;
    @Autowired
    private GoodsService goodsService;

    public void creatHtml(Long spuId) {

        PrintWriter writer = null;

        try {
            Map<String, Object> map = this.goodsService.loadData(spuId);
            Context context=new Context();
            context.setVariables(map);
            writer = new PrintWriter(
                    new File("E:\\nginx-1.14.0\\html\\item"+spuId+".html"));
            engine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                writer.close();
            }
        }



    }
}
