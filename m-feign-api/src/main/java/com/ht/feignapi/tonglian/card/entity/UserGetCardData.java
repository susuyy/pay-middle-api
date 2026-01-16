package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserGetCardData implements Serializable {

    /**
     * 用户openid
     */
    private String openid;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 卡券编码 cardCode
     */
    private String cardCode;

    /**
     * 商户编码
     */
    private String merchantCode;


    /**
     * 批次号
     */
    private String batchCode;

    private String cardGetType;
}
