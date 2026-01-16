package com.ht.user.outlets.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.config.CardOrderPayTraceStateConfig;
import com.ht.user.config.CardOrderPayTraceTypeConfig;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.config.TrxCodeDescribeEnum;
import com.ht.user.outlets.controller.OutletsOrdersController;
import com.ht.user.outlets.entity.OutletsOrderPayTrace;
import com.ht.user.outlets.entity.OutletsOrderRefRefundCancel;
import com.ht.user.outlets.entity.OutletsOrderRefTrace;
import com.ht.user.outlets.mapper.OutletsOrderRefTraceMapper;
import com.ht.user.outlets.paystrategy.PayCompanyStrategy;
import com.ht.user.outlets.paystrategy.PayCompanyStrategyFactory;
import com.ht.user.outlets.paystrategy.PayCompanyTypeEnum;
import com.ht.user.outlets.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.outlets.util.DateStrUtil;
import com.ht.user.outlets.util.MoneyUtils;
import com.ht.user.outlets.util.OrderCodeIntercept;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.ht.user.utils.MyMathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-16
 */
@Service
public class OutletsOrderRefTraceServiceImpl extends ServiceImpl<OutletsOrderRefTraceMapper, OutletsOrderRefTrace> implements IOutletsOrderRefTraceService {

    private Logger logger = LoggerFactory.getLogger(OutletsOrderRefTraceServiceImpl.class);

    @Autowired
    private SybPayService sybPayService;

    @Autowired
    private IOutletsOrdersService outletsOrdersService;

    @Autowired
    private IOutletsOrderPayTraceService outletsOrderPayTraceService;

    @Autowired
    private IOutletsOrderRefundCancelService outletsOrderRefundCancelService;

    @Autowired
    private IOutletsOrderRefRefundCancelService outletsOrderRefRefundCancelService;



