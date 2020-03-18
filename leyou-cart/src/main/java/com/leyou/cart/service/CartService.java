package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.configure.LoginIntercptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.commom.utils.JsonUtils;
import com.leyou.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    static final String KEY_PREFIX = "leyou:cart:uid:";


    public void addCart( Cart cart) {
        //获取UserInfo对象
        UserInfo userInfo = LoginIntercptor.getUserInfo();
        String key=KEY_PREFIX+userInfo.getId();
        //获取Hash操作对象
        BoundHashOperations<String, Object, Object> ops = this.redisTemplate.boundHashOps(key);
        //看redis里有没有当前用户的信息
        Long skuId = cart.getSkuId();
        //先把传过来的cart里的num保存
        Integer num = cart.getNum();
        Boolean hasKey = ops.hasKey(skuId.toString());
        if(hasKey){
            //有，获取购物车信息
            String s = ops.get(skuId.toString()).toString();
            //把购物车信息反序列化
            cart = JsonUtils.parse(s, Cart.class);
            cart.setNum(cart.getNum()+num);

        }else{
            //没有 添加数据
            cart.setUserId(userInfo.getId());
            Sku sku = this.goodsClient.querySkuById(cart.getSkuId());
            cart.setImage(StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
        }
        ops.put(skuId.toString(),JsonUtils.serialize(cart));
    }

    public List<Cart> selCart() {
        UserInfo user = LoginIntercptor.getUserInfo();
        String key=KEY_PREFIX+user.getId();

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        List<Object> carts = hashOps.values();
        if(carts==null){
            return null;
        }

        return carts.stream().map(val->JsonUtils.parse(val.toString(),Cart.class))
                .collect(Collectors.toList());

    }

    public void updateCart(Cart cart) {

        UserInfo userInfo = LoginIntercptor.getUserInfo();
        String key=KEY_PREFIX+userInfo.getId();

        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);

        String jCart = hashOps.get(cart.getSkuId().toString()).toString();

        Cart cart1 = JsonUtils.parse(jCart, Cart.class);

        cart1.setNum(cart.getNum());

        hashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart1));

    }

    public void delCart(Long skuId) {
        UserInfo userInfo = LoginIntercptor.getUserInfo();
        String key=KEY_PREFIX+userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId.toString());
    }
}
