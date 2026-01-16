package com.ht.feignapi.mall.service;

import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.constant.CardUserMallConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserCardsTraceClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCardsTrace;
import com.ht.feignapi.tonglian.config.CardConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

@Service
public class CardMallCheckUseService {

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardMapUserCardsTraceClientService cardMapUserTraceClientService;

    @Autowired
    private MallOrderClientService mallOrderClientService;

    /**
     * 商城购买卡券 验卡
     * @param cardMapUserCards
     * @return
     */
    public String updateMallUsed(CardMapUserCards cardMapUserCards,String state) {
        CardCards card = cardCardsClientService.getCardByCardCode(cardMapUserCards.getCardCode()).getData();
        Integer faceValue = Integer.parseInt(cardMapUserCards.getFaceValue());
        if (("number").equals(card.getType())) {
            Assert.isTrue(faceValue > 0, "次数少于0次");
            cardMapUserCards.setFaceValue(String.valueOf(faceValue - 1));
            Integer count = faceValue - 1 ;
            if (count == 0){
                //修改订单状态
                String orderDetailId = cardMapUserCards.getRefSourceKey();
                mallOrderClientService.updateOrderDetailState(orderDetailId, OrderConstant.PAID_USED_STATE);
                //修改卡券状态
                cardMapUserCards.setState(CardConstant.USER_CARD_STATE_USED);
            }
        } else {
            cardMapUserCards.setState(CardConstant.USER_CARD_STATE_USED);
        }
        cardMapUserClientService.saveOrUpdate(cardMapUserCards);

        //创建验券流水
        CardMapUserCardsTrace cardMapUserCardsTrace = new CardMapUserCardsTrace();
        cardMapUserCardsTrace.setUserId(cardMapUserCards.getUserId());
        cardMapUserCardsTrace.setState("normal");
        cardMapUserCardsTrace.setActionDate(new Date());
        cardMapUserCardsTrace.setCardNo(cardMapUserCards.getCardNo());
        cardMapUserCardsTrace.setCardCode(cardMapUserCards.getCardCode());
        cardMapUserCardsTrace.setMerchantCode(cardMapUserCards.getMerchantCode());
        cardMapUserCardsTrace.setBatchCode(cardMapUserCards.getBatchCode());
        cardMapUserCardsTrace.setCreateAt(new Date());
        if (CardUserMallConstant.MALL_BUY_UN_USE_STATE.equals(state)){
            cardMapUserCardsTrace.setActionType(CardUserMallConstant.MALL_BUY_USE);
        }
        if (CardUserMallConstant.MALL_FREE_UN_USE_STATE.equals(state)){
            cardMapUserCardsTrace.setActionType(CardUserMallConstant.MALL_FREE_USE);
        }
        cardMapUserTraceClientService.saveOrUpdateTrace(cardMapUserCardsTrace);
        return "验卡成功";
    }
}
