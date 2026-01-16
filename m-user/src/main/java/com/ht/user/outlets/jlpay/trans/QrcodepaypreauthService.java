package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;

import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.jlpay.ext.qrcode.trans.request.QrcodePayPreauthRequest;
import com.jlpay.ext.qrcode.trans.response.QrcodePayPreauthResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;
import org.apache.commons.lang3.RandomStringUtils;

public class QrcodepaypreauthService {
    static {
        //设置系统参数
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {
        //组装请求参数
        QrcodePayPreauthRequest request = componentRequestData();
        //交易请求
        QrcodePayPreauthResponse response = TransExecuteService.executor(request, QrcodePayPreauthResponse.class);
        System.out.println("返回参数=========>" + JSON.toJSON(response));

    }

    private static QrcodePayPreauthRequest componentRequestData() {
        QrcodePayPreauthRequest request = new QrcodePayPreauthRequest();
        //必传字段
        request.setMchId("84944035812A01P");//嘉联分配的商户号
        request.setOrgCode("50265462");//嘉联分配的机构号
        request.setNonceStr("123456789abcdefg");//随机字符串
        request.setPayType("alipay");//交易类型    wxpay、alipay、unionpay, jlpay
        request.setOutTradeNo("ZS" + RandomStringUtils.randomNumeric(10));//商家系统内部订单号   机构下唯一
        request.setTotalFee("1");//交易金额
        request.setBody("主扫测试");//商品名
        request.setMchCreateIp("");//终端IP
        request.setNotifyUrl("http://127.0.0.1/qrcode/notify/");//回调地址
        request.setAttach("主扫商品描述");//商品描述
        //非必传字段
        request.setTermNo("12345678");//终端号   unionpay时必须8位
        request.setDeviceInfo("80005611");//终端设备号
        request.setVersion("V1.0.1");//版本号
        request.setCharset("UTF-8");//字符集
        request.setSignType("RSA256");//签名方式
        request.setRemark("主扫备注");//备注
        request.setLongitude("");//经度
        request.setLatitude("");//纬度
        request.setOpUserId("1001");//操作员
        request.setOpShopId("100001");//门店号
        request.setPaymentValidTime("20");//订单支付有效时间,默认20分钟
        return request;
    }

}
