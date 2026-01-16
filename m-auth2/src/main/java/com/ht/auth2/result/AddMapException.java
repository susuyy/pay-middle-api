package com.ht.auth2.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMapException extends RuntimeException {
    Integer code;
    String msg;
    private Object exception;

    public AddMapException(ResultTypeEnum errorEnum, Object exception) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
        this.exception = exception;
    }

    public AddMapException(ResultTypeEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
    }

}
