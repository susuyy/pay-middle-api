package com.ht.user.outlets.paystrategy;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.user.config.CardOrderPayTraceStateConfig;
import com.ht.user.config.CardOrderPayTraceTypeConfig;
import com.ht.user.outlets.allinpay.SybConstants;
import com.ht.user.outlets.allinpay.SybUtil;
import com.ht.user.outlets.config.MerchantCashMapConstant;
import com.ht.user.outlets.config.TrxCodeDescribeEnum;
import com.ht.user.outlets.controller.OutletsOrdersController;
import com.ht.user.outlets.entity.OutletsOrderPayTrace;
import com.ht.user.outlets.entity.OutletsOrderRefRefundCancel;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.jlpay.contants.*;
import com.ht.user.outlets.jlpay.trans.*;
import com.ht.user.outlets.service.IOutletsOrderPayTraceService;
import com.ht.user.outlets.util.DateStrUtil;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.jlpay.ext.qrcode.trans.request.MicroPayRequest;
import com.jlpay.ext.qrcode.trans.response.CancelResponse;
import com.jlpay.ext.qrcode.trans.response.MicroPayResponse;
import com.jlpay.ext.qrcode.trans.response.OrderChnQueryResponse;
import com.jlpay.ext.qrcode.trans.response.RefundResponse;
import com.jlpay.ext.qrcode.trans.service.TransExecuteService;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static com.ht.user.outlets.config.MerchantCashMapConstant.cashClientAddQrDeviceMap;

@Service
public class JlpayService implements PayCompanyStrategy, InitializingBean {

    private Logger logger = LoggerFactory.getLogger(JlpayService.class);

    @Autowired
    private IOutletsOrderPayTraceService outletsOrderPayTraceService;

