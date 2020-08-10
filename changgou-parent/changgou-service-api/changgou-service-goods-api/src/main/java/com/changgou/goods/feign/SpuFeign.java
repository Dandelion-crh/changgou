package com.changgou.goods.feign;

import com.changgou.goods.pojo.Spu;
import com.changgou.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "goods")
@RequestMapping(value = "/spu")
public interface SpuFeign {
    /**
     *根据SpuId查询Spu信息
     */
    @GetMapping("/{id}")
    Result<Spu> findById(@PathVariable(name = "id") String id);
}
