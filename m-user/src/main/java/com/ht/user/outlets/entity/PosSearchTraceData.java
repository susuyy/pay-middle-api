package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PosSearchTraceData implements Serializable {

    private String posSerialNum;

    private String cashId;

    private String orderCode;

    private String payCode;

    private long pageNo;

    private long pageSize;

    private String startTime;

    private String endTime;

    private String version;
}
