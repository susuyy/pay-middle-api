package com.ht.feignapi.tonglian.card.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.result.Result;

import com.ht.feignapi.tonglian.admin.entity.MerchantUserCardVo;
import com.ht.feignapi.tonglian.admin.entity.OrdersVo;
import com.ht.feignapi.tonglian.admin.entity.UserFreeCard;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardLimitsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardOrderClientService;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.config.CardConstant;
import com.ht.feignapi.tonglian.config.CardLimitType;
import com.ht.feignapi.tonglian.config.MerchantCardsType;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.utils.TimeUtil;
import com.ht.feignapi.util.DateStrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/14 17:07
 */
@Service
public class CardMapMerchantCardService {

    @Autowired
    private CardCardsClientService cardsClientService;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(CardMapMerchantCardService.class);

    @Autowired
    private CardCardsService cardCardsService;

    @Autowired
    private CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    private CardOrderClientService orderClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private CardLimitsClientService cardLimitsClientService;

    @Autowired
    private MapMerchantPrimesClientService merchantPrimesClientService;

    @Autowired
    private InventoryClientService inventoryClientService;

    public List<Merchants> getCardMerchants(String merchantCode, String cardCode) {
        List<String> merchantsCodes = merchantCardClientService.getCardMerchantCodes(cardCode).getData();
        Result<Merchants> result = merchantsClientService.getMerchantByCode(merchantCode);
        Merchants merchants = result.getData();
        List<Merchants> merchantsList = new ArrayList<>();
        if (CardConstant.MERCHANT_TYPE_OBJECT.equals(merchants.getType())) {
            merchantsCodes.forEach(e -> {
                Result<Merchants> merchantsResult = merchantsClientService.getMerchantByCode(e);
                Merchants m = merchantsResult.getData();
                merchantsList.add(m);
            });
        } else {
            merchantsList.add(merchants);
        }
        return merchantsList;
    }

    public IPage<MerchantCardListVo> getMerchantCardListVos(String merchantCode, MerchantCardSearch merchantCardSearch, Merchants merchants, Long pageNo, Long pageSize) {
        IPage<MerchantCardListVo> page;
        if (CardConstant.MERCHANT_TYPE_OBJECT.equals(merchants.getType())) {
            logger.info("**********进入OBJECT分支，获取商户**********");
            List<Merchants> merchantsList = merchantsClientService.getSubMerchants(merchantCode).getData();
            logger.info("**************MerchantList***********"+JSON.toJSONString(merchantsList));
            List<String> merchantCodes = merchantsList.stream().map(Merchants::getMerchantCode).collect(Collectors.toList());
            logger.info("**************MerchantList***********"+JSON.toJSONString(merchantCodes));
            merchantCardSearch.setMerchantCodes(merchantCodes);
            page = merchantCardClientService.getObjectAndSonMerchantCards(merchantCardSearch, pageNo, pageSize).getData();
            logger.info("**************MerchantList***********"+JSON.toJSONString(page));
        } else {
            page = merchantCardClientService.getCardProductsByMerchantCode(merchantCode, merchantCardSearch, pageNo, pageSize).getData();
            logger.info("**************MerchantList***********"+JSON.toJSONString(page));
        }
        if (page!=null) {
            for (MerchantCardListVo merchantCardListVo : page.getRecords()) {
                Integer inventory = inventoryClientService.getInventory(merchantCardListVo.getMerchantCode(), merchantCardListVo.getCardCode()).getData();
                Assert.notNull(inventory, "获取库存出错");
                merchantCardListVo.setInventory(inventory);
            }
        }else {
            page=new Page<MerchantCardListVo>();
        }
        return page;
    }

