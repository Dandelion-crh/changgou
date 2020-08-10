package com.changgou.item.controller;

import com.changgou.item.service.PageService;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类名: PageController
 * 作者: crh
 * 日期: 2020/6/8 0008下午 7:41
 */
@RestController
@RequestMapping(value = "/page")
public class PageController {

    @Autowired
    private PageService pageService;

    /**
     *创建静态页
     */

    @GetMapping(value = "/createHtml/{id}")
    public Result createHtml(@PathVariable(value = "id") String id){
        //生成静态页
        pageService.createPageHtml(id);
        return new Result(true, StatusCode.OK,"生成成功!");
    }

}
