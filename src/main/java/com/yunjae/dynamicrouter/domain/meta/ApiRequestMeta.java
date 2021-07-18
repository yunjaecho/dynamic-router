package com.yunjae.dynamicrouter.domain.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiRequestMeta {

    private String apiId;

    private HttpMethod httpMethod;

    private String uri;

    private List<ApiRequestParameter> apiRequestParameters;
}
