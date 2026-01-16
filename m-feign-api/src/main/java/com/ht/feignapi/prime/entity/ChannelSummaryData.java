package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChannelSummaryData implements Serializable {

    /**
     * 机构渠道id
     */
    private String channelId;

    /**
     * 机构渠道名称
     */
    private String channelName;

    /**
     * 卡类型
     */
    private String cardType;

    /**
     * 预付款总额
     */
    private Long cardTotalAmount;

    /**
     * 已核销金额
     */
    private Long consumeAmount;

    /**
     * 未核销金额
     */
    private Long remainingAmount;

    /**
     * 未核销金额
     */
    private Long refundAmount;
}
