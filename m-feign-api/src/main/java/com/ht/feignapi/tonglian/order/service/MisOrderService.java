package com.ht.feignapi.tonglian.order.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.order.client.MNSAliyunClient;
import com.ht.feignapi.tonglian.order.entity.*;
import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import com.ht.feignapi.tonglian.user.entity.RetCalculationData;
import com.ht.feignapi.util.DateStrUtil;
import com.ht.feignapi.util.PosMD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.TreeMap;

@Service
public class MisOrderService {

    @Autowired
    private MNSAliyunClient mnsAliyunClient;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    /**
     * 封装响应体
     * @param misOrder
     * @param custData
     * @return
     */
    public RspData packageRspData(MisOrder misOrder, CustData custData) {
        RspData rspData = new RspData();
        rspData.setCARDNO("");
        rspData.setREJCODE_CN("交易成功");
        rspData.setMERCH_ID(misOrder.getSTORE_ID());
        rspData.setTRANS_CHANNEL("006");
        String batchNo = IdWorker.getIdStr();
        rspData.setBATCH_NO(batchNo);
        rspData.setTIME(DateStrUtil.nowDateStrHHmmSS());
        rspData.setREJCODE("00");
        rspData.setSIGN(misOrder.getSIGN_DATA());
        rspData.setDATE(DateStrUtil.nowDateStrMMdd());
        rspData.setTER_ID("POS-" + misOrder.getCASH_ID());
        rspData.setORDER_NO(custData.getORDER_NO());
        rspData.setCARD_FEE("000000000000");
        rspData.setAMOUNT(custData.getAMOUNT());
        rspData.setISS_NO("");
        String refNo = IdWorker.getIdStr();
        rspData.setREF_NO(refNo);
        rspData.setTRACE_NO("000800");
        rspData.setPRINT_FLAG("0");
        rspData.setCARDTYPE("001");
        rspData.setBUSINESS_ID(misOrder.getBUSINESS_ID());
        rspData.setTRANS_TICKET_NO(custData.getORDER_NO());
        rspData.setAUTH_NO("932870");
        rspData.setMERCH_NAME("海旅");
        rspData.setEXP_DATE("2907");
        rspData.setCARD_TYPE_IDENTY("1");
        rspData.setISS_NAME("");
        rspData.setUSDK_VERSION_CODE("S-LANDIA8-I00V5.0200108");
        rspData.setOPER_NO("01|操作员1");
        rspData.setWILD_CARD_SIGN("0");
        rspData.setTRANSTYPE("005");
        rspData.setSIGN_FLAG("0");
        return rspData;
    }

    /**
     * 发送misOrderD订单消息至消息队列
     * @param queueName
     * @param misOrder
     */
    public boolean sendMisOrderMessage(String queueName, MisOrder misOrder) {
        MessageData messageData = new MessageData();
        messageData.setMessageBody(JSONObject.toJSONString(misOrder));
        messageData.setQueueName(queueName);
        return mnsAliyunClient.sendMessage(messageData).getData();
    }

    /**
     * 发送misOrderD订单消息至消息队列
     * @param queueName
     * @param misOrderData
     */
    public boolean sendMisOrderMessage(String queueName, MisOrderData misOrderData) {
        MessageData messageData = new MessageData();
        messageData.setMessageBody(JSONObject.toJSONString(misOrderData));
        messageData.setQueueName(queueName);
        return mnsAliyunClient.sendMessage(messageData).getData();
    }

    /**
     * 拉取云mis订单数据
     * @param cashId
     * @return
     */
    public MisOrderData pullMisOrder(String cashId) {
        String messageJsonData = mnsAliyunClient.consumerMessage("POS-" + cashId).getData();
        if (StringUtils.isEmpty(messageJsonData)){
            return null;
        }
        MisOrderData misOrderData = JSONObject.parseObject(messageJsonData, MisOrderData.class);
        return misOrderData;
    }