    public String getShowTimeScope(CardMapMerchantCards cardMapMerchantCards) throws ParseException {
        Result<List<CardLimits>> result = cardLimitsClientService.queryCardGetLimit(cardMapMerchantCards.getCardCode(), cardMapMerchantCards.getBatchCode());
        List<CardLimits> getCardLimitList = result.getData();
        Activity activity = new Activity();
        for (CardLimits cardLimits : getCardLimitList) {
            String type = cardLimits.getType();
            String limitKey = cardLimits.getLimitKey();
            if (CardLimitType.GET_DURATION_LIMIT.equals(type)) {
                activity.setGetTimeScope(limitKey);
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (MerchantCardsType.SELL.equals(cardMapMerchantCards.getType())) {
            Date onSaleDate = cardMapMerchantCards.getOnSaleDate();
            String dateStr = simpleDateFormat.format(onSaleDate);
            Date haltSaleDate = cardMapMerchantCards.getHaltSaleDate();
            if (haltSaleDate == null) {
                dateStr = dateStr + " 至 永久";
            } else {
                String haltTime = simpleDateFormat.format(haltSaleDate);
                dateStr = dateStr + " 至 " + haltTime;
            }
            return dateStr;
        }
        if (MerchantCardsType.FREE.equals(cardMapMerchantCards.getType())) {
            String activityTimeScope = activity.getGetTimeScope();
            String[] split = activityTimeScope.split("~");
            Date parse = simpleDateFormat.parse(split[0]);
            String format = simpleDateFormat.format(parse);
            Date parseEnd = simpleDateFormat.parse(split[1]);
            String formatEnd = simpleDateFormat.format(parseEnd);
            String dateStr = format + " 至 " + formatEnd;
            return dateStr;
        }
        String format = simpleDateFormat.format(new Date());
        return format + " 至 永久";
    }

    public String packageValidTimeStr(CardCards cardCards) {
        if ("beginToEnd".equals(cardCards.getValidityType())) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return "有效期：" + simpleDateFormat.format(cardCards.getValidFrom()).substring(0, 16) + " ~ " + simpleDateFormat.format(cardCards.getValidTo()).substring(0, 16);
        } else if ("validDuration".equals(cardCards.getValidityType())) {
            return "领券后" + cardCards.getValidGapAfterApplied() / 24 + "天生效，有效" + cardCards.getPeriodOfValidity() / 24 + "天";
        }
        return "无限制";
    }

    public CardCards queryByCardCode(String cardCode, String merchantCode) {
        CardCards cardCards = cardCardsService.queryByCardCode(cardCode,merchantCode);
        return cardCards;
    }

    public Page<MerchantUserCardVo> getUserCardList(String merchantCode, Long pageNo, Long pageSize) {
        Page<MerchantUserCardVo> result = merchantCardClientService.getUserCardList(merchantCode, pageNo, pageSize).getData();
        if (result == null || CollectionUtils.isEmpty(result.getRecords())) {
            return new Page<>(pageNo, pageSize);
        }
        result.getRecords().forEach(e -> {
            UserUsers user = authClientService.getUserByIdTL(e.getUserId().toString()).getData();
            e.setNickName(user.getNickName());
            e.setRealName(user.getRealName());
            e.setTel(user.getTel());
        });
        return result;
    }

    public IPage<OrdersVo> getRechargeOrders(String merchantCode, String orderType, Long pageNo, Long pageSize) {
        IPage<OrdersVo> result = orderClientService.getRechargeOrders(merchantCode, orderType, pageSize, pageNo).getData();
        if (result == null || CollectionUtils.isEmpty(result.getRecords())) {
            return new Page<>(pageNo, pageSize);
        }
        result.getRecords().forEach(e -> {
            Result<UserUsers> user = authClientService.getUserByIdTL(e.getUserId().toString());
            if (user != null && user.getData() != null) {
                e.setNickName(user.getData().getNickName());
                e.setGender(user.getData().getGender());
                e.setTel(user.getData().getTel());
                Result<MrcMapMerchantPrimes> mrcMapMerchantPrimesResult = merchantPrimesClientService.queryByUserIdAndMerchantCode(user.getData().getId(), merchantCode);
                if (mrcMapMerchantPrimesResult != null && mrcMapMerchantPrimesResult.getData() != null) {
                    e.setMemberType(mrcMapMerchantPrimesResult.getData().getType());
                }
            }
            Result<UserUsers> salesclerk = authClientService.getUserByIdTL(e.getSaleId().toString());
            if (salesclerk != null && salesclerk.getData() != null) {
                e.setAdminName(salesclerk.getData().getNickName());
            }
            ;
            Result<Merchants> merchants = merchantsClientService.getMerchantByCode(e.getMerchantCode());
            if (merchants != null && merchants.getData() != null) {
                e.setOriginFrom(merchants.getData().getMerchantName());
            }

        });
        return result;
    }

    public IPage<OrdersVo> getOrderList(String merchantCode, Long pageNo, Long pageSize) {
        Result<Page<OrdersVo>> result = orderClientService.getOrderList(merchantCode, pageNo, pageSize);
        if (result == null || result.getData() == null || CollectionUtils.isEmpty(result.getData().getRecords())) {
            return new Page<>(pageNo, pageSize);
        } else {
            result.getData().getRecords().forEach(e -> {
                if (e.getSaleId() != null) {
                    Result<UserUsers> resultUser = authClientService.getUserByIdTL(e.getSaleId().toString());
                    if (resultUser != null) {
                        UserUsers user = resultUser.getData();
                        e.setNickName(user.getNickName());
                    }
                }
                Merchants merchants = merchantsClientService.getMerchantByCode(e.getMerchantCode()).getData();
                e.setOriginFrom(merchants.getMerchantName());
            });
            return result.getData();
        }
    }

    /**
     * 免费领取的过期卡券 不展示
     *
     * @param merchantCardsVO
     * @return
     */
    public Boolean checkFreeTypeInvalid(MerchantCardsVO merchantCardsVO) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if ("free".equals(merchantCardsVO.getMerchantCardType())) {
            String time = "";
            logger.info("**********************"+ JSON.toJSONString(merchantCardsVO) +"*****************");
            List<CardLimits> limits = cardLimitsClientService.queryCardGetLimit(merchantCardsVO.getCardCode(), merchantCardsVO.getBatchCode()).getData();
            for (CardLimits limit : limits) {
                if ("GET-DURATION-LIMIT".equals(limit.getType())) {
                    time = limit.getLimitKey();
                }
            }
            String[] split = time.split("~");
            String endTime = split[1].trim();
            Date date = simpleDateFormat.parse(endTime);
            long dateTime = date.getTime();
            long nowTime = System.currentTimeMillis();
            if (nowTime > dateTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取card展示信息
     * @param cardMapMerchantCards
     * @return
     */
    public CardCards getShowOtherData(CardMapMerchantCards cardMapMerchantCards){
        Result<CardCards> result = merchantCardClientService.getShowOtherData(cardMapMerchantCards);
        Assert.notNull(result,"获取showOtherData接口出错");
        Result<Integer> cardInventory = inventoryClientService.getInventory(cardMapMerchantCards.getMerchantCode(),cardMapMerchantCards.getCardCode());
        Assert.notNull(cardInventory,"获取getInventory接口出错");
        result.getData().setInventory(cardInventory.getData());
        return result.getData();
    }

}
