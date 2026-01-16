package com.ht.feignapi.prime.controller;

import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.constant.MerchantChargeTypeConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.pay.service.AllinpayService;
import com.ht.feignapi.prime.cardconstant.LimitPayTypeConstant;
import com.ht.feignapi.prime.cardenum.CardElectronicEnum;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.client.PrimePayClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.prime.service.PrimeCardService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.result.UserDefinedException;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderPayTraceClientService;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.card.service.CardOrdersService;
import com.ht.feignapi.tonglian.card.service.MSPrimeService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.utils.Cache;
import com.ht.feignapi.tonglian.utils.CacheManager;
import com.ht.feignapi.tonglian.utils.RequestQrCodeDataStrUtil;
import com.ht.feignapi.util.DateStrUtil;
import com.ht.feignapi.util.UserCodeAuthUtil;
import com.ht.feignapi.util.UserCodePreSubUtil;
import org.omg.PortableInterceptor.USER_EXCEPTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.smartcardio.Card;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/ms/primePay")
@CrossOrigin(allowCredentials = "true")
public class PrimePayController {

    private Logger logger = LoggerFactory.getLogger(PrimePayController.class);

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private PrimePayClient primePayClient;

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private PrimeCardService primeCardService;

    @Autowired
    private CardOrderClientService cardOrderClientService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MSPrimeService msPrimeService;

    @Autowired
    private CardOrdersService cardOrdersService;

    @Autowired
    private CardOrderPayTraceClientService cardOrderPayTraceClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private OrderClientService orderClientService;



    /**
     * 获取通联调取 H5 支付数据 (商城购物)
     *
     * @param primeBuyCardData
     * @return
     * @throws Exception
     */
    @PostMapping("/buyCard")
    public Map buyCard(@RequestBody PrimeBuyCardData primeBuyCardData) throws Exception {
        if (!primeBuyCardData.isRead()){
            throw new CheckException(ResultTypeEnum.READ_ERROR);
        }
        List<CardElectronicSell> cardElectronicSellList = primeBuyCardData.getCardElectronicSellList();
        if (cardElectronicSellList==null || cardElectronicSellList.size()<=0){
            throw new CheckException(ResultTypeEnum.NOT_PRODUCTION);
        }
        // 校验库存 与 账户总额
        RetCheckQuantityData retCheckQuantityData = msPrimeClient.checkQuantity(primeBuyCardData).getData();
        if (!retCheckQuantityData.isUserAccountFlag()){
            throw new CheckException(ResultTypeEnum.LIMIT_USER_ACCOUNT);
        }

        if (!retCheckQuantityData.isQuantityFlag()){
            throw new CheckException(ResultTypeEnum.NOT_INVENTORY);
        }

        primeBuyCardData.setUserId(retCheckQuantityData.getVipUser().getId()+"");
        primeBuyCardData.setCardElectronicSellList(retCheckQuantityData.getCardElectronicSellList());
        primeBuyCardData.setUserPhone(retCheckQuantityData.getVipUser().getPhoneNum());
        //创建订单 订单明细 支付流水
        CardOrders cardOrders = cardOrderClientService.createPrimeBuyCardOrder(primeBuyCardData).getData();
        redisTemplate.opsForHash().put("orderInventory",cardOrders.getOrderCode(),retCheckQuantityData.getCardElectronicSellList());

        String merchantCode = primeBuyCardData.getCardElectronicSellList().get(0).getBrhId();
        //封装支付配置数据
        PayOrderData payOrderData = new PayOrderData();
        payOrderData.setOrderCode(cardOrders.getOrderCode());
        payOrderData.setMerchantCode(merchantCode);
        //获取配置的支付数据
        List<MerchantsConfigVO> merchantsConfigVOList = getMerchantsConfigListResult(merchantCode);
        String mchId;
        String appId;
        String md5Key;
        for (MerchantsConfigVO merchantsConfigVO : merchantsConfigVOList) {
            if ("MCHID".equals(merchantsConfigVO.getKey())) {
                mchId = merchantsConfigVO.getValue();
                payOrderData.setMchId(mchId);
            }
            if ("APPID".equals(merchantsConfigVO.getKey())) {
                appId = merchantsConfigVO.getValue();
                payOrderData.setAppId(appId);
            }
            if ("MD5KEY".equals(merchantsConfigVO.getKey())) {
                md5Key = merchantsConfigVO.getValue();
                payOrderData.setMD5Key(md5Key);
            }
        }
        Integer discount = 0;
        payOrderData.setTrxamt(cardOrders.getAmount() - discount);

        //处理名称过长 str.length()>10?str.substring(0,10)+"..." : str

        payOrderData.setBody(cardOrders.getComments().length()>15?cardOrders.getComments().substring(0,15)+"..." : cardOrders.getComments());

        //拉取支付数据
        Map unionOrderMapData = primePayClient.buyCard(payOrderData).getData();
        unionOrderMapData.put("toPay",true);
        unionOrderMapData.put("message","创建支付流水成功,可支付");
        logger.info("拉取到的通联支付数据为:"+unionOrderMapData);
        return unionOrderMapData;
    }

