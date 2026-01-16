package com.ht.user.outlets.service;

import com.ht.user.outlets.entity.OutletsOrderRefTrace;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-16
 */
public interface IOutletsOrderRefTraceService extends IService<OutletsOrderRefTrace> {

    /**
     * 根据订单号查询 通联响应的数据
     * @param orderCode
     * @return
     */
    OutletsOrderRefTrace queryByReqsn(String orderCode);

    /**
     * 查询收银宝中的订单 , 更新本地订单状态
     * @param outletsOrderRefTrace
     */
    void querySybOrderUpDateLocal(OutletsOrderRefTrace outletsOrderRefTrace) throws Exception;

    /**
     * 根据交易类型统计交易成功的 实际交易金额总和
     * @param paramsMap
     * @return
     */
    List<Map<String, Object>> countTrxamt(Map<String, String> paramsMap);

    /**
     * 统计近一周每天的实际交易金额金额（根据交易类型不同划分） 只统计0000交易成功
     * @return
     */
    List<Map<String, Object>> countLastSevenDaysAmount();

    List<OutletsOrderRefTrace> queryTaskCheckPayStateOrder();


}
