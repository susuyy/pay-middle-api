package com.ht.feignapi.tonglian.card.limit;


import com.ht.feignapi.tonglian.card.entity.CardLimits;

import java.util.Calendar;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 17:30
 */
public class DateLimit implements LimitStrategy {

    private CardLimits cardLimits;

    public DateLimit(CardLimits limits){
        this.cardLimits = limits;
    }

    @Override
    public Boolean checkLimit() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(cardLimits.getLimitKey());
    }
}
