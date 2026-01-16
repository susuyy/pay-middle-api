package com.ht.user.card.service.impl;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ht.user.card.common.CardConstant;
import com.ht.user.card.entity.*;
import com.ht.user.card.mapper.CardMapUserCardsMapper;
import com.ht.user.card.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.card.vo.CardMapUserCardsVO;
import com.ht.user.card.vo.PosSelectCardNo;
import com.ht.user.card.vo.PosUserCardVO;
import com.ht.user.config.*;
import com.ht.user.mall.constant.CardUserMallConstant;
import com.ht.user.mall.entity.OrderOrderDetails;
import com.ht.user.utils.QrCodeUtils;
import com.ht.user.utils.QueryCardInfoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 用户，卡绑定关系 服务实现类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@Service
public class CardMapUserCardsServiceImpl extends ServiceImpl<CardMapUserCardsMapper, CardMapUserCards> implements CardMapUserCardsService {
    private Logger logger = LoggerFactory.getLogger(CardMapUserCardsServiceImpl.class);

    @Autowired
    private CardCardsService cardCardsService;

    @Autowired
    private CardMapMerchantCardsService cardMapMerchantCardsService;

    @Autowired
    private CardMapUserCardsTraceService userCardsTraceService;

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Autowired
    private CardMapUserCardsTraceService cardMapUserCardsTraceService;

    @Autowired
    private IActivityService activityService;

    @Autowired
    private IActivityLimitService activityLimitService;

    /**
     * 查询用户与卡的关联信息
     *
     * @param cardCode
     * @param userId
     * @return
     */
    @Override
    public CardMapUserCards queryByCardCodeAndUserId(String cardCode, Long userId) {
        return this.baseMapper.queryByCardCodeAndUserId(cardCode, userId);
    }


    /**
     * 创建卡与用户的关联信息
     *
     * @param cardCode
     * @param userId
     * @param merchantCode
     * @param cardName
     * @param categoryCode
     * @param categoryName
     * @param state
     * @param type
     * @param cardNo
     * @param cardType
     */
    @Override
    public void createCardMapUserCards(String cardCode, Long userId, String merchantCode, String cardName,
                                       String categoryCode, String categoryName, String state, String type, String cardNo, String actionType,
                                       String traceState, String faceValue, String batchCode, String cardType) {
        CardMapUserCards cardMapUserCards = getCardMapUserCards(cardCode, merchantCode, cardName, categoryCode, categoryName, state, type, cardNo, userId);
        cardMapUserCards.setFaceValue(faceValue);
        cardMapUserCards.setBatchCode(batchCode);
        cardMapUserCards.setCardType(cardType);
        this.baseMapper.insert(cardMapUserCards);
        cardMapUserCardsTraceService.createCardMapUserCardsTrace(userId,
                merchantCode,
                cardCode,
                cardNo,
                actionType,
                new Date(),
                traceState,
                batchCode);
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

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean createCardMapUserCards(String merchantCode, String cardCode, Long userId, String cardNo, String batchCode) {
        CardMapUserCards cardMapUserCards = new CardMapUserCards();
        cardMapUserCards.setUserId(userId);
        cardMapUserCards.setCardCode(cardCode);
        cardMapUserCards.setMerchantCode(merchantCode);
        CardCards cards = cardCardsService.queryByCardCode(cardCode);
        cardMapUserCards.setCardName(cards.getCardName());
        cardMapUserCards.setCategoryCode(cards.getCategoryCode());
        cardMapUserCards.setCategoryName(cards.getCategoryName());
        cardMapUserCards.setState("un_use");
        cardMapUserCards.setBatchCode(batchCode);
        cardMapUserCards.setType("virtual");
        cardMapUserCards.setCardNo(cardNo);
        cardMapUserCards.setFaceValue(String.valueOf(cards.getFaceValue()));
        cardMapUserCards.setCardType(cards.getType());
        return this.save(cardMapUserCards);
    }

    /**
     * 用户绑定实体储值卡
     *
     * @param icCardId
     * @param userId
     * @param merchantCode
     */
    @Override
    public void createCardMapUserCards(String icCardId, String userId, String merchantCode) {
        CardMapUserCards cardMapUserCards = new CardMapUserCards();
        cardMapUserCards.setMerchantCode(merchantCode);
        cardMapUserCards.setUserId(Long.parseLong(userId));
        cardMapUserCards.setIcCardId(icCardId);
        this.baseMapper.insert(cardMapUserCards);
    }

    /**
     * 根据实体卡卡号 和商户编码 查询用户信息
     *
     * @param icCardId
     * @param merchantCode
     * @return
     */
    @Override
    public CardMapUserCards queryByIcCardIdAndMerchantCode(String icCardId, String merchantCode) {
        return this.baseMapper.queryByIcCardIdAndMerchantCode(icCardId, merchantCode);
    }

    @Override
    public void updateUserCode(Long userId, String code, String merchantCode) {
        LambdaQueryWrapper<CardMapUserCards> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardMapUserCards::getMerchantCode, merchantCode);
        wrapper.eq(CardMapUserCards::getUserId, userId);
        wrapper.eq(CardMapUserCards::getType, "account");
        CardMapUserCards userCards = this.getOne(wrapper);
        if (userCards != null) {
            userCards.setIcCardId(code);
        } else {
            userCards = new CardMapUserCards();
            userCards.setIcCardId(code);
            userCards.setMerchantCode(merchantCode);
            userCards.setUserId(userId);
            userCards.setType("account");
            userCards.setState("正常");
        }
        this.saveOrUpdate(userCards);
    }

