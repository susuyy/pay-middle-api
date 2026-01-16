package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PrimeConsumerData implements Serializable {

    /**
     * 用户的会员码标识
     */
    private String userFlagCode;

    /**
     * 用户的消费金额 (单位分)
     */
    private Integer amount;

    /**
     * 扩展字段1  (商户号|终端|收银机号|门店号|操作员号)
     */
    private String ext1;

    /**
     * 扩展字段1
     */
    private String ext2;

    /**
     * 扩展字段1
     */
    private String ext3;

}
