package com.ht.user.outlets.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.common.Result;
import com.ht.user.outlets.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.outlets.excel.OutletsOrderPayTraceExcelVO;
import com.ht.user.outlets.vo.OutletsOrderPayTraceVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单支付流水 服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface IOutletsOrderPayTraceService extends IService<OutletsOrderPayTrace> {

    List<OutletsOrderPayTrace> queryPayTrace(SearchTraceData searchTraceData);

    void updateStateByOrderCode(String orderCode, String state);

    String createPosPayTraceFromCashier(PosPayTraceSuccessData posPayTraceSuccessData);


    void updateMisOrderState(PosPayTraceSuccessData posPayTraceSuccessData) throws Exception;


    List<OutletsOrderPayTrace> queryTraceByOrderCodeStateType(String orderCode, String state,String stateOr, String type);

    List<OutletsOrderPayTrace> queryTraceByOrderCode(String orderCode);

    void updateStateAndRefundAmountByOrderCode(String orderCode, String state, long refundAmount);

    void updatePayCodeByOrderCode(String payCode, String orderCode,String payTime,Integer fee);

    List<OutletsOrderPayTraceVO> findlist(Map<String, String> paramsMap);

    List<Map<String, Object>> countSum(Map<String, String> paramsMap);

    List<Map<String, Object>> countAmountSumGroupByMerchId(Map<String, String> paramsMap);

    /**
     * 包装订单支付流水数据
     * @param result
     * @return
     */
    List<OutletsOrderPayTraceExcelVO> packageOutletsOrderPayTrace(List<OutletsOrderPayTraceVO> result);

    /**
     * 统计近三个月每月的销售额
     * @return
     */
    List<Map<String, Object>> countLastThreeMonthsAmount();

    /**
     * 统计近一周每天的销售额
     * @return
     */
    List<Map<String, Object>> countLastSevenDaysAmount();

    /**
     * 根据订单号 查询可退款金额
     * @param paramsMap
     * @return
     */
    Map<String, Object> findRefundableAmount(Map<String, String> paramsMap);

    List<OutletsOrderPayTrace> checkHaveOrder(String cashId);

    Page<OutletsOrderPayTrace> posQueryTrace(PosSearchTraceData posSearchTraceData);

    List<Map<String, Object>> countAmountSumGroupByType(Map<String, String> paramsMap);

    OutletsOrderRefTrace queryByRefTraceNoOrPayCode(CheckPayTraceData checkPayTraceData) throws Exception;

    List<OutletsOrderPayTrace> queryPosTaskCheckPayStateOrder();


    OutletsOrderPayTrace queryByRefBatchCode(String refBatchCode);
}
