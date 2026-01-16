package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ht.user.card.entity.CardLimits;
import com.ht.user.card.mapper.CardLimitsMapper;
import com.ht.user.card.service.CardLimitsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.card.service.CardMapUserCardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-23
 */
@Service
public class CardLimitsServiceImpl extends ServiceImpl<CardLimitsMapper, CardLimits> implements CardLimitsService {

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    @Override
    public List<CardLimits> getLimitsByCardCode(String cardCode, String batchCode) {
        LambdaQueryWrapper<CardLimits> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardLimits::getCardCode, cardCode);
        if (!StringUtils.isEmpty(batchCode))
        {
            wrapper.eq(CardLimits::getBatchCode,batchCode);
        }
        return this.list(wrapper);
    }

    @Override
    public void createLimit(String type, String cardCode, String limitKey, String batchCode) {
        CardLimits limit = new CardLimits();
        limit.setType(type);
        limit.setCardCode(cardCode);
        limit.setLimitKey(limitKey);
        limit.setBatchCode(batchCode);
        limit.setState("1");
        this.save(limit);
    }

    @Override
    public List<CardLimits> getLimits(String cardCode, String groupCode, String batchCode) {
        return this.baseMapper.getLimits(cardCode,groupCode,batchCode);
    }

    private List<CardLimits> getCardAllLimits(String cardCode, String batchCode) {
        List<CardLimits> cardLimitsList = this.baseMapper.getLimits(cardCode, "card_limit_type", batchCode);
        List<CardLimits> cardCreateLimits = this.baseMapper.getLimitsByCardCodeWithOutBatchCode(cardCode);
        cardLimitsList.addAll(cardCreateLimits);
        return cardLimitsList;
    }


    @Override
    public List<CardLimits> queryCardGetLimit(String cardCode, String batchCode) {
        List<CardLimits> cardLimitsList = this.baseMapper.getLimits(cardCode,"get_card_limit_type",batchCode);
        return cardLimitsList;
    }

    @Override
    public List<CardLimits> getLimitsByCardCodeWithOutBatchCode(String cardCode) {
        return this.baseMapper.getLimitsByCardCodeWithOutBatchCode(cardCode);
    }




}
