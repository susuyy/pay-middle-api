package com.ht.feignapi.mall.service;

import com.ht.feignapi.appconstant.CategoryConstant;
import com.ht.feignapi.mall.clientservice.OrderCategoriesClientService;
import com.ht.feignapi.mall.clientservice.OrderProductionsClientService;
import com.ht.feignapi.mall.constant.MerchantCardConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.config.CardConstant;
import com.ht.feignapi.tonglian.config.CardType;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.utils.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.condition.ProducesRequestCondition;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zheng weiguang
 * @Date: 2020/9/21 15:36
 */
@Service
public class MallProductionService {

    @Autowired
    private CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    private OrderProductionsClientService productionsClientService;

    @Autowired
    private OrderCategoriesClientService orderCategoriesClientService;

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    public void saveCardProduction(OrderProductions productions, Inventory inventory){
        CardCards card = new CardCards();
        BeanUtils.copyProperties(productions,card);
        card.setType(CardType.NUMBER.getKey());
        card.setFaceValue(1);
        card.setCardName(productions.getProductionName());
        card.setCardCode(productions.getProductionCode());
        card.setCardPicUrl(productions.getProductionPicUrl());
        card.setBatchCode(inventory.getBatchCode());
        card.setValidityType(CardConstant.BEGIN_TO_END);
        card.setState(CardConstant.CARD_STATE_NORMAL);
        merchantCardClientService.saveMallSellCards(productions.getMerchantCode(),card);
    }


    public void updateCardProduction(OrderProductions productions) {
        CardCards card = cardsClientService.getCardByCardCode(productions.getProductionCode()).getData();
        BeanUtils.copyProperties(productions,card);
        card.setCardName(productions.getProductionName());
        card.setCardCode(productions.getProductionCode());
        card.setCardPicUrl(productions.getProductionPicUrl());
        merchantCardClientService.saveMallSellCards(productions.getMerchantCode(),card);
    }

    public boolean isCardProduction(String categoryLevel3Code, String merchantCode) {
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        Result<OrderCategorys> categoriesResult = orderCategoriesClientService.queryLevelOneCode(categoryLevel3Code,merchants.getBusinessSubjects());
        Assert.notNull(categoriesResult,"获取商品分类出错!");
        Assert.notNull(categoriesResult.getData(),"获取商品分类出错!");
        return categoriesResult.getData().getCategoryLevel01Code().equals(CategoryConstant.CARDS);
    }

    public void decorateProductionInstrument(MallProductions production){
        boolean cardProduction = this.isCardProduction(production.getCategoryCode(),production.getMerchantCode());
        String instrumentType = cardProduction?ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE : ProductionsCategoryConstant.OTHER_PRODUCTION;
        Result<List<OrderProductionsInstruction>> instruments = productionsClientService.getProductionInstruments(production.getMerchantCode(),production.getProductionCode(),instrumentType);
        List<String> instrumentStrList;
        if (instruments!=null && !CollectionUtils.isEmpty(instruments.getData())){
            instrumentStrList = instruments.getData().stream().map(OrderProductionsInstruction::getInstruction).collect(Collectors.toList());
            production.setInstruments(instrumentStrList);
        }else {
            production.setInstruments(new ArrayList<>());
        }
    }

    public void decorateProductionInstrument(OrderProductions production){
        boolean cardProduction = this.isCardProduction(production.getCategoryCode(),production.getMerchantCode());
        String instrumentType = cardProduction?ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE : ProductionsCategoryConstant.OTHER_PRODUCTION;
        Result<List<OrderProductionsInstruction>> instruments = productionsClientService.getProductionInstruments(production.getMerchantCode(),production.getProductionCode(),instrumentType);
        List<String> instrumentStrList = new ArrayList<>();
        if (instruments!=null && !CollectionUtils.isEmpty(instruments.getData())){
            instrumentStrList = instruments.getData().stream().map(OrderProductionsInstruction::getInstruction).collect(Collectors.toList());
            production.setInstruments(instrumentStrList);
        }else {
            production.setInstruments(new ArrayList<>());
        }
    }

    public void parseEndDate(MallProductions production) {
        boolean cardProduction = this.isCardProduction(production.getCategoryCode(),production.getMerchantCode());
        if (cardProduction){
            Result<CardMapMerchantCards> merchantCards = merchantCardClientService.getMerchantCard(production.getMerchantCode(),production.getProductionCode());
            Assert.notNull(merchantCards,"商品对应的卡号不存在!");
            Assert.notNull(merchantCards.getData(),"商品对应的卡号不存在!");
            if (merchantCards.getData().getHaltSaleDate()==null){
                //如果没有填写，默认显示1天后到期。
                production.setEndDate(TimeUtil.addHours(new Date(),1));
            }else {
                production.setEndDate(merchantCards.getData().getHaltSaleDate());
            }
        }else {
            Result<OrderProductions> orderProductionsResult = productionsClientService.getByCode(production.getProductionCode(),production.getMerchantCode());
            Assert.notNull(orderProductionsResult,"对应的实体商品不存在!");
            Assert.notNull(orderProductionsResult.getData(),"对应的实体商品不存在!");
            production.setEndDate(orderProductionsResult.getData().getValidTo());
        }
    }

}
