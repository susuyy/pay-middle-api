package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MsUserAccount implements Serializable {

    /**
     * 账户余额金额
     */
    private Integer amount;

    /**
     * 账户余额卡号
     */
    private String cardId;

    /**
     * 用户表id
     */
    private Long userId;

    /**
     * 查询的账户余额 类型
     */
    private String accountType;

    /**
     * 账户余额类型对应的 通联prdtNo
     */
    private String prdtNo;


}
