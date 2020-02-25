package com.leyou.item.controller;


import com.leyou.commom.PageResult;
import com.leyou.item.service.BrandService;
import com.leyou.pojo.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {
    //page?key=&page=1&rows=5&sortBy=id&desc=false
    @Autowired
    BrandService brandService;
    @RequestMapping("page")
    public ResponseEntity<PageResult<Brand>> qureyBrandsByPage(
            @RequestParam(value = "key" ,required = false)String key,
            @RequestParam(value = "page" ,defaultValue = "1")Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5")Integer rows,
            @RequestParam(value = "sortBy" ,required = false)String sortBy,
            @RequestParam(value = "desc" ,required = false)Boolean desc
    ){
        PageResult<Brand> result =this.brandService.queryBrandsByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping
    public ResponseEntity<Void> saveBrand(Brand brand ,@RequestParam("cids") List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid){
        List<Brand> brands=this.brandService.queryBrandByCid(cid);
        if(CollectionUtils.isEmpty(brands)){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brands);
    }
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryById(@PathVariable("id") Long id){
        Brand brand =this.brandService.queryBrandByid(id);
        if(brand==null){
            ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);

    }


}
