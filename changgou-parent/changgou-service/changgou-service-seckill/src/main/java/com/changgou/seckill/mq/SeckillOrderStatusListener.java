package com.changgou.seckill.mq;

/**
 * 类名: SeckillOrderStatusListener
 * 作者: crh
 * 日期: 2020/6/18 0018下午 11:21
 */


import com.alibaba.fastjson.JSONObject;
import com.changgou.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 监听秒杀支付结果的消费者
 */
@Component
@RabbitListener(queues = "queueSeckillOrder")
public class SeckillOrderStatusListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     *修改秒杀订单的状态
     */
    @RabbitHandler
    public void add(String message){
        //获取消息
        Map<String,String> map = JSONObject.parseObject(message,Map.class);
        //解析支付结果
        String return_code = map.get("return_code");
        if (return_code.equals("SUCCESS")){
            //获取业务的处理结果
            String result_code = map.get("result_code");
            //获取订单的编号
            Map<String,String> attach = JSONObject.parseObject(map.get("attach"),Map.class);
            String username = attach.get("username");
            if (result_code.equals("SUCCESS")){
                //获取交易流水号
                String transaction_id = map.get("transaction_id");
                //修改订单的状态
                seckillOrderService.updatePayStatus(username,transaction_id);
            }else{
                //删除订单
                seckillOrderService.deleteOrder(username);
            }
        }
    }

}
