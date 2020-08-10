package com.changgou.collect.service.impl;

import com.changgou.collect.dao.CollectMapper;
import com.changgou.collect.pojo.GoodsCollect;
import com.changgou.collect.service.CollectService;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CollectServiceImpl
 * @Description
 * @Author _Lx_
 */
@Service
public class CollectServiceImpl implements CollectService {

    @Autowired
    private CollectMapper collectMapper;


    @Autowired(required = false)
    private SkuFeign skuFeign;
    /**
     * 根据用户名查找用户信息
     *
     * @param username
     * @return
     */
    @Override
    public PageInfo<Sku> findByUsername(String username,Integer page,Integer size) {

        List<Sku> skuList = new ArrayList<>();

        // 构建查询条件
        GoodsCollect goodsCollect = new GoodsCollect();
        goodsCollect.setUsername(username);
        // 根据用户名查询出用户的收藏信息集合
        List<GoodsCollect> goodsCollectList = collectMapper.select(goodsCollect);

        // 遍历收藏集合
        for (GoodsCollect collect : goodsCollectList) {
            // 调用SkuFeign获取SkuId
            Result<Sku> skuResult = skuFeign.findById(collect.getSkuid());
            Sku sku = skuResult.getData();
            skuList.add(sku);
        }
        Result<PageInfo> page2 = skuFeign.findPage2(skuList, page, size);
        PageInfo<Sku> data = page2.getData();
        return data;
    }
}
