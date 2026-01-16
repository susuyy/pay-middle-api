package com.ht.feignapi.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenIdException extends RuntimeException {
    Integer code;
    String msg;
    private Object exception;

    public OpenIdException(ResultTypeEnum errorEnum, Object exception) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
        this.exception = exception;
    }

    public OpenIdException(Integer code,String message) {
        this.code = code;
        this.msg = message;
    }

    public OpenIdException(ResultTypeEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
    }

}
