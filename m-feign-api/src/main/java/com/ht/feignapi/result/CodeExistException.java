package com.ht.feignapi.result;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/27 11:35
 */
@Data
public class CodeExistException extends RuntimeException {

    private Integer code;

    public CodeExistException(String msg){
        super(msg);
        this.code = ResultTypeEnum.CODE_EXIST.getCode();
    }
}
