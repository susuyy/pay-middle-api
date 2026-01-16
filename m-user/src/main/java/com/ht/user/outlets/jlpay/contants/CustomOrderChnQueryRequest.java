package com.ht.user.outlets.jlpay.contants;

import com.alibaba.fastjson.annotation.JSONField;
import com.jlpay.ext.qrcode.trans.request.OrderChnQueryRequest;

public class CustomOrderChnQueryRequest extends OrderChnQueryRequest {

    private String service = "chnquery";
    @JSONField(
            name = "transaction_id"
    )
    private String transactionId;

    public CustomOrderChnQueryRequest() {
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
