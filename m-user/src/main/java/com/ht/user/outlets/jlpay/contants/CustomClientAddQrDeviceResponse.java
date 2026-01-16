package com.ht.user.outlets.jlpay.contants;

import com.alibaba.fastjson.annotation.JSONField;
import com.jlpay.ext.qrcode.trans.response.TransBaseResponse;
import lombok.Data;

@Data
public class CustomClientAddQrDeviceResponse extends TransBaseResponse {

    @JSONField(
            name = "merch_no"
    )
    private String merchNo;

    @JSONField(
            name = "term_no"
    )
    private String termNo;

    @JSONField(
            name = "device_no"
    )
    private String deviceNo;

    @JSONField(
            name = "protocol_id"
    )
    private String protocolId;

    @JSONField(
            name = "ret_msg"
    )
    private String retMsg;

    @JSONField(
            name = "ret_code"
    )
    private String retCode;
}
