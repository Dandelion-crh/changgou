package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名: CartServiceImpl
 * 作者: crh
 * 日期: 2020/6/13 0013下午 9:42
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    /**
     * 新增购物车
     * @param skuId
     * @param num
     * @param username
     */
    @Override
    public void addCart(String skuId, Integer num, String username) {

        //删除购物车的这条商品的数据
        if(num <= 0){
            redisTemplate.boundHashOps("Cart_" + username).delete(skuId);
            return;
        }
        //获取购物车的所有的数据
        List<OrderItem> items = redisTemplate.boundHashOps("Cart_" + username).values();
       /* //找到与当前skuid相等的购物车的信息
        if (items != null) {
            for (OrderItem item : items) {
                if (item.getSkuId().equals(skuId)) {
                    item.setNum(item.getNum() + num);
                }
            }
        }*/

       //获取封装的购物车数据
        OrderItem orderItem = getDataToOrderItem(skuId,num);
        //数据保存到redis中
        redisTemplate.boundHashOps("Cart_" + username).put(skuId,orderItem);
    }

    private OrderItem getDataToOrderItem(String skuId, Integer num) {
        //查询sku信息
        Sku sku = skuFeign.findById(skuId).getData();

        //查询spu信息
        Spu spu = spuFeign.findById(sku.getSpuId()).getData();

        //数据进行封装,封装成OrderItem对象
        OrderItem orderItem = new OrderItem();

        //1级分类
        orderItem.setCategoryId1(spu.getCategory1Id());
        //2级分类
        orderItem.setCategoryId2(spu.getCategory2Id());
        //3级分类
        orderItem.setCategoryId3(spu.getCategory3Id());
        //设置spuid
        orderItem.setSpuId(spu.getId());
        //设置skuid
        orderItem.setSkuId(skuId);
        //设置name
        orderItem.setName(sku.getName());
        //设置价格
        orderItem.setPrice(sku.getPrice());
        //设置购买数量
        orderItem.setNum(num);
        //设置金额
        orderItem.setMoney(sku.getPrice() * num);
        //图片
        orderItem.setImage(spu.getImage());
        return orderItem;
    }

    /**
     * 查询购物车
     *
     * @param username
     */
    @Override
    public List<OrderItem> getCart(String username) {
        //查询购物车
        List<OrderItem> list = redisTemplate.boundHashOps("Cart_" + username).values();
        return list;
    }

    /**
     * 查询选中的购物车数据
     *
     * @param username
     * @param ids
     */
    @Override
    public List<OrderItem> getCart(String username, String[] ids) {
        List<OrderItem> list = new ArrayList<>();
        //获取购物车中所有的数据信息
        List<OrderItem> orderItemList = redisTemplate.boundHashOps("Cart_" + username).values();

        for (String id : ids) {
            for (OrderItem orderItem : orderItemList) {
                if (id.equals(orderItem.getSkuId())){
                    list.add(orderItem);
                }
            }
        }
        return list;
    }
}
