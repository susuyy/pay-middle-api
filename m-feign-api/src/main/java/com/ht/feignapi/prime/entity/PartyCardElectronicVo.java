package com.ht.feignapi.prime.entity;


import lombok.Data;

import java.util.Date;

/**
 * <p>
 *  平台 电子卡vo
 * </p>
 *
 * @author hy.wang
 * @since 2021-07-14
 */
@Data
public class PartyCardElectronicVo {


    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    private String userPhone;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 合作机构号
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


    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 具体卡号
     */
    private String cardNo;

    /**
     * 卡分类编码
     */
    private String categoryCode;

    /**
     * 卡分类名称
     */
    private String categoryName;

    /**
     * 卡面值（计次卡有用）
     */
    private String faceValue;

    /**
     * 原始卡金额
     */
    private int amount;

    /**
     * 卡分配状态
     */
    private String state;

    /**
     * 卡分配类型
     */
    private String type;

    /**
     * 卡类型
     */
    private String cardType;

    /**
     * 实体卡卡号
     */
    private String icCardId;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 卡券来源标识(商城购买来源 使用order_detatil的id)
     */
    private String refSourceKey;

    /**
     * 返利明细
     */
    private String couponDetail;

    /**
     * 是否上架
     */
    private String sellState;

    /**
     * 售卖金额
     */
    private String sellAmount;

    private Date createAt;

    private Date updateAt;

    /**
     * 卡图片
     */
    private String backGround;

    /**
     * 字号颜色
     */
    private String color;

    /**
     * 已消费金额
     */
    private String consumeAmount;

    /**
     * 领取状态
     */
    private String getState;

    /**
     * 绑卡密码
     */
    private String password;

    /**
     * 卡有效期开始时间
     */
    private Date validFrom;

    /**
     * 卡有效期
     */
    private Date validityDate;

}
