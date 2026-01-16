package com.ht.user.outlets.service;

import com.ht.user.outlets.entity.OutletsOrderPosRefTrace;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-22
 */
public interface IOutletsOrderPosRefTraceService extends IService<OutletsOrderPosRefTrace> {

    /**
     * 统计近一周每天的银行卡日交易额 只统计rejcode=00交易成功
     * @return
     */
    List<Map<String, Object>> countLastSevenDaysAmount();

    OutletsOrderPosRefTrace queryByOrderCode(String orderCode);

}
