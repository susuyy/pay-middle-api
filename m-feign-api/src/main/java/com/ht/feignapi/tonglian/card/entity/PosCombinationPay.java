package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class PosCombinationPay implements Serializable {

    /**
     * 用户标识
     */
    private String userFlagCode;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 金额 单位分
     */
    private Integer amount;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 富基上送的 payCode
     */
    private String payCode;

    /**
     * 富基上送的 门店号
     */
    private String storeId;

    /**
     * 富基上送的 门店号(主订单保存用)
     */
    private String storeCode;

    /**
     * 富基上送的 收银台台号
     */
    private String cashId;

    /**
     * 富基上送的 商品信息数据
     */
    private String orderDetail;

    /**
     * 购买人手机号
     */
    private String actualPhone;

    /**
     * 购买人身份证
     */
    private String idCardNo;

}
