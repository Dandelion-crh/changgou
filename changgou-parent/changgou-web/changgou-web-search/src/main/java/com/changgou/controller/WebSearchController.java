package com.changgou.controller;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.util.Page;
import com.changgou.util.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 类名: WebSearchController
 * 作者: crh
 * 日期: 2020/6/4 0004上午 11:28
 */

@Controller
@RequestMapping("/search")
public class WebSearchController {

    @Autowired
    private SkuFeign skuFeign;

    @GetMapping(value = "list")
    public String search(Model model, @RequestParam(required = false) Map<String,String> searchMap){
        //远程调用搜索微服务查询搜索得到结果
        Map<String,Object> result = skuFeign.search(searchMap);

        //将搜索得到的结果放入model中,方便前端页面获取和展示数据
        model.addAttribute("result", result);

        //用于页面数据的回显
        model.addAttribute("searchMap",searchMap);

        //获取查询的url
        String url = getUrl(searchMap);
        model.addAttribute("url",UrlUtils.replateUrlParameter(url,"pageNum"));

        //排序查询的url,可以将url中的sortRole,sortField去掉
        String sortUrl = UrlUtils.replateUrlParameter(url,"sortRule","sortField","pageNum");
        model.addAttribute("sortUrl",sortUrl);

        //分页的信息
        String pageNum = result.get("pageNum").toString();//页码
        String pageSize = result.get("pageSize").toString();//每页显示多少条数据
        String totalElements = result.get("totalElements").toString();//总数据条数
        Page<SkuInfo> page = new Page<>(Long.parseLong(totalElements),
                Integer.parseInt(pageNum) + 1,
                Integer.parseInt(pageSize));
        model.addAttribute("page",page);

        return "search";
    }

    private String getUrl(Map<String, String> searchMap) {
        String url = "/search/list";
        if (null != searchMap){
            url +="?";
            //遍历请求参数
            for (Map.Entry<String, String> entry : searchMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                url += key + "=" + value +"&";
            }
            return url.substring(0,url.length() -1);
        }
       return url;
    }
}
