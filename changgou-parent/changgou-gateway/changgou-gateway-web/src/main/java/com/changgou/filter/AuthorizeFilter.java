package com.changgou.filter;


import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 类名: AuthorizeFilter
 * 作者: crh
 * 日期: 2020/6/10 0010下午 10:38
 */
@Configuration
public class AuthorizeFilter implements GlobalFilter, Ordered {

    //令牌头名字
    private static final String AUTHORIZE_TOKEN ="Authorization";
    /**
     *全局过滤器
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //获取request和response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取uri
        String path = request.getURI().getPath();

        //判断如果是/api/user/login,就放行
        if (path.startsWith("/api/user/login")){
            return chain.filter(exchange);
        }

        //获取头文件中的令牌
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);

        //如果头文件中的token为空，则从请求参数获取
        if (StringUtils.isEmpty(token)){
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }

        if (StringUtils.isEmpty(token)){
            //从cookit中获取token
            HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (cookie == null){
                //404无权访问
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                //调用方法结束访问
                return response.setComplete();
            }
            token = cookie.getValue();
            //将cookie存入请求头中
            token = "bearer " + token;
            request.mutate().header(AUTHORIZE_TOKEN,token);
        }

        //如果此时参数还未空，则不允许访问
        if (StringUtils.isEmpty(token)){
            //404无权访问
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //调用方法结束访问
            return response.setComplete();
        }

   /*     //解析令牌
        Claims claims = null;
        try {
            //claims = JwtUtil.parseJWT(token);
            return chain.filter(exchange);
        } catch (Exception e) {
            //404无权访问
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //调用方法结束访问
            return response.setComplete();
        }*/
        return chain.filter(exchange);
    }

    /**
     * 排序
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
