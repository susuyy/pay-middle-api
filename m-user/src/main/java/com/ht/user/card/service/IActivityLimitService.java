package com.ht.user.card.service;

import com.ht.user.card.entity.ActivityLimit;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-28
 */
public interface IActivityLimitService extends IService<ActivityLimit> {

    /**
     * 获取活动规则
     * @param activityCode
     * @return
     */
    List<ActivityLimit> getLimitByCode(String activityCode);

    /**
     * 根据code，删除limit
     * @param activityCode
     */
    void removeByCode(String activityCode);

    /**
     * 保存活动门槛规则
     * @param activityLimit
     * @return
     */
    @Override
    boolean save(ActivityLimit activityLimit);

    /**
     * 获取活动满足的最高等级,如果没有满足的，则返回null
     * @param amount
     * @param activityCode
     * @return
     */
    ActivityLimit getActiveLevel(Integer amount, String activityCode);

    /**
     * 装饰activityLimitMapCard
     * @param activityLimit
     */
    void decorateLimitMapCard(ActivityLimit activityLimit);
}
