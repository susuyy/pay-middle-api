package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.outlets.allinpay.SybUtil;
import com.ht.user.outlets.jlpay.contants.*;
import com.jlpay.ext.qrcode.trans.request.MicroPayRequest;
import com.jlpay.ext.qrcode.trans.response.MicroPayResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;

/**
 *  码付加机
 */
public class ClientAddQrDeviceService {

    static {
        //设置系统参数
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {

        //组装请求参数
        CustomClientAddQrDeviceRequest request = componentRequestData();
        //交易请求
        CustomClientAddQrDeviceResponse response = ClientAddQrDeviceTransExecuteService.executor(request);
        System.out.println("返回参数=========>" + JSON.toJSON(response));

    }

    public static CustomClientAddQrDeviceResponse clientAddQrDevice() {
        //组装请求参数
        CustomClientAddQrDeviceRequest request = componentRequestData();
        //交易请求
        return ClientAddQrDeviceTransExecuteService.executor(request);
    }


    private static CustomClientAddQrDeviceRequest componentRequestData() {
        CustomClientAddQrDeviceRequest request = new CustomClientAddQrDeviceRequest();

        //必传字段
//        request.setMchId(TransConstants.MCH_ID);//嘉联分配的商户号
//        request.setOrgCode(TransConstants.ORG_CODE);//嘉联分配的机构号
//        request.setNonceStr(SybUtil.getValidatecode(16));//随机字符串

        request.setAgentId(TransConstants.ORG_CODE);
        request.setMsgTranCode(TransConstants.MSG_TRAN_CODE);  //接口类型值 固定
        request.setSource(TransConstants.SOURCE);  //码付渠道入网 固定值
//        request.setMerchNo("849602058120001");
        request.setMerchNo(TransConstants.MCH_ID);
        request.setAreaCode(TransConstants.AREA_CODE);  //行政区域代码
        request.setDetAddress("海南省三亚市亚龙湾申亚1号小镇奥特莱斯");  //商户交易真实地址
        request.setSignMethod(TransConstants.SIGN_METHOD); //签名方式 固定

        return request;
    }
}
