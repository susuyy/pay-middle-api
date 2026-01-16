package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;

import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.jlpay.ext.qrcode.trans.request.MicroPayPreauthRequest;
import com.jlpay.ext.qrcode.trans.response.MicroPayPreauthResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;
import org.apache.commons.lang3.RandomStringUtils;

public class MicroPayPreauthService {
    static {
        //璁剧疆绯荤粺鍙傛暟
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {

        //缁勮璇锋眰鍙傛暟
        MicroPayPreauthRequest request = componentRequestData();
        //浜ゆ槗璇锋眰
        MicroPayPreauthResponse response = TransExecuteService.executor( request, MicroPayPreauthResponse.class);
        System.out.println("杩斿洖鍙傛暟=========>" + JSON.toJSON(response));

    }

    private static MicroPayPreauthRequest componentRequestData() {
        MicroPayPreauthRequest request = new MicroPayPreauthRequest();
        //蹇呬紶瀛楁
        request.setMchId("84931015812A00N");//鍢夎仈鍒嗛厤鐨勫晢鎴峰彿
        request.setOrgCode("50264239");//鍢夎仈鍒嗛厤鐨勬満鏋勫彿
        request.setNonceStr("123456789abcdefg");//闅忔満瀛楃涓�
        request.setPayType("alipay");//浜ゆ槗绫诲瀷    wxpay銆乤lipay銆乽nionpay
        request.setOutTradeNo("BS" + RandomStringUtils.randomNumeric(10));//鍟嗗绯荤粺鍐呴儴璁㈠崟鍙�   鏈烘瀯涓嬪敮涓�
        request.setTotalFee("2");//浜ゆ槗閲戦
        request.setBody("琚壂娴嬭瘯");//鍟嗗搧鍚�
        request.setMchCreateIp("172.20.6.21");//缁堢IP
        request.setAuthCode("281129437686940405");//鎺堟潈鐮�
        request.setAttach("琚壂鍟嗗搧鎻忚堪");//鍟嗗搧鎻忚堪
        //闈炲繀浼犲瓧娈�
        request.setTermNo("12345678");//缁堢鍙�   unionpay鏃跺繀椤�8浣�
        request.setDeviceInfo("20190101");//缁堢璁惧鍙�
        request.setVersion("V1.0.3");//鐗堟湰鍙�
        request.setCharset("UTF-8");//瀛楃闆�
        request.setSignType("RSA256");//绛惧悕鏂瑰紡
        request.setRemark("琚壂澶囨敞");//澶囨敞
        request.setLongitude("");//缁忓害
        request.setLatitude("");//绾害
        request.setOpUserId("1001");//鎿嶄綔鍛�
        request.setOpShopId("100001");//闂ㄥ簵鍙�
        request.setPaymentValidTime("20");//璁㈠崟鏀粯鏈夋晥鏃堕棿, 榛樿20鍒嗛挓
        return request;
    }

}
