package com.ht.feignapi.tonglian.card.controller;

import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.result.UserDefinedException;
import com.ht.feignapi.tonglian.card.clientservice.*;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.card.service.CardMapMerchantCardService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商家卡券 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@RestController
@RequestMapping("/tonglian/cardMerchantCards")
@CrossOrigin(allowCredentials = "true")
public class CardMapMerchantCardsController {

    @Autowired
    private CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private InventoryClientService inventoryClientService;

    @Autowired
    private CardMapMerchantCardService cardMapMerchantCardsService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private CardProfilesClientService cardProfilesClientService;


    /**
     * C端公众号  根据卡号获取卡信息
     * @param cardCode
     * @param merchantCode
     * @param batchCode
     * @return
     */
    @GetMapping("/cardMsg/{cardCode}/{merchantCode}/{batchCode}")
    public MerchantCardsDetailVO getCardMsgByCardCode(@PathVariable(value = "cardCode") String cardCode,@PathVariable("merchantCode") String merchantCode,@PathVariable("batchCode")String batchCode) throws ParseException {
        if (StringUtils.isEmpty(cardCode) || StringUtils.isEmpty(batchCode)){
            throw new UserDefinedException(ResultTypeEnum.PARA_MISSING_EXCEPTION,"卡号和批次号参数必传");
        }
        CardMapMerchantCards cardMapMerchantCards = merchantCardClientService.queryByCardCodeAndBatchCode(cardCode, batchCode).getData();
        Merchants merchants = merchantsClientService.getMerchantByCode(cardMapMerchantCards.getMerchantCode()).getData();
        CardCards cardCards = cardsClientService.getCard(cardMapMerchantCards.getCardCode(),cardMapMerchantCards.getMerchantCode(),batchCode).getData();
        Integer cardInventory = inventoryClientService.getInventory(cardMapMerchantCards.getMerchantCode(),cardCode).getData();
        MerchantCardsDetailVO merchantCardsDetailVO = new MerchantCardsDetailVO();
        BeanUtils.copyProperties(cardCards,merchantCardsDetailVO);
        BeanUtils.copyProperties(cardMapMerchantCards,merchantCardsDetailVO);
        merchantCardsDetailVO.setMerchantsAddress(merchants.getLocation());
        merchantCardsDetailVO.setMerchantsName(merchants.getMerchantName());
        merchantCardsDetailVO.setMerchantsPhone(merchants.getMerchantContact());
        merchantCardsDetailVO.setMerchantsPic(merchants.getMerchantPicUrl());
        merchantCardsDetailVO.setMerchantCardType(cardMapMerchantCards.getType());
        merchantCardsDetailVO.setDesc(cardCards.getNotice());
        merchantCardsDetailVO.setInventory(cardInventory);
        merchantCardsDetailVO.setCardType(cardCards.getType());
        merchantCardsDetailVO.setBatchCode(cardMapMerchantCards.getBatchCode());
        String dateScope = cardMapMerchantCardsService.getShowTimeScope(cardMapMerchantCards);
        merchantCardsDetailVO.setShowTimeScope(dateScope);
        String validTimeStr = cardMapMerchantCardsService.packageValidTimeStr(cardCards);
        merchantCardsDetailVO.setValidTimeStr(validTimeStr);
        List<CardProfiles> cardProfiles= cardProfilesClientService.queryByCardCode(merchantCardsDetailVO.getCardCode()).getData();
        CardMapMerchantCards cardMapMerchantCardsDB=merchantCardClientService.queryByCardCodeAndBatchCode(cardCode,batchCode).getData();
        merchantCardsDetailVO.setPayToMerchantCode(cardMapMerchantCardsDB.getMerchantCode());
        if (cardProfiles==null){
            merchantCardsDetailVO.setUseFlagDesc(new ArrayList());
        }else {
            List<String> list = new ArrayList<>();
            for (CardProfiles cardProfile : cardProfiles) {
                if (!StringUtils.isEmpty(cardProfile.getValue())){
                    list.add(cardProfile.getValue());
                }
            }
            if (list.size()>0) {
                merchantCardsDetailVO.setUseFlagDesc(list);
            }else {
                merchantCardsDetailVO.setUseFlagDesc(new ArrayList());
            }
        }
        return merchantCardsDetailVO;
    }

