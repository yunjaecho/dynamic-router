package com.yunjae.dynamicrouter.config;

import com.yunjae.dynamicrouter.service.manger.route.RouterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;


@Configuration
public class RouterConfig {

    private final RouterService service;

    public RouterConfig(RouterService service) {
        this.service = service;
    }


    @Bean(name = "routerFunction")
    public RouterFunction<ServerResponse> routerFunction() {
        return service.getServerResponseRouterFunction();
    }
}
