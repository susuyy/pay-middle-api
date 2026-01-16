package com.ht.user.card.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.card.entity.CardMapUserCardsTrace;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 15:49
 */
public interface CardMapUserCardsTraceService extends IService<CardMapUserCardsTrace> {

    /**
     * 用户与卡的绑定关系 流水创建
     * @param userId
     * @param merchantCode
     * @param cardCode
     * @param cardNo
     * @param actionType
     * @param actionDate
     * @param state
     * @param batchCode
     */
    void createCardMapUserCardsTrace(Long userId, String merchantCode, String cardCode, String cardNo, String actionType, Date actionDate, String state, String batchCode);

    /**
     * 分页获取pos 发券流水
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @param actionType
     * @return
     */
    IPage listPage(String merchantCode, Integer pageNo, Integer pageSize, String actionType);
}
