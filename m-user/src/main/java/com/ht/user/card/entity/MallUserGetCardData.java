package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class MallUserGetCardData implements Serializable {

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
     * 原批次号
     */
    private String batchCode;

}
