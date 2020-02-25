package com.leyou.search.controller;

import com.leyou.commom.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class SearchController {

    @Autowired
    SearchService searchService;

    //请求路径在网关上路由的路径前面有search，所以这里不用再加search了
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest){
        SearchResult result=this.searchService.search(searchRequest);
        if(result==null|| CollectionUtils.isEmpty(result.getItems())){
            return  ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}
