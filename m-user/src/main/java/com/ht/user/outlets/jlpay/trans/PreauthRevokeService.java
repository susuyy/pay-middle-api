package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;

import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.jlpay.ext.qrcode.trans.request.PreauthRevokeRequest;
import com.jlpay.ext.qrcode.trans.response.PreauthRevokeResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;
import org.apache.commons.lang3.RandomStringUtils;

public class PreauthRevokeService {
    static {
        //设置系统参数
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {
        //组装请求参数
        PreauthRevokeRequest request = componentRequestData();
        //交易请求
        PreauthRevokeResponse response = TransExecuteService.executor(request, PreauthRevokeResponse.class);
        System.out.println("返回参数=========>" + JSON.toJSON(response));

    }

    private static PreauthRevokeRequest componentRequestData() {
        PreauthRevokeRequest request = new PreauthRevokeRequest();
        //必传字段
        request.setMchId("84931015812A00N");//嘉联分配的商户号
        request.setOrgCode("50264239");//嘉联分配的机构号
        request.setNonceStr("123456789abcdefg");//随机字符串
        request.setOutTradeNo("TK" + RandomStringUtils.randomNumeric(10));//商家系统内部订单号   机构下唯一
        request.setOriOutTradeNo("BS1165439705");//商家系统内部原订单号   机构下唯一
        request.setOriTransactionId("8001158120200914101822264063");//原预授权嘉联订单号，
        request.setGuaranteeAuthCode("007412");//原授权码交易返回的授权码
        request.setTotalFee("1");//交易金额（退货金额）
        request.setMchCreateIp("172.20.6.21");//终端IP
        request.setPayType("alipay");//交易类型    wxpay、alipay、unionpay
        //非必传字段
        request.setVersion("V1.0.1");//版本号
        request.setCharset("UTF-8");//字符集
        request.setSignType("RSA256");//签名方式
        request.setLongitude("");//经度
        request.setLatitude("");//纬度
        return request;
    }

}
