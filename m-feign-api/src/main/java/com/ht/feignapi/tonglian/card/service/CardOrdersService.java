package com.ht.feignapi.tonglian.card.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetPageData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.controller.CardOrderPayTraceController;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.config.*;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.user.entity.RetCalculationData;
import com.ht.feignapi.tonglian.user.entity.RetSettlementUserMoneyData;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/17 19:49
 */
@Service
public class CardOrdersService {
    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private MSPrimeService msPrimeService;

    @Autowired
    private MapMerchantPrimesClientService mapMerchantPrimesClientService;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    private final static Logger log = LoggerFactory.getLogger(CardOrdersService.class);


    public RetPageData orderListPage(String openid, List<Merchants> merchantAndSon, String type, String state, Integer pageNo, Integer pageSize) {
        UserUsers usrUsers = authClientService.queryByOpenid(openid).getData();
        return orderClientService.selectOrderPage(usrUsers.getId(), merchantAndSon, type, state, pageNo, pageSize).getData();
    }

    public PlaceOrderResult placeOrder(UserPlaceOrderData userPlaceOrderData) {
        UserUsers usrUsers = authClientService.queryByOpenid(userPlaceOrderData.getOpenid()).getData();
        return orderClientService.placeOrder(userPlaceOrderData, usrUsers.getId()).getData();
    }

