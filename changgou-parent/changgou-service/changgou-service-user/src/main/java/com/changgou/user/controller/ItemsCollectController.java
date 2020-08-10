package com.changgou.user.controller;
import com.changgou.user.pojo.ItemsCollect;
import com.changgou.user.service.ItemsCollectService;
import com.github.pagehelper.PageInfo;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/****
 * @Author:shenkunlin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/itemsCollect")
@CrossOrigin
public class ItemsCollectController {

    @Autowired
    private ItemsCollectService itemsCollectService;

    /***
     * ItemsCollect分页条件搜索实现
     * @param itemsCollect
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false)  ItemsCollect itemsCollect, @PathVariable  int page, @PathVariable  int size){
        //调用ItemsCollectService实现分页条件查询ItemsCollect
        PageInfo<ItemsCollect> pageInfo = itemsCollectService.findPage(itemsCollect, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * ItemsCollect分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用ItemsCollectService实现分页查询ItemsCollect
        PageInfo<ItemsCollect> pageInfo = itemsCollectService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param itemsCollect
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<ItemsCollect>> findList(@RequestBody(required = false)  ItemsCollect itemsCollect){
        //调用ItemsCollectService实现条件查询ItemsCollect
        List<ItemsCollect> list = itemsCollectService.findList(itemsCollect);
        return new Result<List<ItemsCollect>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        //调用ItemsCollectService实现根据主键删除
        itemsCollectService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改ItemsCollect数据
     * @param itemsCollect
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  ItemsCollect itemsCollect,@PathVariable String id){
        //设置主键值
        itemsCollect.setId(id);
        //调用ItemsCollectService实现修改ItemsCollect
        itemsCollectService.update(itemsCollect);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增ItemsCollect数据
     * @param itemsCollect
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   ItemsCollect itemsCollect){
        //调用ItemsCollectService实现添加ItemsCollect
        itemsCollectService.add(itemsCollect);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询ItemsCollect数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ItemsCollect> findById(@PathVariable String id){
        //调用ItemsCollectService实现根据主键查询ItemsCollect
        ItemsCollect itemsCollect = itemsCollectService.findById(id);
        return new Result<ItemsCollect>(true,StatusCode.OK,"查询成功",itemsCollect);
    }

    /***
     * 查询ItemsCollect全部数据
     * @return
     */
    @GetMapping
    public Result<List<ItemsCollect>> findAll(){
        //调用ItemsCollectService实现查询所有ItemsCollect
        List<ItemsCollect> list = itemsCollectService.findAll();
        return new Result<List<ItemsCollect>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
