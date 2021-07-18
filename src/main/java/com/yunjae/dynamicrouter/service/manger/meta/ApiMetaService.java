package com.yunjae.dynamicrouter.service.manger.meta;

import com.yunjae.dynamicrouter.domain.meta.ApiRequestMeta;
import com.yunjae.dynamicrouter.domain.meta.ApiRequestParameter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ApiMetaService {

    private List<ApiRequestMeta> apiRequestMetas = new ArrayList<>();

    @PostConstruct
    public void init() {
        ApiRequestMeta BA01 = new ApiRequestMeta("은행-001", HttpMethod.GET, "/bank/accounts", Arrays.asList(
                new ApiRequestParameter("HEADER", "Authorization", true, null),
                new ApiRequestParameter("HEADER", "x-api-tran-id", true, null),
                new ApiRequestParameter("QUERY", "org_code", true, null),
                new ApiRequestParameter("QUERY", "search_timestamp", true, null),
                new ApiRequestParameter("QUERY", "next_page", false, null),
                new ApiRequestParameter("QUERY", "limit", true, null)
        ));
        apiRequestMetas.add(BA01);

        ApiRequestMeta BA02 = new ApiRequestMeta("은행-002", HttpMethod.POST, "/bank/accounts/deposit/basic", Arrays.asList(
                new ApiRequestParameter("HEADER", "Authorization", true, null),
                new ApiRequestParameter("HEADER", "x-api-tran-id", true, null),
                new ApiRequestParameter("BODY", "org_code", true, null),
                new ApiRequestParameter("BODY", "account_num", true, null),
                new ApiRequestParameter("BODY", "seqno", false, null),
                new ApiRequestParameter("BODY", "search_timestamp", true, null)
        ));
        apiRequestMetas.add(BA02);
    }

    public List<ApiRequestMeta> getApiRequestMetas() {
        return apiRequestMetas;
    }

    public ApiRequestMeta getApiRequestMeta(HttpMethod httpMethod, String uri) {
        for (ApiRequestMeta apiMeta : apiRequestMetas) {
            if (apiMeta.getHttpMethod() == httpMethod && uri.contains(apiMeta.getUri())) {
                return apiMeta;
            }
        }
        return null;
    }
}
