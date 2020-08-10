package com.changgou.text;

import com.alibaba.fastjson.JSON;
import com.changgou.order.OrderApplication;
import com.changgou.order.controller.OrderController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * 类名: OrderServicetext
 * 作者: crh
 * 日期: 2020/6/22 0022下午 4:38
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class OrderServicetext {

    @Autowired(required = false)
    private OrderController orderController;

    @Test
    public void findOrderByUsername(){
        String username = "sz";
        String order = JSON.toJSONString(orderController.findOrder(username).getData());
        String s = JSON.toJSONString(order);
        //打印订单信息
        System.out.println(s);
    }
}