    /**
     * 组合支付 创建流水
     * @param posCombinationPay
     * @param state
     */
    public void posCombinationPay(PosCombinationPay posCombinationPay, RetPosCombinationPayEleCard retPosCombinationPayEleCard, String state) {
        List<CardOrderPayTrace> saveCardPayTraceList =new ArrayList<>();
        List<CardConsumeDetails> cardConsumeDetailsList = retPosCombinationPayEleCard.getCardConsumeDetailsList();
        for (CardConsumeDetails cardConsumeDetails : cardConsumeDetailsList) {
            if (cardConsumeDetails.getCardPaidAmount()>0) {
                // 流水创建
                CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
                cardOrderPayTrace.setOrderCode(posCombinationPay.getOrderCode());
                cardOrderPayTrace.setAmount(cardConsumeDetails.getCardPaidAmount());
                cardOrderPayTrace.setUserFlag(posCombinationPay.getUserFlagCode());
                cardOrderPayTrace.setMerchantCode(posCombinationPay.getMerchantCode());
                cardOrderPayTrace.setState(state);
                cardOrderPayTrace.setPayCode(posCombinationPay.getPayCode());
                cardOrderPayTrace.setRefTraceNo(posCombinationPay.getPayCode());
                cardOrderPayTrace.setTraceNo(IdWorker.getIdStr());
                cardOrderPayTrace.setSourceId(cardConsumeDetails.getCardNo());

                if ("self".equals(cardConsumeDetails.getPartyOrSelf())) {
                    cardOrderPayTrace.setRefBatchCode(cardConsumeDetails.getCardElectronic().getBatchCode());
                    cardOrderPayTrace.setRefCardType(cardConsumeDetails.getCardElectronic().getCardType());
                    cardOrderPayTrace.setRefCardName(cardConsumeDetails.getCardElectronic().getCardName());
                    cardOrderPayTrace.setUserPhone(cardConsumeDetails.getCardElectronic().getUserPhone());
                    cardOrderPayTrace.setRefRemainFaceValue(cardConsumeDetails.getCardElectronic().getFaceValue());
                    cardOrderPayTrace.setRefCardBrhId(cardConsumeDetails.getCardElectronic().getRefMerchantCode());
                }else {
                    cardOrderPayTrace.setRefBatchCode(cardConsumeDetails.getPartyCardElectronic().getBatchCode());
                    cardOrderPayTrace.setRefCardType(cardConsumeDetails.getPartyCardElectronic().getCardType());
                    cardOrderPayTrace.setRefCardName(cardConsumeDetails.getPartyCardElectronic().getCardName());
                    cardOrderPayTrace.setUserPhone(cardConsumeDetails.getPartyCardElectronic().getUserPhone());
                    cardOrderPayTrace.setRefRemainFaceValue(cardConsumeDetails.getPartyCardElectronic().getFaceValue());
                    cardOrderPayTrace.setRefCardBrhId(cardConsumeDetails.getPartyCardElectronic().getChannelId());
                }

                cardOrderPayTrace.setCreateAt(new Date());
                cardOrderPayTrace.setUpdateAt(new Date());
                saveCardPayTraceList.add(cardOrderPayTrace);
            }
        }
        cardOrderPayTraceClientService.createPosCombinationPayTrace(saveCardPayTraceList,
                posCombinationPay.getAmount(),
                retPosCombinationPayEleCard.getUserId(),
                posCombinationPay.getStoreCode(),
                posCombinationPay.getActualPhone(),
                posCombinationPay.getIdCardNo());

        //记录订单上送的商品数据
        if (!StringUtils.isEmpty(posCombinationPay.getOrderDetail()) && !"null".equals(posCombinationPay.getOrderDetail())){
            String orderGoodsStr = posCombinationPay.getOrderDetail();
            List list = JSONObject.parseObject(orderGoodsStr, List.class);
            for (Object data : list) {
                UploadOrderDetails uploadOrderDetails = JSONObject.parseObject(JSONObject.toJSONString(data), UploadOrderDetails.class);
                CardOrdersGoods cardOrdersGoods = new CardOrdersGoods();
                cardOrdersGoods.setOrderCode(posCombinationPay.getOrderCode());
                cardOrdersGoods.setGoodsGroupCode(uploadOrderDetails.getGoodsGroupCode());
                cardOrdersGoods.setCategoryCode(uploadOrderDetails.getCategoryCode());
                cardOrdersGoods.setBrandCode(uploadOrderDetails.getBrandCode());
                cardOrdersGoods.setGoodsCode(uploadOrderDetails.getGoodsCode());
                cardOrdersGoods.setGoodsName(uploadOrderDetails.getGoodsName());
                cardOrdersGoods.setGoodsCount(Integer.parseInt(uploadOrderDetails.getGoodsCount()));
                cardOrdersGoods.setGoodsPrice(Integer.parseInt(uploadOrderDetails.getGoodsPrice()));
                cardOrdersGoods.setGoodsDiscount(Integer.parseInt(uploadOrderDetails.getGoodsDiscount()));
                cardOrdersGoods.setGoodsPayPrice(Integer.parseInt(uploadOrderDetails.getGoodsPayPrice()));
                cardOrdersGoods.setGoodsActivityType(uploadOrderDetails.getGoodsActivityType());
                cardOrdersGoods.setCreateAt(new Date());
                cardOrdersGoods.setUpdateAt(new Date());
                cardOrderPayTraceClientService.saveOrderGoods(cardOrdersGoods);
            }
        }
    }

