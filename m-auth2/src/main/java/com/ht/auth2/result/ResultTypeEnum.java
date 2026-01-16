package com.ht.auth2.result;

import lombok.Getter;

@Getter
public enum ResultTypeEnum {

    SERVICE_SUCCESS(1200, "成功"),
    SERVICE_ERROR(1500, "服务异常"),

    TOKEN_ERROR(40001, "token失效"),

    RESPONSE_PACK_ERROR(2100, "结果封装异常"),

    BIND_EXCEPTION(10101, "参数异常"),
    PARA_MISSING_EXCEPTION(10102, "参数不完整"),

    LOGIN_ERROR(30001, "登录失败"),
    MAP_EXIST(10103,"关联已存在,请勿重复关联"),
    ORG_PASSWORD_ERROR(10104,"原密码错误,无法修改密码");

    private Integer code;
    private String message;

    ResultTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
