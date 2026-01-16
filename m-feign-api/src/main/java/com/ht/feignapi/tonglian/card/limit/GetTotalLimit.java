package com.ht.feignapi.tonglian.card.limit;


import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardLimits;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 10:18
 */
public class GetTotalLimit implements LimitStrategy{
    private CardMapUserClientService userCardsService;

    private final CardLimits cardLimits;
    private final Long userId;
    private final String batchCode;

    public GetTotalLimit(CardLimits cardLimits, Long userId,CardMapUserClientService userCardsService,String batchCode){
        this.userCardsService = userCardsService;
        this.cardLimits = cardLimits;
        this.userId = userId;
        this.batchCode = batchCode;
    }

    @Override
    public Boolean checkLimit() {
        Integer cardAmount = userCardsService.getUserCardAmount(userId,batchCode,cardLimits.getCardCode(),"").getData();
        return cardAmount < Integer.parseInt(this.cardLimits.getLimitKey());
    }
}
