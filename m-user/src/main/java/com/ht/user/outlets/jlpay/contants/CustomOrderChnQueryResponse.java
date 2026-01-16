package com.ht.user.outlets.jlpay.contants;

import com.alibaba.fastjson.annotation.JSONField;

import com.jlpay.ext.qrcode.trans.response.OrderChnQueryResponse;
import lombok.Data;

@Data
public class CustomOrderChnQueryResponse extends OrderChnQueryResponse {

    @JSONField(
            name = "pay_time"
    )
    private String payTime;

    @JSONField(
            name = "sub_openid"
    )
    private String subOpenid;

    @JSONField(
            name = "chn_transaction_id"
    )
    private String chnTransactionId;

    @JSONField(
            name = "pay_type"
    )
    private String payType;

    public CustomOrderChnQueryResponse() {
    }


}
