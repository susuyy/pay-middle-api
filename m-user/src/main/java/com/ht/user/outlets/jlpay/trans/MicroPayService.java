package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.outlets.allinpay.SybUtil;
import com.ht.user.outlets.config.MerchantCashMapConstant;
import com.ht.user.outlets.jlpay.contants.CustomMicroPayResponse;
import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.jlpay.ext.qrcode.trans.request.MicroPayRequest;
import com.jlpay.ext.qrcode.trans.response.MicroPayResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;

import java.util.Properties;


/**
 * @author suyy
 * 条形码支付demo
 * 此接口可能会返回待确认的订单状态，此时需要接入方调用订单查询接口主动查询订单实际支付情况，
 * 建议等待5秒后发起查询，间隔10秒，15秒，20秒…，如果仍然为“待确认”状态，调用撤销接口关闭该笔交易。
 */
public class MicroPayService {

    static {
        //设置系统参数
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {

        //组装请求参数
        MicroPayRequest request = componentRequestData(1L, IdWorker.getIdStr(),"测试","测试","132375910371543986"
        ,"","","","","","cashId");
        //交易请求
        MicroPayResponse response = TransExecuteService.executor( request, MicroPayResponse.class);
        System.out.println("返回参数=========>" + JSON.toJSON(response));

    }

    public static CustomMicroPayResponse scanPay(long trxamt, String reqsn,
                                                 String body, String remark,
                                                 String authcode, String limitPay,
                                                 String idno, String truename, String asinfo,String termNo,String cashId) {
        //组装请求参数
        MicroPayRequest request = componentRequestData( trxamt, reqsn,
                 body, remark,
                 authcode, limitPay,
                 idno, truename, asinfo,termNo,cashId);
        //交易请求
        return TransExecuteService.executor(request, CustomMicroPayResponse.class);
    }


    private static MicroPayRequest componentRequestData(long trxamt,String reqsn,
                                                        String body,String remark,
                                                        String authcode,String limitPay,
                                                        String idno,String truename,String asinfo,String termNo,
                                                        String cashId) {
        MicroPayRequest request = new MicroPayRequest();
        //必传字段
        request.setMchId(TransConstants.MCH_ID);//嘉联分配的商户号
        request.setOrgCode(TransConstants.ORG_CODE);//嘉联分配的机构号
        request.setNonceStr(SybUtil.getValidatecode(16));//随机字符串
        request.setOutTradeNo(reqsn);//商家系统内部订单号   机构下唯一
        request.setTotalFee(trxamt+"");//交易金额
        request.setBody(body);//商品名
        request.setAuthCode(authcode);//授权码
        request.setAttach(remark);//商品描述
        request.setOpShopId(cashId);//门店号
        request.setMchCreateIp("47.106.184.141");
        request.setTermNo(termNo);
        return request;
    }

}
