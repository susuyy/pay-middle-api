package com.ht.feignapi.tonglian.card.limit;

import com.ht.feignapi.tonglian.card.entity.CardLimits;
import com.ht.feignapi.tonglian.utils.TimeUtil;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/10 17:24
 */
public class GetDateRangeLimit implements LimitStrategy {

    private CardLimits cardLimits;

    public  GetDateRangeLimit(CardLimits cardLimits){
        this.cardLimits = cardLimits;
    }

    @Override
    public Boolean checkLimit() {
        Date now = new Date();
        String[] timeRangeLimit = cardLimits.getLimitKey().split("~");
        Assert.isTrue(timeRangeLimit.length==2,"小时限制参数有误");
        String date = TimeUtil.format(now,"yyyy-MM-dd");
        Date beginTime = TimeUtil.parseDate(timeRangeLimit[0],"yyyy-MM-dd HH:mm");
        Date endTime =TimeUtil.parseDate( timeRangeLimit[1],"yyyy-MM-dd HH:mm");
        return now.after(beginTime) && now.before(endTime);
    }
}
