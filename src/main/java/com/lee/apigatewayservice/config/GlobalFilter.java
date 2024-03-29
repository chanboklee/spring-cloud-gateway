package com.lee.apigatewayservice.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter(){
        super(Config.class);
    }

    @Data
    public static class Config {
        // Put the configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom PRE filter
        return (exchange, chain) -> {
            // 기존의 HttpServletRequset가 아닌 비동기 방식인 ServerHttpRequest.. 사용
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage : {}", config.getBaseMessage());
            if(config.isPreLogger()){
                log.info("Global Filter Start : request id -> {}", request.getId());
            }

            // Custom POST filter
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.postLogger){
                    log.info("Global Filter End : response code -> {}", response.getStatusCode());
                }
            }));
        };
    }
}
