package com.changgou.service.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 类名: RabbitMQconfig
 * 作者: crh
 * 日期: 2020/6/16 0016下午 2:36
 */
@Configuration
public class RabbitMQConfig {

   /* @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;*/
    /**
     *声明队列
     */
    @Bean("payQueue")
    public Queue payQueue(){
        return QueueBuilder.durable("queue.order").build();
    }

    /**
     *声明交换机
     */
    @Bean("payExchange")
    public Exchange payExchange(){
        return ExchangeBuilder.topicExchange("exchange.order").durable(true).build();
    }

    /**
     *秒杀支付状态队列创建队列
     */
    @Bean("queueSeckillOrder")
    public Queue queueSeckillOrder(){
        return new Queue("queueSeckillOrder");
    }
    /**
     *秒杀状态队列绑定交换机
     */
    @Bean
    public Binding basicBindingSeckill(@Qualifier(value = "payExchange") Exchange payExchange,
                                       @Qualifier(value = "queueSeckillOrder")Queue queueSeckillOrder){
        return BindingBuilder.bind(queueSeckillOrder).to(payExchange).with("queueSeckillOrder").noargs();
    }

    /**
     *声明绑定
     */
    @Bean
    public Binding payBinding(@Qualifier(value = "payQueue") Queue payQueue,
                              @Qualifier(value = "payExchange") Exchange payExchange){
        return BindingBuilder.bind(payQueue).to(payExchange).with("queue.order").noargs();
    }
}
