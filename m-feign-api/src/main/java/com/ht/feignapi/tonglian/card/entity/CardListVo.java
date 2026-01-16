package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 11:05
 */
@Data
public class CardListVo implements Serializable {
    private String merchantCode;
    private String merchantName;
    private String cardCode;
    private String cardName;
    private String type;
    private String typeName;
    private String validTimeStr;
    private String faceValue;
    private String durationDes;
    private String validityType;

    private String categoryName;
    private String categoryCode;
    private String cardPicUrl;
    //开始时间
    private String validFrom;
    private Integer validGapAfterApplied;
    private Integer periodOfValidity;
    //结束时间
    private String validTo;

    private String state;
}
