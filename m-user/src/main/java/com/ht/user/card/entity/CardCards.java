package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 卡定义
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_cards")
public class CardCards implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
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
     * 卡分类编码
     */
    private String categoryCode;

    /**
     * 卡分类名称
     */
    private String categoryName;

    /**
     * 卡状态
     */
    private String state;

    /**
     * 卡类型
     */
    private String type;

    /**
     * 卡券类型中文描述
     */
    @TableField(exist = false)
    private String cardTypeStr;

    private Date createAt;

    private Date updateAt;

    private Integer faceValue;

    /**
     * 有效时长：单位：小时
     */
    private Integer periodOfValidity;

    private Integer price;

    @TableField(exist = false)
    private Integer inventory;

    private String validityType;

    //开始时间
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date validFrom;

    //结束时间
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date validTo;

    //优惠券图片
    private String cardPicUrl;

    /**
     * 领取后几天生效: 单位：小时
     */
    private Integer validGapAfterApplied;

    /**
     * 是否可转移: 默认：N 不可转移， Y 可转移
     */
    private Boolean flagTransfer;

    /**
     * 次数卡，卡的次数
     */
    private Integer batchTimes;

    /**
     * 使用须知
     */
    private String notice;

    /**
     * 次数卡，单位
     */
    private String unit;

    @TableField(exist = false)
    private List<String> profiles;

    //详细说明
    @TableField(exist = false)
    private String desc;

//    //商家名
//    @TableField(exist = false)
//    private String merchantsName;
//
//    //商家图片
//    @TableField(exist = false)
//    private String merchantsPic;
//
//    //商家地址
//    @TableField(exist = false)
//    private String merchantsAddress;
//
//    //商家电话
//    @TableField(exist = false)
//    private String merchantsPhone;

    //商家卡券类型  售卖  免费领取  充值领取
    @TableField(exist = false)
    private String merchantCardType;

    @TableField(exist = false)
    private List<CardLimits> limits;

    @TableField(exist = false)
    private String batchCode;

    //上架时间
    @TableField(exist = false)
    private Date onSaleDate;

    //下架时间
    @TableField(exist = false)
    private Date haltSaleDate;


}
