package com.ht.feignapi.tonglian.card.limit;

import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardLimits;
import com.ht.feignapi.tonglian.utils.TimeUtil;

import java.util.Date;

/**
 * @description: 用于判断pos每天领取上限
 * @author: zheng weiguang
 * @Date: 2020/7/8 10:14
 */

public class DailyLimit implements LimitStrategy{
    private CardMapUserClientService userCardsService;

    private final CardLimits cardLimits;
    private final Long userId;
    private final String batchCode;


    public DailyLimit(CardLimits cardLimits, Long userId, CardMapUserClientService userCardsService, String batchCode){
        this.userCardsService = userCardsService;
        this.cardLimits = cardLimits;
        this.userId = userId;
        this.batchCode = batchCode;
    }

    @Override
    public Boolean checkLimit() {
        String date = TimeUtil.format(new Date(),"yyyy-MM-dd");
        Integer cardAmount = userCardsService.getUserCardAmount(userId,batchCode,cardLimits.getCardCode(),date).getData();
        return cardAmount < Integer.parseInt(this.cardLimits.getLimitKey());
    }
}
