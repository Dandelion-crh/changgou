package com.changgou.item.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.CategoryFeign;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.item.service.PageService;

import com.changgou.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名: PageServiceImpl
 * 作者: crh
 * 日期: 2020/6/4 0004下午 11:23
 */
@Service
public class PageServiceImpl implements PageService {

    //静态文件所存储的位置
    @Value("${pagepath}")
    private String pagepath;

    //模板引擎对象
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SkuFeign skuFeign;

    /***
     * 加载静态页所需的数据
     */
    public Map<String,Object> buildDataModel(String spuid){
        //创建一个Map用于存储静态所有数据
        Map<String, Object> dataMap = new HashMap<String,Object>();

        //查询Spu
        Result<Spu> spuResult = spuFeign.findById(spuid);
        Spu spu = spuResult.getData();

        //List<Sku>查询
        Sku sku = new Sku();
        sku.setSpuId(spuid);
        Result<List<Sku>> skuResult = skuFeign.findList(sku);
        List<Sku> skuList = skuResult.getData();

        //获取分类信息
        dataMap.put("category1",categoryFeign.findById(spu.getCategory1Id()).getData());
        dataMap.put("category2",categoryFeign.findById(spu.getCategory2Id()).getData());
        dataMap.put("category3",categoryFeign.findById(spu.getCategory3Id()).getData());
        //商品图片集合
        dataMap.put("imageList",spu.getImages().split(","));
        //spu存储
        dataMap.put("spu",spu);
        //List<Sku>存储
        dataMap.put("skuList",skuList);
        //specList
        dataMap.put("specList", JSON.parseObject(spu.getSpecItems(),Map.class));
        return dataMap;
    }


    /**
     * 根据SPUID生成静态页
     * @param id
     */
    @Override
    public void createPageHtml(String id) {
        try {
            //创建上下文对象
            Context context = new Context();
            //数据模型,用于存储页面需要填充的数据
            Map<String, Object> dataModel = buildDataModel(id);
            context.setVariables(dataModel);

            //生成的静态页的位置
            File dest = new File(pagepath, id + ".html");
            //生成静态页
            PrintWriter printWriter = new PrintWriter(dest, "UTF-8");
            /**
             *1.模板名字
             * 2.上下文对象
             * 3:文件输出对象
             */
            templateEngine.process("item",context,printWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
