package com.ht.feignapi.tonglian.card.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.entity.Inventory;
import com.ht.feignapi.mall.service.InventoryService;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.admin.entity.UserFreeCard;
import com.ht.feignapi.tonglian.admin.excel.entity.UserCardImportVo;
import com.ht.feignapi.tonglian.card.clientservice.*;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.config.CardLimitType;
import com.ht.feignapi.tonglian.config.UserCardsTypeConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import com.ht.feignapi.tonglian.utils.CardMoneyAddUtil;
import com.ht.feignapi.tonglian.utils.QrCodeUtils;
import com.ht.feignapi.tonglian.utils.TimeUtil;

import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/17 10:53
 */
@Service
public class CardUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MapMerchantPrimesClientService merchantsPrimeClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CardDistributeTraceClientService cardDistributeTraceClientService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private CardMapMerchantCardClientService cardMapMerchantCardClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private MerchantPrimeService merchantPrimeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public void sendCardToUsers(String merchantCode, String cardCode, List<Long> userIds, UserUsers adminUser) {
        Inventory inventory = inventoryService.createInventory(merchantCode, cardCode, userIds.size());
        cardDistributeTraceClientService.createDistributeTrace(merchantCode, cardCode, userIds.size(), "群发券--指定会员发券", adminUser.getNickName(), "", inventory.getBatchCode());
        userIds.forEach(e -> cardMapUserClientService.createCardMapUserCards(merchantCode, cardCode, e, IdWorker.getIdStr(), inventory.getBatchCode()));
    }

    public void sendCardToUsers(String objectMerchantCode, String cardCode, String memberType, UserUsers adminUser, String cardMerchantCode) {
        List<MrcMapMerchantPrimes> primes = merchantsPrimeClientService.getUserByMemberType(objectMerchantCode, memberType).getData();
        logger.info("会员列表：" + primes);
        Inventory inventory = inventoryService.createInventory(objectMerchantCode, cardCode, primes.size());
        cardDistributeTraceClientService.createDistributeTrace(objectMerchantCode, cardCode, primes.size(), "群发券--指定会员等级发券", adminUser.getNickName(), "", inventory.getBatchCode());
        primes.forEach(e -> cardMapUserClientService.createCardMapUserCards(cardMerchantCode, cardCode, e.getUserId(), IdWorker.getIdStr(), inventory.getBatchCode()));
    }

    public void sendCardToPhoneOrOpenIdUsers(String merchantCode, String cardCode, List<UserCardImportVo> list, UserUsers adminUsers) {
        String batchCode = TimeUtil.format(new Date(), "yyyyMMddHHmmssSSS") + cardCode;
        Inventory inventory = inventoryService.createInventory(merchantCode, cardCode, list.size());
        cardDistributeTraceClientService.createDistributeTrace(merchantCode, cardCode, list.size(), "群发券--导入发券", adminUsers.getNickName(), "", inventory.getBatchCode());
        list.forEach(e -> {
            UserUsers user = merchantPrimeService.primeQueryUserByTelAndCode(e.getPhoneOrOpenId(), merchantCode);
            Assert.notNull(user, "该商户下没有这个用户");
            cardMapUserClientService.createCardMapUserCards(merchantCode, cardCode, user.getId(), IdWorker.getIdStr(), inventory.getBatchCode());
        });
    }

    public List<CardMapUserCards> queryByPhoneNumAndMerchantCode(String phoneNum, String merchantCode, String state) {
        UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(phoneNum, merchantCode);
        List<CardMapUserCards> cardMapUserCardsList = cardMapUserClientService.getByUserIdAndMerchantCodeNotIc(userUsers.getId(), merchantCode, state).getData();
        return cardMapUserCardsList;
    }

    /**
     * 生成用券二维码
     *
     * @param cardNo
     * @return
     */
    public String createQrCode(String cardNo) {
        String romCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        String content = cardNo + romCode;
        stringRedisTemplate.opsForValue().set(cardNo, romCode, 10, TimeUnit.MINUTES);
        String qrCode = QrCodeUtils.creatRrCode(content, 200, 200);
        String replace = qrCode.replace("\r\n", "");
        String replaceOne = replace.replace("\n", "");
        String replaceTwo = replaceOne.replace("\r", "");
        return replaceTwo;
    }

    /**
     * 校验用户已领取卡券是否过期
     *
     * @param cardCards
     * @param createAt
     */
    public Boolean checkUserCardInvalid(CardCards cardCards, Date createAt) {
        if (StringUtils.isEmpty(cardCards.getValidityType())) {
            return false;
        }

        if ("beginToEnd".equals(cardCards.getValidityType())) {
            return checkBeginToEnd(cardCards.getValidFrom(), cardCards.getValidTo());
        }

        if ("validDuration".equals(cardCards.getValidityType())) {
            return checkValidDuration(cardCards.getPeriodOfValidity(), cardCards.getValidGapAfterApplied(), createAt);
        }
        return false;
    }

    /**
     * 校验用户已领取卡券是否过期
     *
     * @param validityType
     * @param validFrom
     * @param validTo
     * @param periodOfValidity
     * @param validGapAfterApplied
     * @param createAt
     * @return
     */
    public Boolean checkUserCardInvalid(String validityType, Date validFrom, Date validTo, Integer periodOfValidity, Integer validGapAfterApplied, Date createAt) {
        if (StringUtils.isEmpty(validityType)) {
            return false;
        }

        if ("beginToEnd".equals(validityType)) {
            return checkBeginToEnd(validFrom, validTo);
        }

        if ("validDuration".equals(validityType)) {
            return checkValidDuration(periodOfValidity, validGapAfterApplied, createAt);
        }
        return false;
    }

    /**
     * 校验 validDuration 过期类型的卡券过期
     *
     * @param periodOfValidity
     * @param validGapAfterApplied
     * @param createAt
     */
    private Boolean checkValidDuration(Integer periodOfValidity, Integer validGapAfterApplied, Date createAt) {
        int validGapAfterAppliedTime = validGapAfterApplied / 24;
        int periodOfValidityTime = periodOfValidity / 24;
        Calendar cal = Calendar.getInstance();
        cal.setTime(createAt);
        cal.add(cal.DATE, periodOfValidityTime);
        cal.add(cal.DATE, validGapAfterAppliedTime);
        long timeInMillis = cal.getTimeInMillis();
        System.out.println(timeInMillis);
        long nowTime = System.currentTimeMillis();
        System.out.println(nowTime);
        return nowTime > timeInMillis;
    }

    /**
     * 校验 beginToEnd 过期类型的卡券过期
     *
     * @param validFrom
     * @param validTo
     */
    private Boolean checkBeginToEnd(Date validFrom, Date validTo) {
        Date date = new Date();
        int i = date.compareTo(validTo);
        return i > -1;
    }

    public void createUserFreeCards(UserFreeCard userFreeCard, String merchantCode) {
        Inventory inventory = inventoryService.createInventory(merchantCode, userFreeCard.getCardCode(), userFreeCard.getInventory());
        Assert.notNull(inventory, "保存库存失败");
        userFreeCard.setBatchCode(inventory.getBatchCode());
        userFreeCard.setInventory(inventory.getInventory());
        Result<String> cardsResult = cardMapMerchantCardClientService.createUserFreeCards(userFreeCard, merchantCode);
        Assert.notNull(cardsResult, "获取卡券失败");
        Assert.notNull(cardsResult.getData(), "获取卡券失败");
    }

    /**
     * 创建卡与用户的关联信息
     *
     * @param cardCode
     * @param phoneNum
     * @param merchantCode
     * @param cardName
     * @param categoryCode
     * @param categoryName
     * @param state
     * @param type
     * @param cardNo
     */
    public void createCardMapUserCards(String cardCode, String phoneNum, String merchantCode, String cardName, String categoryCode, String categoryName, String state, String type, String cardNo) {
        UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(phoneNum, merchantCode);
        CardMapUserCards cardMapUserCards = getCardMapUserCards(cardCode, merchantCode, cardName, categoryCode, categoryName, state, type, cardNo, userUsers.getId());
        cardMapUserClientService.saveOrUpdate(cardMapUserCards);
    }

    private CardMapUserCards getCardMapUserCards(String cardCode, String merchantCode, String cardName, String categoryCode, String categoryName, String state, String type, String cardNo, Long userId) {
        CardMapUserCards cardMapUserCards = new CardMapUserCards();
        cardMapUserCards.setMerchantCode(merchantCode);
        cardMapUserCards.setCardCode(cardCode);
        cardMapUserCards.setUserId(userId);
        cardMapUserCards.setCardName(cardName);
        cardMapUserCards.setCategoryCode(categoryCode);
        cardMapUserCards.setCategoryName(categoryName);
        cardMapUserCards.setState(state);
        cardMapUserCards.setType(type);
        cardMapUserCards.setCardNo(cardNo);
        cardMapUserCards.setUpdateAt(new Date());
        return cardMapUserCards;
    }

    public Integer queryCardNum(Long userId, String merchantCode) {
        List<Merchants> merchantAndSon = merchantsClientService.getSubMerchants(merchantCode).getData();
        ArrayList<String> list = new ArrayList<>();
        for (Merchants merchants : merchantAndSon) {
            list.add(merchants.getMerchantCode());
        }
        return cardMapUserClientService.selectCount(userId, list, "account", "un_use").getData();
    }

    /**
     * 用户账户余额增加
     *
     * @param userId
     * @param amount
     */
    public void userAccountMoneyAdd(Long userId, Integer amount) {
        CardMapUserCards cardMapUserCards = cardMapUserClientService.queryByUserIdAndAccount(userId, UserCardsTypeConfig.ACCOUNT).getData();
        try {
            CardMoneyAddUtil.cardMoneyAdd(cardMapUserCards.getCardNo(), amount);
        } catch (IOException e) {
            logger.info("调取通联余额增加接口" + userId + "失败" + e);
            e.printStackTrace();
        }
    }

    public List<CardMapUserCardsVO> queryUserNumberCardList(String openid, Long userId, String merchantCode) {
        ArrayList<CardMapUserCardsVO> cardMapUserCardsVOS = new ArrayList<>();
        List<CardMapUserCardsVO> list = cardMapUserClientService.selectUserNumberList(userId, merchantCode).getData();
        for (CardMapUserCardsVO cardMapUserCardsVO : list) {
            CardCards cardCards = cardCardsClientService.getCardByCardCode(cardMapUserCardsVO.getCardCode()).getData();
            Boolean ifInvalid = cardUserService.checkUserCardInvalid(cardCards, cardMapUserCardsVO.getCreateAt());
            if (ifInvalid) {
                continue;
            }
            boolean flag = cardLimitsService.checkCardUseLimit(cardMapUserCardsVO.getCardCode(), cardMapUserCardsVO.getMerchantCode(), userId, cardMapUserCardsVO.getBatchCode());
            if (flag) {
                cardMapUserCardsVOS.add(cardMapUserCardsVO);
            }
        }
        return cardMapUserCardsVOS;
    }

    /**
     * pos端获取用户可用的虚拟卡券
     *
     * @param userId
     * @param merchantCode
     * @param type
     * @param state
     * @param amount
     * @return
     */
    public List<CardMapUserCardsVO> queryByUserIdAndMerchantCodeAndTypeAndState(Long userId, String merchantCode, String type, String state, Integer amount) {
        List<CardMapUserCardsVO> cardMapUserCardsVOS = new ArrayList<CardMapUserCardsVO>();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        List<CardMapUserCards> cardMapUserCardsList = cardMapUserClientService.posCanUseCard(userId, merchantCode, type, state, merchants.getBusinessSubjects()).getData();
        for (CardMapUserCards cardMapUserCard : cardMapUserCardsList) {
//            CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.queryByCardCodeAndBatchCode(cardMapUserCard.getCardCode(), cardMapUserCard.getBatchCode()).getData();
//            CardCards cardCards = cardCardsClientService.getCardByCardCode(cardMapUserCard.getCardCode()).getData();
//            Boolean ifInvalid = cardUserService.checkUserCardInvalid(cardCards, cardMapUserCard.getCreateAt());
//            if (ifInvalid){
//                continue;
//            }
            CardMapUserCardsVO cardMapUserCardsVO = new CardMapUserCardsVO();
//            if (cardMapMerchantCards!=null){
//                BeanUtils.copyProperties(cardMapUserCard, cardMapUserCardsVO);
//                cardMapUserCardsVO.setCardFaceValue(cardMapMerchantCards.getCardFaceValue());
//            }else {
//                BeanUtils.copyProperties(cardMapUserCard, cardMapUserCardsVO);
//                cardMapUserCardsVO.setCardFaceValue(cardCards.getFaceValue()+"");
//            }
            BeanUtils.copyProperties(cardMapUserCard, cardMapUserCardsVO);
//            cardMapUserCardsVO.setCardCardsType(cardCards.getType());
            cardMapUserCardsVO.setCardFaceValue(cardMapUserCard.getFaceValue());
            cardMapUserCardsVO.setCardCardsType(cardMapUserCard.getCardType());

            cardMapUserCardsVO.setBatchCode(cardMapUserCard.getBatchCode());
            cardMapUserCardsVO.setMerchantCode(cardMapUserCard.getMerchantCode());

            //卡券 使用的 限制条件判断
            boolean useFlag = cardLimitsService.checkCardUseLimit(cardMapUserCardsVO.getCardCode(), cardMapUserCardsVO.getMerchantCode(), cardMapUserCardsVO.getUserId(), amount, cardMapUserCard.getBatchCode());
            if (useFlag) {
                cardMapUserCardsVOS.add(cardMapUserCardsVO);
            }
        }
        return cardMapUserCardsVOS;
    }

    /**
     * 校验卡券使用规则
     *
     * @param cardNoList
     * @param amount
     * @return
     */
    public UseCardData checkUseCard(List<PosSelectCardNo> cardNoList, Integer amount) {
        List<String> cardTypeList = new ArrayList<>();
        Long userId = -1L;
        for (PosSelectCardNo posSelectCardNO : cardNoList) {
            CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(posSelectCardNO.getCardNo()).getData();
            userId = cardMapUserCards.getUserId();
            CardCards cardCards = cardCardsClientService.getCardByCardCode(cardMapUserCards.getCardCode()).getData();
            Boolean ifInvalid = checkUserCardInvalid(cardCards, cardMapUserCards.getCreateAt());
//            if (ifInvalid){
//                UseCardData useCardData = new UseCardData();
//                useCardData.setUseLimitFlag(false);
//                useCardData.setUseMessage("存在过期卡券,"+cardMapUserCards.getCardName());
//                return useCardData;
//            }
            if ("frozen".equals(cardCards.getState())) {
                UseCardData useCardData = new UseCardData();
                useCardData.setUseLimitFlag(false);
                useCardData.setUseMessage("存在冻结卡券");
                useCardData.setUserId(userId);
                return useCardData;
            }
            boolean useLimitFlag = cardLimitsService.checkCardUseLimit(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getUserId(), amount, cardMapUserCards.getBatchCode());
            if (!useLimitFlag) {
                UseCardData useCardData = new UseCardData();
                useCardData.setUseLimitFlag(false);
                useCardData.setUseMessage("卡券使用条件不满足," + cardCards.getCardName());
                useCardData.setUserId(userId);
                return useCardData;
            }
            cardTypeList.add(cardCards.getType());
        }
        if (cardTypeList.size() > 1) {
            for (String cardType : cardTypeList) {
                if (!cardType.equals("money")) {
                    UseCardData useCardData = new UseCardData();
                    useCardData.setUseLimitFlag(false);
                    useCardData.setUseMessage("非金额券不能使用多张");
                    useCardData.setUserId(userId);
                    return useCardData;
                }
            }
        }
        UseCardData useCardData = new UseCardData();
        useCardData.setUseLimitFlag(true);
        useCardData.setUseMessage("卡券使用校验通过");
        useCardData.setUserId(userId);
        return useCardData;
    }


    public List<PosUserCardVO> packagePosUserCardVOList(List<CardMapUserCardsVO> cardMapUserCardsVOList) {
        List<PosUserCardVO> posUserCardVOList = new ArrayList<>();
        if (cardMapUserCardsVOList.size() <= 0) {
            return posUserCardVOList;
        }

        for (CardMapUserCardsVO cardMapUserCardsVO : cardMapUserCardsVOList) {
            PosUserCardVO posUserCardVO = packagePosUserCardVO(cardMapUserCardsVO);
            posUserCardVOList.add(posUserCardVO);
        }
        return posUserCardVOList;
    }

    public PosUserCardVO packagePosUserCardVO(CardMapUserCardsVO cardMapUserCardsVO) {
        if (StringUtils.isEmpty(cardMapUserCardsVO.getCardCode())) {
            return new PosUserCardVO();
        }
        PosUserCardVO posUserCardVO = new PosUserCardVO();
        posUserCardVO.setCardCode(cardMapUserCardsVO.getCardCode());
//        CardCards cardCards = cardCardsClientService.getCardByCardCode(cardMapUserCardsVO.getCardCode()).getData();
//        posUserCardVO.setBatchTimes(cardCards.getBatchTimes());
        posUserCardVO.setCardName(cardMapUserCardsVO.getCardName());
        posUserCardVO.setCardNo(cardMapUserCardsVO.getCardNo());
        posUserCardVO.setCategoryCode(cardMapUserCardsVO.getCategoryCode());
        posUserCardVO.setCategoryName(cardMapUserCardsVO.getCategoryName());
        posUserCardVO.setFaceValue(Integer.parseInt(cardMapUserCardsVO.getCardFaceValue()));
        posUserCardVO.setIcCardId(cardMapUserCardsVO.getIcCardId());
        posUserCardVO.setState(cardMapUserCardsVO.getState());
        posUserCardVO.setType(cardMapUserCardsVO.getType());
//        posUserCardVO.setValidFrom(cardCards.getValidFrom());
//        posUserCardVO.setValidTo(cardCards.getValidTo());
        posUserCardVO.setCardCardsType(cardMapUserCardsVO.getCardCardsType());
//        posUserCardVO.setCardCardsState(cardCards.getState());
        return posUserCardVO;
    }

    /**
     * 海旅 ,组合支付核销
     *
     * @param userFlagCode
     * @param amount
     * @param orderCode
     * @param merchantCode
     */
    public void posCombinationPay(String userFlagCode, Integer amount, String orderCode, String merchantCode) {

    }


    /**
     * 获取用户卡券列表的卡券可使用时间
     *
     * @param cardCards
     * @return
     */
    public String getUserCardShowTimeScope(CardCards cardCards, Date createAt) {
        String validityType = cardCards.getValidityType();

        String startStrData = DateStrUtil.dateToStr(createAt);

        if ("validDuration".equals(validityType)) {
            int validGapAfterAppliedTime = cardCards.getValidGapAfterApplied() / 24;
            int periodOfValidityTime = cardCards.getPeriodOfValidity() / 24;
            Calendar cal = Calendar.getInstance();
            cal.setTime(createAt);
            cal.add(cal.DATE, periodOfValidityTime);
            cal.add(cal.DATE, validGapAfterAppliedTime);
            Date date = cal.getTime();
            String showEndDate = DateStrUtil.dateToStr(date);
            return startStrData + "至" + showEndDate;
        } else if ("beginToEnd".equals(validityType)) {
            String start = DateStrUtil.dateToStr(cardCards.getValidFrom());
            String end = DateStrUtil.dateToStr(cardCards.getValidTo());
            return start + "至" + end;
        } else {
            return startStrData + "至 永久";
        }
    }

    public void checkGetTypeCard(String icCardId) {

    }

    /**
     * 计算支付后剩余的账户余额金额
     * @param list
     * @param userMoney
     * @param amount
     * @return
     */
    public Integer afterUserAccount(List<CardMapUserCardsVO> list, int userMoney, Integer amount) {
        int cardFaceMoney = 0;
        if (list != null && list.size() > 0) {
            for (CardMapUserCardsVO cardMapUserCardsVO : list) {
                if (cardMapUserCardsVO != null && !StringUtils.isEmpty(cardMapUserCardsVO.getCardCode())) {
                    if ("discount".equals(cardMapUserCardsVO.getCardCardsType())) {
                        String cardFaceValue = cardMapUserCardsVO.getCardFaceValue();
                        int discount = Integer.parseInt(cardFaceValue);
                        Double d = (100 - discount) * 0.01;
                        Double v = amount * d;
                        cardFaceMoney = v.intValue();
                    } else {
                        int parseIntCardValue = Integer.parseInt(cardMapUserCardsVO.getCardFaceValue());
                        cardFaceMoney = cardFaceMoney + parseIntCardValue;
                    }
                }
            }
        }
        Integer userNeedPay = amount - cardFaceMoney;
        if (userNeedPay <= 0) {
            return userMoney;
        } else if (userMoney - userNeedPay <= 0) {
            return 0;
        } else {
            return userMoney - userNeedPay;
        }
    }

}
