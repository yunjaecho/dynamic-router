package com.yunjae.dynamicrouter.service.manger;

import com.yunjae.dynamicrouter.handler.ApiHandler;
import com.yunjae.dynamicrouter.service.manger.meta.ApiMetaService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Service
public class RouterService {

    private final ApiHandler apiHandler;
    private final ApiMetaService service;

    public RouterService(ApiHandler apiHandler, ApiMetaService service) {
        this.apiHandler = apiHandler;
        this.service = service;
    }

    public RouterFunction<ServerResponse> getServerResponseRouterFunction() {
        RouterFunctions.Builder routes = RouterFunctions.route();

        service.getApiRequestMetas().forEach(meta -> {
            if (meta.getHttpMethod() == HttpMethod.GET) {
                routes.add(RouterFunctions.route()
                        .GET(meta.getUri(), accept(MediaType.APPLICATION_JSON), apiHandler::getCommApiData).build());
            } else if(meta.getHttpMethod() == HttpMethod.POST) {
                routes.add(RouterFunctions.route()
                        .POST(meta.getUri(), accept(MediaType.APPLICATION_JSON), apiHandler::getCommApiData).build());
            }
        });

        return routes.build();
    }
}
