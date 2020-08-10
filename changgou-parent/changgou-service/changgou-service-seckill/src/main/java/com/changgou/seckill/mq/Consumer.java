package com.changgou.seckill.mq;

/**
 * 类名: Consumer
 * 作者: crh
 * 日期: 2020/6/18 0018上午 12:20
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import com.changgou.util.IdWorker;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 *消费者监听mq秒杀下单队列,进行下单操作
 */
@Component
@RabbitListener(queues = "seckillOrderQueue")
public class Consumer {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     *新增订单
     */
    @RabbitHandler
    public void addOrder(String message){
        //将消息进行类型转换
        SeckillStatus seckillStatus = JSONObject.parseObject(message, SeckillStatus.class);

        /*//获取订单信息,看用户是否有未支付的订单 *前面消息生产者已做判断不需要在这判断
        Object o = redisTemplate.boundHashOps("Order").get(seckillStatus.getUsername());
        if (o != null){
            System.out.println("用户有未支付的订单,不能重复下单");
            return;
        }
       */

        //从队列中获取商品
        Object o = redisTemplate.boundListOps("SeckillGoodsStockQueue_" + seckillStatus.getGoodsId()).rightPop();
        if (o == null){
            throw new RuntimeException("商品售罄");
        }


        //获取商品的信息
        SeckillGoods seckillGoods = (SeckillGoods)redisTemplate.boundHashOps("SeckillGoods_" + seckillStatus.getTime()).get(seckillStatus.getGoodsId());
        //判断商品是否存在,同时判断商品是否还有库存
        if (seckillStatus != null && seckillGoods.getStockCount() > 0 ){
            //构建Order对象
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId("No" + idWorker.nextId());//设置id
            seckillOrder.setSeckillId(seckillGoods.getCostPrice().toString());//设置商品的支付金额
            seckillOrder.setMoney(seckillGoods.getCostPrice().toString());//设置商品的支付金额
            seckillOrder.setUserId(seckillStatus.getUsername());//设置购买的用户
            seckillOrder.setCreateTime(new Date());//订单的创建时间
            seckillOrder.setStatus("0");//订单状态
            //将订单信息存入redis
            redisTemplate
                    .boundHashOps("Order")
                    .put(seckillStatus.getUsername(),seckillOrder);
            //修改用户号的排队状态
            seckillStatus.setStatus(2);//设置排队状态
            seckillStatus.setMoney(Float.parseFloat(seckillOrder.getMoney()));//设置待支付的金额
            seckillStatus.setOrderId(seckillOrder.getId());
            stringRedisTemplate
                    .boundValueOps("SeckillStatus_" + seckillStatus.getUsername())
                    .set(JSONObject.toJSONString(seckillStatus));

            //发送延时消息到RabbitMQ中
            sendTimerMessage(seckillStatus);

            //商品库存减库存
            //扣减库存
            //seckillGoods.setStockCount(seckillGoods.getStockCount() -1);
            Long seckillGoodsCount = redisTemplate
                    .boundHashOps("SeckillGoodsCount")
                    .increment(seckillGoods.getId(), -1);
            //商品卖完了
            if (seckillGoodsCount == 0){
                //商品从redis移除
                redisTemplate
                        .boundHashOps("SeckillGoods_" + seckillStatus.getTime())
                        .delete(seckillStatus.getGoodsId());
                //同步到数据库
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            }else{
                //设置商品剩余库存
                seckillGoods.setStockCount(seckillGoodsCount.intValue());
                //将商品保存到redis中去
                redisTemplate
                        .boundHashOps("SeckillGoods_" +seckillStatus.getTime())
                        .put(seckillGoods.getId(),seckillGoods);
            }
        }
    }

    /**
     *发送延时消息到RabbitMQ
     */
    public void sendTimerMessage(SeckillStatus seckillStatus){
        //发送延时消息到RabbitMQ中
        rabbitTemplate.convertAndSend("seckillordertimerdelay", (Object)JSON.toJSONString(seckillStatus), new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("60000");//10000  10秒
                return message;
            }
        });
    }
}
