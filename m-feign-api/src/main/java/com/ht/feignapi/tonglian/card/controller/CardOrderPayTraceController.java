package com.ht.feignapi.tonglian.card.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.SearchData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.result.UserDefinedException;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.card.service.CardOrdersService;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.card.service.MSPrimeService;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceStateConfig;
import com.ht.feignapi.tonglian.config.CardOrderPayTraceTypeConfig;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.merchant.service.MerchantPrimeService;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.utils.Cache;
import com.ht.feignapi.tonglian.utils.CacheManager;
import com.ht.feignapi.tonglian.utils.RequestQrCodeDataStrUtil;
import com.ht.feignapi.util.DESUtil;
import com.ht.feignapi.util.UserCodeAuthUtil;
import com.ht.feignapi.util.UserCodePreSubUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单支付流水 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@RestController
@RequestMapping("/tonglian/orderPayTrace")
@CrossOrigin(allowCredentials = "true")
public class CardOrderPayTraceController {

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private MerchantPrimeService merchantPrimeService;

    @Autowired
    private CardOrdersService cardOrdersService;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private DESUtil desUtil;

    @Autowired
    private MSPrimeService msPrimeService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    private final static Logger logger = LoggerFactory.getLogger(CardOrderPayTraceController.class);

    /**
     * pos端收银  返回核算过后的金额 包含默认选用卡券 和 用户卡券列表
     *
     * @param settlement
     * @return
     */
    @PostMapping("/settlement")
    public PosCashierData settlement(@RequestBody Settlement settlement) {
        //新开卡类型的支付
        CardPhysical cardPhysical = null;
        Long userId;
        if (!StringUtils.isEmpty(settlement.getIcCardId())){
            cardPhysical = msPrimeService.checkGetCardElectronic(settlement.getIcCardId());
        }
        if (cardPhysical!=null){
            RetCardElectronicPosCashier retCardElectronicPosCashier = cardOrdersService.cardElectronicPosCashier(cardPhysical,settlement.getAmount(),settlement.getMerchantCode());
            userId = retCardElectronicPosCashier.getUserId();
            if (userId==null) {
                return retCardElectronicPosCashier.getPosCashierData();
            }
        }else {
            userId = getUserId(settlement);
        }
        return cardOrdersService.settlement(userId,settlement,cardPhysical);
    }

    private Long getUserId(@RequestBody Settlement settlement) {
        Long userId;
        if (!StringUtils.isEmpty(settlement.getUserFlagCode())) {
            String userFlagCode = settlement.getUserFlagCode();
            String openid = RequestQrCodeDataStrUtil.subStringQrCodeData(userFlagCode);
            UserUsers usrUsers = authClientService.queryByOpenid(openid).getData();
            userId = usrUsers.getId();
        } else if (!StringUtils.isEmpty(settlement.getTel())) {
            if (StringUtils.isEmpty(settlement.getAuthCode())) {
                throw new UserDefinedException(ResultTypeEnum.AUTH_CODE_ERROR);
            }
            Cache cacheInfo = CacheManager.getCacheInfo(settlement.getTel() + "&authCode");
            Cache cache = (Cache) cacheInfo.getValue();
            String cacheAuthCode = (String) cache.getValue();
            if (!cacheAuthCode.equals(settlement.getAuthCode())) {
                throw new UserDefinedException(ResultTypeEnum.AUTH_CODE_ERROR);
            }
            //查看用户userId
            UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(settlement.getTel(), settlement.getMerchantCode());
            userId = userUsers.getId();
        } else {
            //查看用户userId
            String icCardId = settlement.getIcCardId();
            CardMapUserCards cardMapUserCards = cardMapUserClientService.getByIcCard(icCardId).getData();
            if (cardMapUserCards==null){
                throw new CheckException(ResultTypeEnum.USER_NULL);
            }
            userId = cardMapUserCards.getUserId();
        }
        return userId;
    }

    /**
     * pos 端收银 返回 计算过后的金额 不直接结算
     *
     * @param settlementCard
     * @return
     */
    @PostMapping("/settlementCardMoney")
    public SettlementMoneyData settlementCardMoney(@RequestBody SettlementCard settlementCard) {
        //新开卡类型的支付
        CardPhysical cardPhysical = null;
        Long userId;
        if (!StringUtils.isEmpty(settlementCard.getIcCardId())){
            cardPhysical = msPrimeService.checkGetCardElectronic(settlementCard.getIcCardId());
        }
        if (cardPhysical!=null){
            RetSettlement retSettlement = cardOrdersService.cardElectronicSettlement(cardPhysical,settlementCard.getAmount(),settlementCard.getMerchantCode());
            userId=retSettlement.getUserId();
            if (userId==null) {
                return retSettlement.getSettlementMoneyData();
            }
        }else {
            userId = getUserId(settlementCard);
        }
        return cardOrdersService.settlementCardMoney(userId,settlementCard,cardPhysical);
    }

