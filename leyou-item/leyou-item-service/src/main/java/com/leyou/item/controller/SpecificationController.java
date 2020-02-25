package com.leyou.item.controller;

import com.leyou.item.service.SpecificationService;
import com.leyou.pojo.SpecGroup;
import com.leyou.pojo.SpecParam;
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
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    SpecificationService specificationService;
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsById(@PathVariable("cid") Long cid){
        List<SpecGroup> groups=this.specificationService.queryGroupsByCid(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(groups);
    }
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam( value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "serching",required = false) Boolean serching
    ){
        List<SpecParam> groups=this.specificationService.queryParam(gid,cid,generic,serching);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>>querySpecGroup(@PathVariable("cid") Long cid){

        List<SpecGroup> groups=this.specificationService.queryGroupWithParam(cid);
        if(CollectionUtils.isEmpty(groups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groups);
    }
}
