package com.ht.merchant.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDefinedException extends RuntimeException {
    Integer code;
    String msg;
    private Object exception;

    public UserDefinedException(ResultTypeEnum errorEnum, Object exception) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
        this.exception = exception;
    }

    public UserDefinedException(ResultTypeEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
    }

}
