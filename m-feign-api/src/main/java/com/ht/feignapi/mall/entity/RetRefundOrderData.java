package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetRefundOrderData implements Serializable {

    private String randomstr;
    private String trxcode;
    private String fintime;
    private String cusid;
    private String appid;
    private String fee;
    private String sign;
    private String trxid;
    private String trxstatus;
    private String reqsn;
    private String retcode;
    private String errmsg;

    private boolean serverFlag;
}
