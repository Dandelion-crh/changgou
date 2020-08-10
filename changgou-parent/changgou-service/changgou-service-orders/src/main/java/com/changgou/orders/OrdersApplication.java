package com.changgou.orders;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 类名: OrderApplication
 * 作者: crh
 * 日期: 2020/6/24 0024下午 2:38
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = "com.changgou.orders.dao")
public class OrdersApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class,args);
    }
}