    /**
     * 组合支付 创建流水
     * @param posCombinationPay
     * @param state
     */
    public void posCombinationPay(PosCombinationPay posCombinationPay, RetPosCombinationPayEleCard retPosCombinationPayEleCard, String state,String posSerialNum) {
        List<CardOrderPayTrace> saveCardPayTraceList =new ArrayList<>();
        List<CardConsumeDetails> cardConsumeDetailsList = retPosCombinationPayEleCard.getCardConsumeDetailsList();
        for (CardConsumeDetails cardConsumeDetails : cardConsumeDetailsList) {
            if (cardConsumeDetails.getCardPaidAmount()>0) {
                // 流水创建
                CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
                cardOrderPayTrace.setOrderCode(posCombinationPay.getOrderCode());
                cardOrderPayTrace.setAmount(cardConsumeDetails.getCardPaidAmount());
                cardOrderPayTrace.setUserFlag(posCombinationPay.getUserFlagCode());
                cardOrderPayTrace.setMerchantCode(posCombinationPay.getMerchantCode());
                cardOrderPayTrace.setSourceId(cardConsumeDetails.getCardNo());
                cardOrderPayTrace.setState(state);
                cardOrderPayTrace.setPosSerialNum(posSerialNum);
                cardOrderPayTrace.setPayCode(posCombinationPay.getPayCode());
                cardOrderPayTrace.setCreateAt(new Date());
                cardOrderPayTrace.setUpdateAt(new Date());
                saveCardPayTraceList.add(cardOrderPayTrace);
            }
        }
        cardOrderPayTraceClientService.createPosCombinationPayTrace(saveCardPayTraceList,
                posCombinationPay.getAmount(),
                retPosCombinationPayEleCard.getUserId(),
                posCombinationPay.getStoreCode(),
                posCombinationPay.getActualPhone(),
                posCombinationPay.getIdCardNo());

        //记录订单上送的商品数据
        if (!StringUtils.isEmpty(posCombinationPay.getOrderDetail()) && !"null".equals(posCombinationPay.getOrderDetail())){
            String orderGoodsStr = posCombinationPay.getOrderDetail();
            List list = JSONObject.parseObject(orderGoodsStr, List.class);
            for (Object data : list) {
                UploadOrderDetails uploadOrderDetails = JSONObject.parseObject(JSONObject.toJSONString(data), UploadOrderDetails.class);
                CardOrdersGoods cardOrdersGoods = new CardOrdersGoods();
                cardOrdersGoods.setOrderCode(posCombinationPay.getOrderCode());
                cardOrdersGoods.setGoodsGroupCode(uploadOrderDetails.getGoodsGroupCode());
                cardOrdersGoods.setCategoryCode(uploadOrderDetails.getCategoryCode());
                cardOrdersGoods.setBrandCode(uploadOrderDetails.getBrandCode());
                cardOrdersGoods.setGoodsCode(uploadOrderDetails.getGoodsCode());
                cardOrdersGoods.setGoodsName(uploadOrderDetails.getGoodsName());
                cardOrdersGoods.setGoodsCount(Integer.parseInt(uploadOrderDetails.getGoodsCount()));
                cardOrdersGoods.setGoodsPrice(Integer.parseInt(uploadOrderDetails.getGoodsPrice()));
                cardOrdersGoods.setGoodsDiscount(Integer.parseInt(uploadOrderDetails.getGoodsDiscount()));
                cardOrdersGoods.setGoodsPayPrice(Integer.parseInt(uploadOrderDetails.getGoodsPayPrice()));
                cardOrdersGoods.setGoodsActivityType(uploadOrderDetails.getGoodsActivityType());
                cardOrdersGoods.setCreateAt(new Date());
                cardOrdersGoods.setUpdateAt(new Date());
                cardOrderPayTraceClientService.saveOrderGoods(cardOrdersGoods);
            }
        }
    }

    /**
     * 计算 电子卡实体卡金额
     * @param cardPhysical
     * @param consumeAmount
     * @param merchantCode
     * @return
     */
    public RetCardElectronicPosCashier cardElectronicPosCashier(CardPhysical cardPhysical, Integer consumeAmount, String merchantCode) {
        RetCardElectronicPosCashier retCardElectronicPosCashier = new RetCardElectronicPosCashier();
        //实体卡绑定电话确定用户信息
        retCardElectronicPosCashier.setUserId(cardPhysical.getUserId());

        PosCashierData posCashierData = new PosCashierData();
        int cardMoney = Integer.parseInt(cardPhysical.getFaceValue());
        int amount = consumeAmount - cardMoney;
        if (amount > 0){
            posCashierData.setAmount(amount);
        }else {
            posCashierData.setAmount(0);
        }
        posCashierData.setPosUserCardVOS(new ArrayList<PosUserCardVO>());
        posCashierData.setUserAccount(0);
        posCashierData.setAfterUserAccount(0);
        posCashierData.setElectronicCardAccount(cardMoney);
        int afterCardMoney = cardMoney - consumeAmount;
        if (afterCardMoney <= 0){
            posCashierData.setAfterElectronicCardAccount(0);
        }else {
            posCashierData.setAfterElectronicCardAccount(afterCardMoney);
        }
        posCashierData.setCardDiscountMoney(0);

        retCardElectronicPosCashier.setPosCashierData(posCashierData);
        return retCardElectronicPosCashier;
    }

