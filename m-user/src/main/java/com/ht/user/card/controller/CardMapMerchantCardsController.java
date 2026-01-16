package com.ht.user.card.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.MerchantCardListVo;
import com.ht.user.admin.vo.MerchantCardSearch;
import com.ht.user.card.common.CardConstant;
import com.ht.user.card.common.CardType;
import com.ht.user.card.entity.*;
import com.ht.user.card.service.*;
import com.ht.user.config.MerchantCardsType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * <p>
 * 商家卡券 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@RestController
@RequestMapping("/merchant-card")
@CrossOrigin(allowCredentials = "true")
public class CardMapMerchantCardsController {

    @Autowired
    private CardMapMerchantCardsService cardMapMerchantCardsService;

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    @Autowired
    private CardCardsService cardCardsService;

    /**
     * 查找merchantCard
     * @param cardCode
     * @param merchantCode
     * @param batchCode
     * @return
     */
    @GetMapping("/{cardCode}/{merchantCode}/{batchCode}")
    public CardMapMerchantCards queryByCardCodeAndMerchantCodeBatchCode(@PathVariable("cardCode") String cardCode, @PathVariable("merchantCode") String merchantCode, @PathVariable("batchCode") String batchCode){
        return cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardCode,merchantCode,batchCode);
    }

    /**
     * 商城根据cardCode和merchantCode查询
     * @param merchantCode
     * @param cardCode
     * @return
     */
    @GetMapping("/mallCard/{merchantCode}/{cardCode}")
    public CardMapMerchantCards getMallMerchantCard(@PathVariable("merchantCode") String merchantCode,@PathVariable("cardCode") String cardCode){
        return cardMapMerchantCardsService.getMerchantCard(merchantCode,cardCode);
    }

    /**
     * 通过cardCode和merchantCode获取该cardCode所有的商户列表
     * @param cardCode
     * @return
     */
    @GetMapping("/owners/{cardCode}")
    public List<String> getCardOwnerMerchants(@PathVariable("cardCode") String cardCode){
        return cardMapMerchantCardsService.getCardMerchants(cardCode);
    }

    /**
     * 获取主体与子商户所有的卡券
     * @param merchantCardSearch
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/allMerchantCards")
    public IPage<MerchantCardListVo> getObjectAndSonMerchantCards(
            @RequestBody(required = false) MerchantCardSearch merchantCardSearch,
            @RequestParam(value = "pageNo",required = false,defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize){
        IPage<MerchantCardListVo> page = new Page<>(pageNo,pageSize);
        List<MerchantCardListVo> list = cardMapMerchantCardsService.getObjectAndSonMerchantCards(merchantCardSearch.getMerchantCodes(),merchantCardSearch,page);
        list.forEach(e -> {
            e.setPrice(e.getPrice() / 100);
        });
        page.setRecords(list);
        System.out.println(JSON.toJSON(page));
        return page;
    }

    /**
     * 获取商户所有的卡券
     * @param merchantCode
     * @param merchantCardSearch
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/selfCards/{merchantCode}")
    public IPage<MerchantCardListVo> getCardProductsByMerchantCode(
            @PathVariable("merchantCode") String merchantCode,@RequestBody(required = false) MerchantCardSearch merchantCardSearch,
            @RequestParam(value = "pageNo",required = false,defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize){
        IPage<MerchantCardListVo> page = new Page<>(pageNo,pageSize);
        System.out.println(page);
        List<MerchantCardListVo> list = cardMapMerchantCardsService.getCardProductsByMerchantCode(merchantCode,merchantCardSearch,page);
        list.forEach(e -> {
            e.setPrice(e.getPrice() / 100);
        });
        page.setRecords(list);
        return page;
    }

    /**
     * 通过卡号批次号，获取卡实例信息
     * @param cardCode
     * @param batchCode
     * @return
     */
    @GetMapping("/msg/{cardCode}/{batchCode}")
    public CardMapMerchantCards queryByCardCodeAndBatchCode(@PathVariable("cardCode") String cardCode,@PathVariable("batchCode") String batchCode){
        return cardMapMerchantCardsService.queryByCardCodeAndBatchCode(cardCode,batchCode);
    }

    /**
     * 获取需要显示属性
     * @param cardMapMerchantCards
     * @return
     */
    @GetMapping("/cardMsg")
    public CardCards getShowOtherData(@RequestBody CardMapMerchantCards cardMapMerchantCards){
        return cardMapMerchantCardsService.getShowOtherData(cardMapMerchantCards);
    }

    /**
     * 获取某个商户下的某种类型卡券
     * @param merchantCode
     * @param type
     * @return
     */
    @GetMapping("/{merchantCode}/type/{type}")
    public List<CardMapMerchantCards> queryListByMerchantCode(@PathVariable("merchantCode") String merchantCode,@PathVariable("type") String type) throws ParseException {
        return cardMapMerchantCardsService.queryListByMerchantCode(merchantCode,type);
    }

    /**
     * 获取用户虚拟卡券列表 不包含计次券
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @GetMapping("/{merchantCode}/userCard/{userId}/{state}")
    public List<CardMapUserCards> selectByUserIdAndMerchantCodeNoNumber(
            @PathVariable("userId") Long userId,
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("state") String state){
        return cardMapUserCardsService.getCardPayList(userId,merchantCode,state);
    }


    /**
     * 商城 查询 卡券类商品 封装完整数据
     * @param cardCode
     * @param storeMerchantCode
     * @param type
     * @return
     */
    @GetMapping("/mallQueryCodeMerchantCodeType")
    public CardMapMerchantCards mallQueryCodeMerchantCodeType(@RequestParam("cardCode") String cardCode,
                                                              @RequestParam("storeMerchantCode") String storeMerchantCode,
                                                              @RequestParam("type")String type){
        return cardMapMerchantCardsService.mallQueryCodeMerchantCodeType(cardCode,storeMerchantCode,type);
    }

    /**
     * 保存商城出售的门票之类的一次性券
     * @param merchantCode
     * @param card
     */
    @PostMapping("/cardProduction/{merchantCode}")
    public void saveMallSellCards(@PathVariable("merchantCode") String merchantCode,@RequestBody CardCards card){
        cardCardsService.saveOrUpdate(card);

        CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.getMerchantCard(merchantCode,card.getCardCode());
        if (cardMapMerchantCards==null){
            cardMapMerchantCards = new CardMapMerchantCards();
        }
        cardMapMerchantCards.setMerchantCode(merchantCode);
        cardMapMerchantCards.setPrice(card.getPrice());
        cardMapMerchantCards.setBatchCode(card.getBatchCode());
        cardMapMerchantCards.setCardCode(card.getCardCode());
        cardMapMerchantCards.setCardName(card.getCardName());
        cardMapMerchantCards.setCardType(CardType.NUMBER.getKey());
        cardMapMerchantCards.setType(MerchantCardsType.MALL_SELL);
        cardMapMerchantCards.setState(CardConstant.MERCHANT_CARD_STATE_NORMAL);
        cardMapMerchantCards.setCardFaceValue("1");
        cardMapMerchantCards.setOnSaleState(CardConstant.MERCHANT_CARD_ON_SALE_STATE_N);
        cardMapMerchantCards.setOnSaleDate(card.getOnSaleDate());
        cardMapMerchantCards.setHaltSaleDate(card.getHaltSaleDate());
        cardMapMerchantCards.setCategoryCode(card.getCategoryCode());
        cardMapMerchantCards.setCategoryName(card.getCategoryName());
        cardMapMerchantCardsService.saveOrUpdate(cardMapMerchantCards);
    }

    /**
     * 查询商城售卖卡券
     * @param merchantCodes
     * @param pageNo
     * @param pageSize
     * @param productionName
     * @param productionCode
     * @param onSaleState
     * @return
     */
    @GetMapping("/{merchantCodes}/type/mallSell")
    public IPage<CardMapMerchantCards> getMallSellMerchantCards(
            @PathVariable("merchantCodes") List<String> merchantCodes,
            @RequestParam(value = "pageNo",required = false,defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize,
            @RequestParam(value = "productionName",required = false,defaultValue = "") String productionName,
            @RequestParam(value = "productionCode",required = false,defaultValue = "") String productionCode,
            @RequestParam(value = "state", required = false, defaultValue = CardConstant.MERCHANT_CARD_ON_SALE_STATE_N) String onSaleState){
        return cardMapMerchantCardsService.getMallSellCard(merchantCodes,pageNo,pageSize,productionName,productionCode,onSaleState);
    }

    /**
     *  查询商品
     * @param merchantCodes
     * @param state
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    @GetMapping("/orderProduction/{merchantCodes}")
    public IPage<CardMapMerchantCards> getCardProductionPage(
            @PathVariable("merchantCodes") List<String> merchantCodes,
            @RequestParam(value = "state",required = false,defaultValue = "") String state,
            @RequestParam(value = "pageNo",required = false,defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize,
            @RequestParam(value = "type",required = false,defaultValue = "cards") String type) {
        return cardMapMerchantCardsService.getCardProductionPage(merchantCodes,state,pageNo,pageSize,type);
    }

    /**
     * 保存商户发布的免费卡券
     * @param card
     * @param merchantCode
     */
    @PostMapping("/mallCoupon/{merchantCode}")
    public void saveMallCoupon(@RequestBody CardCards card,@PathVariable("merchantCode") String merchantCode){
        cardMapMerchantCardsService.saveMallCoupon(card,merchantCode);
    }

    /**
     * 保存更新卡券
     * @param cardMapMerchantCards
     */
    @PostMapping
    public void saveMerchantCard(@RequestBody CardMapMerchantCards cardMapMerchantCards){
        cardMapMerchantCardsService.saveOrUpdate(cardMapMerchantCards);
    }
}

