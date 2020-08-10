package com.changgou.collect.controller;

import com.changgou.collect.service.CollectService;
import com.changgou.goods.pojo.Sku;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CollectController
 * @Description
 * @Author _Lx_
 */
@Controller
@RequestMapping(value = "/collect")
@CrossOrigin
public class CollectController {

    @Autowired
    private CollectService collectService;

    @RequestMapping(value = "/list/{username}/{page}/{size}")
    public Result<PageInfo<Sku>> findByUsername(@PathVariable String username,@PathVariable int page, @PathVariable int size ){
        // 根据用户名查询用户名信息
        PageInfo<Sku> skuPageInfo = collectService.findByUsername(username, page, size);
        return new Result<>(true, StatusCode.OK, "查询用户信息成功", skuPageInfo);
    }

    @GetMapping("/list")
    public String searchl(Model model){
        String username = "jason";
        Integer size = 8;
        Integer pageNum = 1;
        // 获取分页的信息
        PageInfo<Sku> pageInfo = collectService.findByUsername(username, pageNum,size);
        List<Sku> list = pageInfo.getList();
        model.addAttribute("pageInfo",list);
        return "home";
    }
}

