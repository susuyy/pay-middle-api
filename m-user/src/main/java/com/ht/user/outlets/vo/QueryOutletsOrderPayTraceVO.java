package com.ht.user.outlets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/22 14:31
 */
@Data
public class QueryOutletsOrderPayTraceVO implements Serializable {

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 开始日期
     */
    private String startCreateAt;

    /**
     * 结束日期
     */
    private String endCreateAt;

}
