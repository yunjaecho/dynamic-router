package com.yunjae.dynamicrouter.service.manger.api;

import com.yunjae.dynamicrouter.domain.meta.ApiRequestMeta;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiCallService {

    public Mono<Map> apiCall(ApiRequestMeta request) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("rsp_code", "000000");
        resultMap.put("rsp_msg", "성공");

        return Mono.just(resultMap);
    }
}
