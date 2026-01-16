package com.ht.user.outlets.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.MisOrderData;
import com.ht.user.config.CardOrderPayTraceStateConfig;
import com.ht.user.config.CardOrderPayTraceTypeConfig;
import com.ht.user.config.CardOrdersStateConfig;
import com.ht.user.config.CardOrdersTypeConfig;
import com.ht.user.ordergoods.entity.UploadOrderDetails;

import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.config.MerchantCashMapConstant;
import com.ht.user.outlets.config.TrxCodeDescribeEnum;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.mapper.OutletsOrdersMapper;
import com.ht.user.outlets.paystrategy.PayCompanyStrategy;
import com.ht.user.outlets.paystrategy.PayCompanyStrategyFactory;
import com.ht.user.outlets.paystrategy.PayCompanyTypeEnum;
import com.ht.user.outlets.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.outlets.util.*;
import com.ht.user.outlets.vo.OutletsOrderPayTraceVO;
import com.ht.user.outlets.vo.OutletsOrdersVO;
import com.ht.user.result.ResultTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 订单主表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Service
public class OutletsOrdersServiceImpl extends ServiceImpl<OutletsOrdersMapper, OutletsOrders> implements IOutletsOrdersService {

    @Autowired
    private IOutletsOrdersGoodsService outletsOrdersGoodsService;

    @Autowired
    private IOutletsOrderDetailsService outletsOrderDetailsService;

    @Autowired
    private IOutletsOrderPayTraceService outletsOrderPayTraceService;

    @Autowired
    private IOutletsOrderRefTraceService outletsOrderRefTraceService;

    @Autowired
    private IOutletsOrderRefundCancelService outletsOrderRefundCancelService;

    @Autowired
    private IOutletsOrderRefRefundCancelService outletsOrderRefRefundCancelService;

    @Autowired
    private SybPayService sybPayService;

    private Logger logger = LoggerFactory.getLogger(OutletsOrdersServiceImpl.class);