    private Long getUserId(@RequestBody SettlementCard settlementCard) {
        Long userId = null;
        if (!StringUtils.isEmpty(settlementCard.getUserFlagCode())) {
            String userFlagCode = settlementCard.getUserFlagCode();
            String openid = RequestQrCodeDataStrUtil.subStringQrCodeData(userFlagCode);
            UserUsers usrUsers = authClientService.queryByOpenid(openid).getData();
            userId = usrUsers.getId();
        } else if (!StringUtils.isEmpty(settlementCard.getTel())) {
            //查看用户userId
            UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(settlementCard.getTel(), settlementCard.getMerchantCode());
            userId = userUsers.getId();
        } else {
            //查看用户userId
            String icCardId = settlementCard.getIcCardId();
            CardMapUserCards cardMapUserCards = cardMapUserClientService.getByIcCard(icCardId).getData();
            if (cardMapUserCards==null){
                throw new CheckException(ResultTypeEnum.CARD_NULL);
            }
            userId = cardMapUserCards.getUserId();
        }
        return userId;
    }

    /**
     * pos端收银 微信,支付宝支付成功  数据记录接口(会员收银 组合支付)
     *
     * @param posPayTraceData
     * @return
     */
    @PostMapping("/settlementSuccess")
    public String settlementSuccess(@RequestBody PosPayTraceData posPayTraceData) {
        Long userId;
        if (!StringUtils.isEmpty(posPayTraceData.getUserFlagCode())){
            String userFlagCode = posPayTraceData.getUserFlagCode();
            String openid = RequestQrCodeDataStrUtil.subStringQrCodeData(userFlagCode);
            UserUsers userUsers = authClientService.queryByOpenid(openid).getData();
            userId = userUsers.getId();
            posPayTraceData.setUserId(userId);
        }else if (!StringUtils.isEmpty(posPayTraceData.getTel())){
            UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(posPayTraceData.getTel(), posPayTraceData.getMerchantCode());
            userId = userUsers.getId();
            posPayTraceData.setUserId(userId);
        }else if (!StringUtils.isEmpty(posPayTraceData.getIcCardId())){
            //新卡
            CardPhysical cardPhysical = msPrimeService.checkGetCardElectronic(posPayTraceData.getIcCardId());
            if (cardPhysical!=null){
                Long cardElectronicUserId = cardPhysical.getUserId();
                posPayTraceData.setUserId(cardElectronicUserId==null ? -1L : cardElectronicUserId);
                String orderCode = cardOrderPayTraceClientService.createPosPayTraceFromCashier(posPayTraceData).getData();
                return orderCode;
            }
            CardMapUserCards cardMapUserCards=cardMapUserClientService.queryByIcCardId(posPayTraceData.getIcCardId()).getData();
            userId = cardMapUserCards.getUserId();
            posPayTraceData.setUserId(userId);
        }else {
            userId = -1L;
            posPayTraceData.setUserId(userId);
        }
        String orderCode = cardOrderPayTraceClientService.createPosPayTraceFromCashier(posPayTraceData).getData();
        return orderCode;
    }

    /**
     * pos端收银  通过选定的卡券  直接支付  返回 核算过后的金额
     *
     * @param settlementCard
     * @return
     */
    @PostMapping("/settlementCardEnd")
    public SettlementMoneyData settlementCardEnd(@RequestBody SettlementCard settlementCard) {
        //新卡核销
        //新开卡类型的支付
        CardPhysical cardPhysical = null;
        Long userId;
        if (!StringUtils.isEmpty(settlementCard.getIcCardId())){
            cardPhysical = msPrimeService.checkGetCardElectronic(settlementCard.getIcCardId());
        }
        if (cardPhysical!=null){
            userId = cardPhysical.getUserId();
            if (userId ==null){
                //电子卡直接核销扣钱
                RetSettlement retSettlement = msPrimeService.settlementCardElectronic(cardPhysical, settlementCard.getAmount(), settlementCard.getOrderCode(),userId, settlementCard.getTerId());
                return retSettlement.getSettlementMoneyData();
            }else {
                //核算 钱 余额里面的
                RetAccountPayData retAccountPayData = userUsersService.settlementUserMoneyEnd(settlementCard.getCardNoList(),
                        settlementCard.getAmount(), userId,settlementCard.getOrderCode());
                SettlementMoneyData settlementMoneyData = new SettlementMoneyData();
                //电子卡直接核销扣钱
                RetSettlement retSettlement = msPrimeService.settlementCardElectronic(cardPhysical, retAccountPayData.getAmount(), settlementCard.getOrderCode(),userId, settlementCard.getTerId());
                settlementMoneyData.setAmount(retSettlement.getSettlementMoneyData().getAmount());
                settlementMoneyData.setIsToPay(retSettlement.getSettlementMoneyData().getAmount() > 0);
                settlementMoneyData.setOrderCode(retAccountPayData.getOrderCode());
                return settlementMoneyData;
            }
        }else {
            userId = getUserId(settlementCard);
        }

        //核算 钱 余额里面的
        RetAccountPayData retAccountPayData = userUsersService.settlementUserMoneyEnd(settlementCard.getCardNoList(),
                settlementCard.getAmount(), userId,settlementCard.getOrderCode());
        SettlementMoneyData settlementMoneyData = new SettlementMoneyData();
        settlementMoneyData.setAmount(retAccountPayData.getAmount());
        settlementMoneyData.setIsToPay(retAccountPayData.getAmount() > 0);
        settlementMoneyData.setOrderCode(retAccountPayData.getOrderCode());
        return settlementMoneyData;
    }


