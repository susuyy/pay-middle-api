package com.ht.feignapi.mall.entity;

import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
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
     * 免税卡 商品
     */
    private CardElectronicSell cardElectronicSell;

    /**
     * 顶层分类编码
     */
    private String levelThreeCode;
}
