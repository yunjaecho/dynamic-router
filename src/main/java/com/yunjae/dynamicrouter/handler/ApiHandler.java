package com.yunjae.dynamicrouter.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunjae.dynamicrouter.domain.meta.ApiRequestMeta;
import com.yunjae.dynamicrouter.domain.meta.ApiRequestParameter;
import com.yunjae.dynamicrouter.service.manger.api.ApiCallService;
import com.yunjae.dynamicrouter.service.manger.meta.ApiMetaService;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static reactor.core.publisher.MonoExtensionsKt.toMono;

@Component
public class ApiHandler {

    private final ApiMetaService service;
    private final ApiCallService apiCallService;

    public ApiHandler(ApiMetaService service, ApiCallService apiCallService) {
        this.service = service;
        this.apiCallService = apiCallService;
    }

    public Mono<ServerResponse> getCommApiData(ServerRequest request)  {

        String uri = request.uri().toString();
        HttpMethod method = request.method();
        ServerRequest.Headers headers = request.headers();
        MultiValueMap<String, String> queryParams = request.queryParams();
        Map<String, String> pathParams = request.pathVariables();

        Map<String, String> resultMap = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> bodyMap = new HashMap<>();

        ApiRequestMeta apiRequestMeta = service.getApiRequestMeta(method, uri);
        List<ApiRequestParameter> apiRequestParameters = apiRequestMeta.getApiRequestParameters();

        resultMap.put("rsp_code", "50002");

        for (ApiRequestParameter apiReqParam : apiRequestParameters) {
            // Request Parameter 필수 여부 체크
            if (apiReqParam.isMandatory()) {
                // Header 체크
                if (apiReqParam.getReqType().equals("HEADER")) {
                    List<String> header = headers.header(apiReqParam.getName());
                    if (!(header != null && header.size() == 1)) {
                        resultMap.put("rsp_msg", "Header 정보(" + apiReqParam.getName() + ") 존재하지 않습니다.");
                        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                    } else {
                        String headerValue = header.get(0);
                        if (headerValue == null || headerValue.isEmpty()) {
                            resultMap.put("rsp_msg", apiReqParam.getName() + ") 값이 없습니다.");
                            return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                        }
                    }
                    // Header 값 설정
                    apiReqParam.setValue(header.get(0));
                }
                // Path Variable 체크
                else if(apiReqParam.getReqType().equals("PATH")) {
                    if (pathParams.get(apiReqParam.getName()) == null || pathParams.get(apiReqParam.getName()).isEmpty()) {
                        resultMap.put("rsp_msg", "PATH 정보(" + apiReqParam.getName() + ") 존재하지 않습니다.");
                        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                    }
                    // Path Variable 값 설정
                    apiReqParam.setValue(pathParams.get(apiReqParam.getName()));
                }
                // Query Param 체크
                else if(apiReqParam.getReqType().equals("QUERY")) {
                    if (queryParams.get(apiReqParam.getName()) == null || queryParams.get(apiReqParam.getName()).isEmpty()) {
                        resultMap.put("rsp_msg", "QUERY 정보(" + apiReqParam.getName() + ") 존재하지 않습니다.");
                        return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                    }

                    // Query Param 값 설정
                    pathParams.get(queryParams.get(apiReqParam.getName()));
                }
            }
        }

        if (method == HttpMethod.POST) {
            return request.bodyToMono(Map.class)
                    .publishOn(Schedulers.boundedElastic())
                    .switchIfEmpty(Mono.error(new IllegalStateException("Post json required")))
                    .flatMap(data -> {
                        for (ApiRequestParameter apiReqParam : apiRequestParameters) {
                            if (apiReqParam.getReqType().equals("BODY")) {
                                if (apiReqParam.isMandatory() && (data.get(apiReqParam.getName()) == null || ((String)(data.get(apiReqParam.getName()))).isEmpty())) {
                                    resultMap.put("rsp_msg", "BODY 정보(" + apiReqParam.getName() + ") 존재하지 않습니다.");
                                    return ServerResponse.badRequest().contentType(MediaType.APPLICATION_JSON).body(Mono.just(resultMap), Map.class);
                                }
                                apiReqParam.setValue((String)data.get(apiReqParam.getName()));
                            }
                        }

                        Mono<Map> resultMono = apiCallService.apiCall(apiRequestMeta);

                        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                .body(resultMono, Map.class);

                    })
                    .onErrorResume(err -> {
                        err.printStackTrace();
                        return ServerResponse.badRequest().build();
                    });
        } else {
            Mono<Map> resultMono = apiCallService.apiCall(apiRequestMeta);

            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(resultMono, Map.class);
        }
    }


}
