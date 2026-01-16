package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;


/**
 *
 * 查询卡条件数据
 *
 * @author hy.wang
 * @since 2021-08-18
 */
@Data
public class CardQueryData implements Serializable {

    /**
     * 用户手机号
     */
    private String userPhone;

    /**
     * 页号
     */
    private Integer pageNo;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 商户编号
     */
    private String merchantCode;

    /**
     * 退款码
     */
    private String refundCode;



}
