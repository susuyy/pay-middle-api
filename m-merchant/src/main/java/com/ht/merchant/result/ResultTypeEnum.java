package com.ht.merchant.result;

import lombok.Getter;

@Getter
public enum ResultTypeEnum {

    SERVICE_SUCCESS(1200, "成功"),
    SERVICE_ERROR(1500, "服务异常"),

    RESPONSE_PACK_ERROR(2100, "结果封装异常"),

    BIND_EXCEPTION(10101, "参数异常"),
    PARA_MISSING_EXCEPTION(10102, "参数不完整"),
    CODE_EXIST(11128,"code已存在"),
    SORT_EXIST(11129,"排序已存在");

    private Integer code;
    private String message;

    ResultTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
