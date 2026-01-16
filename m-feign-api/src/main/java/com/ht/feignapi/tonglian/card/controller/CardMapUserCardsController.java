package com.ht.feignapi.tonglian.card.controller;

import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.result.UserDefinedException;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.card.service.CardLimitsService;
import com.ht.feignapi.tonglian.card.service.CardMapMerchantCardService;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.config.UserCardsStateConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.user.controller.UserUsersController;
import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户，卡绑定关系 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("/tonglian/userCard")
public class CardMapUserCardsController {

    private Logger logger = LoggerFactory.getLogger(UserUsersController.class);

    @Autowired
    private CardUserService cardMapUserCardsService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardMapMerchantCardService cardMapMerchantCardService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private InventoryClientService inventoryClientService;

    /**
     * C端公众号 获取用户 卡券列表
     *
     * @param openid
     * @param merchantCode
     * @param state
     * @return
     */
    @GetMapping("/getUserCardList/{openid}/{merchantCode}/{state}")
    public List<UserCardVO> getUserCardList(@PathVariable("openid") String openid, @PathVariable("merchantCode") String merchantCode, @PathVariable("state") String state) throws ParseException {
        UserUsers usrUsers = authClientService.queryByOpenid(openid).getData();
        List<UserCardVO> userCardVOList = new ArrayList<>();
        if (usrUsers == null) {
            return userCardVOList;
        }
        List<Merchants> merchantAndSonList = merchantsClientService.getSubMerchants(merchantCode).getData();
        for (Merchants merchants : merchantAndSonList) {
            List<CardMapUserCards> cardMapUserCardsList = cardMapUserClientService.getByUserIdAndMerchantCode(usrUsers.getId(), merchants.getMerchantCode(), state).getData();
            for (CardMapUserCards cardMapUserCards : cardMapUserCardsList) {
                CardCards cardCards = cardMapMerchantCardService.queryByCardCode(cardMapUserCards.getCardCode(), merchants.getMerchantCode());
                //将用户未使用的过期卡券作废
                if ("un_use".equals(state)){
                    Boolean ifInvalid = cardMapUserCardsService.checkUserCardInvalid(cardCards, cardMapUserCards.getCreateAt());
                    if (ifInvalid){
                        List<PosSelectCardNo> cardNoList=new ArrayList<>();
                        PosSelectCardNo posSelectCardNo = new PosSelectCardNo();
                        posSelectCardNo.setCardNo(cardMapUserCards.getCardNo());
                        cardNoList.add(posSelectCardNo);
                        cardMapUserClientService.updateUserCardsState(cardNoList,UserCardsStateConfig.INVALID);
                        continue;
                    }
                }
                UserCardVO userCardVO = new UserCardVO();
                BeanUtils.copyProperties(cardCards, userCardVO);
                userCardVO.setCardNo(cardMapUserCards.getCardNo());
                userCardVO.setIcCardId(cardMapUserCards.getIcCardId());
                userCardVO.setType(cardMapUserCards.getType());
                userCardVO.setNotice(cardCards.getNotice());
                userCardVO.setCardName(cardMapUserCards.getCardName());
                userCardVO.setCardCardsType(cardCards.getType());
                CardMapMerchantCards cardMapMerchantCards = merchantCardClientService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode()).getData();
                String showTimeScope;
                if (cardMapMerchantCards!=null) {
                    showTimeScope = cardMapUserCardsService.getUserCardShowTimeScope(cardCards,cardMapUserCards.getCreateAt());
                }else {
                    showTimeScope = "免费赠送";
                }
                userCardVO.setShowTimeScope(showTimeScope);
                userCardVOList.add(userCardVO);
            }
        }
        return userCardVOList;
    }

    /**
     * pos端 根据手机号 获取用户虚拟卡列表
     *
     * @param phoneNum
     * @param merchantCode
     * @return
     */
    @GetMapping("/getListByTel/{phoneNum}/{merchantCode}")
    public List<PosUserCardVO> getUserCardListByPhoneNum(@PathVariable("phoneNum") String phoneNum, @PathVariable("merchantCode") String merchantCode) {
        List<CardMapUserCards> cardMapUserCardsList = cardMapUserCardsService.queryByPhoneNumAndMerchantCode(phoneNum, merchantCode, UserCardsStateConfig.UN_USE);
        List<PosUserCardVO> posUserCardVOList = new ArrayList<>();
        for (CardMapUserCards cardMapUserCards : cardMapUserCardsList) {
            CardCards cardCards = cardMapMerchantCardService.queryByCardCode(cardMapUserCards.getCardCode(), merchantCode);
            PosUserCardVO posUserCardVO = new PosUserCardVO();
            BeanUtils.copyProperties(cardCards, posUserCardVO);
            posUserCardVO.setCardNo(cardMapUserCards.getCardNo());
            posUserCardVO.setIcCardId(cardMapUserCards.getIcCardId());
            posUserCardVO.setType(cardMapUserCards.getType());
            posUserCardVOList.add(posUserCardVO);
        }
        return posUserCardVOList;
    }

    /**
     * C端 返回 用户卡券(门店扫码支付使用)
     * @param qrPayCalCardData
     * @return
     */
    @PostMapping("/calculationCardList")
    public List<UserCashPayCardData> calculationCardList(@RequestBody QrPayCalCardData qrPayCalCardData) {
        UserUsers usrUsers = authClientService.queryByOpenid(qrPayCalCardData.getOpenid()).getData();
        if (usrUsers == null) {
            logger.info("该用户不是会员,没有卡券");
            return new ArrayList<>();
        }
        List<CardMapUserCards> cardMapUserCardsList = merchantCardClientService.selectByUserIdAndMerchantCodeNoNumber(usrUsers.getId(), qrPayCalCardData.getMerchantCode(), UserCardsStateConfig.UN_USE).getData();
        List<UserCashPayCardData> userCashPayCardDataArrayList = new ArrayList<>();
        for (CardMapUserCards cardMapUserCards : cardMapUserCardsList) {
            CardCards cardCards = cardsClientService.getCard(cardMapUserCards.getCardCode(), qrPayCalCardData.getMerchantCode(), cardMapUserCards.getBatchCode()).getData();
            Boolean ifInvalid = cardUserService.checkUserCardInvalid(cardCards, cardMapUserCards.getCreateAt());
            if (ifInvalid){
                continue;
            }
            UserCashPayCardData userCashPayCardData = new UserCashPayCardData();
            BeanUtils.copyProperties(cardCards, userCashPayCardData);
            userCashPayCardData.setCardNo(cardMapUserCards.getCardNo());
            userCashPayCardData.setIcCardId(cardMapUserCards.getIcCardId());
            userCashPayCardData.setType(cardMapUserCards.getType());
            userCashPayCardData.setNotice(cardCards.getNotice());
            userCashPayCardData.setCardCardsType(cardCards.getType());
            userCashPayCardData.setCardName(cardMapUserCards.getCardName());
            userCashPayCardDataArrayList.add(userCashPayCardData);
        }
        return userCashPayCardDataArrayList;
    }


    /**
     * 获取用户的计次卡券列表(新版本迭代)
     * @param openid
     * @param merchantCode
     * @return
     */
    @GetMapping("/userNumberCardList")
    public List<CardMapUserCardsVO> queryUserNumberCardList(@RequestParam("openid") String openid,@RequestParam("merchantCode")String merchantCode){
        try {
            Result<UserUsers> result = authClientService.queryByOpenid(openid);
            UserUsers userUsers = result.getData();
            List<CardMapUserCardsVO> listResult = cardMapUserCardsService.queryUserNumberCardList(openid,userUsers.getId(), merchantCode);
            return listResult;
        } catch (Exception e) {
            logger.info(e.getMessage()+"====="+e.toString());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * c扫B 核销 计次 卡券(新版本迭代)
     * @param cardMapUserCards
     */
    @PostMapping("/qrUseNumberCard")
    public Integer qrUseNumberCard(@RequestBody CardMapUserCards cardMapUserCards){
        Result<Integer> result = cardMapUserClientService.qrUseNumberCard(cardMapUserCards);
        return result.getData();
    }

    /**
     * C端公众号 用户领券 (免费领券)
     *
     * @param userGetCardData
     * @return
     */
    @PostMapping("/userGetCard")
    public String userGetCard(@RequestBody UserGetCardData userGetCardData) {
        UserUsers userUsers = authClientService.queryByOpenid(userGetCardData.getOpenid()).getData();
        userGetCardData.setUserId(userUsers.getId());
        try {
            cardLimitsService.checkCardGetLimit(userGetCardData.getCardCode(), userGetCardData.getMerchantCode(), userGetCardData.getUserId(), userGetCardData.getBatchCode());
        }catch (Exception e){
            throw new CheckException(ResultTypeEnum.USER_GET_CARD_ERROR.getCode(),e.getMessage());
        }
        Boolean booleanResult = false;
        //todo 库存检查
        Result<Integer> cardInventory = inventoryClientService.getInventory(userGetCardData.getMerchantCode(),userGetCardData.getCardCode());
        if (cardInventory.getData() < 1) {
            throw new UserDefinedException(ResultTypeEnum.INVENTORY);
        }
        booleanResult = cardMapUserClientService.userGetCard(userGetCardData).getData();
        if (booleanResult) {
            Map<String,Integer> map = new HashMap<>();
            map.put("amount",1);
            inventoryClientService.subtractInventory(userGetCardData.getMerchantCode(),userGetCardData.getCardCode(),map);
            return "领取成功";
        } else {
            throw new CheckException(ResultTypeEnum.USER_GET_CARD_ERROR);
        }
    }

    /**
     * 根据绑定的cardNo 生成二维码图片
     *
     * @param cardNo
     * @return
     */
    @GetMapping("/qrCode/{cardNo}")
    public String getQrCode(@PathVariable("cardNo") String cardNo) {
        return cardMapUserCardsService.createQrCode(cardNo);
    }

    /**
     * 创建通联账户卡 与用户绑定关联关系(  CardMapUserCardsService类方法同名 )
     * @param cardCode
     * @param phoneNum
     * @param objectMerchantCode
     * @param cardName
     * @param categoryCode
     * @param categoryName
     * @param state
     * @param type
     * @param cardId
     */
    @PostMapping("/cardMapUser")
    public void createCardMapUserCards(@RequestParam("cardCode") String cardCode, @RequestParam("phoneNum")String phoneNum,
                                       @RequestParam("objectMerchantCode")String objectMerchantCode, @RequestParam("cardName")String cardName,
                                       @RequestParam("categoryCode")String categoryCode, @RequestParam("categoryName")String categoryName,
                                       @RequestParam("state")String state, @RequestParam("type")String type, @RequestParam("cardId")String cardId){
        cardMapUserCardsService.createCardMapUserCards(cardCode,phoneNum,objectMerchantCode,cardName,categoryCode,categoryName,state,type,cardId);
    }


    /**
     * 查询用户的卡券数量(  CardMapUserCardsService类方法同名 )
     * @param userId
     * @param merchantCode
     * @return
     */
    @GetMapping("/amount/{merchantCode}/{userId}")
    public Integer queryCardNum(@PathVariable("userId") Long userId,@PathVariable("merchantCode") String merchantCode){
        return cardMapUserCardsService.queryCardNum(userId,merchantCode);
    }

}

