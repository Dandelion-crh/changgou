package com.changgou.service;

import java.util.Map;

public interface WeixinPayService {
    /*****
     * 创建二维码
     * @param dataMap
     * @return
     */
    Map createNative(Map<String,String> dataMap);

    /**
     *关闭支付
     * @param orderId
     * @return
     * @throws Exception
     */
    Map<String,String> closePay(String orderId) throws Exception;
}
