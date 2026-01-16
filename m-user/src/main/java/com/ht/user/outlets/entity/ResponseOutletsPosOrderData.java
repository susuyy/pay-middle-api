package com.ht.user.outlets.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ResponseOutletsPosOrderData implements Serializable {

    /**
     * 业务编码
     */
    private String businessId;

    /**
     * 金额 单位分
     */
    private Integer amount;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 限制支付方式
     */
    private String limitPayType;

    /**
     * pos checkTrace 字段
     */
    private String checkTrace;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date limitPayTime;

}
