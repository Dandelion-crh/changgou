package com.changgou.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;




/**
 * 类名: SeckillOrderConfig
 * 作者: crh
 * 日期: 2020/6/17 0017下午 11:19
 */

@Configuration
public class SeckillOrderConfig {
    
    /**
     *声明队列
     */
    @Bean
    public Queue seckillGoodsQueue(){
        return QueueBuilder.durable("seckillOrderQueue").build();
    }

    /**
     *声明交换机
     */
    @Bean
    public Exchange seckillExchange(){
        return ExchangeBuilder.directExchange("seckillOrderExchange").build();
    }

    
    /**
     *绑定
     */
    @Bean
    public Binding seckillBinding(Queue seckillGoodsQueue,
                                  Exchange seckillExchange){
        return BindingBuilder.bind(seckillGoodsQueue).to(seckillExchange).with("seckillOrderQueue").noargs();
    }

}
