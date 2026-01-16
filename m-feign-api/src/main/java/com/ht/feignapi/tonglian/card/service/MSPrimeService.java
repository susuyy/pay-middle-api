package com.ht.feignapi.tonglian.card.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.inject.internal.cglib.transform.$ClassTransformer;
import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceStateConfig;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceTypeConfig;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MSPrimeService {

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    @Autowired
    private CardOrdersService cardOrdersService;

    /**
     * 获取
     *
     * @param icCardId
     * @return
     */
    public CardPhysical checkGetCardElectronic(String icCardId) {
        CardPhysical cardPhysical = msPrimeClient.queryByIcCardId(icCardId).getData();
        return cardPhysical;
    }

    /**
     * 电子卡直接核销扣钱
     *
     * @param cardPhysical
     * @param amount
     * @param orderCode
     * @param userId
     * @param terId
     * @return
     */
    public RetSettlement settlementCardElectronic(CardPhysical cardPhysical, Integer amount, String orderCode, Long userId, String terId) {
        RetSettlement retSettlement = new RetSettlement();
        retSettlement.setUserId(cardPhysical.getUserId());
        int faceValue = Integer.parseInt(cardPhysical.getFaceValue());
        int deductAmount = faceValue - amount;
        SettlementMoneyData settlementMoneyData = new SettlementMoneyData();
        int consumeMoney = 0;
        if (deductAmount >= 0) {
            consumeMoney = amount;
            settlementMoneyData.setAmount(0);
            settlementMoneyData.setIsToPay(false);
            settlementMoneyData.setAfterElectronicCardAccount(deductAmount);
        } else {
            consumeMoney = faceValue;
            settlementMoneyData.setAmount(deductAmount * -1);
            settlementMoneyData.setIsToPay(true);
            settlementMoneyData.setAfterElectronicCardAccount(0);
        }
//        if (deductAmount >= 0) {
//            consumeMoney = amount;
//            settlementMoneyData.setAmount(0);
//            settlementMoneyData.setIsToPay(false);
//            settlementMoneyData.setAfterElectronicCardAccount(deductAmount);
//        } else {
////            consumeMoney = faceValue;
////            settlementMoneyData.setAmount(deductAmount * -1);
////            settlementMoneyData.setIsToPay(true);
////            settlementMoneyData.setAfterElectronicCardAccount(0);
//            throw new CheckException(ResultTypeEnum.CARD_MONEY_ERROR.getCode(),"卡余额:"+new BigDecimal(faceValue).divide(new BigDecimal(100),2) +",不足支付,请重新推单");
//        }
        //金额0校验
        if (faceValue > 0) {
            //创建流水
            if (userId == null) {
                userId = -1L;
            }

            //测试用
            if (StringUtils.isEmpty(terId)) {
                terId = "TEST-HT000090-TEST";
            }

//            cardOrderPayTraceClientService.createCardElectronicPayTrace(consumeMoney, orderCode,
//                    cardPhysical.getCardCode(),
//                    userId,
//                    terId);
            //扣除实体卡金额
            CardPhysical data = msPrimeClient.updateCardPhysicalMoney(consumeMoney, cardPhysical).getData();

            cardOrderPayTraceClientService.createCardPhysicalPayTrace(consumeMoney, orderCode,
                    cardPhysical.getCardCode(),
                    userId,
                    terId,
                    data);
        }
        settlementMoneyData.setUserAccount(0);
        settlementMoneyData.setAfterUserAccount(0);
        settlementMoneyData.setElectronicCardAccount(faceValue);
        settlementMoneyData.setCardDiscountMoney(0);
        settlementMoneyData.setOrderCode(orderCode);
        retSettlement.setSettlementMoneyData(settlementMoneyData);
        return retSettlement;
    }

    /**
     * 计算 卡消费信息 ,返回 原始卡余额,支付后卡余额,剩余支付金额
     *
     * @param cardPhysical
     * @param consumeAmount
     * @return
     */
    public RetCardElectronicCalculate calculateCardElectronicMoney(CardPhysical cardPhysical, Integer consumeAmount) {
        RetCardElectronicCalculate retCardElectronicCalculate = new RetCardElectronicCalculate();
        int cardEleMoney = Integer.parseInt(cardPhysical.getFaceValue());
        retCardElectronicCalculate.setOriCardAmount(cardEleMoney);
        if (consumeAmount >= cardEleMoney) {
            retCardElectronicCalculate.setPayAmount(consumeAmount - cardEleMoney);
            retCardElectronicCalculate.setAfterCardAmount(0);
        } else {
            Integer afterElectronicCard = cardEleMoney - consumeAmount;
            retCardElectronicCalculate.setAfterCardAmount(afterElectronicCard);
            retCardElectronicCalculate.setPayAmount(0);
        }
        return retCardElectronicCalculate;
    }

    /**
     * 海旅富基账户总额支付
     *
     * @param userFlagCode
     * @param amount
     * @return
     */
    public RetPosCombinationPayEleCard posCombinationPayEleCard(String userFlagCode, Integer amount) {
        RetPosCombinationPayEleCard retPosCombinationPayEleCard = msPrimeClient.posCombinationPayEleCard(userFlagCode, amount).getData();

        if (!retPosCombinationPayEleCard.isCheckUserFlag()) {
            throw new CheckException(ResultTypeEnum.QR_AUTH_CODE_ERROR);
        }

        if (!retPosCombinationPayEleCard.isSingleCardValidFlag()) {
            throw new CheckException(ResultTypeEnum.CARD_USE_VALID);
        }

        return retPosCombinationPayEleCard;
    }

    /**
     * 海旅 电子卡支付
     *
     * @param userFlagCode
     * @param amount
     * @return
     */
    public RetPosCombinationPayEleCard handlerPosCombinationPayEleCard(String userFlagCode, Integer amount) {
        RetPosCombinationPayEleCard retPosCombinationPayEleCard = msPrimeClient.posCombinationPayEleCard(userFlagCode, amount).getData();

        if (!retPosCombinationPayEleCard.isCheckUserFlag()) {
            throw new CheckException(ResultTypeEnum.AUTH_VALID_CODE_ERROR);
        }

        if (!retPosCombinationPayEleCard.isSingleCardValidFlag()) {
            throw new CheckException(ResultTypeEnum.CARD_USE_VALID);
        }

        return retPosCombinationPayEleCard;
    }

    public RetPosCombinationPayEleCard consumeEleCardMoney(Integer paidAmount, String userFlagCode) {
        return msPrimeClient.consumeEleCardMoney(paidAmount, userFlagCode).getData();
    }

    /**
     * 退款电子卡消费订单
     *
     * @param cardOrderPayTraceList
     * @param operator
     * @return
     */
    public List<CardOrderPayTrace> posCombinationRefund(List<CardOrderPayTrace> cardOrderPayTraceList,
                                                        String operator,
                                                        String refundCode,
                                                        int refundAmount,
                                                        String payTraceNo) {
        List<CardOrderPayTrace> requestRefundCardOrderPayTraceList = new ArrayList<>();
        List<CardOrderPayTrace> refundCardOrderPayTraceList = new ArrayList<>();

        if (!StringUtils.isEmpty(payTraceNo)) {
            CardOrderPayTrace doCardOrderPayTrace = null;
            for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
                if (cardOrderPayTrace.getTraceNo().equals(payTraceNo)) {
                    doCardOrderPayTrace = cardOrderPayTrace;
                }
            }
            doRefundOnPayTrace(doCardOrderPayTrace, operator, refundCode);
            refundCardOrderPayTraceList.add(doCardOrderPayTrace);
        } else if (refundAmount > 0) {
            int totalPayMoney = 0;
            for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
                if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                        && (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType()))) {
                    totalPayMoney = totalPayMoney + cardOrderPayTrace.getAmount();
                }
            }
            if (totalPayMoney < refundAmount) {
                throw new CheckException(ResultTypeEnum.REFUND_MONEY_ERROR);
            }
            for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
                if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                        && (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType()))) {
                    if (cardOrderPayTrace.getAmount() >= refundAmount) {
                        CardOrderPayTrace cardOrderPayTraceRefund = new CardOrderPayTrace();
                        BeanUtils.copyProperties(cardOrderPayTrace, cardOrderPayTraceRefund);
                        cardOrderPayTraceRefund.setAmount(refundAmount);
                        requestRefundCardOrderPayTraceList.add(cardOrderPayTraceRefund);
                        refundCardOrderPayTraceList.add(cardOrderPayTrace);
                        break;
                    } else {
                        requestRefundCardOrderPayTraceList.add(cardOrderPayTrace);
                        refundCardOrderPayTraceList.add(cardOrderPayTrace);
                        refundAmount = refundAmount - cardOrderPayTrace.getAmount();
                    }
                }
            }
            msPrimeClient.posCombinationRefund(requestRefundCardOrderPayTraceList, operator, refundCode);
        } else {
            for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
                if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                        && (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType()))) {
                    requestRefundCardOrderPayTraceList.add(cardOrderPayTrace);
                    refundCardOrderPayTraceList.add(cardOrderPayTrace);
                }
            }
            msPrimeClient.posCombinationRefund(requestRefundCardOrderPayTraceList, operator, refundCode);
        }
        return refundCardOrderPayTraceList;
    }

    /**
     * 处理单条流水退款
     *
     * @param doCardOrderPayTrace
     * @param operator
     * @param refundCode
     */
    public void doRefundOnPayTrace(CardOrderPayTrace doCardOrderPayTrace, String operator, String refundCode) {
        if (doCardOrderPayTrace == null || !doCardOrderPayTrace.getState().equals(CardOrderPayTraceStateConfig.PAID)) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ERROR);
        }
        List<CardOrderPayTrace> refundCardOrderPayTraceList = new ArrayList<>();
        if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(doCardOrderPayTrace.getType())
                || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(doCardOrderPayTrace.getType())) {
            refundCardOrderPayTraceList.add(doCardOrderPayTrace);
            msPrimeClient.posCombinationRefund(refundCardOrderPayTraceList, operator, refundCode);
        }
    }

    /**
     * 查询退款订单
     *
     * @param orderCode
     * @param merchantCode
     * @param refundCode
     * @return
     */
    public List<CardRefundOrder> queryRefundOrder(String orderCode, String merchantCode, String refundCode) {
        return msPrimeClient.queryRefundOrder(orderCode, merchantCode, refundCode).getData();
    }

    /**
     * 账户总额计算 pos返回
     *
     * @param vipUser
     * @param consumeAmount
     * @return
     */
    public RetCardElectronicPosCashier userAllCardElectronicPosCashier(VipUser vipUser, Integer consumeAmount) {
        RetCardElectronicPosCashier retCardElectronicPosCashier = new RetCardElectronicPosCashier();
        retCardElectronicPosCashier.setUserId(vipUser.getId());

        PosCashierData posCashierData = new PosCashierData();

        Integer totalCardMoney = msPrimeClient.getUserAllCardMoney(vipUser.getOpenid()).getData();
        if (totalCardMoney == null) {
            totalCardMoney = 0;
        }

        int amount = consumeAmount - totalCardMoney;
        if (amount > 0) {
            posCashierData.setAmount(amount);
        } else {
            posCashierData.setAmount(0);
        }
        posCashierData.setPosUserCardVOS(new ArrayList<PosUserCardVO>());
        posCashierData.setUserAccount(0);
        posCashierData.setAfterUserAccount(0);
        posCashierData.setElectronicCardAccount(totalCardMoney);
        int afterCardMoney = totalCardMoney - consumeAmount;
        if (afterCardMoney <= 0) {
            posCashierData.setAfterElectronicCardAccount(0);
        } else {
            posCashierData.setAfterElectronicCardAccount(afterCardMoney);
        }
        posCashierData.setCardDiscountMoney(0);

        retCardElectronicPosCashier.setPosCashierData(posCashierData);
        return retCardElectronicPosCashier;
    }

    /**
     * pos 扫码支付核销
     *
     * @param authOpenid
     * @param vipUser
     * @param amount
     * @param orderCode
     * @param merchantCode
     * @return
     */
    public RetAccountPayData settlementUserCodeCardMoneyEnd(String authOpenid, VipUser vipUser, Integer amount, String orderCode, String merchantCode) {
        RetPosCombinationPayEleCard retPosCombinationPayEleCard = posCombinationPayEleCard(authOpenid, amount);
        if (retPosCombinationPayEleCard.getPaidAmount() > 0) {
            List<CardOrderPayTrace> data = cardOrderPayTraceClientService.queryPayTrace(orderCode).getData();
            PosCombinationPay posCombinationPay = new PosCombinationPay();
            posCombinationPay.setOrderCode(orderCode);
            posCombinationPay.setAmount(amount);
            posCombinationPay.setMerchantCode(merchantCode);
            posCombinationPay.setUserFlagCode(vipUser.getOpenid());
            if (data != null && data.size() > 0) {
                posCombinationPay.setPayCode(data.get(0).getPayCode());
                cardOrdersService.posCombinationPay(posCombinationPay, retPosCombinationPayEleCard, CardOrderPayTraceStateConfig.PAID, data.get(0).getPosSerialNum());
            } else {
                posCombinationPay.setPayCode(IdWorker.getIdStr());
                cardOrdersService.posCombinationPay(posCombinationPay, retPosCombinationPayEleCard, CardOrderPayTraceStateConfig.PAID);
            }
        }
        RetAccountPayData retAccountPayData = new RetAccountPayData();
        retAccountPayData.setOrderCode(orderCode);
        retAccountPayData.setAmount(retPosCombinationPayEleCard.getNeedPaidAmount());
        return retAccountPayData;
    }

    /**
     * pos 手机核销卡
     *
     * @param vipUser
     * @param amount
     * @param orderCode
     * @param merchantCode
     * @return
     */
    public RetAccountPayData settlementUserTelCardMoneyEnd(VipUser vipUser, Integer amount, String orderCode, String merchantCode) {
        RetPosCombinationPayEleCard retPosCombinationPayEleCard = consumeEleCardMoney(amount, vipUser.getOpenid());
        if (retPosCombinationPayEleCard.getPaidAmount() > 0) {
            List<CardOrderPayTrace> data = cardOrderPayTraceClientService.queryPayTrace(orderCode).getData();
            PosCombinationPay posCombinationPay = new PosCombinationPay();
            posCombinationPay.setOrderCode(orderCode);
            posCombinationPay.setAmount(amount);
            posCombinationPay.setMerchantCode(merchantCode);
            posCombinationPay.setUserFlagCode(vipUser.getOpenid());
            if (data != null && data.size() > 0) {
                posCombinationPay.setPayCode(data.get(0).getPayCode());
                cardOrdersService.posCombinationPay(posCombinationPay, retPosCombinationPayEleCard, CardOrderPayTraceStateConfig.PAID, data.get(0).getPosSerialNum());
            } else {
                posCombinationPay.setPayCode(IdWorker.getIdStr());
                cardOrdersService.posCombinationPay(posCombinationPay, retPosCombinationPayEleCard, CardOrderPayTraceStateConfig.PAID);
            }
        }
        RetAccountPayData retAccountPayData = new RetAccountPayData();
        retAccountPayData.setOrderCode(orderCode);
        retAccountPayData.setAmount(retPosCombinationPayEleCard.getNeedPaidAmount());
        return retAccountPayData;
    }

    public List<CardOrderPayTrace> posCombinationCancel(List<CardOrderPayTrace> cardOrderPayTraceList, String operator, String cancelCode) {
        List<CardOrderPayTrace> requestRefundCardOrderPayTraceList = new ArrayList<>();
        List<CardOrderPayTrace> refundCardOrderPayTraceList = new ArrayList<>();
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                    && (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                    || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType()))) {
                requestRefundCardOrderPayTraceList.add(cardOrderPayTrace);
                cardOrderPayTrace.setRefundAmount(cardOrderPayTrace.getAmount());
                refundCardOrderPayTraceList.add(cardOrderPayTrace);
            }
        }
        msPrimeClient.posCombinationRefund(requestRefundCardOrderPayTraceList, operator, cancelCode);
        return refundCardOrderPayTraceList;
    }

    /**
     * 退款电子卡消费订单
     *
     * @param cardOrderPayTraceList
     * @param operator
     * @return
     */
    public List<CardOrderPayTrace> posCombinationRefundMatching(List<CardOrderPayTrace> cardOrderPayTraceList,
                                                        String operator,
                                                        String refundCode,
                                                        int refundAmount,
                                                        String payTraceNo) {
        List<CardOrderPayTrace> requestRefundCardOrderPayTraceList = new ArrayList<>();
        List<CardOrderPayTrace> refundCardOrderPayTraceList = new ArrayList<>();

        if (!StringUtils.isEmpty(payTraceNo)) {
            CardOrderPayTrace doCardOrderPayTrace = null;
            for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
                if (cardOrderPayTrace.getTraceNo().equals(payTraceNo)) {
                    cardOrderPayTrace.setRefundAmount(cardOrderPayTrace.getAmount());
                    doCardOrderPayTrace = cardOrderPayTrace;
                }
            }
            doRefundOnPayTrace(doCardOrderPayTrace, operator, refundCode);
            refundCardOrderPayTraceList.add(doCardOrderPayTrace);
        } else if (refundAmount > 0) {
            consumeRefundPart(cardOrderPayTraceList,refundAmount,operator,refundCode,requestRefundCardOrderPayTraceList,refundCardOrderPayTraceList);
        } else {
            consumeRefundAll(cardOrderPayTraceList,refundAmount,operator,refundCode,requestRefundCardOrderPayTraceList,refundCardOrderPayTraceList);
        }
        return refundCardOrderPayTraceList;
    }

    private void consumeRefundPart(List<CardOrderPayTrace> cardOrderPayTraceList,int refundAmount,String operator, String refundCode,
                               List<CardOrderPayTrace> requestRefundCardOrderPayTraceList,List<CardOrderPayTrace> refundCardOrderPayTraceList){

        List<CardRefundOrder> cardRefundOrders = msPrimeClient.queryRefundOrder(cardOrderPayTraceList.get(0).getOrderCode(), "HLSC", "").getData();

        //已退款金额
        int totalRefundAmount = 0;
        for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
            totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
        }

        //已支付金额
        int totalPayMoney = 0;
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.REFUND.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.CANCEL.equals(cardOrderPayTrace.getState())){

                if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){
                    totalPayMoney = totalPayMoney + cardOrderPayTrace.getAmount();
                }
            }
        }

        if (totalPayMoney < (refundAmount+totalRefundAmount)) {
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ERROR);
        }

        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.REFUND.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.CANCEL.equals(cardOrderPayTrace.getState())){

                if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){

                    int oneRefundAmount = 0;
                    for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
                        if (cardRefundOrder.getTransNo().equals(cardOrderPayTrace.getTraceNo())){
                            oneRefundAmount = oneRefundAmount + cardRefundOrder.getAmount();
                        }
                    }

                    if (refundAmount>0) {
                        if (oneRefundAmount < cardOrderPayTrace.getAmount()) {
                            int oneCanRefundAmount = cardOrderPayTrace.getAmount() - oneRefundAmount;
                            if (oneCanRefundAmount >= refundAmount) {
                                CardOrderPayTrace cardOrderPayTraceRefund = new CardOrderPayTrace();
                                BeanUtils.copyProperties(cardOrderPayTrace, cardOrderPayTraceRefund);
                                cardOrderPayTraceRefund.setAmount(refundAmount);

                                Integer oriRefundAmount = cardOrderPayTrace.getRefundAmount();
                                oriRefundAmount = oriRefundAmount == null? 0:oriRefundAmount;
                                cardOrderPayTrace.setRefundAmount(oriRefundAmount+refundAmount);

                                requestRefundCardOrderPayTraceList.add(cardOrderPayTraceRefund);
                                refundCardOrderPayTraceList.add(cardOrderPayTrace);
                                break;
                            } else {
                                CardOrderPayTrace cardOrderPayTraceRefund = new CardOrderPayTrace();
                                BeanUtils.copyProperties(cardOrderPayTrace, cardOrderPayTraceRefund);
                                cardOrderPayTraceRefund.setAmount(oneCanRefundAmount);

                                Integer oriRefundAmount = cardOrderPayTrace.getRefundAmount();
                                oriRefundAmount = oriRefundAmount == null? 0:oriRefundAmount;
                                cardOrderPayTrace.setRefundAmount(oriRefundAmount+oneCanRefundAmount);

                                requestRefundCardOrderPayTraceList.add(cardOrderPayTraceRefund);
                                refundCardOrderPayTraceList.add(cardOrderPayTrace);
                                refundAmount = refundAmount - oneCanRefundAmount;
                            }
                        }
                    }
                }
            }
        }
        msPrimeClient.posCombinationRefund(requestRefundCardOrderPayTraceList, operator, refundCode);
    }

    private void consumeRefundAll(List<CardOrderPayTrace> cardOrderPayTraceList,int refundAmount,String operator, String refundCode,
                                   List<CardOrderPayTrace> requestRefundCardOrderPayTraceList,List<CardOrderPayTrace> refundCardOrderPayTraceList){

        List<CardRefundOrder> cardRefundOrders = msPrimeClient.queryRefundOrder(cardOrderPayTraceList.get(0).getOrderCode(), "HLSC", "").getData();

        //已退款金额
        int totalRefundAmount = 0;
        for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
            totalRefundAmount = totalRefundAmount + cardRefundOrder.getAmount();
        }

        //已支付金额
        int totalPayMoney = 0;
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.REFUND.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.CANCEL.equals(cardOrderPayTrace.getState())){

                if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){
                    totalPayMoney = totalPayMoney + cardOrderPayTrace.getAmount();
                }
            }
        }

        refundAmount = totalPayMoney - totalRefundAmount;
        if (refundAmount<=0){
            throw new CheckException(ResultTypeEnum.REFUND_MONEY_ZERO_ERROR);
        }

        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceStateConfig.PAID.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.REFUND.equals(cardOrderPayTrace.getState())
                    ||CardOrderPayTraceStateConfig.CANCEL.equals(cardOrderPayTrace.getState())){

                if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())
                        || CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){

                    int oneRefundAmount = 0;
                    for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
                        if (cardRefundOrder.getTransNo().equals(cardOrderPayTrace.getTraceNo())){
                            oneRefundAmount = oneRefundAmount + cardRefundOrder.getAmount();
                        }
                    }

                    if (refundAmount>0) {
                        if (oneRefundAmount < cardOrderPayTrace.getAmount()) {
                            int oneCanRefundAmount = cardOrderPayTrace.getAmount() - oneRefundAmount;
                            if (oneCanRefundAmount >= refundAmount) {
                                CardOrderPayTrace cardOrderPayTraceRefund = new CardOrderPayTrace();
                                BeanUtils.copyProperties(cardOrderPayTrace, cardOrderPayTraceRefund);
                                cardOrderPayTraceRefund.setAmount(refundAmount);

                                Integer oriRefundAmount = cardOrderPayTrace.getRefundAmount();
                                oriRefundAmount = oriRefundAmount == null? 0:oriRefundAmount;
                                cardOrderPayTrace.setRefundAmount(oriRefundAmount+refundAmount);

                                requestRefundCardOrderPayTraceList.add(cardOrderPayTraceRefund);
                                refundCardOrderPayTraceList.add(cardOrderPayTrace);
                                break;
                            } else {
                                CardOrderPayTrace cardOrderPayTraceRefund = new CardOrderPayTrace();
                                BeanUtils.copyProperties(cardOrderPayTrace, cardOrderPayTraceRefund);
                                cardOrderPayTraceRefund.setAmount(oneCanRefundAmount);
                                Integer oriRefundAmount = cardOrderPayTrace.getRefundAmount();
                                oriRefundAmount = oriRefundAmount == null? 0:oriRefundAmount;
                                cardOrderPayTrace.setRefundAmount(oriRefundAmount+oneCanRefundAmount);
                                requestRefundCardOrderPayTraceList.add(cardOrderPayTraceRefund);
                                refundCardOrderPayTraceList.add(cardOrderPayTrace);
                                refundAmount = refundAmount - oneCanRefundAmount;
                            }
                        }
                    }
                }
            }
        }
        msPrimeClient.posCombinationRefund(requestRefundCardOrderPayTraceList, operator, refundCode);
    }

    public void refreshRemainFaceValue(List<CardOrderPayTrace> refundCardOrderPayTraces) {
        for (CardOrderPayTrace refundCardOrderPayTrace : refundCardOrderPayTraces) {
            if (CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(refundCardOrderPayTrace.getType())){
                CardPhysical cardPhysical = msPrimeClient.queryByCardCode(refundCardOrderPayTrace.getSourceId()).getData();
                if (cardPhysical!=null) {
                    refundCardOrderPayTrace.setRefRemainFaceValue(cardPhysical.getFaceValue());
                }
            }else if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(refundCardOrderPayTrace.getType())){
                CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(refundCardOrderPayTrace.getSourceId()).getData();
                if (cardElectronic!=null) {
                    refundCardOrderPayTrace.setRefRemainFaceValue(cardElectronic.getFaceValue());
                }else {
                    PartyCardElectronic partyCardElectronic = msPrimeClient.queryPartyCardElectronicByCardNo(refundCardOrderPayTrace.getSourceId()).getData();
                    if (partyCardElectronic!=null) {
                        refundCardOrderPayTrace.setRefRemainFaceValue(partyCardElectronic.getFaceValue());
                    }
                }
            }
        }
    }
}
