package com.ht.feignapi.mall.service;

import com.alibaba.excel.event.Order;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.clientservice.MapMerchantPointsClientService;
import com.ht.feignapi.mall.clientservice.PrimePointsTraceClientService;
import com.ht.feignapi.mall.constant.CardUserMallConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardLimitsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.card.service.CardLimitsService;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CouponService {

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private CardLimitsService cardLimitsService;

    @Autowired
    private MapMerchantPrimesClientService mapMerchantPrimesClientService;

    @Autowired
    private MapMerchantPointsClientService mapMerchantPointsClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private PrimePointsTraceClientService primePointsTraceClientService;


    /**
     * 计算优惠金额
     *
     * @param couponFlag
     * @param orderOrders
     * @return
     */
    public RetStatementDiscountData statementDiscount(List<String> couponFlag, OrderOrders orderOrders) {
        MoreMerchantCouponData moreMerchantCouponData = checkMoreMerchantCoupon(couponFlag);

        if (moreMerchantCouponData.getFlag()) {
            RetStatementDiscountData retStatementDiscountData = new RetStatementDiscountData();
            retStatementDiscountData.setUseFlag(false);
            retStatementDiscountData.setMessage("同一商户的优惠券只能使用一张,请重新选择");
            retStatementDiscountData.setDiscount(0);
            return retStatementDiscountData;
        }

        Integer discount = 0;
        List<CardMapUserCards> cardMapUserCardsList = moreMerchantCouponData.getCardMapUserCardsList();
        //优惠数据封装
        ArrayList<UserCardDiscountData> userCardDiscountList = new ArrayList<>();

        for (CardMapUserCards cardMapUserCards : cardMapUserCardsList) {
            //查询detail,确定coupon作用在哪几个明细上
            List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(orderOrders.getOrderCode()).getData();
            Integer detailTotalAmount = 0;
            List<OrderOrderDetails> useDetailList = new ArrayList<>();
            for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
                if (orderOrderDetails.getMerchantCode().equals(cardMapUserCards.getMerchantCode())) {
                    detailTotalAmount = detailTotalAmount + orderOrderDetails.getAmount();
                    useDetailList.add(orderOrderDetails);
                }
            }

            //校验使用规则
            CardCards cardCards = cardCardsClientService.getCardByCardCode(cardMapUserCards.getCardCode()).getData();
            if ("coupon".equals(cardCards.getType())) {
                boolean useLimit = cardLimitsService.checkCardUseLimit(cardCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getUserId(), cardMapUserCards.getBatchCode());
                if (!useLimit) {
                    RetStatementDiscountData retStatementDiscountData = new RetStatementDiscountData();
                    retStatementDiscountData.setUseFlag(false);
                    retStatementDiscountData.setMessage("选用的卡券存在限制");
                    retStatementDiscountData.setDiscount(0);
                    return retStatementDiscountData;
                }
            } else {
                boolean useLimit = cardLimitsService.checkCardUseLimit(cardCards.getCardCode(), cardMapUserCards.getMerchantCode(), cardMapUserCards.getUserId(), detailTotalAmount, cardMapUserCards.getBatchCode());
                if (!useLimit) {
                    RetStatementDiscountData retStatementDiscountData = new RetStatementDiscountData();
                    retStatementDiscountData.setUseFlag(false);
                    retStatementDiscountData.setMessage("选用的卡券存在限制");
                    retStatementDiscountData.setDiscount(0);
                    return retStatementDiscountData;
                }
            }
            Integer couponDiscount = 0;
            if ("discount".equals(cardCards.getType())) {
                couponDiscount = userUsersService.discountTypeMoney(cardMapUserCards.getFaceValue(), detailTotalAmount);
            } else {
                couponDiscount = Integer.parseInt(cardMapUserCards.getFaceValue());
            }
            Integer oneDetailDiscount = couponDiscount / useDetailList.size();
            discount = discount + couponDiscount;
            //修改明细折扣
            mallOrderClientService.updateDetailDiscount(useDetailList, oneDetailDiscount);

            UserCardDiscountData userCardDiscountData = new UserCardDiscountData();
            userCardDiscountData.setDiscount(couponDiscount);
            userCardDiscountData.setCardMapUserCards(cardMapUserCards);
            userCardDiscountList.add(userCardDiscountData);
        }

        RetStatementDiscountData retStatementDiscountData = new RetStatementDiscountData();
        retStatementDiscountData.setUseFlag(true);
        retStatementDiscountData.setMessage("使用卡券下单成功");
        retStatementDiscountData.setDiscount(discount);
        retStatementDiscountData.setCardMapUserCardsList(cardMapUserCardsList);
        retStatementDiscountData.setUserCardDiscountDataList(userCardDiscountList);
        return retStatementDiscountData;
    }

    /**
     * 校验是否一个商户 使用了 多个优惠券 同时校验规则
     *
     * @param couponFlag
     * @return
     */
    private MoreMerchantCouponData checkMoreMerchantCoupon(List<String> couponFlag) {
        List<CardMapUserCards> list = new ArrayList<>();
        Map map = new HashMap();
        for (String cardNo : couponFlag) {
            CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(cardNo).getData();
            boolean containsKey = map.containsKey(cardMapUserCards.getMerchantCode());
            if (containsKey) {
                MoreMerchantCouponData moreMerchantCouponData = new MoreMerchantCouponData();
                moreMerchantCouponData.setFlag(true);
                return moreMerchantCouponData;
            }
            map.put(cardMapUserCards.getMerchantCode(), cardMapUserCards);
            list.add(cardMapUserCards);
        }
        MoreMerchantCouponData moreMerchantCouponData = new MoreMerchantCouponData();
        moreMerchantCouponData.setFlag(false);
        moreMerchantCouponData.setCardMapUserCardsList(list);
        return moreMerchantCouponData;
    }

    /**
     * 校验使用的优惠券中是否存在被挂起
     *
     * @param cardNoList
     * @return
     */
    public boolean checkUsed(List<String> cardNoList) {
        for (String cardNo : cardNoList) {
            CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(cardNo).getData();
            if (CardUserMallConstant.MALL_USED.equals(cardMapUserCards.getState())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算积分抵扣 返回可优惠积分
     *
     * @param orderOrders
     * @param orderOrderDetailsList
     * @return
     */
    public ReturnPointsData statementPointsDiscount(OrderOrders orderOrders, List<OrderOrderDetails> orderOrderDetailsList) {
        String ordersMerchantCode = orderOrders.getMerchantCode();
        Merchants merchants = merchantsClientService.getMerchantByCode(ordersMerchantCode).getData();
        UserUsers userUsers = authClientService.getUserByIdTL(orderOrders.getUserId().toString()).getData();
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryMyMrcMapMerchantPrimes(userUsers.getId(), userUsers.getOpenId(), merchants.getBusinessSubjects()).getData();
        ReturnPointsData returnPointsData = new ReturnPointsData();
        if (mrcMapMerchantPrimes == null) {
            returnPointsData.setUsePoints(0);
            returnPointsData.setPrimesId(-1L);
            return returnPointsData;
        }

        Integer myPoints = 0;
        if (mrcMapMerchantPrimes.getPrimePoints() != null) {
            myPoints = mrcMapMerchantPrimes.getPrimePoints();
        }
        returnPointsData.setPrimesId(mrcMapMerchantPrimes.getId());

        Integer productionsTotalPoints = 0;
        Map<Long, Integer> productionPointsMap = new HashMap<>();
        for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
            MrcPrimeDiscountPoints primeDiscountPoints = mapMerchantPointsClientService.queryPrimeDiscountPoints(orderOrderDetails.getMerchantCode(), orderOrderDetails.getProductionCode()).getData();
            if (primeDiscountPoints != null) {
                int quantity = orderOrderDetails.getQuantity().intValue();
                Integer limitAmountPerOrder = primeDiscountPoints.getLimitAmountPerOrder();
                if (limitAmountPerOrder==null){
                    limitAmountPerOrder = 0;
                }
                int useQuantity = limitAmountPerOrder >= quantity ? quantity : limitAmountPerOrder;
                if (useQuantity>0) {
                    for (int i = 0; i < useQuantity; i++) {
                        Integer orderDetailPoints = primeDiscountPoints.getPoints();
                        productionsTotalPoints = productionsTotalPoints + orderDetailPoints;
                        if (productionsTotalPoints > myPoints) {
                            break;
                        } else {
                            Integer orgMapPoints = productionPointsMap.get(orderOrderDetails.getId());
                            if (orgMapPoints == null) {
                                productionPointsMap.put(orderOrderDetails.getId(), orderDetailPoints);
                            } else {
                                productionPointsMap.put(orderOrderDetails.getId(), orgMapPoints + orderDetailPoints);
                            }
                        }
                    }
                }
            }
        }
        if (myPoints >= productionsTotalPoints) {
            returnPointsData.setUsePoints(productionsTotalPoints);
            returnPointsData.setProductionPointsMap(productionPointsMap);
        } else {
            Integer usePoints = 0;
            Set<Long> keySet = productionPointsMap.keySet();
            for (Long ket : keySet) {
                Integer mapPoints = productionPointsMap.get(ket);
                usePoints=mapPoints+usePoints;
            }
            returnPointsData.setUsePoints(usePoints);
            returnPointsData.setProductionPointsMap(productionPointsMap);
        }
        return returnPointsData;
    }

    /**
     * 扣除用户积分
     *
     * @param id
     * @param usePoints
     */
    public void deductPointsById(String id, Integer usePoints) {
        mapMerchantPrimesClientService.deductPointsById(id, usePoints);
    }

    /**
     * 创建积分  流水
     *
     * @param merchantCode
     * @param userId
     * @param debit
     * @param credit
     * @param state
     * @param orderType
     * @param orderCode
     */
    public void saveTrace(String merchantCode, Long userId, Integer debit, Integer credit,
                          String state, String orderType, String orderCode) {
        MrcPrimePointsTrace mrcPrimePointsTrace = new MrcPrimePointsTrace();
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        mrcPrimePointsTrace.setMerchantCode(merchants.getBusinessSubjects());
        mrcPrimePointsTrace.setUserId(userId);
        UserUsers userUsers = authClientService.getUserByIdTL(userId.toString()).getData();
        mrcPrimePointsTrace.setOpenId(userUsers.getOpenId());
        mrcPrimePointsTrace.setDebit(debit);
        mrcPrimePointsTrace.setCredit(credit);
        mrcPrimePointsTrace.setState(state);
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryMyMrcMapMerchantPrimes(userUsers.getId(), userUsers.getOpenId(), merchants.getBusinessSubjects()).getData();
        mrcPrimePointsTrace.setType(mrcMapMerchantPrimes.getType());
        mrcPrimePointsTrace.setRefType(orderType);
        mrcPrimePointsTrace.setRefOrder(orderCode);
        mrcPrimePointsTrace.setCreateAt(new Date());
        mrcPrimePointsTrace.setUpdateAt(new Date());
        primePointsTraceClientService.save(mrcPrimePointsTrace);
    }


    /**
     * 支付页面展示积分数据
     * @param productPointsDataList
     * @param objectMerchantCode
     * @param openId
     * @return
     */
    public ShowProductPointsData showPayPointsData(List<ProductPointsData> productPointsDataList,String objectMerchantCode,String openId) {
        UserUsers userUsers = authClientService.queryByOpenid(openId).getData();
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryMyMrcMapMerchantPrimes(userUsers.getId(), userUsers.getOpenId(), objectMerchantCode).getData();
        ShowProductPointsData showProductPointsData = new ShowProductPointsData();
        if (mrcMapMerchantPrimes == null) {
            showProductPointsData.setUsePoints(0);
            showProductPointsData.setPrimesId(-1L);
            return showProductPointsData;
        }

        int myPoints = 0;
        if (mrcMapMerchantPrimes.getPrimePoints() != null) {
            myPoints = mrcMapMerchantPrimes.getPrimePoints();
        }
        showProductPointsData.setPrimesId(mrcMapMerchantPrimes.getId());
        showProductPointsData.setUserPoints(myPoints);
        int productionsTotalPoints = 0;
        Map<String, Integer> productionPointsMap = new HashMap<>();
        for (ProductPointsData productPointsData : productPointsDataList) {
            MrcPrimeDiscountPoints primeDiscountPoints = mapMerchantPointsClientService.queryPrimeDiscountPoints(productPointsData.getStoreMerchantCode(), productPointsData.getProductionCode()).getData();
            if (primeDiscountPoints != null) {
                int quantity = productPointsData.getQuantity();
                Integer limitAmountPerOrder = primeDiscountPoints.getLimitAmountPerOrder();
                if (limitAmountPerOrder==null){
                    limitAmountPerOrder = 0;
                }
                int useQuantity = limitAmountPerOrder >= quantity ? quantity : limitAmountPerOrder;
                if (useQuantity>0) {
                    for (int i = 0; i < useQuantity; i++) {
                        Integer orderDetailPoints = primeDiscountPoints.getPoints();
                        productionsTotalPoints = productionsTotalPoints + orderDetailPoints;
                        if (productionsTotalPoints > myPoints) {
                            break;
                        } else {
                            Integer orgMapPoints = productionPointsMap.get(productPointsData.getProductionCode());
                            if (orgMapPoints == null) {
                                productionPointsMap.put(productPointsData.getProductionCode(), orderDetailPoints);
                            } else {
                                productionPointsMap.put(productPointsData.getProductionCode(), orgMapPoints + orderDetailPoints);
                            }
                        }
                    }
                }
            }
        }
        if (myPoints >= productionsTotalPoints) {
            showProductPointsData.setUsePoints(productionsTotalPoints);
            showProductPointsData.setProductionPointsMap(productionPointsMap);
        } else {
            Integer usePoints = 0;
            Set<String> keySet = productionPointsMap.keySet();
            for (String ket : keySet) {
                Integer mapPoints = productionPointsMap.get(ket);
                usePoints=mapPoints+usePoints;
            }
            showProductPointsData.setUsePoints(usePoints);
            showProductPointsData.setProductionPointsMap(productionPointsMap);
        }
        return showProductPointsData;
    }
}
