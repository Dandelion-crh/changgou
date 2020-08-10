package com.changgou.oauth.controller;

import com.changgou.oauth.service.AuthService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import com.changgou.util.CookieTools;
import com.changgou.util.Result;
import com.changgou.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/user")
public class AuthController {

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    private Integer cookieMaxAge;

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param response
     * @return
     */
    @PostMapping(value = "/login")
    public Result<AuthToken> login(String username,
                                   String password,
                                   HttpServletRequest request,
                                   HttpServletResponse response){
        //获取令牌
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);

        if(authToken != null){
            //将token信息保存到cookie中去
           // CookieUtil.addCookie(response, cookieDomain,"/", "Authorization", authToken.getAccessToken(), cookieMaxAge, false);
            //令牌存储到cookie
            CookieTools.setCookie(request,response,"Authorization",authToken.getAccessToken());
            //用户名存储到cookie
            CookieTools.setCookie(request,response,"cuname",username);
            return new Result<>(true, StatusCode.OK, "登录成功", authToken);
        }
        return new Result<>(false, StatusCode.LOGINERROR, "用户名或密码错误");
    }
}
