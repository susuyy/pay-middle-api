package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.common.ActivityConstant;
import com.ht.user.card.common.CardConstant;
import com.ht.user.card.entity.Activity;
import com.ht.user.card.entity.ActivityLimit;
import com.ht.user.card.entity.ActivityLimitMapCard;
import com.ht.user.card.entity.CardCards;
import com.ht.user.card.mapper.ActivityMapper;
import com.ht.user.card.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-28
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    @Autowired
    private IActivityLimitService activityLimitService;

    @Autowired
    private CardMapMerchantCardsService merchantCardsService;

    @Autowired
    private IActivityLimitMapCardService activityMapCardService;

    @Override
    public Activity getById(Serializable id) {
        Activity activity = super.getById(id);
        List<ActivityLimit> limits = activityLimitService.getLimitByCode(activity.getActivityCode());
        activity.setLimitList(limits);
        if (!CollectionUtils.isEmpty(limits)) {
            limits.forEach(e -> {
                activityLimitService.decorateLimitMapCard(e);
            });
        }
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Activity activity) {
        activity.setActivityCode(IdWorker.getIdStr());
        if (!CollectionUtils.isEmpty(activity.getLimitList())) {
            activity.getLimitList().forEach(e->{
                e.setMerchantCode(activity.getMerchantCode());
                e.setActivityCode(activity.getActivityCode());
                activityLimitService.save(e);
            });
        }
        return super.save(activity);
    }

    @Override
    public Page<Activity> getList(Page<Activity> page, String objMerchantCode) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getMerchantCode, objMerchantCode);
        wrapper.ne(Activity::getState,ActivityConstant.DELETED);
        return this.baseMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        Activity activity = super.getById(id);
        if (activity == null) {
            return false;
        }
        activity.setState(ActivityConstant.DELETED);
        activityLimitService.removeByCode(activity.getActivityCode());
        activityMapCardService.removeByCode(activity.getActivityCode());
        return super.updateById(activity);
    }

    @Override
    public Activity getActiveActivity(String merchantCode) {
        Date now = new Date();
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getMerchantCode,merchantCode);
        wrapper.le(Activity::getValidFrom,now);
        wrapper.ge(Activity::getValidTo,now);
        wrapper.eq(Activity::getState, ActivityConstant.ENABLE);
        wrapper.orderByAsc(Activity::getPriority);
        List<Activity> list = this.list(wrapper);
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }
}
