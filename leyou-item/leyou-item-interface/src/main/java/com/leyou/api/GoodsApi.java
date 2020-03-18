package com.leyou.api;

import com.leyou.bo.SpuBo;
import com.leyou.commom.PageResult;
import com.leyou.pojo.Sku;
import com.leyou.pojo.Spu;
import com.leyou.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {
    /*
     * @Description: 分页查询商品
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/19
     */
    @GetMapping("spu/page")
    PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );

    /*
     * @Description:根据spu商品ID查询spu商品详情
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/19
     */
    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetailById(@PathVariable("spuId") Long spuId);

    /*
     * @Description: 根据spu的id查询sku
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/19
     */
    @GetMapping("sku/list")
    List<Sku> querySkuBySpuId(@RequestParam("id") Long spuId);

    /*
     * @Description: 根据id查询spu
     * @return:
     * @Author: Half Tonight
     * @Date:2020/2/23
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id") Long id);
}

