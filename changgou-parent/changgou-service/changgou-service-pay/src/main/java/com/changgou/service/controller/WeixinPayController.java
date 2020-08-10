package com.changgou.service.controller;

import com.alibaba.fastjson.JSONObject;
import com.changgou.service.WeixinPayService;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 类名: WeixinPayController
 * 作者: crh
 * 日期: 2020/6/16 0016上午 8:22
 */
@RestController
@RequestMapping(value = "/weixin/pay")
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;

    /**
     * 创建二维码
     * @param dataMap
     * @return
     */
    @RequestMapping(value = "/create/native")
    public Result<Map> createNative(@RequestParam Map<String,String> dataMap){
        Map resultMap = weixinPayService.createNative(dataMap);
        return new Result<Map>(true, StatusCode.OK,"创建二维码预付订单成功!",resultMap);
    }

    /**
     *回调方法
     */
    @RequestMapping(value = "/notify/url")
    public String notify(HttpServletRequest request){

          try {
              //获取输入流
              InputStream is = request.getInputStream();
              //定义一个输出流
              ByteArrayOutputStream os = new ByteArrayOutputStream();
              byte[] buffer = new byte[1024];
              int len;//读取数据的长度
              //读取输入流
              while((len = is.read(buffer)) != -1){
                  os.write(buffer,0,len);
              }
              //关闭流
              is.close();
              os.close();

              //获取输出流的内容:xml格式的文本数据
              String result = new String(os.toByteArray(), "UTF-8");
              //将xml格式的数据转换成map类型的数据
              Map<String, String> map = WXPayUtil.xmlToMap(result);
              System.out.println(map);

              Map<String,String> attach = JSONObject.parseObject(map.get("attach"), Map.class);
              //将消息发送给RabbitMQ
              rabbitTemplate.convertAndSend(attach.get("exchange"), attach.get("queue"), JSONObject.toJSONString(map));

              //返回给微信端的内容
              Map<String,String> res = new HashMap<>();
              res.put("return_code","SUCCESS");
              res.put("return_msg","OK");

              return WXPayUtil.mapToXml(res);
          }catch (Exception e){
              e.printStackTrace();
              return null;
          }
    }

    @RequestMapping("/pay/fail")
    public Result<Map<String,String>> closePay(@RequestParam("id") String orderId){
        Map<String, String> map = null;
        try {
            map = weixinPayService.closePay(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(true,StatusCode.OK,"关闭支付成功!",map);
    }
}
