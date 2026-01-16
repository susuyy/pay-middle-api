package com.ht.user.card.service;

import com.ht.user.card.entity.CardLimits;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-23
 */
public interface CardLimitsService extends IService<CardLimits> {

    /**
     * 通过卡号，获取卡使用规则
     * @param cardCode 卡号
     * @param batchCode
     * @return 卡规则
     */
    List<CardLimits> getLimitsByCardCode(String cardCode, String batchCode);


    /**
     * 创建卡券限制实例
     * @param type
     * @param cardCode
     * @param limitKey
     * @param batchCode
     */
    void createLimit(String type, String cardCode, String limitKey, String batchCode);

    List<CardLimits> getLimits(String cardCode, String groupCode, String batchCode);

    /**
     * 查询卡券领取的限制条件
     * @param cardCode
     * @param batchCode
     * @return
     */
    List<CardLimits> queryCardGetLimit(String cardCode, String batchCode);

    List<CardLimits> getLimitsByCardCodeWithOutBatchCode(String cardCode);
}
