package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MerchantCardsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 卡状态
     */
    private String cardCardsType;


    /**
     * 卡状态
     */
    private String state;

    /**
     * 领取条件类型
     */
    private String merchantCardType;

    /**
     * 面值
     */
    private Integer faceValue;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 库存
     */
    private Integer inventory;

    //开始时间
//    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date validFrom;

    //结束时间
//    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date validTo;

    //优惠券图片
    private String cardPicUrl;

    //详细说明
    private String desc;

    //商家名称
    private String merchantsName;

    //商家图片
    private String merchantsPic;

    //商家地址
    private String merchantsAddress;

    //商家电话
    private String merchantsPhone;

    //前端展示时间段
    private String showTimeScope;

    /**
     * 卡状态
     */
    private String cardCardsState;

    //卡券使用标签
    private List useFlagDesc;

    /**
     * 创建时间
     */
    private Date createAt;

}
