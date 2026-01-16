package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商家卡券
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_map_merchant_cards")
public class CardMapMerchantCards implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 卡编码
     */
    private String cardCode;

    /**
     * 卡类型
     */
    private String cardType;

    @TableField(exist = false)
    private String cardTypeStr;

    /**
     * 卡名称
     */
    private String cardName;

    /**
     * 商户卡券类型
     */
    private String type;

    /**
     * 商户卡券状态
     */
    private String state;

    /**
     * 卡面值
     */
    private String cardFaceValue;

    /**
     * 卡价格：单位：分
     */
    private Integer price;

    /**
     * 市价
     */
    private Integer referencePrice;

    /**
     * 上下架状态：默认：N 下架，Y 上架
     */
    private String onSaleState;

    private Date createAt;

    private Date updateAt;

    @JsonFormat(pattern = "YYYY-MM-dd",timezone = "GMT+8")
    private Date onSaleDate;

    @JsonFormat(pattern = "YYYY-MM-dd",timezone = "GMT+8")
    private Date haltSaleDate;

    @TableField(exist = false)
    private Integer inventory;

    @TableField(exist = false)
    private String categoryCode;

    @TableField(exist = false)
    private String categoryName;

    /**
     * 批次号
     */
    private String batchCode;

    @TableField(exist = false)
    private String cardCardsState;
}
