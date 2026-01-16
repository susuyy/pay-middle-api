package com.ht.user.card.service;

import com.ht.user.card.entity.ActivityLimitMapCard;
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
public interface IActivityLimitMapCardService extends IService<ActivityLimitMapCard> {

    /**
     * 获取活动卡券号code列表
     * @param activityCode
     * @return
     */
    List<ActivityLimitMapCard> getListByActivityCode(String activityCode);

    void removeByCode(String activityCode);

    /**
     * 获取某个limit下对应的card
     * @param activityCode
     * @param limitKey
     * @return
     */
    List<ActivityLimitMapCard> getActivityLimitMapCards(String activityCode, String limitKey);
}
