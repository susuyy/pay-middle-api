package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MallUnionOrderData implements Serializable {

    private Long userId;

    /**
     * 用户openid (前端提交)
     */
    private String openId;

    /**
     * 产品编码 (前端提交)
     */
    private String productionCode;

    /**
     * 产品分类编码末级 (前端提交)
     */
    private String categoryCode;


    private String cardBatchCode;

    /**
     * 数量(前端提交)
     */
    private Integer quantity;

    /**
     * 具体的 门店商户编码(前端提交)
     */
    private String storeMerchantCode;

    /**
     * 主体门店编码 (前端提交)
     */
    private String objectMerchantCode;

    /**
     * 使用的优惠券标识(前端提交)
     */
    private List<String> couponFlag;

    /**
     * 派送数据记录(前端提交)
     */
    private OrderWayBills orderWayBills;

    /**
     * 卡券类产品
     */
    private CardMapMerchantCards cardMapMerchantCards;

    /**
     * 产品 顶级分类
     */
    private String categoryLevel01Code;

    /**
     * 优惠金额 折扣金额
     */
    private Integer discount;

    /**
     * 用户使用的 优惠券
     */
    private CardMapUserCards cardMapUserCards;
}
