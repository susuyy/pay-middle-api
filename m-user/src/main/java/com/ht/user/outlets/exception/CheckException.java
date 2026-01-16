package com.ht.user.outlets.exception;

import com.ht.user.result.ResultTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckException extends RuntimeException {
    Integer code;
    String msg;
    private Object exception;

    public CheckException(ResultTypeEnum errorEnum, Object exception) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
        this.exception = exception;
    }

    public CheckException(ResultTypeEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
    }

    public CheckException(ResultTypeEnum errorEnum, String message) {
        this.code = errorEnum.getCode();
        this.msg = message;
    }

    public CheckException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
