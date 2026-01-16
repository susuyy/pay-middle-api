package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;


@Data
public class QrPaymentData implements Serializable {

    /**
     * 用户标识
     */
    private String paymentQrCode;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 金额 单位分
     */
    private Integer amount;

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



}
