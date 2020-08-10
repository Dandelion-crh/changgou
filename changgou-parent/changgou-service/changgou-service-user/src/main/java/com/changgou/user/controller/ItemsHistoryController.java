package com.changgou.user.controller;
import com.changgou.user.pojo.ItemsHistory;
import com.changgou.user.service.ItemsHistoryService;
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
@RequestMapping("/itemsHistory")
@CrossOrigin
public class ItemsHistoryController {

    @Autowired
    private ItemsHistoryService itemsHistoryService;

    /***
     * ItemsHistory分页条件搜索实现
     * @param itemsHistory
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false)  ItemsHistory itemsHistory, @PathVariable  int page, @PathVariable  int size){
        //调用ItemsHistoryService实现分页条件查询ItemsHistory
        PageInfo<ItemsHistory> pageInfo = itemsHistoryService.findPage(itemsHistory, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * ItemsHistory分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用ItemsHistoryService实现分页查询ItemsHistory
        PageInfo<ItemsHistory> pageInfo = itemsHistoryService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param itemsHistory
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<ItemsHistory>> findList(@RequestBody(required = false)  ItemsHistory itemsHistory){
        //调用ItemsHistoryService实现条件查询ItemsHistory
        List<ItemsHistory> list = itemsHistoryService.findList(itemsHistory);
        return new Result<List<ItemsHistory>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        //调用ItemsHistoryService实现根据主键删除
        itemsHistoryService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改ItemsHistory数据
     * @param itemsHistory
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  ItemsHistory itemsHistory,@PathVariable String id){
        //设置主键值
        itemsHistory.setId(id);
        //调用ItemsHistoryService实现修改ItemsHistory
        itemsHistoryService.update(itemsHistory);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增ItemsHistory数据
     * @param itemsHistory
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   ItemsHistory itemsHistory){
        //调用ItemsHistoryService实现添加ItemsHistory
        itemsHistoryService.add(itemsHistory);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询ItemsHistory数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<ItemsHistory> findById(@PathVariable String id){
        //调用ItemsHistoryService实现根据主键查询ItemsHistory
        ItemsHistory itemsHistory = itemsHistoryService.findById(id);
        return new Result<ItemsHistory>(true,StatusCode.OK,"查询成功",itemsHistory);
    }

    /***
     * 查询ItemsHistory全部数据
     * @return
     */
    @GetMapping
    public Result<List<ItemsHistory>> findAll(){
        //调用ItemsHistoryService实现查询所有ItemsHistory
        List<ItemsHistory> list = itemsHistoryService.findAll();
        return new Result<List<ItemsHistory>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
