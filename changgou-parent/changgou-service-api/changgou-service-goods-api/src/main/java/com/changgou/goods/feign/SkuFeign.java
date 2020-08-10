package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import com.changgou.util.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@FeignClient(name = "goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /***
     * 根据审核状态查询Sku列表
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(value = "status") String status);

    /**
     * 根据条件搜索
     * @param sku
     * @return
     */
    @PostMapping(value = "/search")
    Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);

    /***
     * 根据ID查询Sku数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    Result<Sku> findById(@PathVariable("id") String id);

    /**
     * 扣减商品库存
     * @param dataMap
     * @return
     */
    @GetMapping(value = "/decr/count")
    Result descount(@RequestParam Map<String,Integer> dataMap);

    /**
     *查询收藏分页信息
     */
    @RequestMapping("/list/{page}/{size}")
    Result<PageInfo> findPage2(@RequestBody(required = false) List<Sku> skuList, @PathVariable(value ="page") int page, @PathVariable(value ="size") int size);
}
