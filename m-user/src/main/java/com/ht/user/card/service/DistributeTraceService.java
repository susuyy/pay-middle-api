package com.ht.user.card.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.card.entity.DistributeTrace;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-09
 */
public interface DistributeTraceService extends IService<DistributeTrace> {

    /**
     * 保存发卡trace
     * @param merchantCode
     * @param cardCode
     * @param amount 数目
     * @param comments 描述
     * @param source 来源
     * @param activityCode 活动号
     * @param batchCode
     */
    void createDistributeTrace(String merchantCode, String cardCode, int amount, String comments, String source, String activityCode, String batchCode);

    /**
     * 获取发券历史
     * @param traceType
     * @param merchantCode
     * @param page
     * @return
     */
    List<DistributeTrace> getList(String traceType, String merchantCode, IPage<DistributeTrace> page);
}
