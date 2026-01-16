package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.outlets.allinpay.SybUtil;
import com.ht.user.outlets.jlpay.contants.CustomRefundResponse;
import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.jlpay.ext.qrcode.trans.request.RefundRequest;
import com.jlpay.ext.qrcode.trans.response.RefundResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author zhaoyang2
 * 退货demo
 */
public class RefundService {

    static {
        //设置系统参数
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {
        //组装请求参数
        RefundRequest request = componentRequestData(1L, IdWorker.getIdStr(),"","1579041215226331138");
        //交易请求
        RefundResponse response = TransExecuteService.executor(request, RefundResponse.class);
        System.out.println("返回参数=========>" + JSON.toJSON(response));

    }

    public static CustomRefundResponse refund(long trxamt, String reqsn, String oldtrxid, String oldreqsn) {
        //组装请求参数
        RefundRequest request = componentRequestData(trxamt, reqsn, oldtrxid, oldreqsn);
        //交易请求
        return TransExecuteService.executor(request, CustomRefundResponse.class);
    }


    private static RefundRequest componentRequestData(long trxamt, String reqsn, String oldtrxid, String oldreqsn) {
        RefundRequest request = new RefundRequest();
        //必传字段
        request.setMchId(TransConstants.MCH_ID);//嘉联分配的商户号
        request.setOrgCode(TransConstants.ORG_CODE);//嘉联分配的机构号
        request.setNonceStr(SybUtil.getValidatecode(16));//随机字符串
        request.setOutTradeNo(reqsn);//商家系统内部订单号   机构下唯一
        request.setOriOutTradeNo(oldreqsn);//商家系统内部原订单号   机构下唯一
        request.setTotalFee(trxamt+"");//交易金额（退货金额）
        request.setMchCreateIp("47.106.184.141");
        return request;
    }

}
