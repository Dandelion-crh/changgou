package com.changgou.dao;

import com.changgou.search.pojo.SkuInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SkuInfoDao extends ElasticsearchRepository<SkuInfo,String> {
}
