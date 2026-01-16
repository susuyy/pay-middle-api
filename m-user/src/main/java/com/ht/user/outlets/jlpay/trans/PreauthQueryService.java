package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;

import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.jlpay.ext.qrcode.trans.request.PreauthQueryRequest;
import com.jlpay.ext.qrcode.trans.response.PreauthQueryResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;

public class PreauthQueryService {
    static {
        //璁剧疆绯荤粺鍙傛暟
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {
        //缁勮璇锋眰鍙傛暟
        PreauthQueryRequest request = componentRequestData();
        //浜ゆ槗璇锋眰
        PreauthQueryResponse response = TransExecuteService.executor(request, PreauthQueryResponse.class);
        System.out.println("杩斿洖鍙傛暟=========>" + JSON.toJSON(response));

    }

    private static PreauthQueryRequest componentRequestData() {
        PreauthQueryRequest request = new PreauthQueryRequest();
        //蹇呬紶瀛楁
        request.setMchId("84931015812A00N");//鍢夎仈鍒嗛厤鐨勫晢鎴峰彿
        request.setOrgCode("50264239");//鍢夎仈鍒嗛厤鐨勬満鏋勫彿
        request.setNonceStr("123456789");//闅忔満瀛楃涓�
        request.setOutTradeNo("BS6596018774");//鍟嗗绯荤粺鍐呴儴璁㈠崟鍙�   鏈烘瀯涓嬪敮涓�
        //闈炲繀浼犲瓧娈�
        request.setVersion("V1.0.1");//鐗堟湰鍙�
        request.setCharset("UTF-8");//瀛楃闆�
        request.setSignType("RSA256");//绛惧悕鏂瑰紡

        return request;
    }

}