    /**
     * 用户 免费领取卡券 一天一次
     *
     * @param userId
     * @param cardCode
     * @param merchantCode
     * @param batchCode
     * @return
     */
    @Override
    public Boolean userGetCard(Long userId, String cardCode, String merchantCode, String batchCode) {
        CardCards cardCards = cardCardsService.queryByCardCode(cardCode);
        CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.queryByCardCodeAndBatchCode(cardCode, batchCode);
        //创建 卡券 与用户信息
        String cardNo = IdWorker.getIdStr();
        cardMapUserCardsService.createCardMapUserCards(
                cardCode,
                userId,
                cardMapMerchantCards.getMerchantCode(),
                cardMapMerchantCards.getCardName(),
                cardCards.getCategoryCode(),
                cardCards.getCategoryName(),
                UserCardsStateConfig.UN_USE,
                UserCardsTypeConfig.VIRTUAL,
                cardNo,
                UserCardsTraceActionTypeConfig.FREE_GET,
                UserCardsTraceStateConfig.NORMAL,
                cardCards.getFaceValue().toString(),
                batchCode,
                cardMapMerchantCards.getCardType());
        return true;
    }

    @Override
    public CardMapUserCards queryByIcCardId(String icCardId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("ic_card_id", icCardId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public CardMapUserCards queryByUserIdAndAccount(Long userId, String type) {
        return this.baseMapper.selectByUserIdAndType(userId, type);
    }


    /**
     * 计算出优惠力度最大的卡券
     *
     * @param cardMapUserCardsVOList
     * @param amount
     */
    @Override
    public CardMapUserCardsVO settlementCardMoney(List<CardMapUserCardsVO> cardMapUserCardsVOList, Integer amount) {
        ArrayList<CardCouponData> couponDataList = new ArrayList<>();
        for (CardMapUserCardsVO cardMapUserCardsVO : cardMapUserCardsVOList) {
            CardCards cardCards = cardCardsService.queryByCardCode(cardMapUserCardsVO.getCardCode());
            if (!"discount".equals(cardCards.getType()) && !"frozen".equals(cardCards.getState())) {
                CardCouponData cardCouponData = new CardCouponData();
                cardCouponData.setType(cardCards.getType());
                cardCouponData.setCouponMoney(Integer.parseInt(cardMapUserCardsVO.getCardFaceValue()));
                cardCouponData.setCardMapUserCardsVO(cardMapUserCardsVO);
                couponDataList.add(cardCouponData);
            } else {
                String cardFaceValue = cardMapUserCardsVO.getCardFaceValue();
                int discount = Integer.parseInt(cardFaceValue);
                Double d = (100 - discount) * 0.01;
                Double v = amount * d;
                int couponMoney = v.intValue();
                CardCouponData cardCouponData = new CardCouponData();
                cardCouponData.setType(cardCards.getType());
                cardCouponData.setCouponMoney(couponMoney);
                cardCouponData.setCardMapUserCardsVO(cardMapUserCardsVO);
                couponDataList.add(cardCouponData);
            }
        }

        CardCouponData cardCouponDataMin = couponDataList.get(0);
        for (CardCouponData cardCouponData : couponDataList) {
            if (cardCouponData.getCouponMoney() > cardCouponDataMin.getCouponMoney()) {
                cardCouponDataMin = cardCouponData;
            } else if (cardCouponData.getCouponMoney() == cardCouponDataMin.getCouponMoney()) {
                if (!"coupon".equals(cardCouponDataMin.getType()) && "coupon".equals(cardCouponData.getType())) {
                    cardCouponDataMin = cardCouponData;
                } else if (!"coupon".equals(cardCouponDataMin.getType()) && "money".equals(cardCouponData.getType())) {
                    cardCouponDataMin = cardCouponData;
                }
            }
        }
        return cardCouponDataMin.getCardMapUserCardsVO();
    }

    /**
     * 查询最优惠的折扣券
     *
     * @param discountCardArrayList
     * @return
     */
    private CardMapUserCardsVO findMaxDiscountCard(ArrayList<CardMapUserCardsVO> discountCardArrayList) {
        int minDis = Integer.parseInt(discountCardArrayList.get(0).getCardFaceValue());
        for (CardMapUserCardsVO cardMapUserCardsVO : discountCardArrayList) {
            String cardFaceValue = cardMapUserCardsVO.getCardFaceValue();
            int dis = Integer.parseInt(cardFaceValue);
            if (dis <= minDis) {
                minDis = dis;
            }
        }
        for (CardMapUserCardsVO cardMapUserCardsVO : discountCardArrayList) {
            if (cardMapUserCardsVO.getCardFaceValue().equals(minDis + "")) {
                return cardMapUserCardsVO;
            }
        }
        return new CardMapUserCardsVO();
    }

    /**
     * 计算优惠力度最大的 满减券 或是 金额券
     *
     * @param amount
     * @param cardMapUserCardsVOList
     * @return
     */
    public CardMapUserCardsVO findMaxCouponUserCard(Integer amount, ArrayList<CardMapUserCardsVO> cardMapUserCardsVOList) {
        //封装 卡券优惠 数据列表
        ArrayList<CouponMoneyData> couponMoneyList = new ArrayList<>();
        for (CardMapUserCardsVO cardMapUserCardsVO : cardMapUserCardsVOList) {
            String cardFaceValueStr = cardMapUserCardsVO.getCardFaceValue();
            int cardFaceValueInt = Integer.parseInt(cardFaceValueStr);
            Integer couponMoney = cardFaceValueInt - amount;
            if (couponMoney < 0) {
                CouponMoneyData couponMoneyData = new CouponMoneyData();
                couponMoneyData.setCardNo(cardMapUserCardsVO.getCardNo());
                couponMoneyData.setCouponMoney(couponMoney * -1);
                couponMoneyList.add(couponMoneyData);
            } else {
                CouponMoneyData couponMoneyData = new CouponMoneyData();
                couponMoneyData.setCardNo(cardMapUserCardsVO.getCardNo());
                couponMoneyData.setCouponMoney(couponMoney);
                couponMoneyList.add(couponMoneyData);
            }
        }
        //计算出卡券 最大的优惠力度的 卡券数据 couponMoneyData 中 couponMoney 最小的数据
        CouponMoneyData couponMoneyDataMin = couponMoneyList.get(0);
        for (int i = 0; i < couponMoneyList.size(); i++) {
            if (couponMoneyDataMin.getCouponMoney() > couponMoneyList.get(i).getCouponMoney()) {
                couponMoneyDataMin = couponMoneyList.get(i);
            }
        }
        //找出优惠力度最大的卡券信息
        for (CardMapUserCardsVO cardMapUserCardsVO : cardMapUserCardsVOList) {
            if (cardMapUserCardsVO.getCardNo().equals(couponMoneyDataMin.getCardNo())) {
                return cardMapUserCardsVO;
            }
        }
        return new CardMapUserCardsVO();
    }

    @Override
    public PosUserCardVO packagePosUserCardVO(CardMapUserCardsVO cardMapUserCardsVO) {
        if (StringUtils.isEmpty(cardMapUserCardsVO.getCardCode())) {
            return new PosUserCardVO();
        }
        PosUserCardVO posUserCardVO = new PosUserCardVO();
        posUserCardVO.setCardCode(cardMapUserCardsVO.getCardCode());
        CardCards cardCards = cardCardsService.queryByCardCode(cardMapUserCardsVO.getCardCode());
        posUserCardVO.setBatchTimes(cardCards.getBatchTimes());
        posUserCardVO.setCardName(cardMapUserCardsVO.getCardName());
        posUserCardVO.setCardNo(cardMapUserCardsVO.getCardNo());
        posUserCardVO.setCategoryCode(cardMapUserCardsVO.getCategoryCode());
        posUserCardVO.setCategoryName(cardMapUserCardsVO.getCategoryName());
        posUserCardVO.setFaceValue(Integer.parseInt(cardMapUserCardsVO.getCardFaceValue()));
        posUserCardVO.setIcCardId(cardMapUserCardsVO.getIcCardId());
        posUserCardVO.setState(cardMapUserCardsVO.getState());
        posUserCardVO.setType(cardMapUserCardsVO.getType());
        posUserCardVO.setValidFrom(cardCards.getValidFrom());
        posUserCardVO.setValidTo(cardCards.getValidTo());
        posUserCardVO.setCardCardsType(cardMapUserCardsVO.getCardCardsType());
        posUserCardVO.setCardCardsState(cardCards.getState());
        return posUserCardVO;
    }

    @Override
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

    /**
     * 根据卡编号查询卡券信息
     *
     * @param cardNo
     * @return
     */
    @Override
    public CardMapUserCards queryByCardNo(String cardNo) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("card_no", cardNo);
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 修改用户卡券 状态
     *
     * @param cardNoList
     * @param state
     */
    @Override
    public void updateUserCardsState(List<PosSelectCardNo> cardNoList, String state) {
        for (PosSelectCardNo posSelectCardNO : cardNoList) {
            this.baseMapper.updateUserCardsState(posSelectCardNO.getCardNo(), state);
            CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(posSelectCardNO.getCardNo());
            cardMapUserCardsTraceService.createCardMapUserCardsTrace(cardMapUserCards.getUserId(),cardMapUserCards.getMerchantCode(),cardMapUserCards.getCardCode(),
                    cardMapUserCards.getCardNo(),UserCardsTraceActionTypeConfig.USED,new Date(),state,cardMapUserCards.getBatchCode());
        }
    }

    @Override
    public String createQrCode(String cardNo) {
        String romCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        String content = cardNo + romCode;
        String qrCode = QrCodeUtils.creatRrCode(content, 200, 200);
        String replace = qrCode.replace("\r\n", "");
        String replaceOne = replace.replace("\n", "");
        String replaceTwo = replaceOne.replace("\r", "");
        return replaceTwo;
    }

    @Override
    public Integer getUserCardAmount(Long userId, String cardCode, String type, String date) {
        return this.baseMapper.getUserCardAmount(userId, cardCode, type, date);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean posSendCard(String merchantCode, String cardCode, Long userId, String batchCode) {
        String cardNo = IdWorker.getIdStr();
        this.createCardMapUserCards(merchantCode, cardCode, userId, cardNo, batchCode);

        CardMapUserCardsTrace trace = new CardMapUserCardsTrace();
        trace.setUserId(userId);
        trace.setMerchantCode(merchantCode);
        trace.setCardCode(cardCode);
        trace.setBatchCode(batchCode);
        trace.setCardNo(cardNo);
        trace.setActionType("pos");
        trace.setState("1");
        trace.setActionDate(new Date());
        userCardsTraceService.save(trace);
        return true;
    }

    @Override
    public void updateStateByCardNo(String cardNo, String state) {
        this.baseMapper.updateUserCardsState(cardNo, state);
    }

    @Override
    public Integer afterUserAccount(List<CardMapUserCardsVO> list, int userMoney, Integer amount) {
        int cardFaceMoney = 0;
        if (list != null && list.size() > 0) {
            for (CardMapUserCardsVO cardMapUserCardsVO : list) {
                if (cardMapUserCardsVO!=null && !StringUtils.isEmpty(cardMapUserCardsVO.getCardCode())) {
                    CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(cardMapUserCardsVO.getCardNo());
                    CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode());
                    if (cardMapMerchantCards != null) {
                        if ("discount".equals(cardMapMerchantCards.getCardType())) {
                            String cardFaceValue = cardMapMerchantCards.getCardFaceValue();
                            int discount = Integer.parseInt(cardFaceValue);
                            Double d = (100 - discount) * 0.01;
                            Double v = amount * d;
                            cardFaceMoney = v.intValue();
                        } else {
                            int parseIntCardValue = Integer.parseInt(cardMapMerchantCards.getCardFaceValue());
                            cardFaceMoney = cardFaceMoney + parseIntCardValue;
                        }
                    } else {
                        CardCards cardCards = cardCardsService.queryByCardCode(cardMapUserCards.getCardCode());
                        if ("discount".equals(cardCards.getType())) {
                            int discount = cardCards.getFaceValue();
                            Double d = (100 - discount) * 0.01;
                            Double v = amount * d;
                            cardFaceMoney = v.intValue();
                        } else {
                            int parseIntCardValue = cardCards.getFaceValue();
                            cardFaceMoney = cardFaceMoney + parseIntCardValue;
                        }
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

    @Override
    public Integer afterUserAccount(List<PosSelectCardNo> list, int userMoney, Integer amount, Boolean flag) {
        int cardFaceMoney = 0;
        if (list != null && list.size() > 0) {
            for (PosSelectCardNo posSelectCardNo : list) {
                CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(posSelectCardNo.getCardNo());
//                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getBatchCode());
//                if (cardMapMerchantCards != null) {
                    if ("discount".equals(cardMapUserCards.getCardType())) {
                        String cardFaceValue = cardMapUserCards.getFaceValue();
                        int discount = Integer.parseInt(cardFaceValue);
                        Double d = (100 - discount) * 0.01;
                        Double v = amount * d;
                        cardFaceMoney = v.intValue();
                    } else {
                        int parseIntCardValue = Integer.parseInt(cardMapUserCards.getFaceValue());
                        cardFaceMoney = cardFaceMoney + parseIntCardValue;
                    }
//                } else {
//                    CardCards cardCards = cardCardsService.queryByCardCode(cardMapUserCards.getCardCode());
//                    if ("discount".equals(cardCards.getType())) {
//                        int discount = cardCards.getFaceValue();
//                        Double d = (100 - discount) * 0.01;
//                        Double v = amount * d;
//                        cardFaceMoney = v.intValue();
//                    } else {
//                        int parseIntCardValue = cardCards.getFaceValue();
//                        cardFaceMoney = cardFaceMoney + parseIntCardValue;
//                    }
//                }
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

    @Override
    public List<CardMapUserCards> getCardPayList(long userId, String merchantCode, String state) {
        String mallState = "";
        if (UserCardsStateConfig.UN_USE.equals(state)) {
            mallState = UserCardsStateConfig.MALL_FREE_UN_USE;
        }

        if (UserCardsStateConfig.USED.equals(state)) {
            mallState = UserCardsStateConfig.MALL_FREE_USED;
        }

        List<CardMapUserCards> cardMapUserCardsList = this.baseMapper.selectByUserIdAndMerchantCodeNoNumber(userId, merchantCode, state, mallState);
        return cardMapUserCardsList;
    }

    @Override
    public CardMapUserCards getUserVipCard(String merchantCode, Long userId) {
        LambdaQueryWrapper<CardMapUserCards> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardMapUserCards::getMerchantCode, merchantCode);
        queryWrapper.eq(CardMapUserCards::getUserId, userId);
        queryWrapper.eq(CardMapUserCards::getCardCode, "867");
        return this.getOne(queryWrapper);
    }

    /**
     * 获取用户的计次卡券列表
     *
     * @param userId
     * @param merchantCode
     * @param userId
     * @param merchantCode
     * @return
     */
    @Override
    public List<CardMapUserCardsVO> selectUserNumberList(String userId, String merchantCode) {
        return this.baseMapper.selectUserNumberList(Long.parseLong(userId), merchantCode, "number", UserCardsStateConfig.UN_USE, "normal");
    }

    @Override
    public Integer qrUseNumberCard(String cardNo) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("card_no", cardNo);
        CardMapUserCards cardMapUserCards = this.baseMapper.selectOne(queryWrapper);
        Integer count = Integer.parseInt(cardMapUserCards.getFaceValue()) - 1;
        if (count <= 0) {
            cardMapUserCards.setState(UserCardsStateConfig.USED);
        }
        cardMapUserCards.setFaceValue(count.toString());
        this.baseMapper.updateById(cardMapUserCards);
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
        cardMapUserCardsTrace.setActionType("qr_use");
        cardMapUserCardsTraceService.save(cardMapUserCardsTrace);
        return count;
    }

    /**
     * 获取用户的可用卡券列表
     *
     * @param userId
     * @param merchantCode
     * @param type
     * @param state
     * @param objMerchantCode
     * @return
     */
    @Override
    public List<CardMapUserCards> posCanUseCard(Long userId, String merchantCode, String type, String state, String objMerchantCode) {
        return this.baseMapper.selectByUserIdAndMerchantCodeAndTypeAndState(userId, merchantCode, type, state, objMerchantCode);
    }

    /**
     * 获取用户在商户下的卡券数量
     *
     * @param userId
     * @param merchantCodeList
     * @param type
     * @param state
     * @return
     */
    @Override
    public Integer selectCount(Long userId, List<String> merchantCodeList, String type, String state) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.in("merchant_code", merchantCodeList);
        queryWrapper.ne("type", "account");
        queryWrapper.eq("state", "un_use");
        return this.baseMapper.selectCount(queryWrapper);
    }

    /**
     * 创建账户余额 数据
     *
     * @param cardCode
     * @param userId
     * @param objectMerchantCode
     * @param cardName
     * @param categoryCode
     * @param categoryName
     * @param state
     * @param type
     * @param cardId
     */
    @Override
    public void createCardMapUserCards(String cardCode, Long userId, String objectMerchantCode, String cardName, String categoryCode, String categoryName, String state, String type, String cardId) {
        CardMapUserCards cardMapUserCards = new CardMapUserCards();
        cardMapUserCards.setUserId(userId);
        cardMapUserCards.setMerchantCode(objectMerchantCode);
        cardMapUserCards.setCardCode(cardCode);
        cardMapUserCards.setCardNo(cardId);
        cardMapUserCards.setCardName(cardName);
        cardMapUserCards.setCategoryCode(categoryCode);
        cardMapUserCards.setCategoryName(categoryName);
        cardMapUserCards.setState(state);
        cardMapUserCards.setType(type);
        cardMapUserCards.setCreateAt(new Date());
        cardMapUserCards.setUpdateAt(new Date());
        this.baseMapper.insert(cardMapUserCards);
    }

    /**
     * 商城创建商品用户关系 和 流水
     *
     * @param cardMapUserCards
     */
    @Override
    public void saveCardMapUserAndTrace(CardMapUserCards cardMapUserCards) {
        try {
            this.baseMapper.insert(cardMapUserCards);
            cardMapUserCardsTraceService.createCardMapUserCardsTrace(cardMapUserCards.getUserId(),
                    cardMapUserCards.getMerchantCode(),
                    cardMapUserCards.getCardCode(),
                    cardMapUserCards.getCardNo(),
                    "mall_buy",
                    new Date(),
                    "normal",
                    cardMapUserCards.getBatchCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 商城用户免费领券
     *
     * @param mallUserGetCardData
     * @return
     */
    @Override
    public Boolean mallUserGetCard(MallUserGetCardData mallUserGetCardData) {
        CardCards cardCards = cardCardsService.queryByCardCode(mallUserGetCardData.getCardCode());
        CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.mallQueryCodeMerchantCodeType(mallUserGetCardData.getCardCode(), mallUserGetCardData.getMerchantCode(), UserCardsStateConfig.MALL_FREE);
        //创建 卡券 与用户信息
        String cardNo = IdWorker.getIdStr();
        cardMapUserCardsService.createCardMapUserCards(
                mallUserGetCardData.getCardCode(),
                mallUserGetCardData.getUserId(),
                cardMapMerchantCards.getMerchantCode(),
                cardMapMerchantCards.getCardName(),
                cardCards.getCategoryCode(),
                cardCards.getCategoryName(),
                UserCardsStateConfig.MALL_FREE_UN_USE,
                UserCardsStateConfig.MALL_FREE,
                cardNo,
                UserCardsTraceActionTypeConfig.MALL_FREE_GET,
                UserCardsTraceStateConfig.NORMAL,
                cardCards.getFaceValue().toString(),
                cardMapMerchantCards.getBatchCode(),
                cardMapMerchantCards.getCardType());
        return true;
    }

    /**
     * 查询用户 商城卡券 包括购买 和 领取
     *
     * @param queryMyCardData
     * @return
     */
    @Override
    public Page<CardMapUserCards> mallQueryUserCard(QueryMyCardData queryMyCardData) {
        String state = queryMyCardData.getState();
        Page<CardMapUserCards> page = new Page<>(queryMyCardData.getPageNo(), queryMyCardData.getPageSize());
        QueryWrapper<CardMapUserCards> queryWrapper = new QueryWrapper<>();
        if ("un_use".equals(queryMyCardData.getState())) {
            queryWrapper.eq("state", CardUserMallConstant.MALL_BUY_UN_USE_STATE).or().eq("state", CardUserMallConstant.MALL_FREE_GET_UN_USE_STATE);
        } else if ("used".equals(queryMyCardData.getState())) {
            state = CardUserMallConstant.MALL_USED;
            queryWrapper.eq("state", state);
        } else if ("invalid".equals(state)) {
            state = CardUserMallConstant.MALL_INVALID;
            queryWrapper.eq("state", state);
        }
        queryWrapper.eq("user_id", queryMyCardData.getUserId());
        List<Merchants> merchantsList = queryMyCardData.getMerchantsList();
        List<String> merchantCodeList = new ArrayList<>();
        for (Merchants merchants : merchantsList) {
            merchantCodeList.add(merchants.getMerchantCode());
        }
        queryWrapper.in("merchant_code", merchantCodeList);
        return this.baseMapper.mallSelectUserCard(page, queryWrapper);
    }

    /**
     * 根据userId 查询卡信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<CardMapUserCardsVO> queryByUserId(long userId) {
        List<CardMapUserCardsVO> cardMapUserCardsVOS = new ArrayList<CardMapUserCardsVO>();
        List<CardMapUserCards> cardMapUserCardsList = this.baseMapper.selectByUserId(userId);
        for (CardMapUserCards cardMapUserCard : cardMapUserCardsList) {
            CardMapMerchantCards cardMsgByCardCode = cardMapMerchantCardsService.getCardMsgByCardCode(cardMapUserCard.getCardCode(), cardMapUserCard.getBatchCode());
            CardMapUserCardsVO cardMapUserCardsVO = new CardMapUserCardsVO();
            BeanUtils.copyProperties(cardMapUserCard, cardMapUserCardsVO);
            cardMapUserCardsVO.setCardFaceValue(cardMsgByCardCode.getCardFaceValue());
            cardMapUserCardsVOS.add(cardMapUserCardsVO);
        }
        return cardMapUserCardsVOS;
    }


    /**
     * 根据用户id 查询钱包信息(调用通联接口)
     *
     * @param userId
     * @return
     */
    @Override
    public BigDecimal queryUserMoney(Long userId) {
        CardMapUserCards cardMapUserCards = this.baseMapper.selectByUserIdAndCategoryCodeAndType(userId, TongLianCardState.CATEGORY.getCode() + "", TongLianCardState.TYPE.getDesc());
        if (cardMapUserCards == null) {
            return BigDecimal.ZERO;
        }
        try {
            String content = QueryCardInfoUtil.queryCardInfo(cardMapUserCards.getCardNo());
            PpcsCardinfoGetReturnData ppcsCardinfoGetReturnData = JSONObject.parseObject(content, PpcsCardinfoGetReturnData.class);
            PpcsCardinfoGetResponse ppcsCardinfoGetResponse = ppcsCardinfoGetReturnData.getPpcsCardinfoGetResponse();
            CardInfo cardInfo = ppcsCardinfoGetResponse.getCardInfo();
            CardProductInfoArrays cardProductInfoArrays = cardInfo.getCardProductInfoArrays();
            if (cardProductInfoArrays == null) {
                return BigDecimal.ZERO;
            }
            List<CardProductInfo> cardProductInfos = cardProductInfoArrays.getCardProductInfo();
            BigDecimal money = BigDecimal.ZERO;
            for (CardProductInfo cardProductInfo : cardProductInfos) {
                String accountBalance = cardProductInfo.getAccountBalance();
                money = money.add(new BigDecimal(accountBalance));
            }
            return money;
        } catch (Exception e) {
            logger.info("用户id:" + userId + ",查询余额失败,调取通联查询余额接口异常:" + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 根据user_id和商户编码获取在商户下的虚拟卡列表
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Override
    public List<CardMapUserCards> queryByUserIdAndMerchantCode(Long userId, String merchantCode, String state) {
        List<CardMapUserCards> cardMapUserCardsList = this.baseMapper.selectByUserIdAndMerchantCode(userId, merchantCode, state);
        return cardMapUserCardsList;
    }

    /**
     * 根据user_id和商户编码获取在商户下的虚拟卡列表
     *
     * @param userId
     * @param merchantCode
     * @param state
     * @return
     */
    @Override
    public List<CardMapUserCards> queryByUserIdAndMerchantCodeNotIc(Long userId, String merchantCode, String state) {
        List<CardMapUserCards> cardMapUserCardsList = this.baseMapper.selectByUserIdAndMerchantCodeNotIc(userId, merchantCode, state);
        return cardMapUserCardsList;
    }

    /**
     * 根据实体卡号和用户关系查询绑定关系
     *
     * @param icCardId
     * @param userId
     * @return
     */
    @Override
    public CardMapUserCards queryByIcCardIdAndUserId(String icCardId, Long userId) {
        return this.baseMapper.selectByIcCardIdAndUserId(icCardId, userId);
    }

    /**
     * 用户根据cardNo 绑定实体卡
     *
     * @param cardNo
     * @param userId
     * @param icCardId
     */
    @Override
    public void bindIcCardId(String cardNo, Long userId, String icCardId) {
        this.baseMapper.updateIcCardIdByCardNoAndUserId(cardNo, userId, icCardId);
    }

    @Override
    public IPage<CardMapUserCards> getUserCardInMerchants(
            Long userId, List<String> merchantCodes, String state,
            Integer pageNo, Integer pageSize, String type) {
        Page<CardMapUserCards> page = new Page<>(pageNo, pageSize);
        return this.baseMapper.getUserCardPage(page, userId, merchantCodes, state, type);
    }

    /**
     * 查询用户商城购买的卡券
     *
     * @param userId
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public Page<CardMapUserCards> queryUserBuyCardList(Long userId, String state, String type, Integer pageNo, Integer pageSize) {
        Page<CardMapUserCards> page = new Page<>(pageNo, pageSize);
        QueryWrapper<CardMapUserCards> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(state)) {
            queryWrapper.eq("state", state);
        }
        if (!StringUtils.isEmpty(type)) {
            queryWrapper.eq("type", type);
        }
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_at");
        return this.baseMapper.selectUserBuyCardList(page, queryWrapper);
    }

    /**
     * 计算折扣券 折扣的金额
     *
     * @param amount
     * @param cardMapUserCards
     * @return
     */
    @Override
    public int calculateDiscountMoney(Integer amount, CardMapUserCards cardMapUserCards) {
        int discount = Integer.parseInt(cardMapUserCards.getFaceValue());
        Double d = (100 - discount) * 0.01;
        Double v = amount * d;
        Integer cardCouponMoney = v.intValue();
        return cardCouponMoney;
    }

    @Override
    public CardMapUserCards queryByUserIdAndAccountAndMerchantCode(Long userId, String merchantCode) {
        QueryWrapper<CardMapUserCards> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_code", merchantCode);
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", "account");
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 满赠活动
     *
     * @param merchantCode
     * @param amount
     * @param userId
     * @return 发放结果
     */
    @Override
    public boolean sendCardForFulfilQuota(String merchantCode, Integer amount, Long userId) {
        //todo 获取merchant目前可用活动
        Activity activity = activityService.getActiveActivity(merchantCode);
        //todo 判断金额满足活动的条件
        ActivityLimit activityLimit = activityLimitService.getActiveLevel(amount, activity.getActivityCode());
        if (activityLimit == null) {
            return false;
        }
        List<ActivityLimitMapCard> cardsList = activityLimit.getCardsList();
        if (!CollectionUtils.isEmpty(cardsList)) {
            cardsList.forEach(e -> {
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.getActivityCard(e.getCardCode(), merchantCode);
                for (int i = 0; i < e.getAmount(); i++) {
                    String cardNo = IdWorker.getIdStr();
                    createCardMapUserCards(merchantCode, cardMapMerchantCards.getCardCode(), userId, cardNo, cardMapMerchantCards.getBatchCode());
                    cardMapUserCardsTraceService.createCardMapUserCardsTrace(userId,merchantCode,cardMapMerchantCards.getCardCode(),cardNo,
                            CardConstant.ACTIVITY_CARD,new Date(),CardConstant.CARD_STATE_NORMAL,cardMapMerchantCards.getBatchCode());
                }
            });
        }
        return true;
    }

    @Override
    public void updateRefundState(List<OrderOrderDetails> orderOrderDetailsList) {
        for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
            List<CardMapUserCards> cardMapUserCardsList = queryByRefKeyAndStateLimit(orderOrderDetails.getId(), UserCardsStateConfig.MALL_BUY_UN_USE_STATE, orderOrderDetails.getQuantity().intValue());
            if (cardMapUserCardsList!=null && cardMapUserCardsList.size()>0) {
                for (CardMapUserCards cardMapUserCards : cardMapUserCardsList) {
                    cardMapUserCards.setState(UserCardsStateConfig.REFUND_ING);
                    this.baseMapper.updateById(cardMapUserCards);
                }
            }
        }
    }

    @Override
    public List<CardMapUserCards> queryByRefKeyAndStateLimit(Long refKey, String state, int quantity) {
        return this.baseMapper.selectByRefKeyAndStateLimit(refKey,state,quantity);
    }

    @Override
    public Integer queryCountByRefKeyAndState(Long refKey, String state) {
        QueryWrapper<CardMapUserCards> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("ref_source_key",refKey);
        queryWrapper.eq("state",state);
        return this.baseMapper.selectCount(queryWrapper);
    }

    @Override
    public Boolean removeUserCardForRefund(Long userId, String productionCode, String refKey) {
        LambdaQueryWrapper<CardMapUserCards> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardMapUserCards::getCardCode, productionCode);
        wrapper.eq(CardMapUserCards::getUserId, userId);
        wrapper.eq(CardMapUserCards::getRefSourceKey, refKey);
        List<CardMapUserCards> userCards = this.list(wrapper);
        if (CollectionUtils.isEmpty(userCards)){
            return null;
        }
        userCards.forEach(e->{
            if (e.getState().equals(UserCardsStateConfig.REFUND_ING)){
                e.setState(CardConstant.USER_CARD_DELETED);
            }
        });
        logger.info("**********退款删除卡券***********"+ JSON.toJSONString(userCards));
        return this.updateBatchById(userCards);
    }
}
