package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ht.user.card.entity.ActivityLimitMapCard;
import com.ht.user.card.mapper.ActivityLimitMapCardMapper;
import com.ht.user.card.service.IActivityLimitMapCardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-28
 */
@Service
public class ActivityLimitMapCardServiceImpl extends ServiceImpl<ActivityLimitMapCardMapper, ActivityLimitMapCard> implements IActivityLimitMapCardService {

    @Override
    public List<ActivityLimitMapCard> getListByActivityCode(String activityCode) {
        LambdaQueryWrapper<ActivityLimitMapCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLimitMapCard::getActivityCode,activityCode);
        return this.list(wrapper);
    }

    @Override
    public void removeByCode(String activityCode) {
        LambdaQueryWrapper<ActivityLimitMapCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLimitMapCard::getActivityCode,activityCode);
        this.remove(wrapper);
    }

    @Override
    public List<ActivityLimitMapCard> getActivityLimitMapCards(String activityCode, String limitKey) {
        LambdaQueryWrapper<ActivityLimitMapCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLimitMapCard::getActivityCode,activityCode);
        wrapper.eq(ActivityLimitMapCard::getLimitKey,limitKey);
        return this.list(wrapper);
    }
}
