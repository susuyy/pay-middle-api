package com.ht.feignapi.tonglian.card.limit;

import com.ht.feignapi.tonglian.card.entity.CardLimits;
import com.ht.feignapi.tonglian.utils.TimeUtil;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 17:30
 */
public class HourLimit implements LimitStrategy {

    private CardLimits cardLimits;

    public HourLimit(CardLimits limits){
        this.cardLimits = limits;
    }

    @Override
    public Boolean checkLimit() {
        Date now = new Date();
        String[] hourLimit = cardLimits.getLimitKey().split("-");
        Assert.isTrue(hourLimit.length==2,"小时限制参数有误");
        String date = TimeUtil.format(now,"yyyy-MM-dd");
        Date beginTime = TimeUtil.parseDate(date + " " + hourLimit[0],"yyyy-MM-dd HH:mm");
        Date endTime =TimeUtil.parseDate(date + " " + hourLimit[1],"yyyy-MM-dd HH:mm");
        return now.after(beginTime) && now.before(endTime);
    }
}
