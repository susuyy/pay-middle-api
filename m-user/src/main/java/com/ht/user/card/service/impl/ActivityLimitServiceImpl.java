package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ht.user.card.entity.ActivityLimit;
import com.ht.user.card.entity.ActivityLimitMapCard;
import com.ht.user.card.entity.CardCards;
import com.ht.user.card.entity.CardMapMerchantCards;
import com.ht.user.card.mapper.ActivityLimitMapper;
import com.ht.user.card.service.CardCardsService;
import com.ht.user.card.service.CardMapMerchantCardsService;
import com.ht.user.card.service.IActivityLimitMapCardService;
import com.ht.user.card.service.IActivityLimitService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
public class ActivityLimitServiceImpl extends ServiceImpl<ActivityLimitMapper, ActivityLimit> implements IActivityLimitService {

    @Autowired
    private IActivityLimitMapCardService activityLimitMapCardService;

    @Autowired
    private CardMapMerchantCardsService cardMapMerchantCardsService;

    @Autowired
    private CardCardsService cardCardsService;

    @Override
    public List<ActivityLimit> getLimitByCode(String activityCode) {
        LambdaQueryWrapper<ActivityLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLimit::getActivityCode, activityCode);
        return this.list(wrapper);
    }

    @Override
    public void removeByCode(String activityCode) {
        LambdaQueryWrapper<ActivityLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLimit::getActivityCode, activityCode);
        this.remove(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(ActivityLimit activityLimit) {
        if (activityLimit.getCardCountMap() != null) {
            List<ActivityLimitMapCard> activityLimitMapCards = new ArrayList<>();
            activityLimit.getCardCountMap().forEach((key,value)->{
                CardCards cards = cardCardsService.queryByCardCode(key);
                CardMapMerchantCards cardExist = cardMapMerchantCardsService.getActivityCard(cards.getCardCode(),activityLimit.getMerchantCode());
                if (cardExist==null){
                    cardMapMerchantCardsService.createActivityCard(cards,activityLimit.getMerchantCode(),activityLimit.getActivityCode());
                }
                ActivityLimitMapCard activityLimitMapCard = new ActivityLimitMapCard();
                activityLimitMapCard.setActivityCode(activityLimit.getActivityCode());
                activityLimitMapCard.setAmount(value);
                activityLimitMapCard.setCardCode(key);
                activityLimitMapCard.setLimitKey(activityLimit.getLimitKey());
                activityLimitMapCard.setCardName(cards==null?"":cards.getCardName());
                activityLimitMapCards.add(activityLimitMapCard);
            });
            activityLimitMapCardService.saveBatch(activityLimitMapCards);
        }
        return super.save(activityLimit);
    }

    @Override
    public ActivityLimit getActiveLevel(Integer amount, String activityCode) {
        LambdaQueryWrapper<ActivityLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivityLimit::getActivityCode,activityCode);
        wrapper.orderByAsc(ActivityLimit::getPriority);
        List<ActivityLimit> list = this.list(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        for (ActivityLimit activityLimit: list){
            if (amount >= Integer.parseInt(activityLimit.getLimitKey())){
                decorateLimitMapCard(activityLimit);
                return activityLimit;
            }
        }
        return null;
    }

    @Override
    public void decorateLimitMapCard(ActivityLimit activityLimit){
        List<ActivityLimitMapCard> list = activityLimitMapCardService.getActivityLimitMapCards(activityLimit.getActivityCode(),activityLimit.getLimitKey());
        activityLimit.setCardsList(list);
    }
}