    @Override
    @Transactional
    public OutletsOrderPayTrace createMisOrderPayTrace(MisOrderData misOrderData) {
        QueryWrapper<OutletsOrders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", misOrderData.getOrderCode());
        OutletsOrders outletsOrders = this.baseMapper.selectOne(queryWrapper);

        OutletsOrderPayTrace outletsOrderPayTrace = new OutletsOrderPayTrace();
        if (outletsOrders == null) {
            OutletsOrders outletsOrdersSave = new OutletsOrders();
            outletsOrdersSave.setOrderCode(misOrderData.getOrderCode());
            outletsOrdersSave.setType(CardOrdersTypeConfig.CONSUME);
            outletsOrdersSave.setState(CardOrderPayTraceStateConfig.UNPAID);
            outletsOrdersSave.setMerchantCode(misOrderData.getStoreCode());
            outletsOrdersSave.setUserId(-1L);
            outletsOrdersSave.setQuantity(BigDecimal.ONE);
            outletsOrdersSave.setAmount(Integer.parseInt(misOrderData.getAmount()));
            outletsOrdersSave.setDiscount(0);
            outletsOrdersSave.setComments("奥特莱斯收银台订单");
            outletsOrdersSave.setLimitPayType(misOrderData.getLimitPayType());
            outletsOrdersSave.setCreateAt(new Date());
            outletsOrdersSave.setUpdateAt(new Date());
            outletsOrdersSave.setStoreCode(misOrderData.getStoreCode());
            outletsOrdersSave.setIdCardNo(misOrderData.getIdCardNo());
            outletsOrdersSave.setActualPhone(misOrderData.getActualPhone());
            outletsOrdersSave.setLimitPayType(misOrderData.getLimitPayType());
            String channelApi = CompanyPayWeight.getPosPayCompanyFlag(CompanyPayWeight.posPayCompany);
            outletsOrdersSave.setChannelApi(channelApi);
            save(outletsOrdersSave);

            //记录订单上送的商品数据
            if (!StringUtils.isEmpty(misOrderData.getOrderDetail()) && !"null".equals(misOrderData.getOrderDetail())) {
                String orderGoodsStr = misOrderData.getOrderDetail();
                List list = JSONObject.parseObject(orderGoodsStr, List.class);
                for (Object data : list) {
                    UploadOrderDetails uploadOrderDetails = JSONObject.parseObject(JSONObject.toJSONString(data), UploadOrderDetails.class);
                    OutletsOrdersGoods outletsOrdersGoods = new OutletsOrdersGoods();
                    outletsOrdersGoods.setOrderCode(misOrderData.getOrderCode());
                    outletsOrdersGoods.setGoodsGroupCode(uploadOrderDetails.getGoodsGroupCode());
                    outletsOrdersGoods.setCategoryCode(uploadOrderDetails.getCategoryCode());
                    outletsOrdersGoods.setBrandCode(uploadOrderDetails.getBrandCode());
                    outletsOrdersGoods.setGoodsCode(uploadOrderDetails.getGoodsCode());
                    outletsOrdersGoods.setGoodsName(uploadOrderDetails.getGoodsName());
                    outletsOrdersGoods.setGoodsCount(Integer.parseInt(uploadOrderDetails.getGoodsCount()));
                    outletsOrdersGoods.setGoodsPrice(Integer.parseInt(uploadOrderDetails.getGoodsPrice()));
                    outletsOrdersGoods.setGoodsDiscount(Integer.parseInt(uploadOrderDetails.getGoodsDiscount()));
                    outletsOrdersGoods.setGoodsPayPrice(Integer.parseInt(uploadOrderDetails.getGoodsPayPrice()));
                    outletsOrdersGoods.setGoodsActivityType(uploadOrderDetails.getGoodsActivityType());
                    outletsOrdersGoods.setCreateAt(new Date());
                    outletsOrdersGoods.setUpdateAt(new Date());
                    outletsOrdersGoodsService.save(outletsOrdersGoods);
                }
            }

            OutletsOrderDetails outletsOrderDetails = new OutletsOrderDetails();
            outletsOrderDetails.setOrderCode(misOrderData.getOrderCode());
            outletsOrderDetails.setMerchantCode(misOrderData.getStoreCode());
            outletsOrderDetails.setQuantity(BigDecimal.ONE);
            outletsOrderDetails.setAmount(Integer.parseInt(misOrderData.getAmount()));
            outletsOrderDetails.setState(CardOrderPayTraceStateConfig.UNPAID);
            outletsOrderDetails.setType(CardOrdersTypeConfig.CONSUME);
            outletsOrderDetails.setDisccount(0);
            outletsOrderDetails.setCreateAt(new Date());
            outletsOrderDetails.setUpdateAt(new Date());
            outletsOrderDetailsService.save(outletsOrderDetails);

            outletsOrderPayTrace.setOrderDetailId(outletsOrderDetails.getId());

            outletsOrderPayTrace.setOrderCode(misOrderData.getOrderCode());
            String cloudMisTrxSsn = IdWorker.getIdStr();
            outletsOrderPayTrace.setPayCode(StringUtils.isEmpty(misOrderData.getPayCode()) ? cloudMisTrxSsn : misOrderData.getPayCode());

            outletsOrderPayTrace.setType(CardOrdersTypeConfig.POS_MIS_ORDER);
            outletsOrderPayTrace.setState(CardOrderPayTraceStateConfig.UNPAID);
            outletsOrderPayTrace.setSource("刷卡订单流水");
            outletsOrderPayTrace.setAmount(Integer.parseInt(misOrderData.getAmount()));
            outletsOrderPayTrace.setPosSerialNum(misOrderData.getCashId());
            outletsOrderPayTrace.setCashId(misOrderData.getCashId());
            outletsOrderPayTrace.setMerchantCode(misOrderData.getStoreCode());
            outletsOrderPayTrace.setMerchId(misOrderData.getStoreCode());
            outletsOrderPayTrace.setMerchName("奥特莱斯");
            if (MerchantCashMapConstant.merchantCashMap!=null){
                if (StringGeneralUtil.checkNotNull(misOrderData.getCashId())){
                    outletsOrderPayTrace.setMerchName(MerchantCashMapConstant.merchantCashMap.get(misOrderData.getCashId()));
                }
            }
            outletsOrderPayTrace.setRefTraceNo(StringUtils.isEmpty(misOrderData.getPayCode()) ? cloudMisTrxSsn : misOrderData.getPayCode());
            outletsOrderPayTrace.setTraceNo(IdWorker.getIdStr());
            outletsOrderPayTrace.setCreateAt(new Date());
            outletsOrderPayTrace.setUpdateAt(new Date());

            outletsOrderPayTrace.setRefBatchCode(IdWorker.getIdStr());

            outletsOrderPayTrace.setChannelApi(channelApi);

            outletsOrderPayTraceService.save(outletsOrderPayTrace);

            return outletsOrderPayTrace;
        } else {
            throw new CheckException(ResultTypeEnum.ORDER_CODE_REPEAT);
        }
    }

