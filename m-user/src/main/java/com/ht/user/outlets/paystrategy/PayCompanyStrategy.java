package com.ht.user.outlets.paystrategy;

import com.ht.user.outlets.allinpay.HttpConnectionUtil;
import com.ht.user.outlets.allinpay.SybConstants;
import com.ht.user.outlets.allinpay.SybUtil;

import java.util.Map;
import java.util.TreeMap;

public interface PayCompanyStrategy {

    /**
     * 发起扫码 支付 订单
     * @param trxamt
     * @param reqsn
     * @param body
     * @param remark
     * @param authcode
     * @param limitPay
     * @param idno
     * @param truename
     * @param asinfo
     * @return
     * @throws Exception
     */
    Map<String, String> scanCodePay(long trxamt,String reqsn,
                                    String body,String remark,
                                    String authcode,String limitPay,
                                    String idno,String truename,String asinfo,String cashId) throws Exception;
    /**
     * 扫码支付 撤销
     * @param trxamt
     * @param reqsn
     * @param oldtrxid
     * @param oldreqsn
     * @return
     * @throws Exception
     */
    Map<String,String> cancel(long trxamt,String reqsn,String oldtrxid,String oldreqsn) throws Exception;

    /**
     * 扫码支付 退款
     * @param trxamt
     * @param reqsn
     * @param oldtrxid
     * @param oldreqsn
     * @return
     * @throws Exception
     */
    Map<String,String> refund(long trxamt,String reqsn,String oldtrxid,String oldreqsn) throws Exception;

    /**
     * 扫码支付 查询
     * @param reqsn
     * @param trxid
     * @return
     * @throws Exception
     */
    Map<String,String> query(String reqsn,String trxid) throws Exception;

    /**
     * pos 退款
     * @param trxamt
     * @param trxid
     * @param reqsn
     * @param remark
     * @return
     * @throws Exception
     */
    Map<String, String> posRefund(String trxamt, String trxid,String reqsn,
                                         String remark) throws Exception;

    /**
     * pos 订单查询 线上接口 我方系统异步数据补充用
     * @param reqsn
     * @param trxid
     * @return
     * @throws Exception
     */
    Map<String, String> posOlQuery(String reqsn, String trxid) throws Exception;

    /**
     * pos 订单查询 可查询大部分数据 对接方调用此功能较多
     * @param orderid
     * @param trxid
     * @param trxdate
     * @return
     * @throws Exception
     */
    Map<String, String> posOrderPayQuery(String orderid, String trxid, String trxdate) throws Exception;

    Map<String, String> queryCheck(String reqsn, String payTraceNo) throws Exception;
}
