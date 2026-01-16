package com.ht.feignapi.tonglian.card.limit;

import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardLimits;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import org.springframework.stereotype.Service;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 14:09
 */
@Service
public class LimitFactory {

    public static LimitStrategy create(CardLimits cardLimits, String merchantCode, Long userId, String batchCode,
                                       MapMerchantPrimesClientService merchantPrimesService, CardMapUserClientService userCardService) {
        if ("GET-LEVLE-LIMIT".equals(cardLimits.getType())){
            //同组或
            return new LevelLimit(userId,merchantCode,cardLimits.getLimitKey(),merchantPrimesService);
        }
        if ("GET-DAILY-LIMIT".equals(cardLimits.getType())){
            return new DailyLimit(cardLimits,userId,userCardService, batchCode);
        }
        if ("GET-TOTAL-LIMIT".equals(cardLimits.getType())){
            return new GetTotalLimit(cardLimits,userId,userCardService,batchCode);
        }
        if ("GET-DURATION-LIMIT".equals(cardLimits.getType())){
            return new GetDateRangeLimit(cardLimits);
        }
        if ("GET-DAY-LIMIT".equals(cardLimits.getType())){
            //同组或
            return new DateLimit(cardLimits);
        }
        if ("GET-WEEK-LIMIT".equals(cardLimits.getType())){
            //同组或
            return new WeekLimit(cardLimits);
        }
        if ("GET-HOUR-LIMIT".equals(cardLimits.getType())){
            //同组或
            return new HourLimit(cardLimits);
        }
        return null;
    }
}
