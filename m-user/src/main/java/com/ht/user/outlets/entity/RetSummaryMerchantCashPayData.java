package com.ht.user.outlets.entity;

import lombok.Data;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.io.Serializable;

@Data
public class RetSummaryMerchantCashPayData implements Serializable {

    /**
     * 店铺名称
     */
    private String merchName;

    /**
     * 收银台号
     */
    private String cashId;

    /**
     * 总支付金额
     */
    private Long totalPayAmount;

    /**
     * 总支付金额
     */
    private Long totalPayFeeAmount;

    /**
     * 总 退款/撤销 金额
     */
    private Long totalRefundAmount;

    /**
     * 总支付金额
     */
    private Long totalRefundFeeAmount;

}
