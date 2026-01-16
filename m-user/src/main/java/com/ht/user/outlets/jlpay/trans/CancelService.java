package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.outlets.allinpay.SybUtil;
import com.ht.user.outlets.jlpay.contants.TransConstants;

import com.jlpay.ext.qrcode.trans.request.CancelRequest;
import com.jlpay.ext.qrcode.trans.request.MicroPayRequest;
import com.jlpay.ext.qrcode.trans.response.CancelResponse;
import com.jlpay.ext.qrcode.trans.response.MicroPayResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author suyy
 *
 */
public class CancelService {

    static {
        //设置系统参数
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {
		//组装请求参数
        CancelRequest request = componentRequestData(0L, IdWorker.getIdStr(),"",IdWorker.getIdStr());
		//交易请求
        CancelResponse response = TransExecuteService.executor( request, CancelResponse.class);
        System.out.println("返回参数=========>" + JSON.toJSON(response));
    }


    public static CancelResponse cancel(long trxamt, String reqsn, String oldtrxid, String oldreqsn) {
        //组装请求参数
        CancelRequest request = componentRequestData( trxamt,  reqsn,  oldtrxid,  oldreqsn);
        //交易请求
        return TransExecuteService.executor( request, CancelResponse.class);
    }

    private static CancelRequest componentRequestData(long trxamt, String reqsn, String oldtrxid, String oldreqsn) {
        CancelRequest request = new CancelRequest();
        //必传参数
        request.setMchId(TransConstants.MCH_ID);//嘉联分配的商户号
        request.setOrgCode(TransConstants.ORG_CODE);//嘉联分配的机构号
        request.setNonceStr(SybUtil.getValidatecode(16));//随机字符串
        request.setOutTradeNo(reqsn);//商家系统内部订单号   机构下唯一
        request.setOriOutTradeNo(oldreqsn);//原外部订单号
        request.setMchCreateIp("36.101.125.236");
        return request;
    }

}
