package com.ht.feignapi.tonglian.card.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.tonglian.card.clientservice.CardLimitsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardLimits;
import com.ht.feignapi.tonglian.card.limit.LimitFactory;
import com.ht.feignapi.tonglian.card.limit.LimitStrategy;
import com.ht.feignapi.tonglian.card.limit.TotalLimit;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
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
public class CardLimitsService  {

    @Autowired
    private CardMapUserClientService cardMapUserCardsService;

    @Autowired
    private MapMerchantPrimesClientService merchantPrimesService;

    @Autowired
    private CardLimitsClientService cardLimitsClientService;


    public boolean checkCardGetLimit(String cardCode, String merchantCode, Long userId, String batchCode) throws CheckException {
        List<CardLimits> cardLimitsList = cardLimitsClientService.getLimits(cardCode,"get_card_limit_type",batchCode).getData();
        return this.checkGetLimit(cardLimitsList, merchantCode, userId, batchCode);
    }

    public boolean checkCardUseLimit(String cardCode, String merchantCode, Long userId, String batchCode) {
        List<CardLimits> cardLimitsList = getCardAllLimits(cardCode, batchCode);
        return checkUseLimit(cardLimitsList,merchantCode,userId,batchCode);
    }

    public boolean checkCardUseLimit(String cardCode, String merchantCode, Long userId, Integer amount, String batchCode) {
        List<CardLimits> cardLimitsList = getCardAllLimits(cardCode, batchCode);
        return checkUseLimit(cardLimitsList,merchantCode,userId,batchCode,amount);
    }

    private List<CardLimits> getCardAllLimits(String cardCode, String batchCode) {
        List<CardLimits> cardLimitsList = cardLimitsClientService.getLimits(cardCode, "card_limit_type", batchCode).getData();
        List<CardLimits> cardCreateLimits = cardLimitsClientService.getLimitsByCardCodeWithOutBatchCode(cardCode).getData();
        cardLimitsList.addAll(cardCreateLimits);
        return cardLimitsList;
    }


    public List<CardLimits> queryCardGetLimit(String cardCode, String batchCode) {
        List<CardLimits> cardLimitsList = cardLimitsClientService.getLimits(cardCode,"get_card_limit_type",batchCode).getData();
        return cardLimitsList;
    }


    private boolean checkGetLimit(List<CardLimits> cardLimitsList, String merchantCode, Long userId, String batchCode) throws CheckException {
        List<CardLimits> levelLimits = cardLimitsList.stream().filter(e -> "GET-LEVEL-LIMIT".equals(e.getType())).collect(Collectors.toList());
        boolean levelLimitResult = checkInGroupLimit(levelLimits, merchantCode, userId, batchCode);
        if (!levelLimitResult){
            throw new CheckException("用户等级不足，无领券条件");
        }
        List<CardLimits> dayLimits = cardLimitsList.stream().filter(e -> "GET-DAY-LIMIT".equals(e.getType())).collect(Collectors.toList());
        boolean dayLimitResult = checkInGroupLimit(dayLimits, merchantCode, userId, batchCode);
        if (!dayLimitResult){
            throw new CheckException("不在领取日期");
        }
        List<CardLimits> weekLimits = cardLimitsList.stream().filter(e -> "GET-WEEK-LIMIT".equals(e.getType())).collect(Collectors.toList());
        boolean weekLimitResult = checkInGroupLimit(weekLimits, merchantCode, userId, batchCode);
        if (!weekLimitResult){
            throw new CheckException("不在领取日期");
        }
        List<CardLimits> hourLimits = cardLimitsList.stream().filter(e -> "GET-HOUR-LIMIT".equals(e.getType())).collect(Collectors.toList());
        boolean hourLimitResult = checkInGroupLimit(hourLimits, merchantCode, userId, batchCode);
        if (!hourLimitResult){
            throw new CheckException("不在领取时间段");
        }
        List<CardLimits> dailyLimit = cardLimitsList.stream().filter(e -> "GET-DAILY-LIMIT".equals(e.getType())).collect(Collectors.toList());
        boolean dailyLimitResult = checkInGroupLimit(dailyLimit, merchantCode, userId, batchCode);
        if (!dailyLimitResult){
            throw new CheckException("每日领取数目已达上限");
        }
        List<CardLimits> totalLimit = cardLimitsList.stream().filter(e -> "GET-TOTAL-LIMIT".equals(e.getType())).collect(Collectors.toList());
        boolean totalLimitResult = checkInGroupLimit(totalLimit, merchantCode, userId, batchCode);
        if (!totalLimitResult){
            throw new CheckException("总领取数目已达上限");
        }
        List<CardLimits> dateRangeLimit = cardLimitsList.stream().filter(e -> "GET-DURATION-LIMIT".equals(e.getType())).collect(Collectors.toList());
        boolean dateRangeLimitResult = checkInGroupLimit(dateRangeLimit, merchantCode, userId, batchCode);
        if (!dateRangeLimitResult){
            throw new CheckException("不在领取时间段");
        }
        return true;
    }

    private boolean checkUseLimit(List<CardLimits> cardLimitsList, String merchantCode, Long userId, String batchCode) {
        List<CardLimits> dayLimits = cardLimitsList.stream().filter(e -> "LIMIT-DAY".equals(e.getType())).collect(Collectors.toList());
        boolean dayLimitResult = checkInGroupLimit(dayLimits, merchantCode, userId, batchCode);
        List<CardLimits> weekLimits = cardLimitsList.stream().filter(e -> "LIMIT-WEEK".equals(e.getType())).collect(Collectors.toList());
        boolean weekLimitResult = checkInGroupLimit(weekLimits, merchantCode, userId, batchCode);
        List<CardLimits> hourLimits = cardLimitsList.stream().filter(e -> "LIMIT-HOUR".equals(e.getType())).collect(Collectors.toList());
        boolean hourLimitResult = checkInGroupLimit(hourLimits, merchantCode, userId, batchCode);
        return dayLimitResult && weekLimitResult && hourLimitResult;
    }

    private boolean checkUseLimit(List<CardLimits> cardLimitsList, String merchantCode, Long userId, String batchCode,Integer amount){
        boolean resultStep1 = this.checkUseLimit(cardLimitsList,merchantCode,userId,batchCode);
        List<CardLimits> totalLimits = cardLimitsList.stream().filter(e -> "LIMIT-TOTAL".equals(e.getType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(totalLimits)){
            return resultStep1;
        }else {
            LimitStrategy limit = new TotalLimit(totalLimits.get(0),amount);
            return  resultStep1&&limit.checkLimit();
        }
    }

    private boolean checkInGroupLimit(List<CardLimits> cardLimits, String merchantCode, Long userId, String batchCode) {
        if (cardLimits.size() == 0) {
            return true;
        }
        for (CardLimits cardLimit : cardLimits) {
            LimitStrategy strategy = LimitFactory.create(cardLimit, merchantCode, userId, batchCode, merchantPrimesService, cardMapUserCardsService);
            if (strategy == null) {
                return true;
            }
            if (strategy.checkLimit()) {
                return true;
            }
        }
        return false;
    }
}