    /**
     * 启动 工厂注册
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        PayCompanyStrategyFactory.register(PayCompanyTypeEnum.JLPAY.getPayCompany(), this);
    }


    @Override
    public Map<String, String> scanCodePay(long trxamt, String reqsn, String body,
                                           String remark, String authcode, String limitPay,
                                           String idno, String truename, String asinfo,String cashId) throws Exception {

        //码付加机 嘉联返回的 termNo
        String termNo = cashClientAddQrDeviceMap.get(cashId);
        if (!StringGeneralUtil.checkNotNull(termNo)){
            termNo = cashClientAddQrDeviceMap.get(MerchantCashMapConstant.DEFAULT_CASH);
        }

        //接口调用
        CustomMicroPayResponse response = MicroPayService.scanPay(trxamt, reqsn, body,
                remark, authcode, limitPay,
                idno, truename, asinfo,termNo,cashId);
        logger.info("嘉联扫码支付-返回参数=========>" + JSON.toJSON(response));

        // 转化业务响应数据
        Map<String, String> responseMap = new HashMap<>();

        String retCode = response.getRetCode();
        String status = response.getStatus();

//        if ("00".equals(retCode)){
//            responseMap.put("retcode","SUCCESS");
//        }else {
//            responseMap.put("retcode",retCode);
//        }
        responseMap.put("retcode","SUCCESS");

        //1-待确认2-成功3-失败
        if ("1".equals(status)){
            responseMap.put("trxstatus","2000");
        }else if ("2".equals(status)){
            responseMap.put("trxstatus","0000");
        }else {
            responseMap.put("trxstatus",status);
            responseMap.put("retcode",retCode);
        }

        responseMap.put("chnltrxid",response.getChnTransactionId());
        responseMap.put("trxamt",response.getTotalFee());
        responseMap.put("trxid",response.getTransactionId());
        responseMap.put("reqsn",response.getOutTradeNo());
        responseMap.put("cusid",response.getMchId());  //平台分配的商户号
        responseMap.put("appid", TransConstants.ORG_CODE);  //appid
        responseMap.put("initamt",response.getTotalFee());

        if (StringGeneralUtil.checkNotNull(response.getOrderTime())){
            String orderTime = response.getOrderTime();
            responseMap.put("fintime", DateStrUtil.jiaLianRetOrderDateChange(orderTime));
        }

        String payType = response.getPayType();
//        wxpay、alipay、unionpay、qqpay
        if ("wxpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.WECHAT_PAY.getValue());  //微信
        }else if ("alipay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.ALIPAY_PAY.getValue());  //阿里
        }else if ("unionpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.UNIONPAY_PAY.getValue());  //银联
        }else if ("qqpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.PHONE_QQ_PAY.getValue());  //qq
        }else {
            responseMap.put("trxcode",TrxCodeDescribeEnum.SCAN_PAY.getValue());  //默认扫码支付
        }

        //嘉联无法返回手续费字段  无法记录
        responseMap.put("fee","0");
        responseMap.put("errmsg",response.getRetMsg());  //原因说明

        checkReturnMap(reqsn,responseMap);

        return responseMap;
    }

    @Override
    public Map<String, String> cancel(long trxamt, String reqsn, String oldtrxid, String oldreqsn) throws Exception {
        CustomRefundResponse response = RefundService.refund(trxamt, reqsn, oldtrxid, oldreqsn);
        System.out.println("jl撤销(实际退款)-返回参数=========>" + JSON.toJSON(response));

        // 转化业务响应数据
        Map<String, String> responseMap = new HashMap<>();
        String retCode = response.getRetCode();
        String status = response.getStatus();

        String retMsg = response.getRetMsg();

        if ("外部订单号已经存在[ORDER.EXIST][BIZ_ERROR]".equals(retMsg)){
            return reDoRefund(trxamt,IdWorker.getIdStr(),oldtrxid,oldreqsn);
        }

        //2-成功3-失败
        if ("2".equals(status)){
            responseMap.put("trxstatus","0000");
        }else {
            responseMap.put("trxstatus",status);
            responseMap.put("retcode",retCode);
        }

        responseMap.put("cusid",response.getMchId());  //平台分配的商户号
        responseMap.put("appid", TransConstants.ORG_CODE);  //appid
        responseMap.put("trxid",response.getTransactionId());  //平台的退款交易流水号
        responseMap.put("reqsn",response.getOutTradeNo());  //商户的退款交易订单号

        //交易完成时间yyyyMMddHHmmss
        if (StringGeneralUtil.checkNotNull(response.getOrderTime())){
            String orderTime = response.getOrderTime();
            responseMap.put("fintime", DateStrUtil.jiaLianRetOrderDateChange(orderTime));
        }

        responseMap.put("errmsg",response.getRetMsg());  //失败的原因说明

        String payType = response.getPayType();
        //wxpay、alipay、unionpay、qqpay
        if ("wxpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.WECHAT_CANCEL.getValue());  //微信
        }else if ("alipay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.ALIPAY_CANCEL.getValue());  //阿里
        }else if ("unionpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.UNIONPAY_CANCEL.getValue());  //银联
        }else if ("qqpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.PHONE_QQ_CANCEL.getValue());  //qq
        }else {
            responseMap.put("trxcode",TrxCodeDescribeEnum.SCAN_CANCEL.getValue());  //交易类型 默认扫码撤销
        }

        responseMap.put("chnltrxid",response.getChnTransactionId());  //渠道流水号如支付宝，微信平台订单号

        //嘉联无法返回手续费字段  无法记录
        responseMap.put("fee","0");

        checkReturnMap(reqsn,responseMap);
        return responseMap;
    }

    @Override
    public Map<String, String> refund(long trxamt, String reqsn, String oldtrxid, String oldreqsn) throws Exception {
        CustomRefundResponse response = RefundService.refund(trxamt, reqsn, oldtrxid, oldreqsn);
        System.out.println("jl退款-返回参数=========>" + JSON.toJSON(response));

        Map<String, String> responseMap = new HashMap<>();
        //转化业务响应数据
        String retCode = response.getRetCode();
        String status = response.getStatus();
        String retMsg = response.getRetMsg();

        if ("外部订单号已经存在[ORDER.EXIST][BIZ_ERROR]".equals(retMsg)){
            return reDoRefund(trxamt,IdWorker.getIdStr(),oldtrxid,oldreqsn);
        }

//        if ("00".equals(retCode)){
//            responseMap.put("retcode","SUCCESS");
//        }else {
//            responseMap.put("retcode",retCode);
//        }
        responseMap.put("retcode","SUCCESS");

        //2-成功3-失败
        if ("2".equals(status)){
            responseMap.put("trxstatus","0000");
        }else {
            responseMap.put("trxstatus",status);
            responseMap.put("retcode",retCode);
        }

        responseMap.put("cusid",response.getMchId());  //平台分配的商户号
        responseMap.put("appid", TransConstants.ORG_CODE);  //appid
        responseMap.put("trxid",response.getTransactionId());  //平台的退款交易流水号
        responseMap.put("reqsn",response.getOutTradeNo());  //商户的退款交易订单号

        //交易完成时间yyyyMMddHHmmss
        if (StringGeneralUtil.checkNotNull(response.getOrderTime())){
            String orderTime = response.getOrderTime();
            responseMap.put("fintime", DateStrUtil.jiaLianRetOrderDateChange(orderTime));
        }

        responseMap.put("errmsg",response.getRetMsg());  //失败的原因说明

        String payType = response.getPayType();
        //wxpay、alipay、unionpay、qqpay
        if ("wxpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.WECHAT_REFUND.getValue());  //微信
        }else if ("alipay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.ALIPAY_REFUND.getValue());  //阿里
        }else if ("unionpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.UNIONPAY_REFUND.getValue());  //银联
        }else if ("qqpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.PHONE_QQ_REFUND.getValue());  //qq
        }else {
            responseMap.put("trxcode",TrxCodeDescribeEnum.SCAN_REFUND.getValue());  //交易类型 默认扫码退货
        }

        responseMap.put("chnltrxid",response.getChnTransactionId());  //渠道流水号如支付宝，微信平台订单号

        //嘉联无法返回手续费字段  无法记录
        responseMap.put("fee","0");
        checkReturnMap(reqsn,responseMap);
        return responseMap;
    }

    /**
     * 处理外部订单号已存在重新发起退款
     * @param trxamt
     * @param reqsn
     * @param oldtrxid
     * @param oldreqsn
     * @return
     * @throws Exception
     */
    public Map<String, String> reDoRefund(long trxamt, String reqsn, String oldtrxid, String oldreqsn) throws Exception {
        CustomRefundResponse response = RefundService.refund(trxamt, reqsn, oldtrxid, oldreqsn);
        System.out.println("jl退款-返回参数=========>" + JSON.toJSON(response));

        Map<String, String> responseMap = new HashMap<>();
        //转化业务响应数据
        String retCode = response.getRetCode();
        String status = response.getStatus();

        responseMap.put("retcode","SUCCESS");

        //2-成功3-失败
        if ("2".equals(status)){
            responseMap.put("trxstatus","0000");
        }else {
            responseMap.put("trxstatus",status);
            responseMap.put("retcode",retCode);
        }

        responseMap.put("cusid",response.getMchId());  //平台分配的商户号
        responseMap.put("appid", TransConstants.ORG_CODE);  //appid
        responseMap.put("trxid",response.getTransactionId());  //平台的退款交易流水号
        responseMap.put("reqsn",response.getOutTradeNo());  //商户的退款交易订单号

        //交易完成时间yyyyMMddHHmmss
        if (StringGeneralUtil.checkNotNull(response.getOrderTime())){
            String orderTime = response.getOrderTime();
            responseMap.put("fintime", DateStrUtil.jiaLianRetOrderDateChange(orderTime));
        }

        responseMap.put("errmsg",response.getRetMsg());  //失败的原因说明

        String payType = response.getPayType();
        //wxpay、alipay、unionpay、qqpay
        if ("wxpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.WECHAT_REFUND.getValue());  //微信
        }else if ("alipay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.ALIPAY_REFUND.getValue());  //阿里
        }else if ("unionpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.UNIONPAY_REFUND.getValue());  //银联
        }else if ("qqpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.PHONE_QQ_REFUND.getValue());  //qq
        }else {
            responseMap.put("trxcode",TrxCodeDescribeEnum.SCAN_REFUND.getValue());  //交易类型 默认扫码退货
        }

        responseMap.put("chnltrxid",response.getChnTransactionId());  //渠道流水号如支付宝，微信平台订单号

        //嘉联无法返回手续费字段  无法记录
        responseMap.put("fee","0");

        responseMap.put("newSysReqsn",reqsn);

        checkReturnMap(reqsn,responseMap);
        return responseMap;
    }

