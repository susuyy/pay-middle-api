package com.ht.feignapi.higo.entity;

import com.ht.feignapi.mall.entity.OrderProductions;
import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class HiGoAddShoppingCartData implements Serializable {

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
    private CardElectronicSell cardElectronicSell;

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
    private int quantity;

}
