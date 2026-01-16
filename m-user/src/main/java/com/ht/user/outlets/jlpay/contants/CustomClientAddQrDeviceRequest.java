package com.ht.user.outlets.jlpay.contants;

import com.alibaba.fastjson.annotation.JSONField;
import com.jlpay.ext.qrcode.trans.request.TransBaseRequest;
import lombok.Data;

@Data
public class CustomClientAddQrDeviceRequest extends TransBaseRequest {
    private String service = "clientAddQrDevice";

    @JSONField(
            name = "agentId"
    )
    private String agentId;

    @JSONField(
            name = "msgTranCode"
    )
    private String msgTranCode;

    @JSONField(
            name = "source"
    )
    private String source;

    @JSONField(
            name = "merchNo"
    )
    private String merchNo;

    @JSONField(
            name = "areaCode"
    )
    private String areaCode;

    @JSONField(
            name = "detAddress"
    )
    private String detAddress;

    @JSONField(
            name = "signMethod"
    )
    private String signMethod;

    @JSONField(
            name = "signData"
    )
    private String signData;



}