    /**
     * 获取 配置支付数据
     *
     * @param merchantCode
     * @return
     */
    public List<MerchantsConfigVO> getMerchantsConfigListResult(String merchantCode) {
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        String chargeType = merchants.getChargeType();
        List<MerchantsConfigVO> list;
        if (MerchantChargeTypeConstant.CHARGE_BY_ENTITY.equals(chargeType)) {
            if (StringUtils.isEmpty(merchants.getBusinessSubjects())) {
                list = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
            } else {
                list = merchantsConfigClientService.getPayData(merchants.getBusinessSubjects()).getData();
            }
        } else if (MerchantChargeTypeConstant.CHARGE_BY_STORE.equals(chargeType)) {
            list = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
        } else {
            throw new CheckException(ResultTypeEnum.CHARGE_TYPE_ERROR);
        }
        return list;
    }


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
            if (cardPhysical!=null){
//                if (StringUtils.isEmpty(cardPhysical.getFaceValue()) || Integer.parseInt(cardPhysical.getFaceValue())<=0){
//                    throw new CheckException(ResultTypeEnum.CARD_MONEY_ZERO);
//                }
                RetCardElectronicPosCashier retCardElectronicPosCashier = cardOrdersService.cardElectronicPosCashier(cardPhysical,settlement.getAmount(),settlement.getMerchantCode());
                return retCardElectronicPosCashier.getPosCashierData();
            }else {
                throw new CheckException(ResultTypeEnum.CARD_NULL);
            }
        } else if (!StringUtils.isEmpty(settlement.getMagCard())){
            cardPhysical = msPrimeService.checkGetCardElectronic(settlement.getMagCard());
            if (cardPhysical!=null){
                RetCardElectronicPosCashier retCardElectronicPosCashier = cardOrdersService.cardElectronicPosCashier(cardPhysical,settlement.getAmount(),settlement.getMerchantCode());
                return retCardElectronicPosCashier.getPosCashierData();
            }else {
                throw new CheckException(ResultTypeEnum.CARD_NULL);
            }
        }
        else if (!StringUtils.isEmpty(settlement.getUserFlagCode())){
            String userFlagCode = settlement.getUserFlagCode();
            String authOpenid = UserCodePreSubUtil.userCodePreSubStr(userFlagCode);
            String realOpenId = UserCodeAuthUtil.getRealOpenId(authOpenid);
            VipUser vipUser = msPrimeClient.queryByOpenId(realOpenId).getData();
            RetCardElectronicPosCashier retCardElectronicPosCashier = msPrimeService.userAllCardElectronicPosCashier(vipUser,settlement.getAmount());
            return retCardElectronicPosCashier.getPosCashierData();
        }else if (!StringUtils.isEmpty(settlement.getTel())) {
            if (StringUtils.isEmpty(settlement.getAuthCode())) {
                throw new UserDefinedException(ResultTypeEnum.AUTH_CODE_ERROR);
            }
            Cache cacheInfo = CacheManager.getCacheInfo(settlement.getTel() + "&authCode");
            Cache cache = (Cache) cacheInfo.getValue();
            String cacheAuthCode = (String) cache.getValue();
            if (!cacheAuthCode.equals(settlement.getAuthCode())) {
                throw new UserDefinedException(ResultTypeEnum.AUTH_CODE_ERROR);
            }
            VipUser vipUser = msPrimeClient.queryUserByPhone(settlement.getTel()).getData();
            RetCardElectronicPosCashier retCardElectronicPosCashier = msPrimeService.userAllCardElectronicPosCashier(vipUser,settlement.getAmount());
            return retCardElectronicPosCashier.getPosCashierData();
        } else {
            throw new CheckException(ResultTypeEnum.USER_NULL);
        }
    }


    /**
     * pos端收银 微信,支付宝支付成功  数据记录接口(会员收银 组合支付)
     *
     * @param posPayTraceData
     * @return
     */
    @PostMapping("/settlementSuccess")
    public String settlementSuccess(@RequestBody PosPayTraceData posPayTraceData) {
        logger.info("获取到的订单号为:"+posPayTraceData.getOrderCode());
        Long userId;
        if (!StringUtils.isEmpty(posPayTraceData.getUserFlagCode())){
            String userFlagCode = posPayTraceData.getUserFlagCode();
            String authOpenid = UserCodePreSubUtil.userCodePreSubStr(userFlagCode);
            String realOpenId = UserCodeAuthUtil.getRealOpenId(authOpenid);
            VipUser vipUser = msPrimeClient.queryByOpenId(realOpenId).getData();
            userId = vipUser.getId();
            posPayTraceData.setUserId(userId);
        }else if (!StringUtils.isEmpty(posPayTraceData.getTel())){
            VipUser vipUser = msPrimeClient.queryUserByPhone(posPayTraceData.getTel()).getData();
            userId = vipUser.getId();
            posPayTraceData.setUserId(userId);
        }else if (!StringUtils.isEmpty(posPayTraceData.getIcCardId())){
            //新卡
            CardPhysical cardPhysical = msPrimeService.checkGetCardElectronic(posPayTraceData.getIcCardId());
            if (cardPhysical!=null){
                Long cardElectronicUserId = cardPhysical.getUserId();
                posPayTraceData.setUserId(cardElectronicUserId==null ? -1L : cardElectronicUserId);
                return cardOrderPayTraceClientService.createMisSuccessTrace(posPayTraceData).getData();
            }else {
                throw new CheckException(ResultTypeEnum.CARD_NULL);
            }
        }else if (!StringUtils.isEmpty(posPayTraceData.getMagCard())){
            //新卡
            CardPhysical cardPhysical = msPrimeService.checkGetCardElectronic(posPayTraceData.getMagCard());
            if (cardPhysical!=null){
                Long cardElectronicUserId = cardPhysical.getUserId();
                posPayTraceData.setUserId(cardElectronicUserId==null ? -1L : cardElectronicUserId);
                return cardOrderPayTraceClientService.createMisSuccessTrace(posPayTraceData).getData();
            }else {
                throw new CheckException(ResultTypeEnum.CARD_NULL);
            }
        }else {
            userId = -1L;
            posPayTraceData.setUserId(userId);
        }
        return cardOrderPayTraceClientService.createMisSuccessTrace(posPayTraceData).getData();
    }


    /**
     * pos端收银  通过选定的卡券  直接支付  返回 核算过后的金额
     *
     * @param settlementCard
     * @return
     */
    @PostMapping("/settlementCardEnd")
    public SettlementMoneyData settlementCardEnd(@RequestBody SettlementCard settlementCard) throws InterruptedException {
        logger.info(settlementCard.getOrderCode()+"订单支付处理时间开始:"+ DateStrUtil.nowDateStr());
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(settlementCard.getOrderCode()).getData();

        boolean flag = cardOrdersService.checkOrderPaidMoney(cardOrdersVO,settlementCard.getAmount());
        if (!flag) {
            throw new CheckException(ResultTypeEnum.ORDER_AMOUNT_MEET);
        }
        //实体卡支付
        CardPhysical cardPhysical = null;
        RetAccountPayData retAccountPayData = null;
        if (!StringUtils.isEmpty(settlementCard.getIcCardId())){
            cardPhysical = msPrimeService.checkGetCardElectronic(settlementCard.getIcCardId());
            if (cardPhysical!=null){
                String limitPayType = cardOrdersVO.getLimitPayType();
                if (LimitPayTypeConstant.CARD_PHYSICAL_FREE.equals(limitPayType)){
                    if (!"free".equals(cardPhysical.getType())){
                        throw new CheckException(ResultTypeEnum.LIMIT_PAY_TYPE_ERROR);
                    }
                }else if (LimitPayTypeConstant.CARD_PHYSICAL_NORMAL.equals(limitPayType)){
                    if (!"sell".equals(cardPhysical.getType())){
                        throw new CheckException(ResultTypeEnum.LIMIT_PAY_TYPE_ERROR);
                    }
                }

                if (StringUtils.isEmpty(cardPhysical.getFaceValue()) || Integer.parseInt(cardPhysical.getFaceValue())<=0){
                    throw new CheckException(ResultTypeEnum.CARD_MONEY_ZERO);
                }
                //实体卡直接核销扣钱
                RetSettlement retSettlement = msPrimeService.settlementCardElectronic(cardPhysical, settlementCard.getAmount(), settlementCard.getOrderCode(),cardPhysical.getUserId(),settlementCard.getTerId());
                logger.info(settlementCard.getOrderCode()+"订单支付处理时间结束:"+ DateStrUtil.nowDateStr());
                return retSettlement.getSettlementMoneyData();
            }else {
                throw new CheckException(ResultTypeEnum.CARD_NULL);
            }
        }else if (!StringUtils.isEmpty(settlementCard.getMagCard())){
            cardPhysical = msPrimeService.checkGetCardElectronic(settlementCard.getMagCard());
            if (cardPhysical!=null){
                //电子卡直接核销扣钱
                RetSettlement retSettlement = msPrimeService.settlementCardElectronic(cardPhysical, settlementCard.getAmount(), settlementCard.getOrderCode(),cardPhysical.getUserId(),settlementCard.getTerId());
                return retSettlement.getSettlementMoneyData();
            }else {
                throw new CheckException(ResultTypeEnum.CARD_NULL);
            }
        }
        else if (!StringUtils.isEmpty(settlementCard.getUserFlagCode())){
            String userFlagCode = settlementCard.getUserFlagCode();
            String authOpenid = UserCodePreSubUtil.userCodePreSubStr(userFlagCode);
            String realOpenId = UserCodeAuthUtil.getRealOpenId(authOpenid);
            VipUser vipUser = msPrimeClient.queryByOpenId(realOpenId).getData();
            retAccountPayData = msPrimeService.settlementUserCodeCardMoneyEnd(authOpenid,vipUser,settlementCard.getAmount(),settlementCard.getOrderCode(),settlementCard.getMerchantCode());
        }else if (!StringUtils.isEmpty(settlementCard.getTel())) {
            VipUser vipUser = msPrimeClient.queryUserByPhone(settlementCard.getTel()).getData();
            retAccountPayData = msPrimeService.settlementUserTelCardMoneyEnd(vipUser,settlementCard.getAmount(),settlementCard.getOrderCode(),settlementCard.getMerchantCode());
        } else {
            throw new CheckException(ResultTypeEnum.USER_NULL);
        }
        SettlementMoneyData settlementMoneyData = new SettlementMoneyData();
        settlementMoneyData.setAmount(retAccountPayData.getAmount());
        settlementMoneyData.setIsToPay(retAccountPayData.getAmount() > 0);
        settlementMoneyData.setOrderCode(retAccountPayData.getOrderCode());
        return settlementMoneyData;
    }

    /**
     * 校验支付码 是否为返利卡支付码 或 电子储值卡支付
     *
     * @param qrCode
     * @return
     */
    @GetMapping("/checkQrCodePayType")
    public QrCodeCheckResult checkQrCodePayType(@RequestParam("qrCode") String qrCode) {
        logger.info("收银台上送二维码:"+qrCode);
        QrCodeCheckResult qrCodeCheckResult = new QrCodeCheckResult();
        if (qrCode.contains("ELECARD")){
            String cardNoAuth = UserCodeAuthUtil.getRealOpenId(qrCode);
            String relCardNo = UserCodePreSubUtil.userCodePreSubStr(cardNoAuth);
            CardElectronic cardElectronic = msPrimeClient.queryCardElectronicByCardNo(relCardNo).getData();
            if (cardElectronic==null){
                PartyCardElectronic partyCardElectronic = msPrimeClient.queryPartyCardElectronicByCardNo(relCardNo).getData();
                if (partyCardElectronic==null) {
                    throw new CheckException(ResultTypeEnum.QR_AUTH_CODE_ERROR);
                }else {
                    if (CardElectronicEnum.OFFLINE.getValue().equals(partyCardElectronic.getCardType()) ||
                            CardElectronicEnum.REBATE_PASSWORD_CARD.getValue().equals(partyCardElectronic.getCardType())){
                        qrCodeCheckResult.setCodeType("free");
                        qrCodeCheckResult.setDesc("返利卡二维码");
                    }else {
                        qrCodeCheckResult.setCodeType("normal");
                        qrCodeCheckResult.setDesc("电子储值卡二维码");
                    }
                    return qrCodeCheckResult;
                }
            }else {
                if (CardElectronicEnum.OFFLINE.getValue().equals(cardElectronic.getCardType()) ||
                        CardElectronicEnum.REBATE_PASSWORD_CARD.getValue().equals(cardElectronic.getCardType())) {
                    qrCodeCheckResult.setCodeType("free");
                    qrCodeCheckResult.setDesc("返利卡二维码");
                } else {
                    qrCodeCheckResult.setCodeType("normal");
                    qrCodeCheckResult.setDesc("电子储值卡二维码");
                }
            }
        }else {
            if (qrCode.contains("FREE")){
                qrCodeCheckResult.setCodeType("free");
                qrCodeCheckResult.setDesc("返利卡总额二维码");
            }else {
                qrCodeCheckResult.setCodeType("normal");
                qrCodeCheckResult.setDesc("电子储值卡总额二维码");
            }
        }
        return qrCodeCheckResult;
    }

}
