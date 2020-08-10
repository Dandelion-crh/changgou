package com.changgou;

import com.changgou.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 类名: GoodsApplication
 * 作者: crh
 * 日期: 2020/5/27 0027下午 9:39
 */
@SpringBootApplication
@EnableEurekaClient//注册中心是eureka
@MapperScan(basePackages = {"com.changgou.goods.dao"})
public class GoodsApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoodsApplication.class,args);
    }
   /**
    *ID生成类
    */
    @Bean
    public IdWorker idWorker(){
        return new IdWorker();
    }
}
