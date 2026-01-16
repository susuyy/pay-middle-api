package com.ht.user.outlets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.config.*;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.config.TrxCodeDescribeEnum;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.excel.OutletsOrderPayTraceExcelVO;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.mapper.OutletsOrderPayTraceMapper;
import com.ht.user.outlets.paystrategy.PayCompanyStrategy;
import com.ht.user.outlets.paystrategy.PayCompanyStrategyFactory;
import com.ht.user.outlets.paystrategy.PayCompanyTypeEnum;
import com.ht.user.outlets.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.outlets.service.IOutletsOrdersGoodsService;
import com.ht.user.outlets.service.IOutletsOrdersService;
import com.ht.user.outlets.util.DateStrUtil;
import com.ht.user.outlets.util.MoneyUtils;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.ht.user.outlets.vo.OutletsOrderPayTraceVO;
import com.ht.user.result.ResultTypeEnum;
import com.ht.user.utils.MyMathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 订单支付流水 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Service
public class OutletsOrderPayTraceServiceImpl extends ServiceImpl<OutletsOrderPayTraceMapper, OutletsOrderPayTrace> implements IOutletsOrderPayTraceService {

    private Logger logger = LoggerFactory.getLogger(OutletsOrderPayTraceServiceImpl.class);

    @Autowired
    private IOutletsOrdersService outletsOrdersService;

    @Autowired
    private IOutletsOrderDetailsService outletsOrderDetailsService;

    @Autowired
    private IOutletsOrdersGoodsService outletsOrdersGoodsService;

    @Autowired
    private IOutletsOrderPosRefTraceService outletsOrderPosRefTraceService;

    @Autowired
    private IOutletsOrderRefundCancelService outletsOrderRefundCancelService;

    @Autowired
    private SybPayService sybPayService;

    @Autowired
    private IOutletsOrderRefTraceService outletsOrderRefTraceService;

    @Autowired
    private IOutletsOrderPayTraceService outletsOrderPayTraceService;

    @Autowired
    private IOutletsOrderRefRefundCancelService outletsOrderRefRefundCancelService;

