package com.ht.user.outlets.jlpay.contants;

import com.alibaba.fastjson.annotation.JSONField;
import com.jlpay.ext.qrcode.trans.response.MicroPayResponse;
import lombok.Data;

@Data
public class CustomMicroPayResponse extends MicroPayResponse {

    @JSONField(
            name = "chn_transaction_id"
    )
    private String chnTransactionId;


    @JSONField(
            name = "pay_type"
    )
    private String payType;

    public CustomMicroPayResponse() {
    }


}
