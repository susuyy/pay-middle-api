package com.ht.user.outlets.task;


import com.ht.user.config.CardOrdersStateConfig;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.config.TrxCodeDescribeEnum;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.paystrategy.PayCompanyStrategy;
import com.ht.user.outlets.paystrategy.PayCompanyStrategyFactory;
import com.ht.user.outlets.paystrategy.PayCompanyTypeEnum;
import com.ht.user.outlets.service.IOutletsOrderPayTraceService;
import com.ht.user.outlets.service.IOutletsOrderRefRefundCancelService;
import com.ht.user.outlets.service.IOutletsOrderRefTraceService;
import com.ht.user.outlets.util.DateStrUtil;
import com.ht.user.outlets.util.StringGeneralUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableScheduling
public class PayOrderStateTask {

    private Logger logger = LoggerFactory.getLogger(PayOrderStateTask.class);

    @Autowired
    private IOutletsOrderRefTraceService outletsOrderRefTraceService;

    @Autowired
    private IOutletsOrderPayTraceService outletsOrderPayTraceService;

    @Autowired
    private SybPayService sybPayService;

    @Autowired
    private IOutletsOrderRefRefundCancelService outletsOrderRefRefundCancelService;

    /**
     * 扫码支付订单 刷新支付状态机制
     *
     * @throws Exception
     */
    //2分钟更新一次 刷新未支付订单
    @Scheduled(fixedRate = 120000)
    public void taskQueryPayState() throws Exception {
        List<OutletsOrderRefTrace> list = outletsOrderRefTraceService.queryTaskCheckPayStateOrder();
        for (OutletsOrderRefTrace outletsOrderRefTrace : list) {
            if ("SUCCESS".equals(outletsOrderRefTrace.getRetcode()) && "2000".equals(outletsOrderRefTrace.getTrxstatus())) {
                outletsOrderRefTraceService.querySybOrderUpDateLocal(outletsOrderRefTrace);
            }
        }
    }

    /**
     * pos-mis订单 刷新支付状态机制
     *
     * @throws Exception
     */
    //2分钟更新一次 刷新未支付订单
    @Scheduled(fixedRate = 120000)
    public void taskQueryPosOrderPayState() throws Exception {

        List<OutletsOrderPayTrace> list = outletsOrderPayTraceService.queryPosTaskCheckPayStateOrder();
        for (OutletsOrderPayTrace outletsOrderPayTrace : list) {

            PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(StringGeneralUtil.checkNotNull(outletsOrderPayTrace.getChannelApi()) ? outletsOrderPayTrace.getChannelApi() : PayCompanyTypeEnum.ALLINPAY.getPayCompany());
            Map<String, String> queryOrderMap = payCompanyStrategy.posOrderPayQuery(outletsOrderPayTrace.getRefBatchCode(),
                    "",
                    DateStrUtil.dateStrMMdd(outletsOrderPayTrace.getCreateAt()));

//            Map<String, String> queryOrderMap = sybPayService.posOrderPayQuery(outletsOrderPayTrace.getRefBatchCode(),
//                    "",
//                    DateStrUtil.dateStrMMdd(outletsOrderPayTrace.getCreateAt()));
            if ("SUCCESS".equals(queryOrderMap.get("retcode")) && "0000".equals(queryOrderMap.get("trxstatus"))) {
                //转化map为实体
                PosQueryRefOrderData posQueryRefOrderData = new PosQueryRefOrderData();
                BeanMap beanMap = BeanMap.create(posQueryRefOrderData);
                beanMap.putAll(queryOrderMap);
                //更新订单数据
                PosPayTraceSuccessData posPayTraceSuccessData = new PosPayTraceSuccessData();
                posPayTraceSuccessData.setAmount(Integer.parseInt(posQueryRefOrderData.getAmount()));
                posPayTraceSuccessData.setFee(StringGeneralUtil.checkNotNull(posQueryRefOrderData.getFee()) ? Integer.parseInt(posQueryRefOrderData.getFee()) : 0);
                posPayTraceSuccessData.setTraceNo(posQueryRefOrderData.getTraceno());
                posPayTraceSuccessData.setBatchNo(posQueryRefOrderData.getTermbatchid());
                posPayTraceSuccessData.setMerchId(posQueryRefOrderData.getCusid());
                posPayTraceSuccessData.setMerchName("佰申商业管理（海南）有限公司");
                posPayTraceSuccessData.setTerId(posQueryRefOrderData.getTermno());
                posPayTraceSuccessData.setRefNo(posQueryRefOrderData.getTermrefnum());
                posPayTraceSuccessData.setAuthNo(posQueryRefOrderData.getTermauthno());
                posPayTraceSuccessData.setRejCode("00");
                Date finDate = DateStrUtil.StrToDateyyyyMMddHHmmss(posQueryRefOrderData.getFintime());
                posPayTraceSuccessData.setDate(DateStrUtil.dateStrMMdd(finDate));
                posPayTraceSuccessData.setTime(DateStrUtil.dateStrHHmmss(finDate));
                posPayTraceSuccessData.setRejCodeCn("反查订单交易成功");
                posPayTraceSuccessData.setTransTicketNo(posQueryRefOrderData.getTrxid());
                posPayTraceSuccessData.setOrderCode(outletsOrderPayTrace.getOrderCode());
                posPayTraceSuccessData.setChannelApi("AllinPay");
                posPayTraceSuccessData.setPayTime(posQueryRefOrderData.getFintime());
                outletsOrderPayTraceService.updateMisOrderState(posPayTraceSuccessData);
            }
        }
    }

    /**
     * pos退款/撤销订单,手续费更新
     *
     * @throws Exception
     */
    //2小时更新一次 刷新手续费
    @Scheduled(fixedRate = 7200000)
    public void taskQueryPosOrderRefundFee() throws Exception {
        List<OutletsOrderRefRefundCancel> outletsOrderRefRefundCancels = outletsOrderRefRefundCancelService.queryFeeNullPosTypeData();
        for (OutletsOrderRefRefundCancel outletsOrderRefRefundCancel : outletsOrderRefRefundCancels) {
            List<OutletsOrderPayTrace> outletsOrderPayTraces = outletsOrderPayTraceService.queryTraceByOrderCode(outletsOrderRefRefundCancel.getReqsn());
            String channelApi = StringGeneralUtil.checkNotNull(outletsOrderPayTraces.get(0).getChannelApi()) ? outletsOrderPayTraces.get(0).getChannelApi() : PayCompanyTypeEnum.ALLINPAY.getPayCompany();
            PayCompanyStrategy payCompanyStrategy = PayCompanyStrategyFactory.getByCompanyChannel(channelApi);
            Map<String, String> queryMap = payCompanyStrategy.query("", outletsOrderRefRefundCancel.getOuttrxid());
//            Map<String, String> queryMap = sybPayService.query("", outletsOrderRefRefundCancel.getOuttrxid());
            if ("SUCCESS".equals(queryMap.get("retcode")) && "0000".equals(queryMap.get("trxstatus"))) {
                outletsOrderRefRefundCancel.setFee(queryMap.get("fee"));
                outletsOrderRefRefundCancel.setTrxcode(queryMap.get("trxcode"));
                outletsOrderRefRefundCancel.setTrxcodeDescribe(TrxCodeDescribeEnum.getDescByValueKey(outletsOrderRefRefundCancel.getTrxcode()));
                outletsOrderRefRefundCancelService.updateById(outletsOrderRefRefundCancel);
            }
        }
    }
}
