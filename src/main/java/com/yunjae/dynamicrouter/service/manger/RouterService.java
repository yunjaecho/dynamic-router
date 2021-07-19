package com.yunjae.dynamicrouter.service.manger;

import com.yunjae.dynamicrouter.domain.meta.ApiRequestMeta;
import com.yunjae.dynamicrouter.handler.ApiHandler;
import com.yunjae.dynamicrouter.service.manger.meta.ApiMetaService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

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

        List<ApiRequestMeta> apiRequestMetas = service.getApiRequestMetas();

        apiRequestMetas.forEach(meta -> {
            if (meta.getHttpMethod() == HttpMethod.GET) {
                routes.add(RouterFunctions.route()
                        .GET(meta.getUri(), accept(APPLICATION_JSON), apiHandler::getCommApiData).build());
            } else if(meta.getHttpMethod() == HttpMethod.POST) {
                routes.add(RouterFunctions.route()
                        .POST(meta.getUri(), accept(APPLICATION_JSON), apiHandler::getCommApiData).build());
            }
        });

        return routes.build();
    }
}
