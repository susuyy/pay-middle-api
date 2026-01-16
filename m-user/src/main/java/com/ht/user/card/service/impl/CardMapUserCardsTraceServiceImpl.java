package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.card.entity.CardMapUserCardsTrace;
import com.ht.user.card.mapper.CardMapUserCardsTraceMapper;
import com.ht.user.card.service.CardMapUserCardsTraceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 15:49
 */
@Service
public class CardMapUserCardsTraceServiceImpl extends ServiceImpl<CardMapUserCardsTraceMapper, CardMapUserCardsTrace> implements CardMapUserCardsTraceService {

    /**
     * 用户与卡的绑定关系 流水创建
     *
     * @param userId
     * @param merchantCode
     * @param cardCode
     * @param cardNo
     * @param actionType
     * @param actionDate
     * @param state
     * @param batchCode
     */
    @Override
    public void createCardMapUserCardsTrace(Long userId, String merchantCode, String cardCode, String cardNo, String actionType, Date actionDate, String state, String batchCode) {
        CardMapUserCardsTrace cardMapUserCardsTrace = new CardMapUserCardsTrace();
        cardMapUserCardsTrace.setUserId(userId);
        cardMapUserCardsTrace.setActionDate(actionDate);
        cardMapUserCardsTrace.setMerchantCode(merchantCode);
        cardMapUserCardsTrace.setCardCode(cardCode);
        cardMapUserCardsTrace.setCardNo(cardNo);
        cardMapUserCardsTrace.setActionType(actionType);
        cardMapUserCardsTrace.setActionDate(actionDate);
        cardMapUserCardsTrace.setState(state);
        cardMapUserCardsTrace.setBatchCode(batchCode);
        this.baseMapper.insert(cardMapUserCardsTrace);
    }

    @Override
    public IPage<CardMapUserCardsTrace> listPage(String merchantCode, Integer pageNo, Integer pageSize, String actionType) {
        IPage<CardMapUserCardsTrace> iPage = new Page<>(pageNo, pageSize);
        QueryWrapper<CardMapUserCardsTrace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_code",merchantCode);
        if (!StringUtils.isEmpty(actionType)){
            queryWrapper.eq("action_type",actionType);
        }else {
            queryWrapper.like("action_type","use");
        }
        queryWrapper.orderByDesc("create_at");
        return this.baseMapper.selectPage(iPage, queryWrapper);
    }
}
