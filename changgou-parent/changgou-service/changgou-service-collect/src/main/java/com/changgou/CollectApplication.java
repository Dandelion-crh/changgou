package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName CollectApplication
 * @Description
 * @Author _Lx_
 */
@SpringBootApplication
@EnableFeignClients(basePackages = {"com.changgou.goods.feign"})
@EnableEurekaClient
@MapperScan(basePackages = "com.changgou.collect.dao")
public class CollectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectApplication.class,args);
    }

}
