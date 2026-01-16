package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.util.Date;

/**
 * <p>
 *  平台卡批次vo
 * </p>
 *
 * @author hy.wang
 * @since 2021-07-14
 */
@Data
public class PartyCardBatchVo {


    private Long id;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡面值
     */
    private Long cardFaceValue;

    /**
     * 批次,总大小
     */
    private Long batchSize;

    /**
     * 主体merchantCode
     */
    private String merchantCode;

    /**
     * 外部合作机构merchantCode;
     */
    private String refMerchantCode;

    /**
     * 储值渠道方
     */
    private String channelId;


    /**
     * 渠道合作商编码
     */
    private String channelPartnerCode;


    private String sellAmount;

    /**
     * 卡分配类型
     */
    private String type;

    /**
     * 卡类型
     */
    private String cardType;

    /**
     * 卡图片
     */
    private String backGround;

    private Date createAt;

    private Date updateAt;

}