    @Override
    public OutletsOrderRefTrace queryByReqsn(String orderCode) {
        LambdaQueryWrapper<OutletsOrderRefTrace> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OutletsOrderRefTrace::getReqsn,orderCode);
        return getOne(lambdaQueryWrapper);
    }

    @Override
    public void querySybOrderUpDateLocal(OutletsOrderRefTrace outletsOrderRefTrace) throws Exception {
        List<OutletsOrderPayTrace> outletsOrderPayTraces = outletsOrderPayTraceService.queryTraceByOrderCode(outletsOrderRefTrace.getReqsn());

        String transferOrderCode;
        //不同支付公司传递的订单号不一样
        String channelApi = outletsOrderPayTraces.get(0).getChannelApi();
        if (PayCompanyTypeEnum.JLPAY.getPayCompany().equals(channelApi)){
            transferOrderCode = outletsOrderRefTrace.getRefReqsn();
        }else {
            transferOrderCode = outletsOrderRefTrace.getReqsn();
        }
        PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(channelApi);
        Map<String, String> queryMap = payCompanyStrategy.query(transferOrderCode, outletsOrderRefTrace.getTrxid());
//        Map<String, String> queryMap = sybPayService.query(outletsOrderRefTrace.getReqsn(), outletsOrderRefTrace.getTrxid());
        logger.info("支付公司查询订单响应数据为:"+queryMap);
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
                outletsOrderRefTrace.setErrmsg("无错误,用户异步支付,反查订单更新数据");
                outletsOrderRefTrace.setFee(outletsOrderRefTraceQuery.getFee());
                outletsOrderRefTrace.setUpdateAt(new Date());
                updateById(outletsOrderRefTrace);

                outletsOrderPayTraceService.updatePayCodeByOrderCode(outletsOrderRefTraceQuery.getChnltrxid(),
                        outletsOrderRefTraceQuery.getReqsn(), outletsOrderRefTraceQuery.getFintime(),
                        StringGeneralUtil.checkNotNull(outletsOrderRefTraceQuery.getFee())?Integer.parseInt(outletsOrderRefTraceQuery.getFee()): 0);
            }else if (!"0000".equals(outletsOrderRefTraceQuery.getTrxstatus()) && !"2000".equals(outletsOrderRefTraceQuery.getTrxstatus())){
                outletsOrdersService.updateAllState(outletsOrderRefTraceQuery.getReqsn(), CardOrderPayTraceStateConfig.CLOSE);

                outletsOrderRefTrace.setTrxstatus(outletsOrderRefTraceQuery.getTrxstatus());
                outletsOrderRefTrace.setChnltrxid(outletsOrderRefTraceQuery.getChnltrxid());
                outletsOrderRefTrace.setFintime(outletsOrderRefTraceQuery.getFintime());
                outletsOrderRefTrace.setAcct(outletsOrderRefTraceQuery.getAcct());
                outletsOrderRefTrace.setErrmsg(outletsOrderRefTraceQuery.getErrmsg());
                outletsOrderRefTrace.setFee(outletsOrderRefTraceQuery.getFee());
                outletsOrderRefTrace.setUpdateAt(new Date());
                updateById(outletsOrderRefTrace);

                outletsOrderPayTraceService.updatePayCodeByOrderCode(outletsOrderRefTraceQuery.getChnltrxid(),
                        outletsOrderRefTraceQuery.getReqsn(), outletsOrderRefTraceQuery.getFintime(),
                        StringGeneralUtil.checkNotNull(outletsOrderRefTraceQuery.getFee())?Integer.parseInt(outletsOrderRefTraceQuery.getFee()): 0);
            }
        }
    }

    @Override
    public List<Map<String, Object>> countTrxamt(Map<String, String> paramsMap) {

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

        QueryWrapper<OutletsOrderRefTrace> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("trxcode,IFNULL(sum(trxamt),0) as 'trxamtSum',IFNULL(sum(fee),0) as 'feeSum'")
                .eq("trxstatus", "0000")
                .between(!StringUtils.isEmpty(startCreateAt) && !StringUtils.isEmpty(endCreateAt), "create_at", startCreateAt, endCreateAt)
                .groupBy("trxcode");

        List<Map<String, Object>> mapList = this.listMaps(queryWrapper);
        List<Map<String, Object>> refundMapList = outletsOrderRefundCancelService.countRefundAmountByPayTrxcode(startCreateAt, endCreateAt);
        List<Map<String, Object>> refundFeeMapList = outletsOrderRefRefundCancelService.countRefundFeeByTrxcode(startCreateAt, endCreateAt);

        for (Map<String, Object> map : mapList) {
            String trxcode = (String) map.get("trxcode");
            BigDecimal trxamtSum = new BigDecimal("0.0");
            BigDecimal feeSum = new BigDecimal("0.0");
            BigDecimal refundAmountSum = new BigDecimal("0.0");
            BigDecimal refundFeeSum = new BigDecimal("0.0");
            //实际交易金额
            BigDecimal cumulativeAmountSum = new BigDecimal("0.0");
            //清算金额合计
            BigDecimal liquidationSum = new BigDecimal("0.0");
            //处理金额格式 分转元
            BigDecimal amount = new BigDecimal((Double) map.get("trxamtSum"));
            trxamtSum = MoneyUtils.changeBigDecimalF2YBigDecimal(amount);
            BigDecimal fee = new BigDecimal((Double) map.get("feeSum"));
            feeSum = MoneyUtils.changeBigDecimalF2YBigDecimal(fee);

            //拿到当前循环交易类型下对应的退款金额
            for (Map<String, Object> refundMap : refundMapList) {
                if (trxcode.equals(refundMap.get("payTrxcode"))) {
                    if (!StringUtils.isEmpty(refundMap.get("refundAmountSum"))) {
                        BigDecimal refundAmount = (BigDecimal) refundMap.get("refundAmountSum");
                        refundAmountSum = MoneyUtils.changeBigDecimalF2YBigDecimal(refundAmount);
                    }
                }
            }

            //拿到当前循环交易类型下对应的退款手续费金额
            for (Map<String, Object> refundFeeMap : refundFeeMapList) {
                if (trxcode.equals(refundFeeMap.get("trxcode"))) {
                    if (!StringUtils.isEmpty(refundFeeMap.get("refundFeeSum"))) {
                        BigDecimal refundFee = new BigDecimal((Double) refundFeeMap.get("refundFeeSum"));
                        refundFeeSum = MoneyUtils.changeBigDecimalF2YBigDecimal(refundFee);
                    }
                }
            }

            cumulativeAmountSum = trxamtSum.subtract(refundAmountSum);
            liquidationSum = trxamtSum.subtract(feeSum).subtract(refundAmountSum).add(refundFeeSum);
            map.put("trxamtSum", trxamtSum);
            map.put("feeSum", feeSum);
            map.put("refundAmountSum", refundAmountSum);
            map.put("refundFeeSum", refundFeeSum);
            map.put("cumulativeAmountSum", MyMathUtil.KeepTwoDecimalPlaces(cumulativeAmountSum));
            map.put("liquidationSum", MyMathUtil.KeepTwoDecimalPlaces(liquidationSum));

        }


        return mapList;
    }

    @Override
    public List<Map<String, Object>> countLastSevenDaysAmount() {

        List<Map<String, Object>> newMapList = new ArrayList<>();

        //获得近七天的日期和对应日期的 日实际交易金额
        List<Map<String, Object>> mapList = this.baseMapper.selectLastSevenDaysAmount();

        //处理 日实际交易金额格式 分转元
        for (Map<String, Object> map : mapList) {
            Double dayAmount = (Double) map.get("dayAmount");
            Integer intAmount = dayAmount.intValue();
            map.put("dayAmount", MoneyUtils.changeF2YBigDecimal(intAmount));
        }

        //第一个循环为了获得三种交易类型
        for (int j = 0; j < 3; j++) {
            String trxcode = "";
            String trxcodeDescribe = "";
            switch (j) {
                case 0:
                    trxcode = TrxCodeDescribeEnum.WECHAT_PAY.getValue();
                    trxcodeDescribe = TrxCodeDescribeEnum.WECHAT_PAY.getDesc();
                    break;
                case 1:
                    trxcode = TrxCodeDescribeEnum.ALIPAY_PAY.getValue();
                    trxcodeDescribe = TrxCodeDescribeEnum.ALIPAY_PAY.getDesc();
                    break;
                case 2:
                    trxcode = TrxCodeDescribeEnum.DIGITAL_CURRENCY_PAY.getValue();
                    trxcodeDescribe = TrxCodeDescribeEnum.DIGITAL_CURRENCY_PAY.getDesc();
                    break;
                default:
                    break;
            }

            //第二个循环为了获得近七天的日期
            //此处为了处理数据缺失导致的问题，比如某一天没有订单创建，那么sql语句中按照日期分组后就没有该日期这一组，自然也会缺失这一天的日实际交易金额
            for (int i = 6; i >= 0; i--) {
                HashMap<String, Object> newMap = new HashMap<>();

                //获得近七天的日期date
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE,-i);
                String date= sdf.format(calendar.getTime());

                BigDecimal dayAmount = BigDecimal.ZERO;
                //将获得的日期和从数据库查询的日期作比较，若存才则将数据库得到的销售额赋值，若不存在则默认该天销售额为0.0
                for (Map<String, Object> map : mapList) {
                    if (date.equals(map.get("date")) && trxcode.equals(map.get("trxcode"))) {
                        dayAmount = (BigDecimal) map.get("dayAmount");
                    }
                }
                newMap.put("trxcode", trxcodeDescribe);
                newMap.put("date", date);
                newMap.put("dayAmount", dayAmount);

                newMapList.add(newMap);

            }

        }

        return newMapList;
    }

    @Override
    public List<OutletsOrderRefTrace> queryTaskCheckPayStateOrder() {
        long limitTime = new Date().getTime() - 300 * 1000;
        QueryWrapper<OutletsOrderRefTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("retcode","SUCCESS");
        queryWrapper.eq("trxstatus","2000");
        queryWrapper.ge("create_at",new Date(limitTime));
        queryWrapper.le("create_at",new Date());
        return list(queryWrapper);
    }
}