    /**
     * 用户余额充值 支付成功后 记录支付流水数据
     *
     * @param posPayTraceData
     * @return
     */
    @PostMapping("/posPayTrace")
    public void posPayTrace(@RequestBody PosPayTraceData posPayTraceData) {
        System.out.println("支付成功数据"+posPayTraceData);
        Long userId;
        if (!StringUtils.isEmpty(posPayTraceData.getTel())) {
            UserUsers userUsers = merchantPrimeService.primeQueryUserByTelAndCode(posPayTraceData.getTel(), posPayTraceData.getMerchantCode());
            userId = userUsers.getId();
            posPayTraceData.setUserId(userId);
        } else {
            CardMapUserCards cardMapUserCards = cardMapUserClientService.queryByIcCardId(posPayTraceData.getIcCardId()).getData();
            userId = cardMapUserCards.getUserId();
            posPayTraceData.setUserId(userId);
        }
        cardOrderPayTraceClientService.createPosPayTrace(posPayTraceData);
        //调取通联支付 增加用户余额
        cardUserService.userAccountMoneyAdd(posPayTraceData.getUserId(),posPayTraceData.getAmount());
    }

    /**
     * ori hailv_v1.1
     * 海旅 组合支付
     * @param desDataStr
     * @return
     */
    @PostMapping("/posCombinationPay")
    public String posCombinationPay(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        PosCombinationPay posCombinationPay;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            posCombinationPay = JSONObject.parseObject(decryptDataStr, PosCombinationPay.class);
            logger.info("上送的核销数据为:"+posCombinationPay);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        if (posCombinationPay==null){
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        String userFlagCode = posCombinationPay.getUserFlagCode();
        if (!userFlagCode.contains("TL")){
            throw new CheckException(ResultTypeEnum.QR_AUTH_CODE_ERROR);
        }
        logger.info("获取的用户标识为:"+userFlagCode);
        String relUserFlagCode = UserCodePreSubUtil.userCodePreSubStr(userFlagCode);
        logger.info("截取真实的用户标识为:"+relUserFlagCode);

        //payCode补充,若不上送payCode 则我方生成
        if (StringUtils.isEmpty(posCombinationPay.getPayCode()) || "null".equals(posCombinationPay.getPayCode())){
            posCombinationPay.setPayCode(IdWorker.getIdStr());
        }

        //核销电子卡
        RetPosCombinationPayData retPosCombinationPayData = new RetPosCombinationPayData();
        RetPosCombinationPayEleCard retPosCombinationPayEleCard = msPrimeService.posCombinationPayEleCard(relUserFlagCode, posCombinationPay.getAmount());

        if (retPosCombinationPayEleCard.getPaidAmount()>0) {
            String realOpenId;
            if (relUserFlagCode.contains("FREE")){
                realOpenId = UserCodeAuthUtil.getRealOpenIdFree(relUserFlagCode);
            }else if (relUserFlagCode.contains("ELECARD")){
                String cardNo = UserCodeAuthUtil.getRealOpenId(relUserFlagCode);
                CardActualMapUser cardActualMapUser = msPrimeClient.queryByCardNo(cardNo);
                realOpenId = cardActualMapUser.getVipUserOpenid();
            } else {
                realOpenId = UserCodeAuthUtil.getRealOpenId(relUserFlagCode);
            }

            posCombinationPay.setUserFlagCode(realOpenId);
            cardOrdersService.posCombinationPay(posCombinationPay, retPosCombinationPayEleCard, CardOrderPayTraceStateConfig.PAID);
        }
        retPosCombinationPayData.setState(CardOrderPayTraceStateConfig.PAID);
        retPosCombinationPayData.setOrderCode(posCombinationPay.getOrderCode());
        retPosCombinationPayData.setPaidAmount(retPosCombinationPayEleCard.getPaidAmount());
        retPosCombinationPayData.setNeedPaidAmount(retPosCombinationPayEleCard.getNeedPaidAmount());
        String jsonString = JSONObject.toJSONString(retPosCombinationPayData);
        String encrypt = desUtil.encrypt(jsonString);
        logger.info(posCombinationPay.getOrderCode()+"订单,响应收银台数据为:"+retPosCombinationPayData);
        logger.info(posCombinationPay.getOrderCode()+"订单,响应收银台数据为:"+encrypt);
        return encrypt;
    }


    /**
     * rewrite ori hailv_v1.1.0
     * 海旅 组合支付
     * @param desDataStr
     * @return
     */
    @PostMapping("/handler/posCombinationPay")
    public Result handlerPosCombinationPay(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        PosCombinationPay posCombinationPay;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            posCombinationPay = JSONObject.parseObject(decryptDataStr, PosCombinationPay.class);
            logger.info("handlerPosCombinationPay上送的核销数据为:"+posCombinationPay);
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        if (posCombinationPay==null){
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        String userFlagCode = posCombinationPay.getUserFlagCode();
        if (StringUtils.isEmpty(userFlagCode) || !userFlagCode.contains("TL")){
            throw new CheckException(ResultTypeEnum.AUTH_FORMAT_CODE_ERROR);
        }
        logger.info("handlerPosCombinationPay获取的用户标识为:"+userFlagCode);
        String relUserFlagCode = UserCodePreSubUtil.userCodePreSubStr(userFlagCode);
        logger.info("handlerPosCombinationPay截取真实的用户标识为:"+relUserFlagCode);

        String orderCode = posCombinationPay.getOrderCode();

        boolean isPrePayCardType = cardOrdersService.checkOrderCodeIsPrePayCardType(orderCode);
        if (!isPrePayCardType){
            logger.info("handlerPosCombinationPay orderCode={},isPrePayCardType={}",orderCode,isPrePayCardType);
            throw new CheckException(ResultTypeEnum.CARD_TYPE_PREPAY_NO);
        }

        //payCode补充,若不上送payCode 则我方生成
        if (StringUtils.isEmpty(posCombinationPay.getPayCode()) || "null".equals(posCombinationPay.getPayCode())){
            posCombinationPay.setPayCode(IdWorker.getIdStr());
        }

        //核销电子卡
        RetPosCombinationPayData retPosCombinationPayData = new RetPosCombinationPayData();
        RetPosCombinationPayEleCard retPosCombinationPayEleCard = msPrimeService.handlerPosCombinationPayEleCard(relUserFlagCode, posCombinationPay.getAmount());

        if (retPosCombinationPayEleCard.getPaidAmount()>0) {
            String realOpenId;
            if (relUserFlagCode.contains("FREE")){
                realOpenId = UserCodeAuthUtil.getRealOpenIdFree(relUserFlagCode);
            }else if (relUserFlagCode.contains("ELECARD")){
                String cardNo = UserCodeAuthUtil.getRealOpenId(relUserFlagCode);
                CardActualMapUser cardActualMapUser = msPrimeClient.queryByCardNo(cardNo);
                realOpenId = cardActualMapUser.getVipUserOpenid();
            } else {
                realOpenId = UserCodeAuthUtil.getRealOpenId(relUserFlagCode);
            }

            posCombinationPay.setUserFlagCode(realOpenId);
            cardOrdersService.posCombinationPay(posCombinationPay, retPosCombinationPayEleCard, CardOrderPayTraceStateConfig.PAID);
        }
        retPosCombinationPayData.setState(CardOrderPayTraceStateConfig.PAID);
        retPosCombinationPayData.setOrderCode(posCombinationPay.getOrderCode());
        retPosCombinationPayData.setPaidAmount(retPosCombinationPayEleCard.getPaidAmount());
        retPosCombinationPayData.setNeedPaidAmount(retPosCombinationPayEleCard.getNeedPaidAmount());
        String jsonString = JSONObject.toJSONString(retPosCombinationPayData);
        String encrypt = desUtil.encrypt(jsonString);
        logger.info(posCombinationPay.getOrderCode()+"订单,响应数据为:"+retPosCombinationPayData);
        logger.info(posCombinationPay.getOrderCode()+"订单,加密响应数据为:"+encrypt);
        return Result.success(encrypt);
    }





    /**
     * ori hailv_v1.1
     * 海旅 根据订单号,查询支付流水
     * @param orderCode
     * @return
     */
    @PostMapping("/queryPayTrace")
    public List<CardOrderPayTrace> queryPayTrace(@RequestParam("orderCode") String orderCode,
                                                 @RequestParam(value = "merchantCode",defaultValue = "HLSC",required = false)String merchantCode){
        List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryPayTraceByOrderCodeAndMerchantCode(orderCode, merchantCode).getData();
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())){
                CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(cardOrderPayTrace.getSourceId()).getData();
                if (cardElectronic!=null){
                    if ("offline".equals(cardElectronic.getCardType()) || "rebate_password_card".equals(cardElectronic.getCardType())){
                        cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+cardElectronic.getCardType());
                        cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                        cardOrderPayTrace.setCardIsFree("Y");
                    }else {
                        cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+cardElectronic.getCardType());
                        cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                        cardOrderPayTrace.setCardIsFree("N");
                    }
                }else {
                    PartyCardElectronic partyCardElectronic = msPrimeClient.queryPartyCardElectronicByCardNo(cardOrderPayTrace.getSourceId()).getData();
                    if (partyCardElectronic!=null){
                        if ("offline".equals(partyCardElectronic.getCardType()) || "rebate_password_card".equals(partyCardElectronic.getCardType())){
                            cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+partyCardElectronic.getCardType());
                            cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(partyCardElectronic.getCardType()));
                            cardOrderPayTrace.setCardIsFree("Y");
                        }else {
                            cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+partyCardElectronic.getCardType());
                            cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(partyCardElectronic.getCardType()));
                            cardOrderPayTrace.setCardIsFree("N");
                        }
                    }
                }
            }else if (CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){
                CardPhysical cardPhysical = msPrimeClient.queryByCardCode(cardOrderPayTrace.getSourceId()).getData();
                if ("sell".equals(cardPhysical.getType())){
                    cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_PHYSICAL+cardPhysical.getType());
                    cardOrderPayTrace.setCardTypeName("实体卡储值卡");
                    cardOrderPayTrace.setCardIsFree("N");
                }else{
                    cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_PHYSICAL+cardPhysical.getType());
                    cardOrderPayTrace.setCardTypeName("实体卡赠送卡");
                    cardOrderPayTrace.setCardIsFree("Y");
                }
            }
        }
        return cardOrderPayTraceList;
    }


    /**
     * rewrite ori hailv_v1.1.0
     * 海旅 根据订单号,查询支付流水
     * @param desDataStr
     * @return
     */
    @PostMapping("/handler/queryPayTrace")
    public Result handlerQueryPayTrace(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            logger.error("handlerQueryPayTrace desDataStr={},error={}",desDataStr,e);
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        CardOrdersVO cardQueryData = JSONObject.parseObject(decryptDataStr, CardOrdersVO.class);
        String orderCode = cardQueryData.getOrderCode();
        String merchantCode = cardQueryData.getMerchantCode();
        List<CardOrderPayTrace> cardOrderPayTraces = queryPayTrace(orderCode, merchantCode);
        String cardOrderPayTracesStr = JSONArray.toJSONString(cardOrderPayTraces);
        String encrypt = desUtil.encrypt(cardOrderPayTracesStr);
        logger.info("handlerQueryPayTrace cardOrderPayTraces 响应数据为:{}",cardOrderPayTraces);
        logger.info("handlerQueryPayTrace encrypt 加密响应数据为:{}:",encrypt);
        return Result.success(encrypt);

    }

    /**
     * 海旅 组合支付 仍支付金额 支付成功
     * @param desDataStr
     * @return
     */
    @PostMapping("/posCombinationPaySuccess")
    public void posCombinationPaySuccess(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        PosCombinationPaySuccess posCombinationPaySuccess = JSONObject.parseObject(decryptDataStr, PosCombinationPaySuccess.class);
        String userFlagCode = posCombinationPaySuccess.getUserFlagCode();
        logger.info("获取的用户标识为:"+userFlagCode);
        String relUserFlagCode = UserCodePreSubUtil.userCodePreSubStr(userFlagCode);
        logger.info("截取真实的用户标识为:"+relUserFlagCode);
        posCombinationPaySuccess.setUserFlagCode(UserCodeAuthUtil.getRealOpenId(relUserFlagCode));
        //核销卡金额
//        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(posCombinationPaySuccess.getOrderCode()).getData();
//        Integer paidAmount=cardOrdersVO.getAmount()-Integer.parseInt(posCombinationPaySuccess.getAmount());
//        if (paidAmount>0) {
//            msPrimeService.consumeEleCardMoney(paidAmount, posCombinationPaySuccess.getUserFlagCode());
//        }

        // 创建实际金额支付流水 修改订单状态
        cardOrderPayTraceClientService.createPosCombinationCashPay(posCombinationPaySuccess);
    }

    /**
     * ori hailv_v1.1
     * 海旅 退款查询
     * @param orderCode
     * @return
     */
    @PostMapping("/queryRefundOrder")
    public List<CardRefundOrder> posCombinationRefund(@RequestParam("orderCode") String orderCode,
                                                      @RequestParam("merchantCode")String merchantCode,
                                                      @RequestParam(value = "refundCode",required = false)String refundCode){
        List<CardRefundOrder> cardRefundOrders = msPrimeService.queryRefundOrder(orderCode, merchantCode, refundCode);
        for (CardRefundOrder cardRefundOrder : cardRefundOrders) {
            if (cardRefundOrder.getCardId().contains("ELECARD")){
                CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(cardRefundOrder.getCardId()).getData();
                if (cardElectronic!=null){
                    if ("offline".equals(cardElectronic.getCardType()) || "rebate_password_card".equals(cardElectronic.getCardType())){
                        cardRefundOrder.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+cardElectronic.getCardType());
                        cardRefundOrder.setCardTypeName(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                        cardRefundOrder.setCardIsFree("Y");
                    }else {
                        cardRefundOrder.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+cardElectronic.getCardType());
                        cardRefundOrder.setCardTypeName(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                        cardRefundOrder.setCardIsFree("N");
                    }
                }else {
                    PartyCardElectronic partyCardElectronic = msPrimeClient.queryPartyCardElectronicByCardNo(cardRefundOrder.getCardId()).getData();
                    if (partyCardElectronic!=null){
                        if ("offline".equals(partyCardElectronic.getCardType()) || "rebate_password_card".equals(partyCardElectronic.getCardType())){
                            cardRefundOrder.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+partyCardElectronic.getCardType());
                            cardRefundOrder.setCardTypeName(CardElectronicEnum.getDescByValueKey(partyCardElectronic.getCardType()));
                            cardRefundOrder.setCardIsFree("Y");
                        }else {
                            cardRefundOrder.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+partyCardElectronic.getCardType());
                            cardRefundOrder.setCardTypeName(CardElectronicEnum.getDescByValueKey(partyCardElectronic.getCardType()));
                            cardRefundOrder.setCardIsFree("N");
                        }
                    }
                }
            }else {
                CardPhysical cardPhysical = msPrimeClient.queryByCardCode(cardRefundOrder.getCardId()).getData();
                if ("sell".equals(cardPhysical.getType())){
                    cardRefundOrder.setCardType(CardOrderPayTraceTypeConfig.CARD_PHYSICAL+cardPhysical.getType());
                    cardRefundOrder.setCardTypeName("实体卡储值卡");
                    cardRefundOrder.setCardIsFree("N");
                }else{
                    cardRefundOrder.setCardType(CardOrderPayTraceTypeConfig.CARD_PHYSICAL+cardPhysical.getType());
                    cardRefundOrder.setCardTypeName("实体卡赠送卡");
                    cardRefundOrder.setCardIsFree("Y");
                }
            }
        }
        return cardRefundOrders;
    }


    /**
     * rewrite ori hailv_v1.1.0
     * 海旅 退款查询
     * @param desDataStr
     * @return
     */
    @PostMapping("/handler/queryRefundOrder")
    public Result handlerPosCombinationRefund(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            logger.error("handlerPosCombinationRefund desDataStr={},error={}",desDataStr,e);
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        CardQueryData cardQueryData = JSONObject.parseObject(decryptDataStr, CardQueryData.class);
        String orderCode = cardQueryData.getOrderCode();
        String merchantCode = cardQueryData.getMerchantCode();
        String refundCode = cardQueryData.getRefundCode();
        List<CardRefundOrder> cardRefundOrders = posCombinationRefund(orderCode, merchantCode, refundCode);
        String cardRefundOrderStr = JSONArray.toJSONString(cardRefundOrders);
        String encrypt = desUtil.encrypt(cardRefundOrderStr);
        logger.info("handlerPosCombinationRefund cardRefundOrderStr 响应数据为:{}",cardRefundOrderStr);
        logger.info("handlerPosCombinationRefund encrypt 加密响应数据为:{}:",encrypt);
        return Result.success(encrypt);
    }

    /**
     * 海旅 退款
     * @param desDataStr
     * @return
     */
    @PostMapping("/posCombinationRefund")
    public void posCombinationRefund(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        logger.info("上送的退款数据为:"+decryptDataStr);
        PosCombinationRefundData posCombinationRefundData = JSONObject.parseObject(decryptDataStr, PosCombinationRefundData.class);
        logger.info("上送的退款数据为:"+posCombinationRefundData);
        List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryPayTrace(posCombinationRefundData.getOrderCode()).getData();
        List<CardOrderPayTrace> refundCardOrderPayTraces = msPrimeService.posCombinationRefundMatching(cardOrderPayTraceList,
                posCombinationRefundData.getOperator(),posCombinationRefundData.getRefundCode(),posCombinationRefundData.getRefundAmount(),posCombinationRefundData.getPayTraceNo());

        msPrimeService.refreshRemainFaceValue(refundCardOrderPayTraces);

        cardOrderPayTraceClientService.updateRefundData(refundCardOrderPayTraces);
    }

    /**
     * 海旅控制台 消费订单退款至原卡
     * @param posCombinationRefundData
     * @return
     */
    @PostMapping("/adminConsumeOrderRefund")
    public RetAdminRefundData posCombinationRefund(@RequestBody PosCombinationRefundData posCombinationRefundData){
        String refundPassword = msPrimeClient.queryRefundPassword().getData();
        String reqRefundPassword = posCombinationRefundData.getRefundPassword();
        if (!StringUtils.isEmpty(refundPassword)) {
            if (StringUtils.isEmpty(reqRefundPassword) || !refundPassword.equals(reqRefundPassword)) {
                throw new CheckException(ResultTypeEnum.REFUND_PASSWORD_ERROR);
            }
        }
        String refundCode = IdWorker.getIdStr();
        List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryPayTrace(posCombinationRefundData.getOrderCode()).getData();
        List<CardOrderPayTrace> refundCardOrderPayTraces = msPrimeService.posCombinationRefundMatching(cardOrderPayTraceList,
                posCombinationRefundData.getOperator(),refundCode,posCombinationRefundData.getRefundAmount(),posCombinationRefundData.getPayTraceNo());

        msPrimeService.refreshRemainFaceValue(refundCardOrderPayTraces);

        cardOrderPayTraceClientService.updateRefundData(refundCardOrderPayTraces);
        RetAdminRefundData retAdminRefundData=new RetAdminRefundData();
        retAdminRefundData.setRefundCode(refundCode);
        return retAdminRefundData;
    }

    /**
     * 海旅富基,根据支付流水查询流水记录
     * @param payCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryTraceByPayCode")
    public List<CardOrderPayTrace> queryTraceByPayCode(@RequestParam("payCode") String payCode,
                                                       @RequestParam(value = "merchantCode",required = false,defaultValue = "HLSC")String merchantCode){
        List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryTraceByPayCode(payCode, merchantCode).getData();
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraceList) {
            if (CardOrderPayTraceTypeConfig.CARD_ELECTRONIC.equals(cardOrderPayTrace.getType())){
                CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(cardOrderPayTrace.getSourceId()).getData();
                if (cardElectronic!=null){
                    if ("offline".equals(cardElectronic.getCardType()) || "rebate_password_card".equals(cardElectronic.getCardType())){
                        cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+cardElectronic.getCardType());
                        cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                        cardOrderPayTrace.setCardIsFree("Y");
                    }else {
                        cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+cardElectronic.getCardType());
                        cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(cardElectronic.getCardType()));
                        cardOrderPayTrace.setCardIsFree("N");
                    }
                }else {
                    PartyCardElectronic partyCardElectronic = msPrimeClient.queryPartyCardElectronicByCardNo(cardOrderPayTrace.getSourceId()).getData();
                    if (partyCardElectronic!=null){
                        if ("offline".equals(partyCardElectronic.getCardType()) || "rebate_password_card".equals(partyCardElectronic.getCardType())){
                            cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+partyCardElectronic.getCardType());
                            cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(partyCardElectronic.getCardType()));
                            cardOrderPayTrace.setCardIsFree("Y");
                        }else {
                            cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_ELECTRONIC+"_"+partyCardElectronic.getCardType());
                            cardOrderPayTrace.setCardTypeName(CardElectronicEnum.getDescByValueKey(partyCardElectronic.getCardType()));
                            cardOrderPayTrace.setCardIsFree("N");
                        }
                    }
                }
            }else if (CardOrderPayTraceTypeConfig.CARD_PHYSICAL.equals(cardOrderPayTrace.getType())){
                CardPhysical cardPhysical = msPrimeClient.queryByCardCode(cardOrderPayTrace.getSourceId()).getData();
                if ("sell".equals(cardPhysical.getType())){
                    cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_PHYSICAL+cardPhysical.getType());
                    cardOrderPayTrace.setCardTypeName("实体卡储值卡");
                    cardOrderPayTrace.setCardIsFree("N");
                }else{
                    cardOrderPayTrace.setCardType(CardOrderPayTraceTypeConfig.CARD_PHYSICAL+cardPhysical.getType());
                    cardOrderPayTrace.setCardTypeName("实体卡赠送卡");
                    cardOrderPayTrace.setCardIsFree("Y");
                }
            }
        }
        return cardOrderPayTraceList;
    }


    /**
     * ori hailv_v1.1
     * 根据订单号查询订单
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/queryOrderByCode/{orderCode}")
    public CardOrdersVO queryByOrderCode(@PathVariable("orderCode") String orderCode) {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCode).getData();
        return cardOrdersVO;
    }

    /**
     * rewrite ori hailv_v1.1.0
     * 根据订单号查询订单
     *
     * @param desDataStr
     * @return
     */
    @PostMapping("/handler/queryOrderByCode")
    public Result handlerQueryByOrderCode(@RequestBody DESDataStr desDataStr) {
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            logger.error("handlerQueryByOrderCode desDataStr={},error={}",desDataStr,e);
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        CardOrdersVO cardQueryData = JSONObject.parseObject(decryptDataStr, CardOrdersVO.class);
        String orderCode = cardQueryData.getOrderCode();
        CardOrdersVO cardOrdersVO = queryByOrderCode(orderCode);
        String cardOrdersVOStr = JSONObject.toJSONString(cardOrdersVO);
        String encrypt = desUtil.encrypt(cardOrdersVOStr);
        logger.info("handlerQueryByOrderCode cardOrdersVO 响应数据为:{}",cardOrdersVO);
        logger.info("handlerQueryByOrderCode encrypt 加密响应数据为:{}:",encrypt);
        return Result.success(encrypt);
    }


    /**
     * ori hailv_v1.1
     * 海旅 撤销交易
     * @param desDataStr
     * @return
     */
    @PostMapping("/orderCancel")
    public void orderCancel(@RequestBody DESDataStr desDataStr){
        logger.info("上送的撤销订单加密数据:"+desDataStr.getDesDataStr());
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("上送的撤销订单解密数据:"+decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        OrderCancelData cancelData = JSONObject.parseObject(decryptDataStr, OrderCancelData.class);
        logger.info("上送的撤销订单实体数据:"+cancelData);
        CardOrders data = orderClientService.queryByOrderCodeNotDetail(cancelData.getOrderCode()).getData();
        if (data!=null) {
            if (!CardOrdersStateConfig.PAID.equals(data.getState())) {
                throw new CheckException(ResultTypeEnum.CANCEL_ERROR);
            }
        }else {
            throw new CheckException(ResultTypeEnum.CANCEL_ERROR);
        }
        List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryPayTrace(cancelData.getOrderCode()).getData();
        List<CardOrderPayTrace> refundCardOrderPayTraces = msPrimeService.posCombinationCancel(cardOrderPayTraceList,
                cancelData.getOperator(),cancelData.getCancelCode());
        msPrimeService.refreshRemainFaceValue(refundCardOrderPayTraces);
        orderClientService.updateStateCancel(refundCardOrderPayTraces,cancelData.getOrderCode(),"cancel");
    }

    /**
     * 海旅 交易订单关闭
     * @param desDataStr
     * @return
     */
    @PostMapping("/orderClose")
    public void orderClose(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        OrderCloseData orderCloseData = JSONObject.parseObject(decryptDataStr, OrderCloseData.class);
        CardOrders data = orderClientService.queryByOrderCodeNotDetail(orderCloseData.getOrderCode()).getData();
        if (!CardOrdersStateConfig.UNPAID.equals(data.getState())){
            throw new CheckException(ResultTypeEnum.ORDER_CLOSE_ERROR);
        }
        orderClientService.updateState(orderCloseData.getOrderCode(),"close");
    }


    /**
     * hailv_v1.1.0
     * 查询订单号的退款金额
     * @param desDataStr
     * @return
     */
    @PostMapping("/orderRefundAmount")
    public Result orderRefundAmount(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            logger.error("orderRefundAmount desDataStr={},error={}",desDataStr,e);
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        CardQueryData cardQueryData = JSONObject.parseObject(decryptDataStr, CardQueryData.class);
        String orderCode = cardQueryData.getOrderCode();
        String merchantCode = cardQueryData.getMerchantCode();
        String refundCode = cardQueryData.getRefundCode();
        logger.info("orderRefundAmount orderCode={},merchantCode={},refundCode={}",orderCode,merchantCode,refundCode);
        Long refundAmount = msPrimeClient.findRefundAmount(orderCode, merchantCode,refundCode).getData();
        JSONObject resultJson = new JSONObject();
        resultJson.put("orderCode",orderCode);
        resultJson.put("merchantCode",merchantCode);
        resultJson.put("refundCode",refundCode);
        resultJson.put("refundAmount",refundAmount);
        String encrypt = desUtil.encrypt(resultJson.toString());
        logger.info("orderRefundAmount 响应数据为:{}",resultJson);
        logger.info("orderRefundAmount 加密响应数据为:{}",encrypt);
        return Result.success(encrypt);
    }

    /**
     * rewrite ori hailv_v1.1.0
     * 海旅 订单退款
     * @param desDataStr
     * @return
     */
    @PostMapping("/handler/orderCancel")
    public Result handlerOrderCancel(@RequestBody DESDataStr desDataStr){
        logger.info("handler上送的撤销订单加密数据:"+desDataStr.getDesDataStr());
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
            logger.info("handler上送的撤销订单解密数据:"+decryptDataStr);
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }
        OrderCancelData cancelData = JSONObject.parseObject(decryptDataStr, OrderCancelData.class);
        logger.info("handler上送的撤销订单实体数据:"+cancelData);
        CardOrders data = orderClientService.queryByOrderCodeNotDetail(cancelData.getOrderCode()).getData();
        if (data!=null) {
            if (!CardOrdersStateConfig.PAID.equals(data.getState())) {
                throw new CheckException(ResultTypeEnum.CANCEL_ERROR);
            }
        }else {
            throw new CheckException(ResultTypeEnum.CANCEL_ERROR);
        }
        List<CardOrderPayTrace> cardOrderPayTraceList = cardOrderPayTraceClientService.queryPayTrace(cancelData.getOrderCode()).getData();
        List<CardOrderPayTrace> refundCardOrderPayTraces = msPrimeService.posCombinationCancel(cardOrderPayTraceList,
                cancelData.getOperator(),cancelData.getCancelCode());
        msPrimeService.refreshRemainFaceValue(refundCardOrderPayTraces);
        orderClientService.updateStateCancel(refundCardOrderPayTraces,cancelData.getOrderCode(),"cancel");

        RetPosCombinationOrderCancelData retPosCombinationOrderCancelData = new RetPosCombinationOrderCancelData();
        retPosCombinationOrderCancelData.setMerchantCode(cancelData.getMerchantCode());
        retPosCombinationOrderCancelData.setOrderCode(cancelData.getOrderCode());
        retPosCombinationOrderCancelData.setRefundCode(cancelData.getRefundCode());
        retPosCombinationOrderCancelData.setOperator(cancelData.getOperator());
        retPosCombinationOrderCancelData.setState(CardOrderPayTraceStateConfig.REFUND);
        String jsonString = JSONObject.toJSONString(retPosCombinationOrderCancelData);
        String encrypt = desUtil.encrypt(jsonString);
        logger.info(cancelData.getOrderCode()+"订单退单,响应数据为:"+retPosCombinationOrderCancelData);
        logger.info(cancelData.getOrderCode()+"订单退单,加密响应数据为:"+encrypt);
        return Result.success(encrypt);

    }

}

