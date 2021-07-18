package com.yunjae.dynamicrouter.domain.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiRequestParameter {

    private String reqType; // HEADER, PATH, QUERY, BODY

    // 파리미터 이름
    private String name;

    // 필수 여부
    private boolean isMandatory;

    private String value;
}
