package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserCashPayCardData implements Serializable {

    /**
     * 卡编号
     */
    private String cardCode;

    /**
     * 卡面值
     */
    private Integer faceValue;

    /**
     * 卡次数
     */
    private Integer batchTimes;

    /**
     * 卡类型
     * 满减
     * 金额
     */
    private String cardCardsType;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 有效开始时间
     */
    private Date validFrom;

    /**
     * 有效结束时间
     */
    private Date validTo;

    /**
     * 卡类型
     */
    private String type;

    /**
     * 卡编号
     */
    private String cardNo;

    /**
     * 实体卡卡号
     */
    private String icCardId;

    /**
     * 使用须知
     */
    private String notice;

    /**
     * 卡券使用条件
     */
    private List<CardLimits> cardLimitsList;
}
