package com.ht.user.outlets.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchTraceData implements Serializable {

    private String storeCode;

    private String orderCode;

    private String refundCancelCode;
}
