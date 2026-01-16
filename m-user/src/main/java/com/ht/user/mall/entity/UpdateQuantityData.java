package com.ht.user.mall.entity;

import com.ht.user.card.entity.CardMapMerchantCards;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateQuantityData implements Serializable {

    private String id;

    private Integer quantity;

    private Integer amount;

    /**
     * 卡券产品
     */
    private CardMapMerchantCards cardMapMerchantCards;

    /**
     * 实体产品
     */
    private OrderProductions orderProductions;

    /**
     * 顶层分类编码
     */
    private String levelThreeCode;
}
