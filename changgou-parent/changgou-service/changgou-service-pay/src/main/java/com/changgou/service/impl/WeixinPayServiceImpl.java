package com.changgou.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.changgou.service.WeixinPayService;
import com.changgou.util.HttpClient;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名: WeixinPayServiceImpl
 * 作者: crh
 * 日期: 2020/6/16 0016上午 7:53
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${weixin.appid}")
    private String appid;
    @Value("${weixin.partner}")
    private String partner;
    @Value("${weixin.partnerkey}")
    private String partnerkey;
    @Value("${weixin.notifyurl}")
    private String notifyurl;
    /*****
     * 创建二维码
     * @param dataMap
     * @return
     */
    @Override
    public Map createNative(Map<String,String> dataMap) {
            //定义请求接口地址
            String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

            //定义请求的参数
            Map<String,String> param = new HashMap();
            param.put("appid",appid);                            //客户id
            param.put("mch_id",partner);                         //商户id
            param.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符
            param.put("body","畅构商城支付测试");                //商品描述
            param.put("out_trade_no",dataMap.get("outtradeno"));              //商户订单号
            param.put("total_fee",dataMap.get("totalfee"));                    //标价金额
            param.put("spbill_create_ip","192.168.211.1");       //自己服务器ip
            param.put("notify_url",notifyurl);                   //支付成功通知地址
            param.put("trade_type","NATIVE");                    //交易类型
            //附加数据定义
            Map<String,String> attach = new HashMap<>();
            attach.put("exchange",dataMap.get("exchange"));
            attach.put("queue",dataMap.get("queue"));

            //如果用户名不为空
            if (!StringUtils.isEmpty(dataMap.get("username"))){
                attach.put("username",dataMap.get("username"));
            }

            //附加参数设置
            param.put("attach", JSONObject.toJSONString(attach));
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //初始化HttpClient客户端
            HttpClient httpClient = new HttpClient(url);
            //设置请求参数
            httpClient.setXmlParam(xmlParam);
            //设置https
            httpClient.setHttps(true);
            //请求调用
            httpClient.post();
            //获取响应结果
            String content = httpClient.getContent();
            //将响应结果转换成Map格式数据
            Map<String, String> result = WXPayUtil.xmlToMap(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭支付
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, String> closePay(String orderId) throws Exception {
        //参数设置
        Map<String,String> paramMap = new HashMap<String,String>();
        paramMap.put("appid",appid);//应用ID
        paramMap.put("mch_id",partner);//商户编号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());//随机字符
        paramMap.put("out_trade_no",String.valueOf(orderId));//商家的唯一编号
        //将Map数据转成XML字符
        String xmlParam = WXPayUtil.generateSignedXml(paramMap,partnerkey);

        //确定url
        String url = "https://api.mch.weixin.qq.com/pay/closeorder";

        //发送请求
        HttpClient httpClient = new HttpClient(url);
        //https
        httpClient.setHttps(true);
        //提交参数
        httpClient.setXmlParam(xmlParam);
        //提交
        httpClient.post();

        //获取返回数据
        String content = httpClient.getContent();
        //将返回的数据解析成Map
        return WXPayUtil.xmlToMap(content);
    }
}
