package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    LoadBalancerClient loadBalancerClient;

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {
        String url = "http://localhost:9001/oauth/token";

        //定义head:链表
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.set("Authorization", "Basic " + getHeader(clientId, clientSecret));

        //定义body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.set("grant_type","password");
        body.set("username",username);
        body.set("password",password);
        //发送请求,获取token信息
        /**
         * 1.请求的url地址
         * 2.请求方法的类型
         * 3.请求的参数
         * 4.返回数据的类型
         */
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), Map.class);
        Map map = responseEntity.getBody();

        AuthToken authToken = new AuthToken();
        //获取accesstoken
        String access_token = map.get("access_token").toString();
        String refresh_token = map.get("refresh_token").toString();
        String jti = map.get("jti").toString();

        authToken.setAccessToken(access_token);
        authToken.setRefreshToken(refresh_token);
        authToken.setJti(jti);

        return authToken;
    }

    /**
     * 请求头中的参数处理
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String getHeader(String clientId, String clientSecret){
        String result = clientId + ":" + clientSecret;
        //base64加密
        byte[] encode = Base64.getEncoder().encode(result.getBytes());
        return new String(encode);
    }
}
