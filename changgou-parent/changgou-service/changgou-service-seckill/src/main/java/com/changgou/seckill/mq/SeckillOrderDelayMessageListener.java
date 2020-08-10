package com.changgou.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.feign.WeixinPayFeign;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.seckill.service.SeckillOrderService;
import com.changgou.util.Result;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 类名: SeckillOrderDelayMessageListener
 * 作者: crh
 * 日期: 2020/6/20 0020下午 7:43
 */
@Component
@RabbitListener(queues = "seckillordertimer")
public class SeckillOrderDelayMessageListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private WeixinPayFeign weixinPayFeign;


    /***
     * 读取消息
     * 判断Redis中是否存在对应的订单
     * 如果存在，则关闭支付，再关闭订单
     * @param message
     */
    @RabbitHandler
    public void condumeMessage(@Payload String message){
        //读取消息
        SeckillStatus seckillStatus = JSON.parseObject(message,SeckillStatus.class);

        //获取redis中的订单消息
        String username = seckillStatus.getUsername();
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.boundHashOps("Order").get(username);

        //如果Redis中有订单信息,说明用户未支付
        if (seckillOrder!=null){
            System.out.println("准备回滚---");
            String orderId = seckillStatus.getOrderId();
            //关闭支付
            Result<Map<String,String>> closeResult = weixinPayFeign.closePay(orderId);
            Map<String,String> closeMap = closeResult.getData();

            if (closeMap != null && closeMap.get("return_code").equalsIgnoreCase("success")&&
                    closeMap.get("result_code").equalsIgnoreCase("success")){
                //关闭订单
                seckillOrderService.closeOrder(username);
            }
        }
    }
}
