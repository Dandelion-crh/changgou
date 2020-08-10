package com.changgou;

import com.changgou.util.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 类名: SeckillApplication
 * 作者: crh
 * 日期: 2020/6/17 0017下午 5:51
 */
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.changgou.seckill.dao"})
@EnableFeignClients(basePackages = {"com.changgou.pay.feign"})
@EnableScheduling//开启定时任务
public class SecKillApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecKillApplication.class,args);
    }

    /**
     *声明idworker
     */
   @Bean
    public IdWorker idWorker(){
       return new IdWorker(1,1);
   }
}