    @Override
    public Map<String, String> query(String reqsn, String trxid) throws Exception {
        CustomOrderChnQueryResponse response = OrderChnQueryService.query(reqsn, trxid);
        System.out.println("jl订单查询-返回参数=========>" + JSON.toJSON(response));

        // 转化业务响应数据
        String retCode = response.getRetCode();
        String status = response.getStatus();

        Map<String, String> responseMap = new HashMap<>();

        responseMap.put("retcode","SUCCESS");

        //1-待确认
        //2-成功
        //3-失败
        //4-已撤销
        //5-已退款
        if ("1".equals(status)){
            responseMap.put("trxstatus","2000");
        }else if ("2".equals(status)){
            responseMap.put("trxstatus","0000");
        }else if ("4".equals(status)){
            responseMap.put("trxstatus","0000");
        }else if ("5".equals(status)){
            responseMap.put("trxstatus","0000");
        }else {//状态3和其余处理
            responseMap.put("trxstatus",status+"-"+retCode);
        }

        responseMap.put("chnltrxid",response.getChnTransactionId());

        String payType = response.getPayType();
//        wxpay、alipay、unionpay、qqpay
        if ("wxpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.WECHAT_PAY.getValue());  //微信
        }else if ("alipay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.ALIPAY_PAY.getValue());  //阿里
        }else if ("unionpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.UNIONPAY_PAY.getValue());  //银联
        }else if ("qqpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.PHONE_QQ_PAY.getValue());  //qq
        }else {
            responseMap.put("trxcode",TrxCodeDescribeEnum.SCAN_PAY.getValue());  //默认扫码支付
        }

        responseMap.put("trxamt",response.getTotalFee());
        responseMap.put("trxid",response.getTransactionId());

        //交易完成时间yyyyMMddHHmmss
        if (StringGeneralUtil.checkNotNull(response.getPayTime())){
            String payTime = response.getPayTime();
            responseMap.put("fintime", DateStrUtil.jiaLianRetOrderDateChange(payTime));
        }

        responseMap.put("errmsg",response.getRetMsg());

        OutletsOrderPayTrace outletsOrderPayTrace = outletsOrderPayTraceService.queryByRefBatchCode(response.getOutTradeNo());
        if (!ObjectUtils.isEmpty(outletsOrderPayTrace)){
            responseMap.put("reqsn",outletsOrderPayTrace.getOrderCode());
        }else {
            responseMap.put("reqsn",response.getOutTradeNo());
        }

        responseMap.put("refReqsn",response.getOutTradeNo());
        responseMap.put("cusid",response.getMchId());  //平台分配的商户号
        responseMap.put("appid", TransConstants.ORG_CODE);  //appid
        responseMap.put("initamt",response.getTotalFee());
        responseMap.put("acct",response.getSubOpenid());

        //嘉联无法返回手续费字段  无法记录
        responseMap.put("fee","0");
        checkReturnMap(reqsn,responseMap);
        return responseMap;
    }

