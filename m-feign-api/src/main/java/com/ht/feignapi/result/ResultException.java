package com.ht.feignapi.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Administrator
 */
@Getter
@Setter
public class ResultException extends Exception {
    Integer code;
    String msg;
    private Object exception;

    public ResultException(ResultTypeEnum errorEnum, Object exception) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
        this.exception = exception;
    }

    public ResultException(ResultTypeEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
    }

    public ResultException(String message) {
        this.code = 1500;
        this.msg = message;
    }

}
