package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import lombok.Data;

import java.io.Serializable;

@Data
public class RetUpdateQuantityData implements Serializable {

    private String id;

    private Integer quantity;

    private Integer amount;

    /**
     * 顶层分类编码
     */
    private String levelThreeCode;

    /**
     * 单价
     */
    private Integer unitPrice;

    /**
     * 库存标识
     */
    private boolean inventoryFlag;

    /**
     * 剩余库存量
     */
    private Integer inventoryCount;
}
