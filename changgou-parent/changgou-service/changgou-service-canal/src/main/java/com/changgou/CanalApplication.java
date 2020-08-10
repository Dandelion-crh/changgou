package com.changgou;

import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 类名: CanalApplication
 * 作者: crh
 * 日期: 2020/6/1 0001下午 4:13
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})//不去加载数据库相关的模块
@EnableEurekaClient
@EnableCanalClient//标识为一个canal的监控服务
@EnableFeignClients(basePackages = {"com.changgou.content.feign"})
public class CanalApplication {
    public static void main(String[] args) {
        SpringApplication.run(CanalApplication.class,args);
    }
}
