package com.changgou.user.controller;
import com.changgou.user.pojo.Comments;
import com.changgou.user.service.CommentsService;
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
@RequestMapping("/comments")
@CrossOrigin
public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    /***
     * Comments分页条件搜索实现
     * @param comments
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@RequestBody(required = false)  Comments comments, @PathVariable  int page, @PathVariable  int size){
        //调用CommentsService实现分页条件查询Comments
        PageInfo<Comments> pageInfo = commentsService.findPage(comments, page, size);
        return new Result(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * Comments分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result<PageInfo> findPage(@PathVariable  int page, @PathVariable  int size){
        //调用CommentsService实现分页查询Comments
        PageInfo<Comments> pageInfo = commentsService.findPage(page, size);
        return new Result<PageInfo>(true,StatusCode.OK,"查询成功",pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param comments
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Comments>> findList(@RequestBody(required = false)  Comments comments){
        //调用CommentsService实现条件查询Comments
        List<Comments> list = commentsService.findList(comments);
        return new Result<List<Comments>>(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        //调用CommentsService实现根据主键删除
        commentsService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 修改Comments数据
     * @param comments
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody  Comments comments,@PathVariable String id){
        //设置主键值
        comments.setId(id);
        //调用CommentsService实现修改Comments
        commentsService.update(comments);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 新增Comments数据
     * @param comments
     * @return
     */
    @PostMapping
    public Result add(@RequestBody   Comments comments){
        //调用CommentsService实现添加Comments
        commentsService.add(comments);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询Comments数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Comments> findById(@PathVariable String id){
        //调用CommentsService实现根据主键查询Comments
        Comments comments = commentsService.findById(id);
        return new Result<Comments>(true,StatusCode.OK,"查询成功",comments);
    }

    /***
     * 查询Comments全部数据
     * @return
     */
    @GetMapping
    public Result<List<Comments>> findAll(){
        //调用CommentsService实现查询所有Comments
        List<Comments> list = commentsService.findAll();
        return new Result<List<Comments>>(true, StatusCode.OK,"查询成功",list) ;
    }
}
