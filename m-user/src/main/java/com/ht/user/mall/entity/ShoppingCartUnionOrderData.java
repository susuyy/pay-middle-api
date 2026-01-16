package com.ht.user.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ShoppingCartUnionOrderData implements Serializable {

    private String userId;

    private String openId;

    private List<String> shoppingCartOrderCodeList;

    /**
     * 主体商户编码
     */
    private String objectMerchantCode;

    /**
     * 收银类型
     */
    private String merchantChargeType;

    /**
     * 派送数据记录
     */
    private OrderWayBills orderWayBills;
}
