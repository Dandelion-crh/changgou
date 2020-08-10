package com.changgou.service;

import java.util.Map;

public interface SkuInfoService {

    /**
     *将商品数据从数据库导入到es中去
     */
    void importData();

    /**
     *搜索实现
     */
    Map<String,Object> search(Map<String,String> searchMap);
}
