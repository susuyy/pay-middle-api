package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AdminOrderVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 用户手机
     */
    private String userTel;

    /**
     * 终端号
     */
    private String posSerialNum;

    /**
     * 商户编码
     */
    private String merchantName;

    /**
     * 更新时间
     */
    private Date updateAt;

    /**
     * 订单创建时间
     */
    private Date createAt;

    /**
     * 金额
     */
    private Integer amount;

    /**
     * 订单来源
     */
    private String orderSource;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单支付状态
     */
    private String orderState;

    /**
     * 收银员
     */
    private String sale;


}
