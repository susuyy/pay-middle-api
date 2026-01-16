package com.ht.feignapi.result;

import lombok.Getter;
import lombok.Setter;

/**
 * @author: zheng weiguang
 * @Date: 2020/9/11 18:11
 */
@Getter
@Setter
public class ProductionCodeNotExistException extends RuntimeException{
    Integer code;
    String msg;
    private Object exception;

    public ProductionCodeNotExistException() {
    }

    public ProductionCodeNotExistException(ResultTypeEnum errorEnum, Object exception) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
        this.exception = exception;
    }

    public ProductionCodeNotExistException(ResultTypeEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
    }
}