    /**
     * 计算 电子卡实体卡金额
     * @param cardPhysical
     * @param consumeAmount
     * @param merchantCode
     * @return
     */
    public RetSettlement cardElectronicSettlement(CardPhysical cardPhysical, Integer consumeAmount, String merchantCode) {
        RetSettlement retSettlement = new RetSettlement();
        //实体卡绑定电话确定用户信息
        retSettlement.setUserId(cardPhysical.getUserId());

        SettlementMoneyData settlementMoneyData = new SettlementMoneyData();
        settlementMoneyData.setCardDiscountMoney(0);

        int cardMoney = Integer.parseInt(cardPhysical.getFaceValue());
        int amount = consumeAmount - cardMoney;
        if (amount > 0){
            settlementMoneyData.setAmount(amount);
            settlementMoneyData.setIsToPay(true);
        }else {
            settlementMoneyData.setAmount(0);
            settlementMoneyData.setIsToPay(false);
        }
        settlementMoneyData.setElectronicCardAccount(cardMoney);
        settlementMoneyData.setUserAccount(0);
        settlementMoneyData.setAfterUserAccount(0);
        int afterCardMoney = cardMoney - consumeAmount;
        if (afterCardMoney <= 0){
            settlementMoneyData.setAfterElectronicCardAccount(0);
        }else {
            settlementMoneyData.setAfterElectronicCardAccount(afterCardMoney);
        }
        settlementMoneyData.setCardDiscountMoney(0);
        retSettlement.setSettlementMoneyData(settlementMoneyData);
        return retSettlement;
    }

