package com.ht.user.outlets.paystrategy;

import com.ht.user.outlets.allinpay.SybPayService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AllinpayService implements PayCompanyStrategy, InitializingBean {

    @Autowired
    private SybPayService sybPayService;

    /**
     * 启动 工厂注册
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        PayCompanyStrategyFactory.register(PayCompanyTypeEnum.ALLINPAY.getPayCompany(), this);
    }


    @Override
    public Map<String, String> scanCodePay(long trxamt,
                                           String reqsn,
                                           String body,
                                           String remark,
                                           String authcode,
                                           String limitPay,
                                           String idno,
                                           String truename,
                                           String asinfo,
                                           String cashId) throws Exception {
        return sybPayService.scanPay(trxamt, reqsn, body, remark, authcode, limitPay, idno, truename, asinfo);
    }

    @Override
    public Map<String, String> cancel(long trxamt, String reqsn, String oldtrxid, String oldreqsn) throws Exception {
        return sybPayService.cancel(trxamt,reqsn,oldtrxid,oldreqsn);
    }

    @Override
    public Map<String, String> refund(long trxamt, String reqsn, String oldtrxid, String oldreqsn) throws Exception {
        return sybPayService.refund(trxamt,reqsn,oldtrxid,oldreqsn);
    }

    @Override
    public Map<String, String> query(String reqsn, String trxid) throws Exception {
        return sybPayService.query(reqsn,trxid);
    }

    @Override
    public Map<String, String> posRefund(String trxamt, String trxid, String reqsn, String remark) throws Exception {
        return sybPayService.posRefund(trxamt,trxid,reqsn,remark);
    }

    @Override
    public Map<String, String> posOlQuery(String reqsn, String trxid) throws Exception {
        return sybPayService.posOlQuery(reqsn,trxid);
    }

    @Override
    public Map<String, String> posOrderPayQuery(String orderid, String trxid, String trxdate) throws Exception {
        return sybPayService.posOrderPayQuery(orderid,trxid,trxdate);
    }

    @Override
    public Map<String, String> queryCheck(String reqsn, String trxid) throws Exception {
        return sybPayService.query(reqsn,trxid);
    }

}
