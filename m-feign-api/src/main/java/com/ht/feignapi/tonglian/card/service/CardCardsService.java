package com.ht.feignapi.tonglian.card.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardLimits;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 卡定义 服务实现类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Service
public class CardCardsService {



    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private CardMapMerchantCardClientService cardMapMerchantCardClientService;

    /**
     * 根据卡号查询卡信息
     *
     * @param cardCode
     * @return
     */
    public CardCards queryByCardCode(String cardCode, String merchantCode) {
        CardCards cardCards = cardsClientService.getCardByCardCode(cardCode).getData();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
//        List<CardLimits> limits = cardLimitsService.getLimitsByCardCode(cardCode,"");
//        cardCards.setMerchantsPic(merchants.getMerchantPicUrl());
//        cardCards.setMerchantsAddress(merchants.getLocation());
//        cardCards.setMerchantsPhone(merchants.getMerchantContact());
//        cardCards.setMerchantsName(merchants.getMerchantName());
//        cardCards.setLimits(limits);
        return cardCards;
    }

}