    @Override
    public OutletsOrders queryByOrderCode(String orderCode) {
        try {
            LambdaQueryWrapper<OutletsOrders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OutletsOrders::getOrderCode, orderCode);
            return this.baseMapper.selectOne(lambdaQueryWrapper);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.ORDER_ERROR);
        }
    }

    @Override
    public OutletsOrders queryByOrderCodeNotMany(String orderCode) {
        LambdaQueryWrapper<OutletsOrders> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OutletsOrders::getOrderCode, orderCode);
        return getOne(lambdaQueryWrapper,false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAllState(String orderCode, String state) {
        updateStateByOrderCode(orderCode, state);
        outletsOrderDetailsService.updateStateByOrderCode(orderCode, state);
        outletsOrderPayTraceService.updateStateByOrderCode(orderCode, state);
    }

    @Override
    public List<OutletsOrderPayTrace> checkHaveOrder(String cashId) {
//        QueryWrapper<OutletsOrderPayTrace> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("cash_id", cashId);
//        queryWrapper.eq("type","pos_mis_order");
//        queryWrapper.eq("state","unpaid");
//        queryWrapper.orderByDesc("create_at");
//        queryWrapper.last("limit 1");
//        return outletsOrderPayTraceService.list(queryWrapper);
        return outletsOrderPayTraceService.checkHaveOrder(cashId);
    }

    @Override
    public void updateStateByOrderCode(String orderCode, String state) {
        this.baseMapper.updateStateByOrderCode(orderCode, state, new Date());
    }

    @Override
    public void updateStateChannelByOrderCode(String orderCode, String state, String channelApi) {
        this.baseMapper.updateStateChannelByOrderCode(orderCode, state, new Date(),channelApi);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPaymentQrCodeOrder(OutletsOrderRefTrace outletsOrderRefTrace, QrPaymentData qrPaymentData,String channelApi) {
        String retcode = outletsOrderRefTrace.getRetcode();
        String trxstatus = outletsOrderRefTrace.getTrxstatus();
        String state = CardOrderPayTraceStateConfig.CLOSE;
        if ("SUCCESS".equals(retcode)) {
            if ("0000".equals(trxstatus)) {
                state = CardOrderPayTraceStateConfig.PAID;
            }else if ("2000".equals(trxstatus)) {
                state = CardOrderPayTraceStateConfig.UNPAID;
            }
        }

        OutletsOrders outletsOrders = new OutletsOrders();
        outletsOrders.setOrderCode(qrPaymentData.getOrderCode());
        outletsOrders.setType(CardOrdersTypeConfig.CONSUME);
        outletsOrders.setState(state);
        outletsOrders.setUserId(-1L);
        outletsOrders.setQuantity(BigDecimal.ONE);
        outletsOrders.setAmount(qrPaymentData.getAmount());
        outletsOrders.setComments("扫码消费订单");
        outletsOrders.setDiscount(0);
        outletsOrders.setStoreCode(qrPaymentData.getStoreCode());
        outletsOrders.setActualPhone(qrPaymentData.getActualPhone());
        outletsOrders.setChannelApi(channelApi);
        outletsOrders.setCreateAt(new Date());
        outletsOrders.setUpdateAt(new Date());
        save(outletsOrders);

        OutletsOrderDetails outletsOrderDetails = new OutletsOrderDetails();
        outletsOrderDetails.setOrderCode(qrPaymentData.getOrderCode());
        outletsOrderDetails.setQuantity(BigDecimal.ONE);
        outletsOrderDetails.setAmount(qrPaymentData.getAmount());
        outletsOrderDetails.setState(state);
        outletsOrderDetails.setType(CardOrdersTypeConfig.CONSUME);
        outletsOrderDetails.setDisccount(0);
        outletsOrderDetails.setCreateAt(new Date());
        outletsOrderDetails.setUpdateAt(new Date());
        outletsOrderDetailsService.save(outletsOrderDetails);

        OutletsOrderPayTrace outletsOrderPayTrace = new OutletsOrderPayTrace();
        outletsOrderPayTrace.setOrderCode(qrPaymentData.getOrderCode());
        outletsOrderPayTrace.setPayCode(outletsOrderRefTrace.getChnltrxid());
        outletsOrderPayTrace.setType(CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY);
        outletsOrderPayTrace.setState(state);
        outletsOrderPayTrace.setSource(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefTrace.getTrxcode()));
        outletsOrderPayTrace.setSourceId(qrPaymentData.getPaymentQrCode());
        outletsOrderPayTrace.setAmount(StringGeneralUtil.checkNotNull(outletsOrderRefTrace.getTrxamt()) ? Integer.parseInt(outletsOrderRefTrace.getTrxamt()) : 0);
        outletsOrderPayTrace.setFee(StringGeneralUtil.checkNotNull(outletsOrderRefTrace.getFee()) ? Integer.parseInt(outletsOrderRefTrace.getFee()) : 0);
        outletsOrderPayTrace.setMerchantCode(qrPaymentData.getStoreCode());
        outletsOrderPayTrace.setMerchId(qrPaymentData.getStoreCode());
        outletsOrderPayTrace.setMerchName("奥特莱斯");

        outletsOrderPayTrace.setRefBatchCode(outletsOrderRefTrace.getRefReqsn());

        if (MerchantCashMapConstant.merchantCashMap!=null){
            if (StringGeneralUtil.checkNotNull(qrPaymentData.getCashId())){
                outletsOrderPayTrace.setMerchName(MerchantCashMapConstant.merchantCashMap.get(qrPaymentData.getCashId()));
            }
        }
        outletsOrderPayTrace.setRefTraceNo(outletsOrderRefTrace.getTrxid());
        outletsOrderPayTrace.setTraceNo(IdWorker.getIdStr());
        outletsOrderPayTrace.setCashId(qrPaymentData.getCashId());
        outletsOrderPayTrace.setRefundAmount(0);
        outletsOrderPayTrace.setChannelApi(channelApi);
        outletsOrderPayTrace.setPayTime(outletsOrderRefTrace.getFintime());
        outletsOrderPayTrace.setCreateAt(new Date());
        outletsOrderPayTrace.setUpdateAt(new Date());
        outletsOrderPayTraceService.save(outletsOrderPayTrace);


        outletsOrderRefTrace.setTrxcodeDescribe(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefTrace.getTrxcode()));
        outletsOrderRefTraceService.save(outletsOrderRefTrace);
    }

    @Override
    public void refund(List<OutletsOrderPayTrace> outletsOrderPayTraces, OutletsRefundData outletsRefundData) throws Exception {
        long refundAmount = Long.parseLong(outletsRefundData.getRefundAmount());

        OutletsOrderPayTrace outletsOrderPayTrace = outletsOrderPayTraces.get(0);
        long canRefundAmount = outletsOrderPayTrace.getAmount() - outletsOrderPayTrace.getRefundAmount();

        if (!CardOrderPayTraceStateConfig.PAID.equals(outletsOrderPayTrace.getState())
                && !CardOrderPayTraceStateConfig.REFUND.equals(outletsOrderPayTrace.getState())) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ZERO_ERROR);
        }

        if (canRefundAmount < refundAmount) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ZERO_ERROR);
        }

        if (canRefundAmount <= 0) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ZERO_ERROR);
        }

        String transferRefundCode;
        String oriOrderCode;
        //不同支付公司传递的订单号不一样
        String channelApi = outletsOrderPayTrace.getChannelApi();
        if (PayCompanyTypeEnum.JLPAY.getPayCompany().equals(channelApi)){
            transferRefundCode = OrderCodeIntercept.reserveThirtyOne(outletsRefundData.getRefundCode());
            oriOrderCode = outletsOrderPayTrace.getRefBatchCode();
        }else {
            transferRefundCode = outletsRefundData.getRefundCode();
            oriOrderCode = outletsOrderPayTrace.getOrderCode();
        }

        String serviceType;

        Map<String, String> refundMap;
        if (CardOrderPayTraceTypeConfig.CREDIT_CARD.equals(outletsOrderPayTrace.getType())) {

            //判断是否开启pos退款
            if (!"yes".equals(CompanyPayWeight.ifOpenPos)){
                throw new CheckException(ResultTypeEnum.REFUND_ERROR.getCode(), "通联支付退款失败");
            }

            PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(StringGeneralUtil.checkNotNull(outletsOrderPayTrace.getChannelApi()) ? outletsOrderPayTrace.getChannelApi() : PayCompanyTypeEnum.ALLINPAY.getPayCompany());
            refundMap = payCompanyStrategy.posRefund(refundAmount + "",
                    outletsOrderPayTrace.getPayCode(),
                    outletsRefundData.getRefundCode(),
                    "pos退款");
//            refundMap = sybPayService.posRefund(refundAmount + "",
//                    outletsOrderPayTrace.getPayCode(),
//                    outletsRefundData.getRefundCode(),
//                    "pos退款");
            serviceType = "pos";
        } else if (CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY.equals(outletsOrderPayTrace.getType())) {
            PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(outletsOrderPayTrace.getChannelApi());
            refundMap = payCompanyStrategy.refund(refundAmount, transferRefundCode, outletsOrderPayTrace.getRefTraceNo(), oriOrderCode);
//            refundMap = sybPayService.refund(refundAmount, outletsRefundData.getRefundCode(), outletsOrderPayTrace.getRefTraceNo(), outletsOrderPayTrace.getOrderCode());
            serviceType = "qrCode";
        } else if (CardOrderPayTraceTypeConfig.DIGITAL_RMB.equals(outletsOrderPayTrace.getType())){
            throw new CheckException(ResultTypeEnum.DIGITAL_RMB_REFUND_ERROR);
//            refundMap = sybPayService.posRefund(refundAmount + "",
//                    outletsOrderPayTrace.getPayCode(),
//                    outletsRefundData.getRefundCode(),
//                    "pos退款");
//            serviceType = "pos";
        }else {
            throw new CheckException(ResultTypeEnum.REFUND_ERROR);
        }

        logger.info("支付机构响应的退款数据为:" + refundMap);

        if ("SUCCESS".equals(refundMap.get("retcode"))) {
            if ("0000".equals(refundMap.get("trxstatus"))) {
                // 订单数据处理
                updateStateByOrderCode(outletsRefundData.getOrderCode(), CardOrdersStateConfig.REFUND);
                outletsOrderDetailsService.updateStateByOrderCode(outletsRefundData.getOrderCode(), CardOrdersStateConfig.REFUND);
                outletsOrderPayTraceService.updateStateAndRefundAmountByOrderCode(outletsRefundData.getOrderCode(), CardOrdersStateConfig.REFUND, refundAmount);

                // 退款订单保存

                //转化通联响应的退款数据实体
                OutletsOrderRefRefundCancel outletsOrderRefRefund = new OutletsOrderRefRefundCancel();
                BeanMap beanMap = BeanMap.create(outletsOrderRefRefund);
                beanMap.putAll(refundMap);
                outletsOrderRefRefund.setReqsn(outletsRefundData.getRefundCode());
                //保存通联响应的退款数据
                outletsOrderRefRefund.setTrxcodeDescribe(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefRefund.getTrxcode()));
                outletsOrderRefRefund.setBusinessType(CardOrderPayTraceStateConfig.REFUND);
                outletsOrderRefRefund.setServiceType(serviceType);
                if ("pos".equals(serviceType)){
                    outletsOrderRefRefund.setReqsn(outletsRefundData.getRefundCode());
                }
                outletsOrderRefRefund.setAmount(refundAmount);
                outletsOrderRefRefund.setFee(StringGeneralUtil.checkNotNull(outletsOrderRefRefund.getFee()) ? outletsOrderRefRefund.getFee() : "0");
                outletsOrderRefRefund.setRefReqsn(transferRefundCode);
                outletsOrderRefRefundCancelService.save(outletsOrderRefRefund);

                //保存我方退款订单
                OutletsOrderRefundCancel outletsOrderRefund = new OutletsOrderRefundCancel();
                outletsOrderRefund.setRefundCancelCode(outletsRefundData.getRefundCode());
                outletsOrderRefund.setOriOrderCode(outletsRefundData.getOrderCode());
                outletsOrderRefund.setBackOrderCode(IdWorker.getIdStr());
                outletsOrderRefund.setMerchantCode(outletsOrderPayTrace.getMerchantCode());
                outletsOrderRefund.setMerchId(outletsOrderPayTrace.getMerchId());
                outletsOrderRefund.setMerchName(outletsOrderPayTrace.getMerchName());
                outletsOrderRefund.setCashId(outletsOrderPayTrace.getCashId());
                outletsOrderRefund.setTranNo(outletsOrderPayTrace.getTraceNo());
                outletsOrderRefund.setSourceId(outletsOrderPayTrace.getSourceId());
                outletsOrderRefund.setAmount(refundAmount);
                outletsOrderRefund.setState("success");
                outletsOrderRefund.setExt1(outletsRefundData.getOperator());
                outletsOrderRefund.setCreateAt(new Date());
                outletsOrderRefund.setUpdateAt(new Date());
                outletsOrderRefund.setBusinessType(CardOrderPayTraceStateConfig.REFUND);
                outletsOrderRefund.setServiceType(serviceType);
                outletsOrderRefund.setChannelApi(outletsOrderPayTrace.getChannelApi());

                outletsOrderRefund.setExt3(transferRefundCode);
                if (StringGeneralUtil.checkNotNull(refundMap.get("newSysReqsn"))){
                    outletsOrderRefund.setExt2(refundMap.get("newSysReqsn"));
                }

                if ("qrCode".equals(serviceType)){
                    OutletsOrderRefTrace outletsOrderRefTrace = outletsOrderRefTraceService.queryByReqsn(outletsOrderPayTrace.getOrderCode());
                    if (outletsOrderRefTrace != null){
                        outletsOrderRefund.setPayTrxcode(outletsOrderRefTrace.getTrxcode());
                        outletsOrderRefund.setPayTrxcodeDescribe(outletsOrderRefTrace.getTrxcodeDescribe());
                    }
                }else {
                    outletsOrderRefund.setPayTrxcode(outletsOrderPayTrace.getType());
                    outletsOrderRefund.setPayTrxcodeDescribe(outletsOrderPayTrace.getSource());
                }

                outletsOrderRefundCancelService.save(outletsOrderRefund);
            } else {
                throw new CheckException(ResultTypeEnum.REFUND_ERROR.getCode(), refundMap.get("errmsg"));
            }
        } else {
            throw new CheckException(ResultTypeEnum.REFUND_ERROR.getCode(), refundMap.get("retmsg"));
        }
    }

    @Override
    public void orderCancel(OutletsOrderCancelData cancelData) throws Exception {
        List<OutletsOrderPayTrace> outletsOrderPayTraces = outletsOrderPayTraceService.queryTraceByOrderCode(cancelData.getOrderCode());
        OutletsOrderPayTrace outletsOrderPayTrace = outletsOrderPayTraces.get(0);

        String transferCancelCode;
        String oriOrderCode;
        //不同支付公司传递的订单号不一样
        String channelApi = outletsOrderPayTrace.getChannelApi();
        if (PayCompanyTypeEnum.JLPAY.getPayCompany().equals(channelApi)){
            transferCancelCode = OrderCodeIntercept.reserveThirtyOne(cancelData.getCancelCode());
            oriOrderCode = outletsOrderPayTrace.getRefBatchCode();
        }else {
            transferCancelCode = cancelData.getCancelCode();
            oriOrderCode = outletsOrderPayTrace.getOrderCode();
        }

        String serviceType;

        Map<String, String> cancelMap;
        if (CardOrderPayTraceTypeConfig.CREDIT_CARD.equals(outletsOrderPayTrace.getType())) {

            //判断是否开启pos退款
            if (!"yes".equals(CompanyPayWeight.ifOpenPos)){
                throw new CheckException(ResultTypeEnum.REFUND_ERROR.getCode(), "通联支付退款失败");
            }

            PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(StringGeneralUtil.checkNotNull(outletsOrderPayTrace.getChannelApi()) ? outletsOrderPayTrace.getChannelApi() : PayCompanyTypeEnum.ALLINPAY.getPayCompany());
            cancelMap = payCompanyStrategy.posRefund(outletsOrderPayTrace.getAmount() + "",
                    outletsOrderPayTrace.getPayCode(),
                    cancelData.getCancelCode(),
                    "pos撤销");
//            cancelMap = sybPayService.posRefund(outletsOrderPayTrace.getAmount() + "",
//                    outletsOrderPayTrace.getPayCode(),
//                    cancelData.getCancelCode(),
//                    "pos撤销");
            serviceType = "pos";
        } else if (CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY.equals(outletsOrderPayTrace.getType())) {
            PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(outletsOrderPayTrace.getChannelApi());
            cancelMap = payCompanyStrategy.cancel(outletsOrderPayTrace.getAmount(),
                    transferCancelCode, outletsOrderPayTrace.getRefTraceNo(), oriOrderCode);
//
//            cancelMap = sybPayService.cancel(outletsOrderPayTrace.getAmount(),
//                    cancelData.getCancelCode(), outletsOrderPayTrace.getRefTraceNo(), outletsOrderPayTrace.getOrderCode());
            serviceType = "qrCode";
        } else if (CardOrderPayTraceTypeConfig.DIGITAL_RMB.equals(outletsOrderPayTrace.getType())){
            throw new CheckException(ResultTypeEnum.DIGITAL_RMB_REFUND_ERROR);
//            cancelMap = sybPayService.cancel(outletsOrderPayTrace.getAmount(),
//                    cancelData.getCancelCode(), outletsOrderPayTrace.getRefTraceNo(), outletsOrderPayTrace.getOrderCode());
//            serviceType = "qrCode";
        }else {
            throw new CheckException(ResultTypeEnum.REFUND_ERROR);
        }

        logger.info("支付公司响应的撤销数据为:" + cancelMap);

        if ("SUCCESS".equals(cancelMap.get("retcode"))) {
            if ("0000".equals(cancelMap.get("trxstatus"))) {
                // 订单数据处理
                updateStateByOrderCode(cancelData.getOrderCode(), CardOrdersStateConfig.CANCEL);
                outletsOrderDetailsService.updateStateByOrderCode(cancelData.getOrderCode(), CardOrdersStateConfig.CANCEL);
                outletsOrderPayTraceService.updateStateAndRefundAmountByOrderCode(cancelData.getOrderCode(), CardOrdersStateConfig.CANCEL, outletsOrderPayTrace.getAmount());

                // 退款订单保存

                //转化响应的撤销数据实体
                OutletsOrderRefRefundCancel outletsOrderRefCancel = new OutletsOrderRefRefundCancel();
                BeanMap beanMap = BeanMap.create(outletsOrderRefCancel);
                beanMap.putAll(cancelMap);
                outletsOrderRefCancel.setReqsn(cancelData.getCancelCode());
                //保存响应的撤销数据
                outletsOrderRefCancel.setTrxcodeDescribe(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefCancel.getTrxcode()));
                outletsOrderRefCancel.setBusinessType(CardOrderPayTraceStateConfig.CANCEL);
                outletsOrderRefCancel.setServiceType(serviceType);
                if ("pos".equals(serviceType)){
                    outletsOrderRefCancel.setReqsn(cancelData.getCancelCode());
                }
                outletsOrderRefCancel.setAmount(Long.parseLong(outletsOrderPayTrace.getAmount() + ""));
                outletsOrderRefCancel.setFee(StringGeneralUtil.checkNotNull(outletsOrderRefCancel.getFee()) ? outletsOrderRefCancel.getFee() : "0");
                outletsOrderRefCancel.setRefReqsn(transferCancelCode);
                outletsOrderRefRefundCancelService.save(outletsOrderRefCancel);

                //保存我方撤销订单
                OutletsOrderRefundCancel outletsOrderCancel = new OutletsOrderRefundCancel();
                outletsOrderCancel.setRefundCancelCode(cancelData.getCancelCode());
                outletsOrderCancel.setOriOrderCode(cancelData.getOrderCode());
                outletsOrderCancel.setBackOrderCode(IdWorker.getIdStr());
                outletsOrderCancel.setMerchantCode(outletsOrderPayTrace.getMerchantCode());
                outletsOrderCancel.setMerchId(outletsOrderPayTrace.getMerchId());
                outletsOrderCancel.setMerchName(outletsOrderPayTrace.getMerchName());
                outletsOrderCancel.setCashId(outletsOrderPayTrace.getCashId());
                outletsOrderCancel.setTranNo(outletsOrderPayTrace.getTraceNo());
                outletsOrderCancel.setSourceId(outletsOrderPayTrace.getSourceId());
                outletsOrderCancel.setAmount(Long.parseLong(outletsOrderPayTrace.getAmount() + ""));
                outletsOrderCancel.setState("success");
                outletsOrderCancel.setExt1(cancelData.getOperator());
                outletsOrderCancel.setCreateAt(new Date());
                outletsOrderCancel.setUpdateAt(new Date());
                outletsOrderCancel.setBusinessType(CardOrderPayTraceStateConfig.CANCEL);
                outletsOrderCancel.setServiceType(serviceType);
                outletsOrderCancel.setChannelApi(outletsOrderPayTrace.getChannelApi());

                outletsOrderCancel.setExt3(transferCancelCode);
                if (StringGeneralUtil.checkNotNull(cancelMap.get("newSysReqsn"))){
                    outletsOrderCancel.setExt2(cancelMap.get("newSysReqsn"));
                }

                if ("qrCode".equals(serviceType)){
                    OutletsOrderRefTrace outletsOrderRefTrace = outletsOrderRefTraceService.queryByReqsn(outletsOrderPayTrace.getOrderCode());
                    if (outletsOrderRefTrace != null){
                        outletsOrderCancel.setPayTrxcode(outletsOrderRefTrace.getTrxcode());
                        outletsOrderCancel.setPayTrxcodeDescribe(outletsOrderRefTrace.getTrxcodeDescribe());
                    }
                }else {
                    outletsOrderCancel.setPayTrxcode(outletsOrderPayTrace.getType());
                    outletsOrderCancel.setPayTrxcodeDescribe(outletsOrderPayTrace.getSource());
                }
                outletsOrderCancel.setExt3(transferCancelCode);
                outletsOrderRefundCancelService.save(outletsOrderCancel);
            } else {
                throw new CheckException(ResultTypeEnum.REFUND_ERROR.getCode(), cancelMap.get("errmsg"));
            }
        } else {
            throw new CheckException(ResultTypeEnum.REFUND_ERROR.getCode(), cancelMap.get("retmsg"));
        }
    }

    @Override
    public IPage<OutletsOrdersVO> findPageLeftJoinPayTrace(IPage<OutletsOrders> page, Map<String, String> paramsMap) {

        String orderCode = paramsMap.get("orderCode");
        String type = paramsMap.get("type");
        String state = paramsMap.get("state");
        String actualPhone = paramsMap.get("actualPhone");
        String sourceId = paramsMap.get("sourceId");
        String cashId = paramsMap.get("cashId");
        String payCode = paramsMap.get("payCode");
        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");

        if (StringGeneralUtil.checkNotNull(startCreateAt)) {
            startCreateAt +=  " 00:00:01";
        }
        if (StringGeneralUtil.checkNotNull(endCreateAt)) {
            endCreateAt += " 23:59:59";
        }

        QueryWrapper<OutletsOrders> wrapper = new QueryWrapper();
        wrapper.eq(!StringUtils.isEmpty(orderCode), "oo.order_code", orderCode);
        wrapper.eq(!StringUtils.isEmpty(type), "oo.type", type);
        wrapper.eq(!StringUtils.isEmpty(state), "oo.state", state);
        wrapper.eq(!StringUtils.isEmpty(actualPhone), "oo.actual_phone", actualPhone);
        wrapper.like(!StringUtils.isEmpty(sourceId), "oopt.source_id", sourceId);
        wrapper.eq(!StringUtils.isEmpty(cashId), "oopt.cash_id", cashId);
        if (!StringUtils.isEmpty(payCode)) {
            wrapper.eq("oopt.pay_code", payCode)
                    .or()
                    .eq("oopt.ref_trace_no", payCode);
        }
        wrapper.between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt), "oo.create_at", startCreateAt, endCreateAt);
        //新增查询限制2023年的
        wrapper.ge("oo.create_at","2023-05-01 00:00:01");
        wrapper.orderByDesc("oo.id");

        IPage<OutletsOrders> result = this.baseMapper.findPageLeftJoinPayTrace(page, wrapper);
        return transferToPageDataVo(result);
    }

    public IPage<OutletsOrdersVO> transferToPageDataVo(IPage<OutletsOrders> resultPage) {

        IPage<OutletsOrdersVO> outletsOrdersIPage = new Page<>();
        List<OutletsOrdersVO> recordVos = new ArrayList<>();
        outletsOrdersIPage.setPages(resultPage.getPages());
        outletsOrdersIPage.setTotal(resultPage.getTotal());
        outletsOrdersIPage.setSize(resultPage.getSize());
        outletsOrdersIPage.setCurrent(resultPage.getCurrent());
        outletsOrdersIPage.setRecords(recordVos);
        if (null != resultPage && resultPage.getRecords().size() > 0) {
            List<OutletsOrders> records = resultPage.getRecords();
            for (int i = 0; i < records.size(); i++) {
                OutletsOrders record = records.get(i);
                String orderCode = record.getOrderCode();
                OutletsOrdersVO recordDataVo = new OutletsOrdersVO();
                BigDecimal actualAmount = new BigDecimal("0.0");
                Map<String,String> paramsMap = new HashMap<>();
                paramsMap.put("orderCode", orderCode);
                List<OutletsOrderPayTraceVO> outletsOrderPayTraceVOS = outletsOrderPayTraceService.findlist(paramsMap);
                for (OutletsOrderPayTraceVO outletsOrderPayTraceVO : outletsOrderPayTraceVOS) {
                    if (!StringUtils.isEmpty(outletsOrderPayTraceVO.getAmount())) {
                        actualAmount = actualAmount.add(outletsOrderPayTraceVO.getAmount());
                    }
                }
                BeanUtils.copyProperties(record, recordDataVo);
                recordDataVo.setActualAmount(actualAmount);
                if (null != outletsOrderPayTraceVOS.get(0)) {
                    recordDataVo.setCashId(outletsOrderPayTraceVOS.get(0).getCashId());
                    recordDataVo.setMerchName(outletsOrderPayTraceVOS.get(0).getMerchName());
                }
                if (null != record.getAmount()) {
                    recordDataVo.setAmount(MoneyUtils.changeF2YBigDecimal(record.getAmount()));
                }
                if (null != record.getDiscount()) {
                    recordDataVo.setDiscount(MoneyUtils.changeF2YBigDecimal(record.getDiscount()));
                }
                recordVos.add(recordDataVo);
            }
        }
        return outletsOrdersIPage;

    }


}
