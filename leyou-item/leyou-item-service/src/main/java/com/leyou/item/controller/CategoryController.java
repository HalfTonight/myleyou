package com.leyou.item.controller;

import com.leyou.item.service.CategoryService;
import com.leyou.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;
    @GetMapping("list")
        public ResponseEntity<List<Category>> queryCategoryById(@RequestParam(value = "pid",defaultValue = "0")Long pid){
            if(pid==null||pid<0){
                return ResponseEntity.badRequest().build();
            }
            List<Category> categories=this.categoryService.queryCategoriesById(pid);
            if(CollectionUtils.isEmpty(categories)){
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(categories);
    }
    @GetMapping
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = this.categoryService.queryNamesByIds(ids);
        if(CollectionUtils.isEmpty(names)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id")Long cid3){
        List<Category> list=this.categoryService.queryAllByCid3(cid3);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
