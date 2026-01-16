package com.ht.merchant.result;

import lombok.Data;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/20 16:57
 */
@Data
public class SortExistException extends RuntimeException {
    private Integer code;

    public SortExistException(String msg){
        super(msg);
        this.code = ResultTypeEnum.SORT_EXIST.getCode();
    }
}
