package com.ht.user.outlets.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2021/12/7 11:37
 */
@Data
public class QueryCountCashIdAmountSumVO implements Serializable {

    /**
     * 开始日期
     */
    private String startTime;

    /**
     * 结束日期
     */
    private String endTime;

    /**
     * 收银台号
     */
    private String cashId;

}
