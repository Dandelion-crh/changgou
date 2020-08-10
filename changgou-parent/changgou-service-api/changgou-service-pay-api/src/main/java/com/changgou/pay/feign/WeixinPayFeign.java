package com.changgou.pay.feign;

import com.changgou.util.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * 类名: PayFeign
 * 作者: crh
 * 日期: 2020/6/13 0013下午 5:05
 */
@FeignClient(name = "pay")
public interface WeixinPayFeign {

   @RequestMapping("/weixin/pay/pay/fail")
   Result<Map<String,String>> closePay(@RequestParam("id") String orderId);
}
