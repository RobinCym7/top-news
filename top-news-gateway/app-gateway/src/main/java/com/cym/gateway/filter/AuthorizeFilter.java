package com.cym.gateway.filter;

import com.cym.gateway.utils.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取request以及response对象
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();

        // 2. 判断是否鉴权，即请求是否是向login
        if (request.getURI().getPath().contains("/login")){
            // 2.1 为登录请求，直接放行
            return chain.filter(exchange);
        }

        // 3. 请求并非向login，则解析token
        String token = request.getHeaders().getFirst("token");
        System.err.println(token);
        // 3.1 token不存在
        if (StringUtils.isBlank(token)){
            // 3.1.1 返回401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //3.2 token存在，则解析token
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2){
                // 3.2.1 token过期, 返回401
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            // 将用户ID放入header中
            Object userId = claimsBody.get("id");
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeaders -> {
                httpHeaders.add("userId", userId + "");
            }).build();
            exchange.mutate().request(serverHttpRequest);

        }catch (Exception e){
            // 可能解析失败，如果失败则返回401
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 3.2.2 token未过期
        return chain.filter(exchange);
    }

    /**
     * 设置过滤器的优先级，值越小，优先级越低
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
