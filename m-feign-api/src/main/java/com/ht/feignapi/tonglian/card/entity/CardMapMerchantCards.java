package com.ht.feignapi.tonglian.card.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ht.feignapi.appshow.entity.MallCoupon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

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
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private Date onSaleDate;

    @JsonFormat(pattern = "YYYY-MM-dd",timezone = "GMT+8")
    @DateTimeFormat(pattern = "YYYY-MM-dd")
    private Date haltSaleDate;

    private Integer inventory;

    private String categoryCode;

    private String categoryName;

    /**
     * 批次号
     */
    private String batchCode;

    private String cardCardsState;

    private MallCoupon mallCoupon;
}
