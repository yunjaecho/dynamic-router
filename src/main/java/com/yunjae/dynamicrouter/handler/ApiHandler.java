package com.yunjae.dynamicrouter.handler;

import com.yunjae.dynamicrouter.domain.meta.ApiRequestMeta;
import com.yunjae.dynamicrouter.domain.meta.ApiRequestParameter;
import com.yunjae.dynamicrouter.service.manger.meta.ApiMetaService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ApiHandler {

    private final ApiMetaService service;

    public ApiHandler(ApiMetaService service) {
        this.service = service;
    }

    public Mono<ServerResponse> getCommApiData(ServerRequest request)  {
        String uri = request.uri().toString();
        HttpMethod method = request.method();
        ServerRequest.Headers headers = request.headers();
        MultiValueMap<String, String> queryParams = request.queryParams();
        Map<String, String> pathParams = request.pathVariables();

        String body = null;

        if (method == HttpMethod.POST) {
            body = request.bodyToMono(String.class).toString();
        }

        ApiRequestMeta apiRequestMeta = service.getApiRequestMeta(method, uri);
        List<ApiRequestParameter> apiRequestParameters = apiRequestMeta.getApiRequestParameters();

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("rsp_code", "50002");

        for (ApiRequestParameter apiReqParam : apiRequestParameters) {
            if (apiReqParam.getReqType().equals("HEADER")) {
                List<String> header = headers.header(apiReqParam.getName());
                if (!(header != null && header.size() == 1 && apiReqParam.isMandatory())) {
                    resultMap.put("rsp_msg", "Header 정보(" + apiReqParam.getName() + ") 존재하지 않습니다.");
                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                } else {
                    String headerValue = header.get(0);
                    if (headerValue == null || headerValue.isEmpty()) {
                        resultMap.put("rsp_msg", apiReqParam.getName() + ") 값이 없습니다.");
                        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                    }
                }
            } else if(apiReqParam.getReqType().equals("PATH")) {
                if (pathParams.get(apiReqParam.getName()) == null || pathParams.get(apiReqParam.getName()).isEmpty()) {
                    resultMap.put("rsp_msg", "PATH 정보(" + apiReqParam.getName() + ") 존재하지 않습니다.");
                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                }
            } else if(apiReqParam.getReqType().equals("QUERY")) {
                if (queryParams.get(apiReqParam.getName()) == null || queryParams.get(apiReqParam.getName()).isEmpty()) {
                    resultMap.put("rsp_msg", "QUERY 정보(" + apiReqParam.getName() + ") 존재하지 않습니다.");
                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                }
            }
        }

        resultMap.put("rsp_code", "000000");
        resultMap.put("rsp_msg", "성공");

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(resultMap), Map.class);
    }
}
