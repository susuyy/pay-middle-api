package com.ht.user.outlets.jlpay.trans;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.outlets.allinpay.SybUtil;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.jlpay.contants.CustomOrderChnQueryRequest;
import com.ht.user.outlets.jlpay.contants.CustomOrderChnQueryResponse;
import com.ht.user.outlets.jlpay.contants.TransConstants;
import com.ht.user.result.ResultTypeEnum;
import com.jlpay.ext.qrcode.common.service.CommonService;
import com.jlpay.ext.qrcode.trans.request.OrderChnQueryRequest;
import com.jlpay.ext.qrcode.trans.request.TransBaseRequest;
import com.jlpay.ext.qrcode.trans.response.OrderChnQueryResponse;
import com.jlpay.ext.qrcode.trans.response.TransBaseResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;
import com.jlpay.ext.qrcode.trans.utils.ErrorConstants;
import com.jlpay.ext.qrcode.trans.utils.JlpayException;

/**
 * @author zhaoyang2
 * 璁㈠崟鏌ヨdemo
 */
public class OrderChnQueryService {

    static {
        //设置系统参数
        TransConstants.setJlpayProperty();
    }

    public static void main(String[] args) {
        //组装请求参数
        OrderChnQueryRequest request = componentRequestData("1579041215226331138","411073400099616171622454");
        //交易请求
        OrderChnQueryResponse response = TransExecuteService.executor(request, OrderChnQueryResponse.class);
        System.out.println("返回参数=========>" + JSON.toJSON(response));

    }

    public static CustomOrderChnQueryResponse query(String reqsn, String trxid) {
        //组装请求参数
        OrderChnQueryRequest request = componentRequestData(reqsn, trxid);
        //交易请求
        return TransExecuteService.executor(request, CustomOrderChnQueryResponse.class);
    }

    private static OrderChnQueryRequest componentRequestData(String reqsn, String trxid) {
        CustomOrderChnQueryRequest request = new CustomOrderChnQueryRequest();
        //必传字段
        request.setMchId(TransConstants.MCH_ID);//嘉联分配的商户号
        request.setOrgCode(TransConstants.ORG_CODE);//嘉联分配的机构号
        request.setNonceStr(SybUtil.getValidatecode(16));//随机字符串
        request.setOutTradeNo(reqsn);//商家系统内部订单号
        request.setTransactionId(trxid); // 嘉联平台订单号  机构下唯一 transaction_id
        return request;
    }

    public static CustomOrderChnQueryResponse queryCheck(String reqsn, String trxid) {
        //组装请求参数
        OrderChnQueryRequest request = componentRequestData(reqsn, trxid);
        //交易请求
        try {
            return executorCheck(request);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.BIND_EXCEPTION.getCode(),"交易不存在");
        }
    }

    public static CustomOrderChnQueryResponse  executorCheck(TransBaseRequest request) {
        String sysPriKey = System.getProperty("qrcode.org.privatekey");
        String jlPubKey = System.getProperty("qrocde.jlpay.publickey");
        String tradeUrl = System.getProperty("qrcode.jlpay.tradeUrl");
        if (null != sysPriKey && null != jlPubKey && null != tradeUrl) {
            String requestStr = CommonService.sign(request, sysPriKey);
            String responseStr = CommonService.httpToInvoke(requestStr, tradeUrl + request.getService());
            CommonService.checkSign(responseStr, jlPubKey);
            return JSON.parseObject(responseStr, CustomOrderChnQueryResponse.class);
        } else {
            throw new JlpayException(ErrorConstants.VALIDATE_ERROR_1, "请设置系统参数");
        }
    }
}
