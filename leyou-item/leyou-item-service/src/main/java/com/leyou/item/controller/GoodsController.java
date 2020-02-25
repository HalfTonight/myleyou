package com.leyou.item.controller;

import com.leyou.bo.SpuBo;
import com.leyou.commom.PageResult;
import com.leyou.item.service.GoodsService;
import com.leyou.pojo.Category;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    /*
     * @Description: 分页查询商品
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/19
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable" ,required = false)Boolean saleable,
            @RequestParam(value = "page" ,defaultValue = "1")Integer page,
            @RequestParam(value = "rows" ,defaultValue = "5")Integer rows
    ){

       PageResult<SpuBo> result=this.goodsService.querySpuBoByPage(key,saleable,page,rows);
        if( CollectionUtils.isEmpty(result.getItems())){
            return  ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
    @PostMapping("goods")
    public  ResponseEntity<Void> addGoods(@RequestBody SpuBo spuBo){
        this.goodsService.addGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /*
     * @Description:根据spu商品ID查询spu商品详情
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/19
    */
    @GetMapping("spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("spuId") Long spuId){
        SpuDetail spuDetail=this.goodsService.querySpudetail(spuId);
        if(spuDetail==null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(spuDetail);
    }
    /*
     * @Description: 根据spu的id查询sku
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/19
    */
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long spuId){
        List<Sku> skus=this.goodsService.querySkuBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo){
        this.goodsService.updateGoods(spuBo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    /*
     * @Description: 根据id查询spu
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/23
    */
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu=this.goodsService.querySkuById(id);
        if(spu==null){
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(spu);
    }

}