    /**
     * 计算组合支付 金额数据 初次进入页面逻辑
     * @param userId
     * @param settlement
     * @param cardPhysical
     * @return
     */
    public PosCashierData settlement(Long userId, Settlement settlement,CardPhysical cardPhysical) {
        //获取用户所有虚拟卡券
        List<CardMapUserCardsVO> cardMapUserCardsVOList = cardUserService.queryByUserIdAndMerchantCodeAndTypeAndState(userId, settlement.getMerchantCode(), UserCardsTypeConfig.VIRTUAL, UserCardsStateConfig.UN_USE, settlement.getAmount());
        //封装pos 端需要的卡券列表信息
        List<PosUserCardVO> posUserCardVOList = cardUserService.packagePosUserCardVOList(cardMapUserCardsVOList);
        //计算对用户 优惠力度最大的 卡券
        CardMapUserCardsVO cardMapUserCardsVO = cardMapUserClientService.settlementCardMoney(cardMapUserCardsVOList, settlement.getAmount()).getData();
        //封装 pos 端需要的卡券信息
        if (cardMapUserCardsVO!=null) {
            PosUserCardVO posUserCardVO = cardUserService.packagePosUserCardVO(cardMapUserCardsVO);
            if (posUserCardVOList.size() > 0) {
                for (PosUserCardVO posUserCardVORet : posUserCardVOList) {
                    if (posUserCardVORet.getCardNo().equals(posUserCardVO.getCardNo())) {
                        posUserCardVORet.setDefaultSelect(true);
                    }
                }
            }
        }
        //核算 钱 余额里面的
        PosCashierData posCashierData = new PosCashierData();
        posCashierData.setPosUserCardVOS(posUserCardVOList);
        //封装账户余额返回
        BigDecimal userMoneyBD = cardMapUserClientService.queryUserMoney(userId).getData();
        posCashierData.setUserAccount(Integer.parseInt(userMoneyBD.toString()));
        try {
            RetSettlementUserMoneyData retSettlementUserMoneyData = userUsersService.settlementUserMoney(cardMapUserCardsVO, settlement.getAmount(), userId,userMoneyBD);
            Integer amount = retSettlementUserMoneyData.getAmount();
            //封装电子卡余额
            if (cardPhysical!=null) {
                RetCardElectronicCalculate retCardElectronicCalculate = msPrimeService.calculateCardElectronicMoney(cardPhysical,amount);
                posCashierData.setElectronicCardAccount(retCardElectronicCalculate.getOriCardAmount());
                posCashierData.setAfterElectronicCardAccount(retCardElectronicCalculate.getAfterCardAmount());
                amount = retCardElectronicCalculate.getPayAmount();
            }else {
                posCashierData.setElectronicCardAccount(0);
                posCashierData.setAfterElectronicCardAccount(0);
            }
            posCashierData.setAmount(amount);
            Integer cardDiscountMoney = retSettlementUserMoneyData.getCardDiscountMoney();
            if (cardDiscountMoney==null){
                posCashierData.setCardDiscountMoney(0);
            }else {
                posCashierData.setCardDiscountMoney(retSettlementUserMoneyData.getCardDiscountMoney());
            }
        } catch (Exception e) {
            posCashierData.setAmount(settlement.getAmount());
            e.printStackTrace();
        }
        List<CardMapUserCardsVO> list = new ArrayList<>();
        if (cardMapUserCardsVO!=null) {
            list.add(cardMapUserCardsVO);
        }
        //封装 计算后的余额返回
//        Integer afterUserAccount = cardMapUserClientService.afterUserAccount(list, Integer.parseInt(userMoneyBD.toString()), settlement.getAmount()).getData();
        Integer afterUserAccount = cardUserService.afterUserAccount(list, Integer.parseInt(userMoneyBD.toString()), settlement.getAmount());
        if (afterUserAccount == null){
            posCashierData.setAfterUserAccount(0);
        }else {
            posCashierData.setAfterUserAccount(afterUserAccount);
        }
        return posCashierData;
    }


    /**
     * pos 端收银 返回 计算过后的金额 不直接结算
     *
     * @param settlementCard
     * @return
     */
    public SettlementMoneyData settlementCardMoney(Long userId, SettlementCard settlementCard, CardPhysical cardPhysical) {
        //原组合支付
        //判断卡券类型,校验使用规则
        UseCardData useCardData = cardUserService.checkUseCard(settlementCard.getCardNoList(), settlementCard.getAmount());
        SettlementMoneyData settlementMoneyData = new SettlementMoneyData();
        RetCalculationData retCalculationData;
        if (useCardData.getUseLimitFlag()) {
            //计算金额
            retCalculationData = userUsersService.calculationAmount(settlementCard.getCardNoList(), settlementCard.getAmount(), userId);
            settlementMoneyData.setMessage(useCardData.getUseMessage());
            settlementMoneyData.setMsgFlag(true);
            settlementMoneyData.setCardDiscountMoney(retCalculationData.getCardDiscountMoney());
            settlementMoneyData.setUserAccount(retCalculationData.getOriUserMoneyInt());
        } else {
            //计算金额
            retCalculationData = userUsersService.calculationAmount(new ArrayList<>(), settlementCard.getAmount(), userId);
            settlementMoneyData.setMessage(useCardData.getUseMessage());
            settlementMoneyData.setCardDiscountMoney(0);
            settlementMoneyData.setMsgFlag(false);
            settlementMoneyData.setUserAccount(retCalculationData.getOriUserMoneyInt());
        }
        Integer amount = retCalculationData.getAmount();
        // 计算,封装电子卡余额
        if (cardPhysical!=null) {
            RetCardElectronicCalculate retCardElectronicCalculate = msPrimeService.calculateCardElectronicMoney(cardPhysical,amount);
            settlementMoneyData.setElectronicCardAccount(retCardElectronicCalculate.getOriCardAmount());
            settlementMoneyData.setAfterElectronicCardAccount(retCardElectronicCalculate.getAfterCardAmount());
            amount = retCardElectronicCalculate.getPayAmount();
        }else {
            settlementMoneyData.setElectronicCardAccount(0);
            settlementMoneyData.setAfterElectronicCardAccount(0);
        }
        settlementMoneyData.setAmount(amount);

        if (amount > 0) {
            settlementMoneyData.setIsToPay(true);
        } else {
            settlementMoneyData.setIsToPay(false);
        }
        //封装账户余额返回
//        BigDecimal userMoneyBD = cardMapUserClientService.queryUserMoney(userId).getData();
//        settlementMoneyData.setUserAccount(Integer.parseInt(userMoneyBD.toString()));
        //封装 计算后的余额返回
        Integer afterUserAccount = cardMapUserClientService.afterUserAccount(settlementCard.getCardNoList(), retCalculationData.getOriUserMoneyInt(), settlementCard.getAmount(), true).getData();
        settlementMoneyData.setAfterUserAccount(afterUserAccount);
        return settlementMoneyData;
    }

