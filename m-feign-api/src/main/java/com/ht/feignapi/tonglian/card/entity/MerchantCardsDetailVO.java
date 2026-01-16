package com.ht.feignapi.tonglian.card.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MerchantCardsDetailVO implements Serializable {
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
    private String state;

    /**
     *  卡类型
     *  money 金额券
     *  credit 代金券
     *  coupon  满减券
     *  number 计次券
     */
    private String cardType;


    /**
     * 领取条件类型
     */
    private String merchantCardType;

    /**
     * 面值 为折扣券时,标识着打几折
     */
    private Integer faceValue;

    /**
     * 卡次数  次数卡用
     */
    private Integer batchTimes;

    /**
     * 次数卡 单位
     */
    private String unit;

    /**
     * 价格
     */
    private Integer price;

    /**
     * 库存
     */
    private Integer inventory;

    /**
     * 有效时间分类
     * beginToEnd 开始结束类型  使用
     * validDuration领券后生效类型
     */
    private String validityType;

    /**
     * 领取后几天生效: 单位：小时
     */
    private String validGapAfterApplied;

    /**
     * 有效时长：单位：小时
     */
    private Integer periodOfValidity;


    //开始时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date validFrom;

    //结束时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
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

//    //可用日期
//    private List useDate;
//
//    //可用周
//    private List useWeek;
//
//    //可用时段
//    private List timeScope;
//
//    //可用时段
//    private Integer useFullMoney;

    //批次号
    private String batchCode;

//    private Activity activity = new Activity();

    //前端展示时段
    private String showTimeScope;

    //卡券详情页展示使用时间
    private String validTimeStr;

    //卡券使用标签
    private List useFlagDesc;

    //支付到商户门店号
    private String payToMerchantCode;
}
