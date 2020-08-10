package com.changgou.order.controller;

import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import com.changgou.order.util.TokenDecode;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 类名: CartController
 * 作者: crh
 * 日期: 2020/6/13 0013下午 10:12
 */
@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * id
     * 新增购物车
     * @param id
     * @param num
     * @return
     */
    @GetMapping(value = "/add")
    public Result addCart(String id,Integer num){
        //String username = "crh";
        //获取用户信息
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");

        cartService.addCart(id,num,username);
        return new Result(true, StatusCode.OK,"新增购物车成功!");
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping(value = "/list")
    public Result getCart(){
        //获取用户信息
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");

        List<OrderItem> list = cartService.getCart(username);
        return new Result(true, StatusCode.OK,"查询购物车成功!",list);
    }

    /**
     * 查询购物车
     * @return
     */
    @GetMapping(value = "/list/choose")
    public Result getCart(@RequestParam String[] ids){
        //获取用户信息
        Map<String, String> userInfo = TokenDecode.getUserInfo();
        String username = userInfo.get("username");

        List<OrderItem> list = cartService.getCart(username,ids);
        return new Result(true, StatusCode.OK,"查询购物车成功!",list);
    }
}
