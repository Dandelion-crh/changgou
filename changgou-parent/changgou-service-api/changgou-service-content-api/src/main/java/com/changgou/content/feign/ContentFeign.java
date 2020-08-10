package com.changgou.content.feign;

import com.changgou.content.pojo.Content;
import com.changgou.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "content")
@RequestMapping("/content")
public interface ContentFeign {

    /**
     * 根据广告类别ID查询广告列表
     * @param id
     * @return
     */
    @GetMapping(value = "/list/category/{id}")
    Result<List<Content>> findByCategory(@PathVariable("id") Long id);
}

