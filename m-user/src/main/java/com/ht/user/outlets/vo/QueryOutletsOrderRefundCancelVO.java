package com.ht.user.outlets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/22 17:20
 */
@Data
public class QueryOutletsOrderRefundCancelVO implements Serializable {

    /**
     * 页号
     */
    private Integer pageNo;

    /**
     * 页大小
     */
    private  Integer pageSize;

    /**
     * 退款单号,收银台上送
     */
    private String refundCancelCode;

    /**
     * 原交易订单号
     */
    private String oriOrderCode;

    /**
     * 退款成功标识 success成功  fail失败
     */
    private String state;

    /**
     * 开始日期
     */
    private String startCreateAt;

    /**
     * 结束日期
     */
    private String endCreateAt;

}
