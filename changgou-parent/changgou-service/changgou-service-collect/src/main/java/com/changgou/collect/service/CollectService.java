package com.changgou.collect.service;

import com.changgou.collect.pojo.GoodsCollect;
import com.changgou.goods.pojo.Sku;
import com.changgou.util.Result;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CollectService {
    /**
     * 根据用户名查找用户信息
     * @param username
     * @return
     */
    PageInfo<Sku> findByUsername(String username,Integer page,Integer size);
}

