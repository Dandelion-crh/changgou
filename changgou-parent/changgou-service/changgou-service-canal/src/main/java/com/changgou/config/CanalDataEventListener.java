package com.changgou.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.changgou.CanalApplication;
import com.changgou.content.feign.ContentFeign;
import com.changgou.content.pojo.Content;
import com.changgou.util.Result;
import com.xpand.starter.canal.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * 类名: CanalDataEventListener
 * 作者: crh
 * 日期: 2020/6/1 0001下午 3:46
 */

/**
 *监听到数据库变化以后，处理变化数据的类
 */
@CanalEventListener
public class CanalDataEventListener {

    @Autowired
    private ContentFeign contentFeign;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 数据库新增监听
     * @param eventType:数据库发生变化的类型
     * @param rowData:发生变化的数据
     */
   /* @InsertListenPoint
    public void onEventInsert(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //获取新增后的数据:
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        //进行数据循环
        for (CanalEntry.Column column : beforeColumnsList) {
            //发生变化数据的列的名字
            String name = column.getName();
            //发生变化列的值
            String value = column.getValue();

            System.out.println(name + ":" + value);
        }
    }*/


    /**
     * 数据库修改的监听
     */
   /* @UpdateListenPoint
    public void onEventUpdate(CanalEntry.EventType eventType,CanalEntry.RowData rowData){
        //修改前数据
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        //进行数据循环
        for (CanalEntry.Column column : beforeColumnsList) {
            //发生变化数据的列的名字
            String name = column.getName();
            //发生变化列的值
            String value = column.getValue();

            System.out.println(name + ":" + value);
        }
        //修改后数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        //进行数据循环
        for (CanalEntry.Column column : afterColumnsList) {
            //发生变化数据的列的名字
            String name = column.getName();
            //发生变化列的值
            String value = column.getValue();

            System.out.println(name + ":" + value);
        }
    }*/

    /**
     * 数据库删除的监听
     */
   /* @DeleteListenPoint
    public void onEventDelete(CanalEntry.EventType eventType,CanalEntry.RowData rowData) {
        //删除前数据
        List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();
        //进行数据循环
        for (CanalEntry.Column column : beforeColumnsList) {
            //发生变化数据的列的名字
            String name = column.getName();
            //发生变化列的值
            String value = column.getValue();

            System.out.println(name + ":" + value);
        }
        //删除后数据
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        //进行数据循环
        for (CanalEntry.Column column : afterColumnsList) {
            //发生变化数据的列的名字
            String name = column.getName();
            //发生变化列的值
            String value = column.getValue();

            System.out.println(name + ":" + value);
        }
    }*/

    /**
     * 自定义监听
     */
    /*@ListenPoint(destination = "example",//与canal实例的名字保持一致--canal实例的名字
            schema = "changgou_content", //监听的数据库
            table = {"tb_content","tb_content_category"}, //指定监听的表
            eventType = {CanalEntry.EventType.UPDATE}//监听的事件的类型
    )
    public void onEventCustomUpdate(CanalEntry.RowData rowData, CanalEntry.EventType eventType){

        //修改前的数据的主键ID
        Long id = null;
        //修改后的数据是什么
        List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();
        //进行数据循环
        for (CanalEntry.Column column : afterColumnsList) {
            //发生变化数据的列的名字
            String name = column.getName();
            if(name.equals("category_id")){
                String value = column.getValue();
                //两种方式 方式一:
//                id = Long.parseLong(value);
                //方式二
                id = Long.valueOf(value);
            }
        }

        //-------------------以上的代码都是为了获取category_id这个列的值

        //-----------一下为通过category_id获取最新的广告数据,将数据存入redis

        //远程调用,获取最新的数据信息
        List<Content> list = contentFeign.findByCategory(id).getData();

        //将数据存储到redis
        stringRedisTemplate.boundValueOps("content_" + id).set(JSONObject.toJSONString(list));
    }*/

    /***
     * 修改广告数据修改监听
     * 同步数据到Redis
     * @param eventType
     * @param rowData
     */
    @ListenPoint(destination = "example", schema = "changgou_content", table = {"tb_content"}, eventType = {CanalEntry.EventType.UPDATE,CanalEntry.EventType.INSERT,CanalEntry.EventType.DELETE})
    public void onEventCustomUpdate(CanalEntry.EventType eventType, CanalEntry.RowData rowData) {
        //获取广告分类的ID
        String categoryId = getColumn(rowData, "category_id");
        //根据广告分类ID获取所有广告
        Result<List<Content>> result = contentFeign.findByCategory(Long.valueOf(categoryId));
        //将广告数据存入到Redis缓存
        List<Content> contents = result.getData();
        stringRedisTemplate.boundValueOps("content_"+categoryId).set(JSON.toJSONString(contents));
    }


    /***
     * 获取指定列的值
     * @param rowData
     * @param columnName
     * @return
     */
    public String getColumn(CanalEntry.RowData rowData,String columnName){
        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {
            if(column.getName().equals(columnName)){
                return column.getValue();
            }
        }

        //有可能是删除操作
        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
            if(column.getName().equals(columnName)){
                return column.getValue();
            }
        }
        return null;
    }
}
