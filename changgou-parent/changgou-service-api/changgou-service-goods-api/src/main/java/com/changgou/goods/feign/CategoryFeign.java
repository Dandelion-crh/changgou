package com.changgou.goods.feign;

import com.changgou.goods.pojo.Category;
import com.changgou.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "goods")
@RequestMapping(value = "/category")
public interface CategoryFeign {
    /**
     *获取分类的对象信息
     */
    @GetMapping("/{id}")
    Result<Category> findById(@PathVariable(name = "id") Integer id);
}
