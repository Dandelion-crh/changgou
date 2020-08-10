package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * 购物车的service
 */
public interface CartService {

    /**
     *新增购物车
     */
    void addCart(String skuId,Integer num,String username);

    /**
     *查询购物车
     */
    List<OrderItem> getCart(String username);

    /**
     *查询选中的购物车数据
     */
    List<OrderItem> getCart(String username,String[] ids);
}
