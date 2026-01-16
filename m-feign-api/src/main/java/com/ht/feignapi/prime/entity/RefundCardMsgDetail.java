package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class RefundCardMsgDetail implements Serializable {

    private String cardNo;

    private boolean canRefundFlag;

    private String msg;

    private String operator;

    private String refundDate;

    private String refundState;
}