    /**
     * 根据商家编码 列表分类 获取商家上架 卡券列表
     * @param merchantCode
     * @param type
     * @param category
     * @return
     * @throws ParseException
     */
    @GetMapping("/list/{merchantCode}/{type}")
    public List<MerchantCardsVO> getListByMerchantCode(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("type") String type,
            @RequestParam(required = false,value = "category") String category) throws ParseException {
        List<Merchants> merchantAndSonList = merchantsClientService.getSubMerchants(merchantCode).getData();
        List<MerchantCardsVO> merchantCardsVOList=new ArrayList<>();
        for (Merchants merchants : merchantAndSonList) {
            List<CardMapMerchantCards> cardMapMerchantCardsList = merchantCardClientService.queryListByMerchantCode(merchants.getMerchantCode(), type).getData();
            if (!StringUtils.isEmpty(category)){
                cardMapMerchantCardsList = cardMapMerchantCardsList.stream().filter(e->category.equals(e.getCategoryName())).collect(Collectors.toList());
            }
            for (CardMapMerchantCards cardMapMerchantCards : cardMapMerchantCardsList) {
                MerchantCardsVO merchantCardsVO = new MerchantCardsVO();
                BeanUtils.copyProperties(cardMapMerchantCards,merchantCardsVO);
                CardCards cardCards= cardMapMerchantCardsService.getShowOtherData(cardMapMerchantCards);
                Integer cardInventory = inventoryClientService.getInventory(cardMapMerchantCards.getMerchantCode(),cardMapMerchantCards.getCardCode()).getData();
                Assert.notNull(cardInventory,"获取库存失败");
                if (cardInventory<=0){
                    continue;
                }
                merchantCardsVO.setInventory(cardInventory);
                merchantCardsVO.setMerchantCardType(cardCards.getMerchantCardType());
                merchantCardsVO.setDesc(cardCards.getNotice());
                merchantCardsVO.setBatchCode(cardMapMerchantCards.getBatchCode());
                merchantCardsVO.setPrice(cardMapMerchantCards.getPrice());
                merchantCardsVO.setCardCardsType(cardCards.getType());

                //校验 免费领取的卡券时间,过期则不展示
                Boolean checkFreeTypeInvalid = cardMapMerchantCardsService.checkFreeTypeInvalid(merchantCardsVO);
                if (checkFreeTypeInvalid){
                    continue;
                }

                String showTimeScope = cardMapMerchantCardsService.getShowTimeScope(cardMapMerchantCards);
                merchantCardsVO.setShowTimeScope(showTimeScope);
                merchantCardsVO.setFaceValue(cardCards.getFaceValue());
                if (cardMapMerchantCards.getPrice()==null) {
                    merchantCardsVO.setPrice(0);
                }else {
                    merchantCardsVO.setPrice(cardMapMerchantCards.getPrice());
                }
                merchantCardsVO.setValidFrom(cardCards.getValidFrom());
                merchantCardsVO.setValidTo(cardCards.getValidTo());
                merchantCardsVO.setCardPicUrl(cardCards.getCardPicUrl());
                merchantCardsVO.setMerchantsName(merchants.getMerchantName());
                merchantCardsVO.setMerchantsPic(merchants.getMerchantPicUrl());
                merchantCardsVO.setMerchantsAddress(merchants.getLocation());
                merchantCardsVO.setMerchantsPhone(merchants.getMerchantContact());
                merchantCardsVO.setCreateAt(cardMapMerchantCards.getCreateAt());
                List<CardProfiles> cardProfilesList= cardProfilesClientService.queryByCardCode(cardMapMerchantCards.getCardCode()).getData();
                if (cardProfilesList==null || cardProfilesList.size()<1){
                    merchantCardsVO.setUseFlagDesc(new ArrayList());
                }else {
                    List<String> list = new ArrayList<>();
                    for (CardProfiles cardProfile : cardProfilesList) {
                        if (!StringUtils.isEmpty(cardProfile.getValue())){
                            list.add(cardProfile.getValue());
                        }
                    }
                    if (list.size()>0) {
                        merchantCardsVO.setUseFlagDesc(list);
                    }else {
                        merchantCardsVO.setUseFlagDesc(new ArrayList());
                    }
                }
                merchantCardsVOList.add(merchantCardsVO);
            }
        }
        List<MerchantCardsVO> collect = merchantCardsVOList.stream().sorted(Comparator.comparing(MerchantCardsVO::getCreateAt).reversed()).collect(Collectors.toList());
        return collect;
    }


    /**
     * 获取需要显示属性
     * @param cardMapMerchantCards
     * @return
     */
    @PostMapping("/cardMsg")
    public CardCards getShowOtherData(@RequestBody CardMapMerchantCards cardMapMerchantCards){
        return cardMapMerchantCardsService.getShowOtherData(cardMapMerchantCards);
    }

}

