package com.changgou.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 类名: RabbitListener
 * 作者: crh
 * 日期: 2020/6/16 0016下午 3:06
 */

@Component
@org.springframework.amqp.rabbit.annotation.RabbitListener(queues = "queue.order")
public class RabbitListener {

    @Autowired
    private OrderService orderService;

    /**
     *监听支付消息的内容
     */
    @RabbitHandler
    public void listener(String message){
        //获取消息
        Map<String,String> map = JSONObject.parseObject(message, Map.class);
        String return_code = map.get("return_code");
        if (return_code.equals("SUCCESS")){
            //获取业务处理结果
            String result_code = map.get("result_code");
            //获取订单编号
            String out_trade_no = map.get("out_trade_no");
            if (result_code.equals("SUCCESS")){
                //获取交易流水号
                String transaction_id = map.get("transaction_id");
                //修改订单的状态
                orderService.updateStatus(out_trade_no,transaction_id);
            }else{
                //删除订单
                orderService.deleteOrder(out_trade_no);
            }
        }
    }
}
