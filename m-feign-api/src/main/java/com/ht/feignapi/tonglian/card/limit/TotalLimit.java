package com.ht.feignapi.tonglian.card.limit;


import com.ht.feignapi.tonglian.card.entity.CardLimits;
import lombok.Data;

/**
 * @author: suyangyu
 * @Date: 2020/7/8 17:30
 */
@Data
public class TotalLimit implements LimitStrategy  {

    private CardLimits cardLimits;

    private Integer amount;

    public TotalLimit(CardLimits limits){
        this.cardLimits = limits;
    }


    public TotalLimit(CardLimits limits,Integer amount){
        this.cardLimits = limits;
        this.amount=amount;
    }

    @Override
    public Boolean checkLimit() {
        int totalNeedPayMoneyInt=Integer.parseInt(cardLimits.getLimitKey());
        return amount >= totalNeedPayMoneyInt;
    }
}
