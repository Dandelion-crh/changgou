package com.changgou.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类名: delay
 * 作者: crh
 * 日期: 2020/6/21 0021下午 4:18
 */
@Configuration
public class DelayOrderCongig {
    /**
     *声明死信交换机
     */
    @Bean
    public Exchange delayExchange(){
        return new DirectExchange("delayExchange");
    }

    /**
     *到期数据队列
     */
    @Bean
    public Queue seckillOrderTimerQueue(){
        return new Queue("seckillordertimer");
    }

    /**
     *超时延时队列
     */
    @Bean
    public Queue delaySeckillOrderTimerQueue(){
        return QueueBuilder.durable("seckillordertimerdelay")
                .withArgument("x-dead-letter-exchange","delayExchange")
                //消息超时进入死信队列,绑定死信队列交换机
                .withArgument("x-dead-letter-routing-key","seckillordertimer")//绑定指定的routing-key
                .build();
    }

    /**
     *交换机与队列绑定
     */
    @Bean
    public Binding basicBinding(Queue seckillOrderTimerQueue,
                                Exchange delayExchange){
        return BindingBuilder.bind(seckillOrderTimerQueue)
                .to(delayExchange)
                .with("seckillordertimer").noargs();
    }

}