    @Override
    public Map<String, String> posRefund(String trxamt, String trxid, String reqsn, String remark) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> posOlQuery(String reqsn, String trxid) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> posOrderPayQuery(String orderid, String trxid, String trxdate) throws Exception {
        return null;
    }

    @Override
    public Map<String, String> queryCheck(String reqsn, String trxid) throws Exception {
        CustomOrderChnQueryResponse response = OrderChnQueryService.queryCheck(reqsn, trxid);
        System.out.println("jl订单查询-返回参数=========>" + JSON.toJSON(response));

        // 转化业务响应数据
        String retCode = response.getRetCode();
        String status = response.getStatus();

        Map<String, String> responseMap = new HashMap<>();

        responseMap.put("retcode","SUCCESS");

        //1-待确认
        //2-成功
        //3-失败
        //4-已撤销
        //5-已退款
        if ("1".equals(status)){
            responseMap.put("trxstatus","2000");
        }else if ("2".equals(status)){
            responseMap.put("trxstatus","0000");
        }else if ("4".equals(status)){
            responseMap.put("trxstatus","0000");
        }else if ("5".equals(status)){
            responseMap.put("trxstatus","0000");
        }else {//状态3和其余处理
            responseMap.put("trxstatus",status+"-"+retCode);
        }

        responseMap.put("chnltrxid",response.getChnTransactionId());

        String payType = response.getPayType();
//        wxpay、alipay、unionpay、qqpay
        if ("wxpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.WECHAT_PAY.getValue());  //微信
        }else if ("alipay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.ALIPAY_PAY.getValue());  //阿里
        }else if ("unionpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.UNIONPAY_PAY.getValue());  //银联
        }else if ("qqpay".equals(payType)){
            responseMap.put("trxcode",TrxCodeDescribeEnum.PHONE_QQ_PAY.getValue());  //qq
        }else {
            responseMap.put("trxcode",TrxCodeDescribeEnum.SCAN_PAY.getValue());  //默认扫码支付
        }

        responseMap.put("trxamt",response.getTotalFee());
        responseMap.put("trxid",response.getTransactionId());

        //交易完成时间yyyyMMddHHmmss
        if (StringGeneralUtil.checkNotNull(response.getPayTime())){
            String payTime = response.getPayTime();
            responseMap.put("fintime", DateStrUtil.jiaLianRetOrderDateChange(payTime));
        }

        responseMap.put("errmsg",response.getRetMsg());

        OutletsOrderPayTrace outletsOrderPayTrace = outletsOrderPayTraceService.queryByRefBatchCode(response.getOutTradeNo());
        if (!ObjectUtils.isEmpty(outletsOrderPayTrace)){
            responseMap.put("reqsn",outletsOrderPayTrace.getOrderCode());
        }else {
            responseMap.put("reqsn",response.getOutTradeNo());
        }

        responseMap.put("refReqsn",response.getOutTradeNo());
        responseMap.put("cusid",response.getMchId());  //平台分配的商户号
        responseMap.put("appid", TransConstants.ORG_CODE);  //appid
        responseMap.put("initamt",response.getTotalFee());
        responseMap.put("acct",response.getSubOpenid());

        //嘉联无法返回手续费字段  无法记录
        responseMap.put("fee","0");
        checkReturnMap(reqsn,responseMap);
        return responseMap;
    }

    /**
     * 校验统一返回的map数据 系统统一响应确认
     * @param reqsn
     * @param checkMap
     * @throws Exception
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void checkReturnMap(String reqsn,Map checkMap) throws Exception{
        logger.info(reqsn+"-ret:"+checkMap);
        if(checkMap == null){
            logger.info(reqsn+"嘉联异常,返回数据错误,数据为空");
            throw new CheckException(1500,"返回数据错误");
        }
        if(!"SUCCESS".equals(checkMap.get("retcode"))){
            logger.info(reqsn+"嘉联异常,支付失败-"+checkMap.get("errmsg")+"");
            throw new CheckException(1500,checkMap.get("errmsg")+"");
        }
    }
}