    /**
     * 计算用户 需支付的金额  不直接结算
     *
     * @param cardNoList
     * @param amount
     * @param userId
     * @return
     */
    public RetCalculationData calculationAmount(List<PosSelectCardNo> cardNoList, Integer amount, Long userId) {
        Integer cardCouponMoney = 0;
        RetCalculationData retCalculationData = new RetCalculationData();
        if (cardNoList != null && cardNoList.size() > 0) {
            for (PosSelectCardNo posSelectCardNO : cardNoList) {
                CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(posSelectCardNO.getCardNo()).getData();
                if (cardMapUserCards != null) {
                    if ("discount".equals(cardMapUserCards.getCardType())) {
                        cardCouponMoney = discountTypeMoney(cardMapUserCards.getFaceValue(), amount);
                        retCalculationData.setCardDiscountMoney(cardCouponMoney);
                    } else {
                        int parseIntCardValue = Integer.parseInt(cardMapUserCards.getFaceValue());
                        cardCouponMoney = cardCouponMoney + parseIntCardValue;
                        retCalculationData.setCardDiscountMoney(cardCouponMoney);
                    }
                } else {
                    retCalculationData.setCardDiscountMoney(cardCouponMoney);
                }
            }
        } else {
            retCalculationData.setCardDiscountMoney(0);
        }
        Integer needPayMoney = amount - cardCouponMoney;
        retCalculationData.setUserId(userId);
        BigDecimal userMoney = cardMapUserClientService.queryUserMoney(userId).getData();
        int userMoneyInt = Integer.parseInt(userMoney.toString());
        if (userMoneyInt - needPayMoney > 0) {
            retCalculationData.setAmount(0);
            return retCalculationData;
        } else {
            retCalculationData.setAmount((userMoneyInt - needPayMoney) * -1);
            return retCalculationData;
        }
    }

    /**
     * 折扣券 计算 折扣金额
     *
     * @param cardFaceValue
     * @param amount
     * @return
     */
    public Integer discountTypeMoney(String cardFaceValue, Integer amount) {
        int discount = Integer.parseInt(cardFaceValue);
        Double d = (100 - discount) * 0.01;
        Double v = amount * d;
        Integer cardCouponMoney = v.intValue();
        return cardCouponMoney;
    }

    public boolean checkMD5Sign(MisOrder misOrder) throws Exception {
        System.out.println(misOrder);
        String appId = misOrder.getAPP_ID();
        String businessId = misOrder.getBUSINESS_ID();
        String cashId = misOrder.getCASH_ID();
        String custDataStr = misOrder.getCUST_DATA();
        String signData = misOrder.getSIGN_DATA();
        String storeId = misOrder.getSTORE_ID();
        TreeMap<String,String> param = new TreeMap<>();
        param.put("APP_ID",appId);
        param.put("BUSINESS_ID",businessId);
        param.put("CASH_ID",cashId);
        param.put("CUST_DATA",custDataStr);
        param.put("STORE_ID",storeId);
        param.put("APP_PACKAGE_NM",misOrder.getAPP_PACKAGE_NM());
        param.put("APP_CLASS_NM",misOrder.getAPP_CLASS_NM());
        return PosMD5.validSign(signData, param);
    }

    public String createResponseMD5Sign(ReturnMisOrder returnMisOrder) throws Exception {
        TreeMap<String,String> treeMap = new TreeMap<>();
        treeMap.put("APP_ID", returnMisOrder.getAPP_ID());
        treeMap.put("BUSINESS_ID", returnMisOrder.getBUSINESS_ID());
        treeMap.put("RSP_CODE", returnMisOrder.getRSP_CODE());
        treeMap.put("RSP_DATA", returnMisOrder.getRSP_DATA());
        treeMap.put("RSP_DESC", returnMisOrder.getRSP_DESC());
        treeMap.put("ClOUD_MIS_TRX_SSN", returnMisOrder.getClOUD_MIS_TRX_SSN());
        String md5 = PosMD5.unionSign(treeMap);
        return md5;
    }
}
