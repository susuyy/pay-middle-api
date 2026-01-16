package com.ht.user.mall.entity;

import com.ht.user.card.entity.CardMapMerchantCards;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AddShoppingCartDate implements Serializable {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户openid
     */
    private String openId;

    /**
     * 主体编号
     */
    private String objectMerchantCode;

    /**
     * 门店merchantCode
     */
    private String storeMerchantCode;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 具体商品信息
     */
    private OrderProductions orderProductions;

    /**
     * 卡券类商城商品
     */
    private CardMapMerchantCards cardMapMerchantCards;

    /**
     * 商品 顶级分类
     */
    private String categoryLevel01Code;


    /**
     * 备注,使用须知
     */
    private String notice;
}
