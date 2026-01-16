package com.ht.feignapi.tonglian.card.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.feignapi.appshow.entity.MallCoupon;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.constant.CardUserMallConstant;
import com.ht.feignapi.mall.entity.MallProductions;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserCardsTraceClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCardsTrace;
import com.ht.feignapi.tonglian.card.entity.CardUserTraceQueryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("/tonglian/cardTrace")
public class CardMapUserCardsTraceController {

    @Autowired
    private CardMapUserCardsTraceClientService traceClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private MallAppShowClientService mallAppShowClientService;

    @Autowired
    private AuthClientService authClientService;

    private final static Logger logger = LoggerFactory.getLogger(CardMapMerchantCardsController.class);

    /**
     * pos 分页获取 发券流水
     * @param cardUserTraceQueryData
     * @return
     */
    @PostMapping("/sendList")
    public IPage<CardMapUserCardsTrace> traceSendList(@RequestBody CardUserTraceQueryData cardUserTraceQueryData) {
        return getCardMapUserCardsTraceIPage(cardUserTraceQueryData,"pos");
    }


    /**
     * pos 分页获取 验券流水
     * @param cardUserTraceQueryData
     * @return
     */
    @PostMapping("/useList")
    public IPage<CardMapUserCardsTrace> traceUseList(@RequestBody CardUserTraceQueryData cardUserTraceQueryData) {
        return getCardMapUserCardsTraceIPage(cardUserTraceQueryData,"pos_use");
    }

    /**
     * 商户端获取用券流水
     * @param cardUserTraceQueryData
     * @return
     */
    @PostMapping("/cardTraceList")
    public IPage<CardMapUserCardsTrace> cardTraceList(@RequestBody CardUserTraceQueryData cardUserTraceQueryData) {
        return getCardMapUserCardsTraceIPage(cardUserTraceQueryData,"");
    }

    private IPage<CardMapUserCardsTrace> getCardMapUserCardsTraceIPage(@RequestBody CardUserTraceQueryData cardUserTraceQueryData,String type) {
        IPage<CardMapUserCardsTrace> iPage = traceClientService.listPage(cardUserTraceQueryData.getMerchantCode(),
                type, cardUserTraceQueryData.getPageNo(), cardUserTraceQueryData.getPageSize()).getData();
        logger.info(JSON.toJSONString(iPage));
        List<CardMapUserCardsTrace> records = iPage.getRecords();
        for (CardMapUserCardsTrace record : records) {
            CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(record.getCardNo()).getData();
            logger.info("*********cardMapUserCards****************:" + JSON.toJSONString(cardMapUserCards));
            UserUsers usrUsers = authClientService.getUserByIdTL(record.getUserId().toString()).getData();
            logger.info("*********usrUsers****************:" + JSON.toJSONString(usrUsers));
            Result<CardCards> result = cardsClientService.getCardByCardCode(cardMapUserCards.getCardCode());
            if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(result.getCode()) && result.getData()!=null){
                record.setCardPicUrl(result.getData().getCardPicUrl());
                record.setCardName(result.getData().getCardName());
            }
            if (usrUsers!=null && !StringUtils.isEmpty(usrUsers.getTel())){
                record.setTel(usrUsers.getTel());
            }else {
                record.setTel(record.getUserId()+"");
            }
        }
        return iPage;
    }


}
