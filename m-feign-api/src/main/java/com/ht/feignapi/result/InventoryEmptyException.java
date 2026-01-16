package com.ht.feignapi.result;

/**
 * @author: zheng weiguang
 * @Date: 2020/12/11 14:23
 */
public class InventoryEmptyException extends RuntimeException {
    Integer code;
    String msg;
    private Object exception;

    public InventoryEmptyException() {
    }

    public InventoryEmptyException(ResultTypeEnum errorEnum, Object exception) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
        this.exception = exception;
    }

    public InventoryEmptyException(ResultTypeEnum errorEnum) {
        this.code = errorEnum.getCode();
        this.msg = errorEnum.getMessage();
    }

    public InventoryEmptyException(String errorMsg) {
        this.code = ResultTypeEnum.INVENTORY_NULL.getCode();
        this.msg = errorMsg;
    }
}