    /**
     * 获取电子实体卡绑定的电话对应的userId
     * @param cardElectronic
     * @param merchantCode
     * @return
     */
    public Long getCardElectronicUserId(CardElectronic cardElectronic,String merchantCode){
        if (!StringUtils.isEmpty(cardElectronic.getUserPhone())) {
            MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByTelChangeObjectCode(cardElectronic.getUserPhone(), merchantCode).getData();
            if (mrcMapMerchantPrimes!=null){
                return mrcMapMerchantPrimes.getUserId();
            }
        }
        return null;
    }

    /**
     * 校验订单金额 是否支付完全
     * @param cardOrdersVO
     * @param amount
     * @return
     */
    public boolean checkOrderPaidMoney(CardOrdersVO cardOrdersVO, Integer amount) {

        //订单过期支付限制
        Date orderCreateAt = cardOrdersVO.getCreateAt();
        long nowDate = new Date().getTime();
        long orderTime = orderCreateAt.getTime() + 50 * 1000;   //限制订单支付到期时间
        int diffSeconds = (int) ((orderTime - nowDate) / 1000);
        if (diffSeconds <5 ){
            throw new CheckException(ResultTypeEnum.CARD_ORDER_TIME_ERROR);
        }



        if (CardOrdersStateConfig.UNPAID.equals(cardOrdersVO.getState())){
            return true;
        }
        Integer orderAmount = cardOrdersVO.getAmount();
        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceClientService.queryPayTrace(cardOrdersVO.getOrderCode()).getData();
        int totalTraceAmount = 0;
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            if (CardOrdersStateConfig.PAID.equals(cardOrderPayTrace.getState())){
                totalTraceAmount = totalTraceAmount + cardOrderPayTrace.getAmount();
            }
        }

        if (totalTraceAmount >= orderAmount){
            return false;
        }
        return true;
    }


    /**
     * 订单号对应的卡号 是否为预付费售卖卡 卡种
     * @param orderCode
     * @return
     */
    public boolean checkOrderCodeIsPrePayCardType(String orderCode){
        boolean isPreTypeCard = false;//是否预付费售卖卡 卡种
        String value = CardElectronicEnum.OFFLINE_PREPAY_SELL.getValue();
        try {
            Result<List<CardOrderDetails>> listResult = orderClientService.queryListByOrderCode(orderCode);
            List<CardOrderDetails> data = listResult.getData();
            for(CardOrderDetails cardOrderDetails : data){
                String cardType = cardOrderDetails.getCardType();
                if(value.equals(cardType)){
                    isPreTypeCard = true;
                    break;
                }
            }
        }catch (Exception e){
            log.error("checkOrderCodeCardTypeIsPrePayType error={}",e);
        }
        return isPreTypeCard;
    }


}
