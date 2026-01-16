package com.ht.user.outlets.jlpay.contants;

import com.alibaba.fastjson.annotation.JSONField;
import com.jlpay.ext.qrcode.trans.response.RefundResponse;
import lombok.Data;

@Data
public class CustomRefundResponse extends RefundResponse {

    @JSONField(
            name = "chn_transaction_id"
    )
    private String chnTransactionId;


    @JSONField(
            name = "pay_type"
    )
    private String payType;

    public CustomRefundResponse() {
    }


}
