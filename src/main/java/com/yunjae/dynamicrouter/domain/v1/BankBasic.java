package com.yunjae.dynamicrouter.domain.v1;

/*
 * Created By  : mydata-platform
 * Description :
 * Author                    Date                     Time
 * ------------------       --------------            ------------------
 * YunJae.Cho                2021/07/19                  5:09 오후
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankBasic {
	@JsonProperty(value = "org_code")
	private String orgCode;
}