    @Override
    public List<OutletsOrderPayTrace> queryPayTrace(SearchTraceData searchTraceData) {
        LambdaQueryWrapper<OutletsOrderPayTrace> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        if (StringGeneralUtil.checkNotNull(searchTraceData.getOrderCode())){
            lambdaQueryWrapper.eq(OutletsOrderPayTrace::getOrderCode,searchTraceData.getOrderCode());
        }
        if (StringGeneralUtil.checkNotNull(searchTraceData.getStoreCode())){
            lambdaQueryWrapper.eq(OutletsOrderPayTrace::getMerchantCode,searchTraceData.getStoreCode());
        }
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public void updateStateByOrderCode(String orderCode, String state) {
        this.baseMapper.updateStateByOrderCode(orderCode,state,new Date());
    }

    @Override
    public String createPosPayTraceFromCashier(PosPayTraceSuccessData posPayTraceSuccessData) {
        String orderCode = posPayTraceSuccessData.getOrderCode();
        if (StringUtils.isEmpty(orderCode) || "null".equals(orderCode)){
            orderCode = IdWorker.getIdStr();
        }


        OutletsOrders outletsOrders=new OutletsOrders();
        outletsOrders.setAmount(posPayTraceSuccessData.getAmount());
        outletsOrders.setMerchantCode(posPayTraceSuccessData.getMerchId());
        outletsOrders.setOrderCode(orderCode);
        outletsOrders.setDiscount(0);
        outletsOrders.setQuantity(BigDecimal.ONE);
        outletsOrders.setComments(CardOrderCommentConfig.POS_CASH);
        outletsOrders.setCreateAt(new Date());
        outletsOrders.setUpdateAt(new Date());
        outletsOrders.setState(CardOrdersStateConfig.PAID);
        outletsOrders.setType(CardOrdersTypeConfig.CONSUME);
        outletsOrders.setSaleId("POS_ADMIN");
        outletsOrdersService.save(outletsOrders);

        OutletsOrderDetails outletsOrderDetails=new OutletsOrderDetails();
        outletsOrderDetails.setOrderCode(orderCode);
        outletsOrderDetails.setUpdateAt(new Date());
        outletsOrderDetails.setCreateAt(new Date());
        outletsOrderDetails.setProductionName(CardOrderCommentConfig.POS_CASH);
        outletsOrderDetails.setAmount(posPayTraceSuccessData.getAmount());
        outletsOrderDetails.setState(CardOrdersStateConfig.PAID);
        outletsOrderDetails.setType(CardOrdersTypeConfig.CONSUME);
        outletsOrderDetails.setQuantity(BigDecimal.ONE);
        outletsOrderDetails.setDisccount(0);
        outletsOrderDetailsService.save(outletsOrderDetails);

        OutletsOrderPayTrace outletsOrderPayTrace = new OutletsOrderPayTrace();
        outletsOrderPayTrace.setOrderCode(orderCode);
        outletsOrderPayTrace.setAmount(posPayTraceSuccessData.getAmount());
        outletsOrderPayTrace.setCreateAt(new Date());
        outletsOrderPayTrace.setUpdateAt(new Date());
        outletsOrderPayTrace.setPayCode(posPayTraceSuccessData.getTransTicketNo());
        outletsOrderPayTrace.setRefTraceNo(posPayTraceSuccessData.getTraceNo());
        outletsOrderPayTrace.setPosSerialNum(posPayTraceSuccessData.getTerId());
        outletsOrderPayTrace.setSource("银行卡支出-"+posPayTraceSuccessData.getIssName());
        outletsOrderPayTrace.setSourceId(posPayTraceSuccessData.getCardNo());
        outletsOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        outletsOrderPayTrace.setType(CardOrderPayTraceTypeConfig.CREDIT_CARD);
        outletsOrderPayTrace.setMerchId(posPayTraceSuccessData.getMerchId());
        outletsOrderPayTrace.setMerchName(posPayTraceSuccessData.getMerchName());
        outletsOrderPayTrace.setMerchantCode(posPayTraceSuccessData.getMerchId());
        outletsOrderPayTrace.setRefundAmount(0);
        this.baseMapper.insert(outletsOrderPayTrace);

        //创建保存pos支付数据
        OutletsOrderPosRefTrace outletsOrderPosRefTrace = new OutletsOrderPosRefTrace();
        outletsOrderPosRefTrace.setOrderCode(posPayTraceSuccessData.getOrderCode());
        outletsOrderPosRefTrace.setBusinessId(posPayTraceSuccessData.getBusinessId());
        outletsOrderPosRefTrace.setAmount(posPayTraceSuccessData.getAmount()+"");
        outletsOrderPosRefTrace.setTraceNo(posPayTraceSuccessData.getTraceNo());
        outletsOrderPosRefTrace.setExpDate(posPayTraceSuccessData.getExpDate());
        outletsOrderPosRefTrace.setBatchNo(posPayTraceSuccessData.getBatchNo());
        outletsOrderPosRefTrace.setMerchId(posPayTraceSuccessData.getMerchId());
        outletsOrderPosRefTrace.setMerchName(posPayTraceSuccessData.getMerchName());
        outletsOrderPosRefTrace.setTerId(posPayTraceSuccessData.getTerId());
        outletsOrderPosRefTrace.setRefNo(posPayTraceSuccessData.getRefNo());
        outletsOrderPosRefTrace.setAuthNo(posPayTraceSuccessData.getAuthNo());
        outletsOrderPosRefTrace.setRejcode(posPayTraceSuccessData.getRejCode());
        outletsOrderPosRefTrace.setIssName(posPayTraceSuccessData.getIssName());
        outletsOrderPosRefTrace.setCardno(posPayTraceSuccessData.getCardNo());
        outletsOrderPosRefTrace.setRefDate(posPayTraceSuccessData.getDate());
        outletsOrderPosRefTrace.setRefTime(posPayTraceSuccessData.getTime());
        outletsOrderPosRefTrace.setRejcodeCn(posPayTraceSuccessData.getRejCodeCn());
        outletsOrderPosRefTrace.setCardTypeIdenty(posPayTraceSuccessData.getCardTypeIdenty());
        outletsOrderPosRefTrace.setWildCardSign(posPayTraceSuccessData.getWildCardSign());
        outletsOrderPosRefTrace.setTransTicketNo(posPayTraceSuccessData.getTransTicketNo());
        outletsOrderPosRefTrace.setCardtype(posPayTraceSuccessData.getCardtype());
        outletsOrderPosRefTrace.setCreateAt(new Date());
        outletsOrderPosRefTrace.setUpdateAt(new Date());
        outletsOrderPosRefTraceService.save(outletsOrderPosRefTrace);

        return orderCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMisOrderState(PosPayTraceSuccessData posPayTraceSuccessData) throws Exception {
        outletsOrdersService.updateStateChannelByOrderCode(posPayTraceSuccessData.getOrderCode(),
                CardOrderPayTraceStateConfig.PAID,
                StringGeneralUtil.checkNotNull(posPayTraceSuccessData.getChannelApi()) ? posPayTraceSuccessData.getChannelApi() : "AllinPay");
        outletsOrderDetailsService.updateStateByOrderCode(posPayTraceSuccessData.getOrderCode(),CardOrderPayTraceStateConfig.PAID);

        List<OutletsOrderPayTrace> outletsOrderPayTraces = queryTraceByOrderCodeStateType(posPayTraceSuccessData.getOrderCode(),
                CardOrderPayTraceStateConfig.UNPAID,
                CardOrderPayTraceStateConfig.CLOSE,
                CardOrdersTypeConfig.POS_MIS_ORDER);

        for (OutletsOrderPayTrace outletsOrderPayTrace : outletsOrderPayTraces) {
            outletsOrderPayTrace.setPayCode(posPayTraceSuccessData.getTransTicketNo());
            outletsOrderPayTrace.setRefTraceNo(posPayTraceSuccessData.getTraceNo());
            outletsOrderPayTrace.setPosSerialNum(posPayTraceSuccessData.getTerId());
            outletsOrderPayTrace.setTraceNo(IdWorker.getIdStr());
            if (StringGeneralUtil.checkNotNull(posPayTraceSuccessData.getIssName())){
                outletsOrderPayTrace.setSource("银行卡支出-"+posPayTraceSuccessData.getIssName());
            }else {
                outletsOrderPayTrace.setSource("银行卡支出");
            }
            outletsOrderPayTrace.setType(CardOrderPayTraceTypeConfig.CREDIT_CARD);
            outletsOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
            outletsOrderPayTrace.setAmount(posPayTraceSuccessData.getAmount());
            outletsOrderPayTrace.setFee(posPayTraceSuccessData.getFee() != null ? posPayTraceSuccessData.getFee() : 0);
            if (StringGeneralUtil.checkNotNull(posPayTraceSuccessData.getCardNo())){
                outletsOrderPayTrace.setSourceId(posPayTraceSuccessData.getCardNo());
            }else {
                try {

                    PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(StringGeneralUtil.checkNotNull(posPayTraceSuccessData.getChannelApi()) ? posPayTraceSuccessData.getChannelApi() : PayCompanyTypeEnum.ALLINPAY.getPayCompany());
                    Map<String, String> queryMap = payCompanyStrategy.posOlQuery("", posPayTraceSuccessData.getTransTicketNo());
//                    Map<String, String> queryMap = sybPayService.posOlQuery("", posPayTraceSuccessData.getTransTicketNo());
                    outletsOrderPayTrace.setSourceId(queryMap.get("acct"));
                    posPayTraceSuccessData.setPayTime(queryMap.get("fintime"));
                } catch (Exception e) {
                    outletsOrderPayTrace.setSourceId("not record card");
                }
            }
            outletsOrderPayTrace.setChannelApi(StringGeneralUtil.checkNotNull(posPayTraceSuccessData.getChannelApi()) ? posPayTraceSuccessData.getChannelApi() : PayCompanyTypeEnum.ALLINPAY.getPayCompany());
            outletsOrderPayTrace.setPayTime(StringGeneralUtil.checkNotNull(posPayTraceSuccessData.getPayTime()) ? posPayTraceSuccessData.getPayTime() : DateStrUtil.nowDateStrToyyyyMMddHHmmss());
            updateById(outletsOrderPayTrace);
        }




        // 创建金额支付流水
//        OutletsOrderPayTrace outletsOrderPayTrace = new OutletsOrderPayTrace();
//        if (outletsOrderPayTraces!=null && outletsOrderPayTraces.size()>0){
//            OutletsOrderPayTrace outletsOrderPayTraceOriMis = outletsOrderPayTraces.get(0);
//
//            outletsOrderPayTrace.setPayCode(posPayTraceSuccessData.getTransTicketNo());
//            outletsOrderPayTrace.setRefTraceNo(posPayTraceSuccessData.getTraceNo());
//
//            outletsOrderPayTrace.setOrderDetailId(outletsOrderPayTraceOriMis.getOrderDetailId());
//
//            outletsOrderPayTrace.setPosSerialNum(posPayTraceSuccessData.getTerId());
//            outletsOrderPayTrace.setCashId(outletsOrderPayTraceOriMis.getCashId());
//
//            outletsOrderPayTrace.setMerchId(outletsOrderPayTraceOriMis.getMerchId());
//            outletsOrderPayTrace.setMerchName(outletsOrderPayTraceOriMis.getMerchName());
//            outletsOrderPayTrace.setMerchantCode(outletsOrderPayTraceOriMis.getMerchantCode());
//
//            this.baseMapper.deleteById(outletsOrderPayTraceOriMis);
//        }else {
//            OutletsOrderPayTrace orderPayTrace = queryTraceByOrderCode(posPayTraceSuccessData.getOrderCode()).get(0);
//
//            outletsOrderPayTrace.setPayCode(posPayTraceSuccessData.getTransTicketNo());
//            outletsOrderPayTrace.setRefTraceNo(posPayTraceSuccessData.getTraceNo());
//
//            outletsOrderPayTrace.setOrderDetailId(orderPayTrace.getOrderDetailId());
//
//            outletsOrderPayTrace.setPosSerialNum(posPayTraceSuccessData.getTerId());
//            outletsOrderPayTrace.setCashId(orderPayTrace.getCashId());
//
//            outletsOrderPayTrace.setMerchId(orderPayTrace.getMerchId());
//            outletsOrderPayTrace.setMerchName(orderPayTrace.getMerchName());
//            outletsOrderPayTrace.setMerchantCode(orderPayTrace.getMerchantCode());
//        }
//
//        outletsOrderPayTrace.setTraceNo(IdWorker.getIdStr());
//
//        OutletsOrders outletsOrders = outletsOrdersService.queryByOrderCodeNotMany(posPayTraceSuccessData.getOrderCode());
//
//        if (CardOrderPayTraceTypeConfig.DIGITAL_RMB.equals(outletsOrders.getLimitPayType())){
//            outletsOrderPayTrace.setSource("数字货币-"+posPayTraceSuccessData.getIssName());
//            outletsOrderPayTrace.setType(CardOrderPayTraceTypeConfig.DIGITAL_RMB);
//        }else{
//            outletsOrderPayTrace.setSource("银行卡支出-"+posPayTraceSuccessData.getIssName());
//            outletsOrderPayTrace.setType(CardOrderPayTraceTypeConfig.CREDIT_CARD);
//        }
//
//        outletsOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
//        outletsOrderPayTrace.setAmount(posPayTraceSuccessData.getAmount());
//        outletsOrderPayTrace.setOrderCode(posPayTraceSuccessData.getOrderCode());
//        outletsOrderPayTrace.setSourceId(posPayTraceSuccessData.getCardNo());
//
//        outletsOrderPayTrace.setChannelApi(StringGeneralUtil.checkNotNull(posPayTraceSuccessData.getChannelApi()) ? posPayTraceSuccessData.getChannelApi() : "AllinPay");
//        outletsOrderPayTrace.setPayTime(DateStrUtil.nowDateStrToyyyyMMddHHmmss());
//        cardOrderPayTrace.setCreateAt(new Date());
//        cardOrderPayTrace.setUpdateAt(new Date());
        logger.info("创建支付流水定单,订单号为:"+posPayTraceSuccessData.getOrderCode());

        //创建保存pos支付数据
        OutletsOrderPosRefTrace outletsOrderPosRefTraceQuery = outletsOrderPosRefTraceService.queryByOrderCode(posPayTraceSuccessData.getOrderCode());
        if(outletsOrderPosRefTraceQuery==null) {
            OutletsOrderPosRefTrace outletsOrderPosRefTrace = new OutletsOrderPosRefTrace();
            outletsOrderPosRefTrace.setOrderCode(posPayTraceSuccessData.getOrderCode());
            outletsOrderPosRefTrace.setBusinessId(posPayTraceSuccessData.getBusinessId());
            outletsOrderPosRefTrace.setAmount(posPayTraceSuccessData.getAmount() + "");
            outletsOrderPosRefTrace.setFee(posPayTraceSuccessData.getFee() != null ? posPayTraceSuccessData.getFee() : 0);
            outletsOrderPosRefTrace.setTraceNo(posPayTraceSuccessData.getTraceNo());
            outletsOrderPosRefTrace.setExpDate(posPayTraceSuccessData.getExpDate());
            outletsOrderPosRefTrace.setBatchNo(posPayTraceSuccessData.getBatchNo());
            outletsOrderPosRefTrace.setMerchId(posPayTraceSuccessData.getMerchId());
            outletsOrderPosRefTrace.setMerchName(posPayTraceSuccessData.getMerchName());
            outletsOrderPosRefTrace.setTerId(posPayTraceSuccessData.getTerId());
            outletsOrderPosRefTrace.setRefNo(posPayTraceSuccessData.getRefNo());
            outletsOrderPosRefTrace.setAuthNo(posPayTraceSuccessData.getAuthNo());
            outletsOrderPosRefTrace.setRejcode(posPayTraceSuccessData.getRejCode());
            outletsOrderPosRefTrace.setIssName(posPayTraceSuccessData.getIssName());
            outletsOrderPosRefTrace.setCardno(posPayTraceSuccessData.getCardNo());
            outletsOrderPosRefTrace.setRefDate(posPayTraceSuccessData.getDate());
            outletsOrderPosRefTrace.setRefTime(posPayTraceSuccessData.getTime());
            outletsOrderPosRefTrace.setRejcodeCn(posPayTraceSuccessData.getRejCodeCn());
            outletsOrderPosRefTrace.setCardTypeIdenty(posPayTraceSuccessData.getCardTypeIdenty());
            outletsOrderPosRefTrace.setWildCardSign(posPayTraceSuccessData.getWildCardSign());
            outletsOrderPosRefTrace.setCardtype(posPayTraceSuccessData.getCardtype());
            outletsOrderPosRefTrace.setTransTicketNo(posPayTraceSuccessData.getTransTicketNo());
            outletsOrderPosRefTrace.setCreateAt(new Date());
            outletsOrderPosRefTrace.setUpdateAt(new Date());
            outletsOrderPosRefTraceService.save(outletsOrderPosRefTrace);
        }
    }

    @Override
    public List<OutletsOrderPayTrace> queryTraceByOrderCodeStateType(String orderCode, String state,String stateOr, String type) {
        QueryWrapper<OutletsOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
//        queryWrapper.eq("state",state);

        queryWrapper.and(traceQueryWrapper-> traceQueryWrapper.eq("state",state)
                .or()
                .eq("state",stateOr));

        queryWrapper.eq("type",type);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<OutletsOrderPayTrace> queryTraceByOrderCode(String orderCode) {
        QueryWrapper<OutletsOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void updateStateAndRefundAmountByOrderCode(String orderCode, String state, long refundAmount) {
        this.baseMapper.updateStateRefundAmountByOrderCode(orderCode,state,new Date(),refundAmount);
    }

    @Override
    public void updatePayCodeByOrderCode(String payCode, String orderCode,String payTime,Integer fee) {
        this.baseMapper.updatePayCodeByOrderCode(payCode,orderCode,payTime,fee);
    }

    @Override
    public List<OutletsOrderPayTraceVO> findlist(Map<String, String> paramsMap) {

        String orderCode = paramsMap.get("orderCode");
        String cashId = paramsMap.get("cashId");
        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");
        String startTime = paramsMap.get("startTime");
        String endTime = paramsMap.get("endTime");
        String state = paramsMap.get("state");

        List<String> stateList = new ArrayList();
        stateList.add(CardOrderPayTraceStateConfig.PAID);
        stateList.add(CardOrderPayTraceStateConfig.REFUND);
        stateList.add(CardOrderPayTraceStateConfig.CANCEL);


        LambdaQueryWrapper<OutletsOrderPayTrace> lambda = new QueryWrapper<OutletsOrderPayTrace>().lambda();
        lambda.eq(!StringUtils.isEmpty(orderCode), OutletsOrderPayTrace::getOrderCode, orderCode);
        lambda.eq(!StringUtils.isEmpty(cashId), OutletsOrderPayTrace::getCashId, cashId);
        lambda.between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt), OutletsOrderPayTrace::getCreateAt, startCreateAt, endCreateAt);
        lambda.between(!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime), OutletsOrderPayTrace::getCreateAt, startTime, endTime);
        lambda.in("excel".equals(state), OutletsOrderPayTrace::getState, stateList);
        lambda.orderByDesc(OutletsOrderPayTrace::getId);

        List<OutletsOrderPayTrace> result = this.list(lambda);
        return transferToListDataVo(result);
    }


    public List<OutletsOrderPayTraceVO> transferToListDataVo(List<OutletsOrderPayTrace> resultList) {

        List<OutletsOrderPayTraceVO> recordVos = new ArrayList<>();
        if(null != resultList && resultList.size()>0){
            for (int i = 0; i < resultList.size(); i++) {
                OutletsOrderPayTrace record = resultList.get(i);
                OutletsOrderPayTraceVO recordDataVo = new OutletsOrderPayTraceVO();
                BeanUtils.copyProperties(record,recordDataVo);
                if (null != record.getAmount()) {
                    recordDataVo.setAmount(MoneyUtils.changeF2YBigDecimal(record.getAmount()));
                }
                if (null != record.getRefundAmount()) {
                    recordDataVo.setRefundAmount(MoneyUtils.changeF2YBigDecimal(record.getRefundAmount()));
                }
                recordVos.add(recordDataVo);
            }
        }
        return recordVos;

    }

    @Override
    public List<Map<String, Object>> countSum(Map<String, String> paramsMap) {
        List<Map<String, Object>> newMapList = new ArrayList<>();

        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");

        if (StringUtils.isEmpty(startCreateAt)){
            startCreateAt = DateStrUtil.nowDateStrYearMoonDay();
        } else {
            startCreateAt = startCreateAt + " 00:00:00";
        }
        if (StringUtils.isEmpty(endCreateAt)){
            endCreateAt = DateStrUtil.nowDateStrYearMoonDay();
            endCreateAt = endCreateAt + " 23:59:59";
        } else {
            endCreateAt = endCreateAt + " 23:59:59";
        }

        //只统计已支付，已退款和已撤销状态下的支付流水
        List<String> stateList = new ArrayList<>();
        stateList.add(CardOrderPayTraceStateConfig.PAID);
        stateList.add(CardOrderPayTraceStateConfig.REFUND);
        stateList.add(CardOrderPayTraceStateConfig.CANCEL);

        QueryWrapper<OutletsOrderPayTrace> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("type as 'type',IFNULL(sum(amount),0) as 'amountSum',IFNULL(sum(fee),0) as 'feeSum'")
                .in("state", stateList)
                .between("create_at", startCreateAt, endCreateAt)
                .groupBy("type");

        List<Map<String, Object>> mapList = this.listMaps(queryWrapper);
        List<Map<String, Object>> refundMapList = outletsOrderRefundCancelService.countRefundAmountByServiceType(startCreateAt, endCreateAt);
        List<Map<String, Object>> refundFeeMapList = outletsOrderRefRefundCancelService.countRefundFeeByServiceType(startCreateAt, endCreateAt);


        //初始化数据
        for (int i = 0; i < 2; i++) {
            Map<String, Object> newMap = new HashMap<>();
            String type = "";
            String serviceType = "";
            BigDecimal amountSum = new BigDecimal("0.0");
            BigDecimal feeSum = new BigDecimal("0.0");
            BigDecimal refundAmountSum = new BigDecimal("0.0");
            BigDecimal refundFeeSum = new BigDecimal("0.0");
            //实际交易金额
            BigDecimal cumulativeAmountSum = new BigDecimal("0.0");
            //清算金额合计
            BigDecimal liquidationSum = new BigDecimal("0.0");

            switch (i) {
                //扫码支付
                case 0:
                    type = CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY;
                    serviceType = "qrCode";
                    break;
                //刷卡支付
                case 1:
                    type = CardOrderPayTraceTypeConfig.CREDIT_CARD;
                    serviceType = "pos";
                    break;
                default:
                    break;
            }

            //获得当前循环下的type类型对应的真实的amountSum,feeSum
            for (Map<String, Object> map : mapList) {
                if (type.equals(map.get("type"))) {
                    BigDecimal amount = (BigDecimal) map.get("amountSum");
                    amountSum = MoneyUtils.changeBigDecimalF2YBigDecimal(amount);
                    BigDecimal fee = (BigDecimal) map.get("feeSum");
                    feeSum = MoneyUtils.changeBigDecimalF2YBigDecimal(fee);
                }
            }
            //获得当前循环下的serviceType类型对应的真实的refundAmountSum
            for (Map<String, Object> map : refundMapList) {
                if (serviceType.equals(map.get("serviceType"))) {
                    BigDecimal amount = (BigDecimal) map.get("refundAmountSum");
                    refundAmountSum = MoneyUtils.changeBigDecimalF2YBigDecimal(amount);
                }
            }
            //获得当前循环下的serviceType类型对应的真实的refundFeeSum
            for (Map<String, Object> map : refundFeeMapList) {
                if (serviceType.equals(map.get("serviceType"))) {
                    BigDecimal fee = new BigDecimal((Double) map.get("refundFeeSum"));
                    refundFeeSum = MoneyUtils.changeBigDecimalF2YBigDecimal(fee);
                }
            }

            cumulativeAmountSum = amountSum.subtract(refundAmountSum);
            liquidationSum = amountSum.subtract(feeSum).subtract(refundAmountSum).add(refundFeeSum);
            newMap.put("type", type);
            newMap.put("amountSum", amountSum);
            newMap.put("feeSum", feeSum);
            newMap.put("refundAmountSum", refundAmountSum);
            newMap.put("refundFeeSum", refundFeeSum);
            newMap.put("cumulativeAmountSum", MyMathUtil.KeepTwoDecimalPlaces(cumulativeAmountSum));
            newMap.put("liquidationSum", MyMathUtil.KeepTwoDecimalPlaces(liquidationSum));
            newMapList.add(newMap);

        }

        logger.info("countSum newMapList={}",newMapList);
        return newMapList;

    }

    @Override
    public List<Map<String, Object>> countAmountSumGroupByMerchId(Map<String, String> paramsMap) {
        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");

        if (StringUtils.isEmpty(startCreateAt)){
            startCreateAt = DateStrUtil.nowDateStrYearMoonDay();
        } else {
            startCreateAt = startCreateAt + " 00:00:00";
        }
        if (StringUtils.isEmpty(endCreateAt)){
            endCreateAt = DateStrUtil.nowDateStrYearMoonDay();
            endCreateAt = endCreateAt + " 23:59:59";
        } else {
            endCreateAt = endCreateAt + " 23:59:59";
        }

        QueryWrapper<OutletsOrderPayTrace> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("merch_id as 'merchId',merch_name as 'merchName',IFNULL(sum(amount),0) as 'amountSum'")
                .between("create_at", startCreateAt, endCreateAt)
                .groupBy("merch_id");

        List<Map<String, Object>> mapList = this.listMaps(queryWrapper);
        for (Map<String, Object> map : mapList) {
            BigDecimal amount = (BigDecimal) map.get("amountSum");
            Integer intAmount = amount.intValue();
            map.put("amountSum", MoneyUtils.changeF2YBigDecimal(intAmount));
        }
        return mapList;
    }

    @Override
    public List<OutletsOrderPayTraceExcelVO> packageOutletsOrderPayTrace(List<OutletsOrderPayTraceVO> result) {

        List<OutletsOrderPayTraceExcelVO> list = new ArrayList<>();

        for (OutletsOrderPayTraceVO outletsOrderPayTraceVO : result) {
            OutletsOrderPayTraceExcelVO outletsOrderPayTraceExcelVO = new OutletsOrderPayTraceExcelVO();
            BeanUtils.copyProperties(outletsOrderPayTraceVO, outletsOrderPayTraceExcelVO);
            switch (outletsOrderPayTraceVO.getType()) {
                case CardOrderPayTraceTypeConfig.POS:
                    outletsOrderPayTraceExcelVO.setType("购物订单");
                    break;
                case CardOrderPayTraceTypeConfig.POS_MIS_ORDER:
                    outletsOrderPayTraceExcelVO.setType("pos_mis订单");
                    break;
                case CardOrderPayTraceTypeConfig.POS_COUPON_PAY:
                    outletsOrderPayTraceExcelVO.setType("优惠券抵扣");
                    break;
                case CardOrderPayTraceTypeConfig.CASH_PAY:
                    outletsOrderPayTraceExcelVO.setType("现金支付");
                    break;
                case CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY:
                    outletsOrderPayTraceExcelVO.setType("扫码支付");
                    break;
                case CardOrderPayTraceTypeConfig.CREDIT_CARD:
                    outletsOrderPayTraceExcelVO.setType("刷卡支付");
                    break;
                default:
                    outletsOrderPayTraceExcelVO.setType("");
            }
            switch (outletsOrderPayTraceVO.getState()) {
                case CardOrderPayTraceStateConfig.UNPAID:
                    outletsOrderPayTraceExcelVO.setState("待支付");
                    break;
                case CardOrderPayTraceStateConfig.PAID:
                    outletsOrderPayTraceExcelVO.setState("已支付");
                    break;
                case CardOrderPayTraceStateConfig.REFUNDING:
                    outletsOrderPayTraceExcelVO.setState("退款中");
                    break;
                case CardOrderPayTraceStateConfig.REFUND:
                    outletsOrderPayTraceExcelVO.setState("已退款");
                    break;
                case CardOrderPayTraceStateConfig.CANCEL:
                    outletsOrderPayTraceExcelVO.setState("已撤销");
                    break;
                default:
                    outletsOrderPayTraceExcelVO.setState("");
            }

            if (CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY.equals(outletsOrderPayTraceVO.getType())){
                outletsOrderPayTraceExcelVO.setSybTrxid(outletsOrderPayTraceVO.getRefTraceNo());
            }else if (CardOrderPayTraceTypeConfig.CREDIT_CARD.equals(outletsOrderPayTraceVO.getType())){
                outletsOrderPayTraceExcelVO.setSybTrxid(outletsOrderPayTraceVO.getPayCode());
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            String payTime = outletsOrderPayTraceVO.getPayTime();
            try {
                if (!StringUtils.isEmpty(payTime)) {
                    Date date = df.parse(payTime);
                    String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                    outletsOrderPayTraceExcelVO.setPayTime(sdf);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            list.add(outletsOrderPayTraceExcelVO);
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> countLastThreeMonthsAmount() {

        List<Map<String, Object>> newMapList = new ArrayList<>();

        List<Map<String, Object>> mapList = this.baseMapper.selectLastThreeMonthsAmount();

        //处理月销售额格式 分转元
        for (Map<String, Object> map : mapList) {
            BigDecimal monthAmount = (BigDecimal) map.get("monthAmount");
            map.put("monthAmount", MoneyUtils.changeBigDecimalF2YBigDecimal(monthAmount));
        }

        //此处为了处理数据缺失导致的问题，比如某一月没有订单创建，那么sql语句中按照月份分组后就没有该月份这一组，自然也会缺失该月的销售额
        for (int i = 0; i < 3; i++) {
            HashMap<String, Object> newMap = new HashMap<>();

            //获得近七天的日期date
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(Calendar.MONTH,-i);
            String month= sdf.format(calendar.getTime());

            BigDecimal monthAmount = new BigDecimal("0.0");
            //将获得的月份和从数据库查询的月份作比较，若存才则将数据库得到的销售额赋值，若不存在则默认该月销售额为0.0
            for (Map<String, Object> map : mapList) {
                if (month.equals(map.get("month"))) {
                    monthAmount = (BigDecimal) map.get("monthAmount");
                }
            }
            newMap.put("month", month);
            newMap.put("月销售额", monthAmount);

            newMapList.add(newMap);

        }

        return newMapList;
    }


    @Override
    public List<Map<String, Object>> countLastSevenDaysAmount() {

        List<Map<String, Object>> newMapList = new ArrayList<>();

        //获得近七天的日期和对应日期的日销售额
        List<Map<String, Object>> mapList = this.baseMapper.selectLastSevenDaysAmount();

        //处理日销售额格式 分转元
        for (Map<String, Object> map : mapList) {
            BigDecimal dayAmount = (BigDecimal) map.get("dayAmount");
            Integer intAmount = dayAmount.intValue();
            map.put("dayAmount", MoneyUtils.changeF2YBigDecimal(intAmount));
        }

        //此处为了处理数据缺失导致的问题，比如某一天没有订单创建，那么sql语句中按照日期分组后就没有该日期这一组，自然也会缺失这一天的销售额
        for (int i = 0; i < 7; i++) {
            HashMap<String, Object> newMap = new HashMap<>();

            //获得近七天的日期date
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE,-i);
            String date= sdf.format(calendar.getTime());

            BigDecimal dayAmount = new BigDecimal("0.0");
            //将获得的日期和从数据库查询的日期作比较，若存才则将数据库得到的销售额赋值，若不存在则默认该天销售额为0.0
            for (Map<String, Object> map : mapList) {
                if (date.equals(map.get("date"))) {
                    dayAmount = (BigDecimal) map.get("dayAmount");
                }
            }
            newMap.put("date", date);
            newMap.put("日销售额", dayAmount);

            newMapList.add(newMap);

        }

        return newMapList;
    }

    @Override
    public Map<String, Object> findRefundableAmount(Map<String, String> paramsMap) {

        Map<String, Object> map = new HashMap<>();
        BigDecimal refundableAmount = new BigDecimal("0.0");

        List<OutletsOrderPayTraceVO> outletsOrderPayTraceVOS = findlist(paramsMap);
        for (OutletsOrderPayTraceVO outletsOrderPayTraceVO : outletsOrderPayTraceVOS) {
            BigDecimal amount = outletsOrderPayTraceVO.getAmount();
            BigDecimal refundAmount = outletsOrderPayTraceVO.getRefundAmount();
            refundableAmount = refundableAmount.add(amount.subtract(refundAmount));
        }

        map.put("refundableAmount",refundableAmount);
        return map;
    }

    @Override
    public List<OutletsOrderPayTrace> checkHaveOrder(String cashId) {
        return this.baseMapper.checkHaveOrder(cashId);
    }

    @Override
    public Page<OutletsOrderPayTrace> posQueryTrace(PosSearchTraceData posSearchTraceData) {
        Page<OutletsOrderPayTrace> page = new Page<>(posSearchTraceData.getPageNo(),posSearchTraceData.getPageSize());

        LambdaQueryWrapper<OutletsOrderPayTrace> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        if (StringGeneralUtil.checkNotNull(posSearchTraceData.getPosSerialNum())){
            lambdaQueryWrapper.eq(OutletsOrderPayTrace::getPosSerialNum,posSearchTraceData.getPosSerialNum());
        }
        if (StringGeneralUtil.checkNotNull(posSearchTraceData.getCashId())){
            lambdaQueryWrapper.eq(OutletsOrderPayTrace::getCashId,posSearchTraceData.getCashId());
        }
        if (StringGeneralUtil.checkNotNull(posSearchTraceData.getOrderCode())){
            lambdaQueryWrapper.and(traceQueryWrapper-> traceQueryWrapper.eq(OutletsOrderPayTrace::getOrderCode,posSearchTraceData.getOrderCode())
                    .or()
                    .eq(OutletsOrderPayTrace::getPayCode,posSearchTraceData.getOrderCode())
                    .or()
                    .eq(OutletsOrderPayTrace::getRefBatchCode,posSearchTraceData.getOrderCode()));
//            lambdaQueryWrapper.eq(OutletsOrderPayTrace::getOrderCode,posSearchTraceData.getOrderCode());
        }
        if (StringGeneralUtil.checkNotNull(posSearchTraceData.getPayCode())){
            lambdaQueryWrapper.and(traceQueryWrapper-> traceQueryWrapper.eq(OutletsOrderPayTrace::getOrderCode,posSearchTraceData.getPayCode())
                    .or()
                    .eq(OutletsOrderPayTrace::getPayCode,posSearchTraceData.getPayCode())
                    .or()
                    .eq(OutletsOrderPayTrace::getRefBatchCode,posSearchTraceData.getPayCode()));
//            lambdaQueryWrapper.eq(OutletsOrderPayTrace::getPayCode,posSearchTraceData.getPayCode());
        }
        if (StringGeneralUtil.checkNotNull(posSearchTraceData.getStartTime())){
            String startCreateAt = posSearchTraceData.getStartTime() + " 00:00:00";
            lambdaQueryWrapper.ge(OutletsOrderPayTrace::getCreateAt,startCreateAt);
        }
        if (StringGeneralUtil.checkNotNull(posSearchTraceData.getEndTime())){
            String endCreateAt = posSearchTraceData.getEndTime() + " 23:59:59";
            lambdaQueryWrapper.le(OutletsOrderPayTrace::getCreateAt,endCreateAt);
        }

        lambdaQueryWrapper.and(traceQueryWrapper-> traceQueryWrapper.eq(OutletsOrderPayTrace::getState,"paid")
                .or()
                .eq(OutletsOrderPayTrace::getState,"refund")
                .or()
                .eq(OutletsOrderPayTrace::getState,"cancel"));

        lambdaQueryWrapper.eq(OutletsOrderPayTrace::getType,CardOrderPayTraceTypeConfig.CREDIT_CARD);
        lambdaQueryWrapper.orderByDesc(OutletsOrderPayTrace::getCreateAt);
        return page(page,lambdaQueryWrapper);
    }

    @Override
    public List<Map<String, Object>> countAmountSumGroupByType(Map<String, String> paramsMap) {
        String cashId = paramsMap.get("cashId");
        String startCreateAt = paramsMap.get("startCreateAt");
        String endCreateAt = paramsMap.get("endCreateAt");

        if (StringUtils.isEmpty(startCreateAt)) {
            startCreateAt = DateStrUtil.nowDateStrYearMoonDay();
        }
        if (StringUtils.isEmpty(endCreateAt)) {
            endCreateAt = DateStrUtil.nowDateStrYearMoonDay();
            endCreateAt = endCreateAt + " 23:59:59";
        } else {
            endCreateAt = endCreateAt + " 23:59:59";
        }

        List<String> stateList = new ArrayList<>();
        stateList.add(CardOrderPayTraceStateConfig.PAID);
        stateList.add(CardOrderPayTraceStateConfig.REFUND);
        stateList.add(CardOrderPayTraceStateConfig.CANCEL);

        //获取刷卡支付的统计金额
        QueryWrapper<OutletsOrderPayTrace> cardQueryWrapper = new QueryWrapper<>();
        cardQueryWrapper.select("IFNULL(type,'刷卡支付') as 'type',IFNULL(sum(amount),0) as 'amountSum',IFNULL(sum(fee),0) as 'feeSum'")
                .eq("type", CardOrderPayTraceTypeConfig.CREDIT_CARD)
                .eq(!StringUtils.isEmpty(cashId), "cash_id", cashId)
                .in("state", stateList)
                .between("create_at", startCreateAt, endCreateAt);
        List<Map<String, Object>> mapList = this.listMaps(cardQueryWrapper);

        //获取各种扫码支付的统计金额
        QueryWrapper<OutletsOrderPayTrace> scanQueryWrapper = new QueryWrapper<>();
        scanQueryWrapper.select("source as 'type',IFNULL(sum(amount),0) as 'amountSum',IFNULL(sum(fee),0) as 'feeSum'")
                .eq("type", CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY)
                .eq(!StringUtils.isEmpty(cashId), "cash_id", cashId)
                .in("state", stateList)
                .between("create_at", startCreateAt, endCreateAt)
                .groupBy("source");
        List<Map<String, Object>> scanMapList = this.listMaps(scanQueryWrapper);

        for (Map<String, Object> map : scanMapList) {

            //获取该扫码支付对应的退款金额
            Map<String,String> params = new HashMap<>();
            params.put("payTrxcodeDescribe", (String) map.get("type"));
            params.put("cashId", cashId);
            params.put("startCreateAt", startCreateAt);
            params.put("endCreateAt", endCreateAt);
            Map<String, Object> refundMap = outletsOrderRefundCancelService.countCardRefundAmountByPayTrxcodeDescribeAndCashId(params);
            map.put("refundAmountSum", refundMap.get("refundAmountSum"));
            map.put("refundFeeSum", refundMap.get("refundFeeSum"));

            mapList.add(map);
        }

        for (Map<String, Object> map : mapList) {
            BigDecimal cumulativeAmountSum;
            BigDecimal liquidationSum;

            //获得刷卡支付对应的退款金额
            if (CardOrderPayTraceTypeConfig.CREDIT_CARD.equals(map.get("type"))) {
                Map<String,String> params = new HashMap<>();
                params.put("payTrxcode", (String) map.get("type"));
                params.put("cashId", cashId);
                params.put("startCreateAt", startCreateAt);
                params.put("endCreateAt", endCreateAt);
                Map<String, Object> refundMap = outletsOrderRefundCancelService.countCardRefundAmountByPayTrxcodeAndCashId(params);
                map.put("refundAmountSum", refundMap.get("refundAmountSum"));
                map.put("refundFeeSum", refundMap.get("refundFeeSum"));
                map.put("type", "刷卡支付");
            }

            //金额格式转化 分转元
            BigDecimal amount = (BigDecimal) map.get("amountSum");
            map.put("amountSum", MoneyUtils.changeBigDecimalF2YBigDecimal(amount));
            BigDecimal fee = (BigDecimal) map.get("feeSum");
            map.put("feeSum", MoneyUtils.changeBigDecimalF2YBigDecimal(fee));
            BigDecimal refundAmount = (BigDecimal) map.get("refundAmountSum");
            if (null != refundAmount) {
                map.put("refundAmountSum", MoneyUtils.changeBigDecimalF2YBigDecimal(refundAmount));
            } else {
                map.put("refundAmountSum", 0.0);
            }
            if (null != map.get("refundFeeSum")) {
                BigDecimal refundFee = new BigDecimal((Double) map.get("refundFeeSum"));
                map.put("refundFeeSum", MoneyUtils.changeBigDecimalF2YBigDecimal(refundFee));
            } else {
                map.put("refundFeeSum", 0.0);
            }

            BigDecimal amountSum = new BigDecimal(map.get("amountSum")+"");
            BigDecimal refundAmountSum = new BigDecimal(map.get("refundAmountSum")+"");
            BigDecimal feeSum = new BigDecimal(map.get("feeSum")+"");
            BigDecimal refundFeeSum = new BigDecimal(map.get("refundFeeSum")+"");
            cumulativeAmountSum = amountSum.subtract(refundAmountSum);
            liquidationSum = amountSum.subtract(feeSum).subtract(refundAmountSum).add(refundFeeSum);
            map.put("cumulativeAmountSum", MyMathUtil.KeepTwoDecimalPlaces(cumulativeAmountSum));
            map.put("liquidationSum", MyMathUtil.KeepTwoDecimalPlaces(liquidationSum));
        }

        return mapList;
    }

    @Override
    public OutletsOrderRefTrace queryByRefTraceNoOrPayCode(CheckPayTraceData checkPayTraceData) throws Exception {
        String payTraceNo = checkPayTraceData.getTrxid();
        OutletsOrderPayTrace outletsOrderPayTrace = this.baseMapper.selectByRefTraceNoOrPayCode(payTraceNo);
        if (outletsOrderPayTrace==null){
            Map<String, String> queryMap = new HashMap<>();
            String channelApi;
            PayCompanyStrategy payCompanyStrategyTongLian = PayCompanyStrategyFactory.getByCompanyChannel(PayCompanyTypeEnum.ALLINPAY.getPayCompany());
            Map<String, String> tlQueryMap = null;
            try {
                tlQueryMap = payCompanyStrategyTongLian.query("", payTraceNo);
            } catch (Exception e) {
                tlQueryMap = new HashMap<>();
                tlQueryMap.put("retcode","FAIL");
            }
            if ("SUCCESS".equals(tlQueryMap.get("retcode")) && "0000".equals(tlQueryMap.get("retcode"))){
                queryMap = tlQueryMap;
                channelApi = PayCompanyTypeEnum.ALLINPAY.getPayCompany();
            }else {
                PayCompanyStrategy payCompanyStrategyJiaLian = PayCompanyStrategyFactory.getByCompanyChannel(PayCompanyTypeEnum.JLPAY.getPayCompany());
                queryMap = payCompanyStrategyJiaLian.queryCheck("", payTraceNo);
                channelApi = PayCompanyTypeEnum.JLPAY.getPayCompany();
            }

//            Map<String, String> queryMap = sybPayService.query("", payTraceNo);
            logger.info("收银台反查查询订单响应数据为:"+queryMap);
            //封装查询结果实体
            OutletsOrderRefTrace outletsOrderRefTraceQuery = new OutletsOrderRefTrace();
            BeanMap beanMap = BeanMap.create(outletsOrderRefTraceQuery);
            beanMap.putAll(queryMap);
            //判断支付结果
            if ("SUCCESS".equals(outletsOrderRefTraceQuery.getRetcode())){
                if ("0000".equals(outletsOrderRefTraceQuery.getTrxstatus())){
                    OutletsOrders outletsOrders = new OutletsOrders();
                    if (PayCompanyTypeEnum.JLPAY.getPayCompany().equals(channelApi)){
                        outletsOrders.setOrderCode(outletsOrderRefTraceQuery.getRefReqsn());
                    }else {
                        outletsOrders.setOrderCode(outletsOrderRefTraceQuery.getReqsn());
                    }
                    outletsOrders.setType(CardOrdersTypeConfig.CONSUME);
                    outletsOrders.setState(CardOrdersStateConfig.PAID);
                    outletsOrders.setUserId(-1L);
                    outletsOrders.setQuantity(BigDecimal.ONE);
                    outletsOrders.setAmount(Integer.parseInt(outletsOrderRefTraceQuery.getTrxamt()));
                    outletsOrders.setComments("扫码消费订单(异常订单补单)");
                    outletsOrders.setDiscount(0);
                    outletsOrders.setChannelApi(channelApi);
                    outletsOrders.setCreateAt(new Date());
                    outletsOrders.setUpdateAt(new Date());
                    outletsOrdersService.save(outletsOrders);

                    OutletsOrderDetails outletsOrderDetails = new OutletsOrderDetails();
                    outletsOrderDetails.setOrderCode(outletsOrders.getOrderCode());
                    outletsOrderDetails.setQuantity(BigDecimal.ONE);
                    outletsOrderDetails.setAmount(Integer.parseInt(outletsOrderRefTraceQuery.getTrxamt()));
                    outletsOrderDetails.setState(CardOrdersStateConfig.PAID);
                    outletsOrderDetails.setType(CardOrdersTypeConfig.CONSUME);
                    outletsOrderDetails.setDisccount(0);
                    outletsOrderDetails.setCreateAt(new Date());
                    outletsOrderDetails.setUpdateAt(new Date());
                    outletsOrderDetailsService.save(outletsOrderDetails);

                    OutletsOrderPayTrace outletsOrderPayTraceFix = new OutletsOrderPayTrace();
                    outletsOrderPayTraceFix.setOrderCode(outletsOrders.getOrderCode());
                    outletsOrderPayTraceFix.setPayCode(outletsOrderRefTraceQuery.getChnltrxid());
                    outletsOrderPayTraceFix.setType(CardOrderPayTraceTypeConfig.SCAN_QR_CODE_PAY);
                    outletsOrderPayTraceFix.setState(CardOrderPayTraceStateConfig.PAID);
                    outletsOrderPayTraceFix.setSource(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefTraceQuery.getTrxcode()));
                    outletsOrderPayTraceFix.setSourceId(outletsOrderRefTraceQuery.getAcct());
                    outletsOrderPayTraceFix.setAmount(Integer.parseInt(outletsOrderRefTraceQuery.getTrxamt()));
                    outletsOrderPayTraceFix.setMerchName("system");
                    outletsOrderPayTraceFix.setRefTraceNo(outletsOrderRefTraceQuery.getTrxid());
                    outletsOrderPayTraceFix.setTraceNo(IdWorker.getIdStr());
                    outletsOrderPayTraceFix.setCashId("system");
                    outletsOrderPayTraceFix.setRefundAmount(0);
                    outletsOrderPayTraceFix.setChannelApi(channelApi);
                    outletsOrderPayTraceFix.setPayTime(outletsOrderRefTraceQuery.getFintime());
                    outletsOrderPayTraceFix.setRefBatchCode(outletsOrders.getOrderCode());
                    save(outletsOrderPayTraceFix);

                    outletsOrderRefTraceQuery.setTrxcodeDescribe(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefTraceQuery.getTrxcode()));
                    outletsOrderRefTraceQuery.setRefReqsn(outletsOrders.getOrderCode());
                    outletsOrderRefTraceService.save(outletsOrderRefTraceQuery);

                } else if (!"0000".equals(outletsOrderRefTraceQuery.getTrxstatus())){
                    throw new CheckException(ResultTypeEnum.ORDER_NULL.getCode(),outletsOrderRefTraceQuery.getErrmsg());
                }
            }
            return outletsOrderRefTraceQuery;
        }else {
            OutletsOrderRefTrace outletsOrderRefTrace = outletsOrderRefTraceService.queryByReqsn(outletsOrderPayTrace.getOrderCode());
            if (CardOrderPayTraceStateConfig.UNPAID.equals(outletsOrderPayTrace.getState()) ||
                    CardOrderPayTraceStateConfig.CLOSE.equals(outletsOrderPayTrace.getState())){

                PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(outletsOrderPayTrace.getChannelApi());
                Map<String, String> queryMap = payCompanyStrategy.query("", payTraceNo);
//                Map<String, String> queryMap = sybPayService.query("", payTraceNo);

                logger.info("收银台反查查询订单响应数据为:"+queryMap);
                //封装查询结果实体
                OutletsOrderRefTrace outletsOrderRefTraceQuery = new OutletsOrderRefTrace();
                BeanMap beanMap = BeanMap.create(outletsOrderRefTraceQuery);
                beanMap.putAll(queryMap);
                //判断支付结果
                if ("SUCCESS".equals(outletsOrderRefTraceQuery.getRetcode())){
                    if ("0000".equals(outletsOrderRefTraceQuery.getTrxstatus())) {
                        outletsOrdersService.updateAllState(outletsOrderRefTraceQuery.getReqsn(), CardOrderPayTraceStateConfig.PAID);
                        outletsOrderRefTrace.setTrxstatus(outletsOrderRefTraceQuery.getTrxstatus());
                        outletsOrderRefTrace.setChnltrxid(outletsOrderRefTraceQuery.getChnltrxid());
                        outletsOrderRefTrace.setFintime(outletsOrderRefTraceQuery.getFintime());
                        outletsOrderRefTrace.setAcct(outletsOrderRefTraceQuery.getAcct());
                        outletsOrderRefTrace.setErrmsg("收银台扫码反查订单更新数据");
                        outletsOrderRefTrace.setFee(outletsOrderRefTraceQuery.getFee());
                        outletsOrderRefTraceService.updateById(outletsOrderRefTrace);
                        outletsOrderPayTraceService.updatePayCodeByOrderCode(outletsOrderRefTraceQuery.getChnltrxid(),
                                outletsOrderRefTraceQuery.getReqsn(),
                                outletsOrderRefTraceQuery.getFintime(),
                                StringGeneralUtil.checkNotNull(outletsOrderRefTraceQuery.getFee())?Integer.parseInt(outletsOrderRefTraceQuery.getFee()): 0
                                );

                        outletsOrderRefTraceQuery.setTrxcodeDescribe(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefTraceQuery.getTrxcode()));
                        return outletsOrderRefTraceQuery;
                    }else {
                        throw new CheckException(ResultTypeEnum.NOT_PAY.getCode(),outletsOrderRefTraceQuery.getErrmsg());
                    }
                }
            }
            return outletsOrderRefTrace;
        }
    }

    @Override
    public List<OutletsOrderPayTrace> queryPosTaskCheckPayStateOrder() {
        long limitTime = new Date().getTime() - 300 * 1000;
        QueryWrapper<OutletsOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.ge("create_at",new Date(limitTime));
        queryWrapper.le("create_at",new Date());
        queryWrapper.and(traceQueryWrapper-> traceQueryWrapper.eq("state","close")
                .or()
                .eq("state","unpaid"));
        queryWrapper.and(traceQueryWrapper-> traceQueryWrapper.eq("type","pos_mis_order")
                .or()
                .eq("type","credit_card"));
        return list(queryWrapper);
    }

    @Override
    public OutletsOrderPayTrace queryByRefBatchCode(String refBatchCode) {
        QueryWrapper<OutletsOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("ref_batch_code",refBatchCode);
        return getOne(queryWrapper,false);
    }


}
