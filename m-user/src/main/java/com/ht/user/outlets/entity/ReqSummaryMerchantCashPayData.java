package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReqSummaryMerchantCashPayData implements Serializable {

    private String merchName;

    private String cashId;

    private long pageNo;

    private long pageSize;

    private String startTime;

    private String endTime;
}
