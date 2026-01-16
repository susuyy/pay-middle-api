package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 订单主表
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AddShoppingCartDate implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主体编号
     */
    private String objectMerchantCode;

    /**
     * 门店merchantCode
     */
    private String storeMerchantCode;

    /**
     * 商品信息 实体类商城商品
     */
    private OrderProductions orderProductions;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户openid
     */
    private String openId;

    /**
     * 数量
     */
    private BigDecimal quantity;

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
