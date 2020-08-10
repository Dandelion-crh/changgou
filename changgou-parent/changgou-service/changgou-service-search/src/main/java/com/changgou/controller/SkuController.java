package com.changgou.controller;

import com.changgou.service.SkuInfoService;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import jdk.net.SocketFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 类名: SkuController
 * 作者: crh
 * 日期: 2020/6/1 0001下午 9:40
 */
@RestController
@CrossOrigin
@RequestMapping(value = "search")
public class SkuController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     *搜索数据
     */
    @GetMapping
    public Map<String,Object> search(@RequestParam(required = false) Map<String,String> searchMap){
        Map<String, Object> result = skuInfoService.search(searchMap);
        return result;
    }

    /**
     *数据导入方法
     */
    @GetMapping(value = "/import")
    public Result importData(){
        skuInfoService.importData();
        return new Result<>(true, StatusCode.OK,"导入数据成功");
    }

}
