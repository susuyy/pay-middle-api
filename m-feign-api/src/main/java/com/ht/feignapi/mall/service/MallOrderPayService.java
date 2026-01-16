package com.ht.feignapi.mall.service;

import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.*;
import com.ht.feignapi.mall.constant.*;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.utils.AliMsgSendUtil;
import com.ht.feignapi.tonglian.utils.QrCodeUtils;
import com.ht.feignapi.util.DateStrUtil;
import com.ht.feignapi.util.OrderCodeFactory;
import com.ht.feignapi.util.OrderEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MallOrderPayService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private OrderProductionsClientService orderProductionsClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private ShoppingCartClientService shoppingCartClientService;

    @Autowired
    private OrderWayBillsClientService orderWayBillsClientService;

    @Autowired
    private OrderCategoriesClientService orderCategorysClientService;

    @Autowired
    private CardMapMerchantCardClientService cardMapMerchantCardClientService;

    @Autowired
    private CardCardsClientService cardCardsClientService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryClientService inventoryClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MallAppShowClientService mallAppShowClientService;

    @Autowired
    private UserUsersService userUsersService;

    @Autowired
    private OrderCategorysServeice orderCategorysServeice;

    /**
     * 处理商城购物 支付成功后的业务逻辑
     *
     * @param paySuccess
     */
    public void mallBuySuccess(PaySuccess paySuccess) {
        //修改订单状态
        mallOrderClientService.updateMallOrderAllPaid(paySuccess);

        OrderOrders orderOrders = mallOrderClientService.queryOrderByOrderCode(paySuccess.getOrderCode()).getData();

        // 查询是否存在使用积分 积分扣除
        OrderPayTrace pointsPayTrace = mallOrderClientService.queryOrderTraceSource(paySuccess.getOrderCode(), "prime_points").getData();
        if (pointsPayTrace != null) {
            //扣除用户积分
            couponService.deductPointsById(pointsPayTrace.getSourceId(), pointsPayTrace.getAmount());
            //记录积分流水
            couponService.saveTrace(orderOrders.getMerchantCode(),
                    orderOrders.getUserId(),
                    pointsPayTrace.getAmount(),
                    0,
                    "used",
                    orderOrders.getType(),
                    orderOrders.getOrderCode());
        }


        //查询订单明细
        List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(paySuccess.getOrderCode()).getData();

        for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
            int quantityInt = orderOrderDetails.getQuantity().intValue();
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(orderOrderDetails.getProductionCategoryCode(), orderOrderDetails.getMerchantCode()).getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                for (int i = 0; i < quantityInt; i++) {
                    CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(orderOrderDetails.getProductionCode(),
                            orderOrderDetails.getMerchantCode(),
                            MerchantCardConstant.MALL_SELL_TYPE).getData();
                    CardMapUserCards cardMapUserCards = new CardMapUserCards();
                    cardMapUserCards.setUserId(Long.parseLong(paySuccess.getUserId()));
                    cardMapUserCards.setMerchantCode(cardMapMerchantCards.getMerchantCode());
                    cardMapUserCards.setCardCode(cardMapMerchantCards.getCardCode());
                    cardMapUserCards.setCardNo(IdWorker.getIdStr());
                    cardMapUserCards.setCardName(cardMapMerchantCards.getCardName());
                    cardMapUserCards.setCategoryCode(cardMapMerchantCards.getCategoryCode());
                    cardMapUserCards.setCategoryName(cardMapMerchantCards.getCategoryName());
                    cardMapUserCards.setBatchCode(orderOrderDetails.getActivityCode());

                    //次卡 次数以购买数为主
                    cardMapUserCards.setFaceValue(cardMapMerchantCards.getCardFaceValue() + "");

                    cardMapUserCards.setRefSourceKey(orderOrderDetails.getId().toString());
                    cardMapUserCards.setState(CardUserMallConstant.MALL_BUY_UN_USE_STATE);
                    cardMapUserCards.setType(CardUserMallConstant.MALL_BUY_TYPE);
                    cardMapUserCards.setCardType(cardMapMerchantCards.getCardType());
                    cardMapUserCards.setCreateAt(new Date());
                    cardMapUserCards.setUpdateAt(new Date());
                    cardMapUserClientService.createMallUserProductionsAndTrace(cardMapUserCards);
                }
            } else {
                //todo 实体线下商品
                logger.info("实体商品业务处理 实体线下商品业务逻辑");
            }

            //库存扣减
            Map<String, Integer> map = new HashMap<>();
            map.put("amount", quantityInt);
            inventoryClientService.subtractInventory(orderOrderDetails.getMerchantCode(), orderOrderDetails.getProductionCode(), map);
            //清除购物车数据
            if (!StringUtils.isEmpty(orderOrderDetails.getShoppingCartOrderCode())) {
                shoppingCartClientService.paySuccessDelByOrderCode(orderOrderDetails.getShoppingCartOrderCode());
            }

            List<OrderPayTrace> orderPayTraces = mallOrderClientService.getTraceByOrderCode(paySuccess.getOrderCode()).getData();
            for (OrderPayTrace orderPayTrace : orderPayTraces) {
                if ("card_cards".equals(orderPayTrace.getSource())) {
                    cardMapUserClientService.mallUpdateUserCardsState(orderPayTrace.getPayCode(), CardUserMallConstant.MALL_USED);
                }
            }
            try {
                //暂留阿甘电话
                logger.info("********************发送退款短信******************");
                AliMsgSendUtil.sendNotifyMsg("15108951532","SMS_212275616");
            }catch (ClientException e){
                logger.error("发送短信失败: " + e.toString());
            }
        }
    }


    /**
     * 商品券码 展示 数据
     *
     * @param orderCode
     * @param id
     * @return
     */
    public RetOrderQrCodeDetailData orderQrCodeDetail(String orderCode, String id) {
        OrderOrderDetails orderOrderDetails = mallOrderClientService.queryDetailById(id).getData();

        RetOrderQrCodeDetailData retOrderQrCodeDetailData = new RetOrderQrCodeDetailData();

        List<QrCodeDetailData> qrCodeDetailDataList = new ArrayList<>();
        List<CardMapUserCards> cardMapUserCardsList = cardMapUserClientService.mallQueryListByRefSourceKey(orderOrderDetails.getId().toString()).getData();

        //券码数据
        CardCards cardCards = cardCardsClientService.getCardByCardCode(orderOrderDetails.getProductionCode()).getData();
        for (CardMapUserCards cardMapUserCards : cardMapUserCardsList) {
            String showDateStr = getShowValidityDateStr(cardCards, cardMapUserCards.getCreateAt());
            String romCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
            stringRedisTemplate.opsForValue().set(cardMapUserCards.getCardNo(), romCode, 10, TimeUnit.MINUTES);
            String qrCode = getQrCode(cardMapUserCards.getCardNo() + romCode, 300, 300);
            QrCodeDetailData qrCodeDetailData = new QrCodeDetailData();
            qrCodeDetailData.setCardNo(cardMapUserCards.getCardNo());
            qrCodeDetailData.setQrCode(qrCode);
            qrCodeDetailData.setShowEndDate(showDateStr);
            qrCodeDetailData.setQuantity(Integer.parseInt(cardMapUserCards.getFaceValue()));
            qrCodeDetailData.setProductionName(cardMapUserCards.getCardName());
            qrCodeDetailDataList.add(qrCodeDetailData);
        }
        retOrderQrCodeDetailData.setQrCodeDetailDataList(qrCodeDetailDataList);
        //商户信息
        Merchants merchants = merchantsClientService.getMerchantByCode(orderOrderDetails.getMerchantCode()).getData();
        MerchantsDetailData merchantsDetailData = new MerchantsDetailData();
        merchantsDetailData.setMerchants(merchants);
        merchantsDetailData.setProductionsName(cardMapUserCardsList.get(0).getCardName());
        merchantsDetailData.setQuantity(orderOrderDetails.getQuantity().intValue());
        merchantsDetailData.setAmount(orderOrderDetails.getAmount() - orderOrderDetails.getDiscount());
        retOrderQrCodeDetailData.setMerchantsDetailData(merchantsDetailData);
        //订单数据信息
        OrderQrShowData orderQrShowData = new OrderQrShowData();
        orderQrShowData.setOrderCode(orderCode);
        UserUsers userUsers = authClientService.getUserByIdTL(orderOrderDetails.getUserId().toString()).getData();
        if (StringUtils.isEmpty(userUsers.getTel())) {
            orderQrShowData.setTel("无手机号");
        } else {
            orderQrShowData.setTel(userUsers.getTel());
        }
        orderQrShowData.setPayTime(orderOrderDetails.getUpdateAt());
        orderQrShowData.setQuantity(orderOrderDetails.getQuantity().intValue());
        orderQrShowData.setAmount(orderOrderDetails.getAmount());
        orderQrShowData.setDiscount(orderOrderDetails.getDiscount());
        orderQrShowData.setActualPayment(orderOrderDetails.getAmount() - orderOrderDetails.getDiscount());
        retOrderQrCodeDetailData.setOrderQrShowData(orderQrShowData);
        //商品数据
        MallProductions mallProductions = new MallProductions();
        List<MallProductions> mallProductionsList = mallAppShowClientService.queryByCodeAndMerchant(orderOrderDetails.getProductionCode(), orderOrderDetails.getMerchantCode()).getData();
        if (mallProductionsList != null && mallProductionsList.size() > 0) {
            mallProductions = mallProductionsList.get(0);
        }
        retOrderQrCodeDetailData.setMallProductions(mallProductions);

        return retOrderQrCodeDetailData;
    }

    /**
     * 获取二维码
     *
     * @param content
     * @param height
     * @param width
     * @return
     */
    private String getQrCode(String content, int height, int width) {
        String code = QrCodeUtils.creatRrCode(content, width, height);
        String replace = code.replace("\r\n", "");
        String replaceOne = replace.replace("\n", "");
        String replaceTwo = replaceOne.replace("\r", "");
        return replaceTwo;
    }

    /**
     * 获取商品券码页面展示的过期时间
     *
     * @param cardCards
     * @return
     */
    private String getShowValidityDateStr(CardCards cardCards, Date createAt) {
        String validityType = cardCards.getValidityType();
        if ("validDuration".equals(validityType)) {
            int validGapAfterAppliedTime = cardCards.getValidGapAfterApplied() / 24;
            int periodOfValidityTime = cardCards.getPeriodOfValidity() / 24;
            Calendar cal = Calendar.getInstance();
            cal.setTime(createAt);
            cal.add(cal.DATE, periodOfValidityTime);
            cal.add(cal.DATE, validGapAfterAppliedTime);
            Date date = cal.getTime();
            String showEndDate = DateStrUtil.dateToStr(date);
            return showEndDate;
        } else if ("beginToEnd".equals(validityType)) {
            String start = DateStrUtil.dateToStr(cardCards.getValidFrom());
            String end = DateStrUtil.dateToStr(cardCards.getValidTo());
            return end;
        } else {
            return "永久";
        }
    }


    /**
     * 购物车结算拆单
     *
     * @param shoppingCartUnionOrderData
     * @return
     */
    public RetUnionOrderData shoppingCartUnionOrder(ShoppingCartUnionOrderData shoppingCartUnionOrderData) {
        List<String> shoppingCartOrderCodeList = shoppingCartUnionOrderData.getShoppingCartOrderCodeList();
        List<String> merchantCodeList = new ArrayList<>();
        List<OrderShoppingCart> orderShoppingCartList = new ArrayList<>();
        for (String shoppingCartOrderCode : shoppingCartOrderCodeList) {
            OrderShoppingCart orderShoppingCart = shoppingCartClientService.queryByOrderCode(shoppingCartOrderCode).getData();
            merchantCodeList.add(orderShoppingCart.getMerchantCode());
            orderShoppingCartList.add(orderShoppingCart);
        }

        //校验所有商品的库存
        ReturnCheckInventoryMsg returnCheckInventoryMsg = inventoryService.checkProductionListInventory(shoppingCartOrderCodeList);
        if (!returnCheckInventoryMsg.isHasInventory()) {
            throw new CheckException(ResultTypeEnum.INVENTORY);
        }

        //判断主体收银 还是门店收银
        List<String> storeChargeTypeMerchantCodeList = new ArrayList<>();
        List<String> entityChargeTypeMerchantCodeList = new ArrayList<>();

        for (String merchantCode : merchantCodeList) {
            Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
            if (MerchantChargeTypeConstant.CHARGE_BY_STORE.equals(merchants.getChargeType())) {
                storeChargeTypeMerchantCodeList.add(merchants.getMerchantCode());
            } else {
                entityChargeTypeMerchantCodeList.add(merchants.getMerchantCode());
            }
        }

        //门店 收银分类
        List<String> storeChargeTypeShoppingCartCode = new ArrayList<>();
        for (String merchantCode : storeChargeTypeMerchantCodeList) {
            for (OrderShoppingCart orderShoppingCart : orderShoppingCartList) {
                if (orderShoppingCart.getMerchantCode().equals(merchantCode)) {
                    storeChargeTypeShoppingCartCode.add(orderShoppingCart.getOrderCode());
                }
            }
        }

        //主体 收银分类
        List<String> entityChargeTypeShoppingCartCode = new ArrayList<>();
        for (String merchantCode : entityChargeTypeMerchantCodeList) {
            for (OrderShoppingCart orderShoppingCart : orderShoppingCartList) {
                if (orderShoppingCart.getMerchantCode().equals(merchantCode)) {
                    entityChargeTypeShoppingCartCode.add(orderShoppingCart.getOrderCode());
                }
            }
        }

        RetUnionOrderData retUnionOrderData = new RetUnionOrderData();
        //主体收银类型处理
        if (entityChargeTypeMerchantCodeList.size() > 0) {
            // 主体收银 直接创建订单
            List<ShowShoppingCartDate> showShoppingCartDateList = shoppingCartClientService.queryByOrderCodeList(entityChargeTypeShoppingCartCode).getData();
            //创建订单
            retUnionOrderData = createNoMoreCategoryOrder(showShoppingCartDateList, shoppingCartUnionOrderData.getObjectMerchantCode(), shoppingCartUnionOrderData.getOrderWayBills());
        }

        boolean ifMoreMerchant = true;

        String oneOrderCode = "";
        String oneMerchantCode = "";
        //门店收银处理
        if (storeChargeTypeMerchantCodeList.size() > 0) {
            //门店收银
            Set<String> storeChargeTypeMerchantCodeSet = distinctMerchantCodeList(storeChargeTypeMerchantCodeList);
            ifMoreMerchant = storeChargeTypeMerchantCodeSet.size() > 1;
            for (String merchantCode : storeChargeTypeMerchantCodeSet) {
                List<ShowShoppingCartDate> showShoppingCartDateList = shoppingCartClientService.queryByOrderCodeListAndMerchantCode(storeChargeTypeShoppingCartCode, merchantCode).getData();
                //创建订单
                RetUnionOrderData orderData = createMerchantOrder(showShoppingCartDateList, merchantCode, shoppingCartUnionOrderData.getOrderWayBills());
                if (!ifMoreMerchant) {
                    oneOrderCode = orderData.getOrderCode();
                    oneMerchantCode = orderData.getMerchantCode();
                }
            }
        }
        //响应数据
        if (entityChargeTypeMerchantCodeList.size() > 0 && storeChargeTypeMerchantCodeList.size() < 1) {
            return retUnionOrderData;
        } else if (entityChargeTypeMerchantCodeList.size() < 1 && storeChargeTypeMerchantCodeList.size() > 0 && !ifMoreMerchant) {
            retUnionOrderData.setIsToPay(true);
            retUnionOrderData.setMerchantCode(oneMerchantCode);
            retUnionOrderData.setOrderCode(oneOrderCode);
            retUnionOrderData.setUnionOrderMessage("下单成功,直接支付");
            return retUnionOrderData;
        } else {
            retUnionOrderData.setIsToPay(false);
            retUnionOrderData.setUnionOrderMessage("下单成功,多商户多收银方式,请到订单页详细支付");
            return retUnionOrderData;
        }
    }

    /**
     * 创建一个商户的订单
     *
     * @param shoppingCartOrderCodeList
     * @param orderWayBills
     */
    private RetUnionOrderData createMerchantOrder(List<ShowShoppingCartDate> shoppingCartOrderCodeList, String merchantCode, OrderWayBills orderWayBills) {
        return createProductionsOrder(shoppingCartOrderCodeList, merchantCode, orderWayBills);
    }

    /**
     * 创建订单支付数据
     *
     * @param showShoppingCartDateList
     * @param orderWayBills
     */
    private RetUnionOrderData createProductionsOrder(List<ShowShoppingCartDate> showShoppingCartDateList, String merchantCode, OrderWayBills orderWayBills) {
//        String orderCode = IdWorker.getIdStr();
        String orderCode = OrderCodeFactory.getOrderCode(OrderEnum.ShopOrder);
        //创建订单明细
        Integer amount = 0;
        String orderComment = "";
        Set<String> merchantCodeSet = new HashSet<>();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(showShoppingCartDate.getProductionCategoryCode(), showShoppingCartDate.getMerchantCode()).getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                // 卡券类商品处理
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(showShoppingCartDate.getProductionCode(),
                        showShoppingCartDate.getMerchantCode(),
                        MerchantCardConstant.MALL_SELL_TYPE).getData();
                Integer totalPrice = cardMapMerchantCards.getPrice() * showShoppingCartDate.getQuantity().intValue();
                OrderOrderDetails orderOrderDetails = mallOrderClientService.saveOrderOrderDetails(orderCode,
                        cardMapMerchantCards.getMerchantCode(),
                        showShoppingCartDate.getUserId(),
                        showShoppingCartDate.getQuantity().intValue(),
                        totalPrice,
                        showShoppingCartDate.getProductionCode(),
                        showShoppingCartDate.getProductionName(),
                        showShoppingCartDate.getProductionCategoryCode(),
                        showShoppingCartDate.getProductionCategoryName(),
                        showShoppingCartDate.getActivityCode(),
                        showShoppingCartDate.getOrderCode(),
                        OrderConstant.UNPAID_STATE,
                        OrderConstant.SHOP_TYPE,
                        showShoppingCartDate.getDiscount()).getData();
                amount = amount + totalPrice;
                orderComment = orderComment + cardMapMerchantCards.getCardName() + " ";
            } else {
                //实体商品处理
                OrderProductions orderProductions = orderProductionsClientService.getByCode(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode()).getData();
                Integer price = orderProductions.getPrice() * showShoppingCartDate.getQuantity().intValue();
                OrderOrderDetails orderOrderDetails = mallOrderClientService.saveOrderOrderDetails(orderCode,
                        orderProductions.getMerchantCode(),
                        showShoppingCartDate.getUserId(),
                        showShoppingCartDate.getQuantity().intValue(),
                        price,
                        showShoppingCartDate.getProductionCode(),
                        showShoppingCartDate.getProductionName(),
                        showShoppingCartDate.getProductionCategoryCode(),
                        showShoppingCartDate.getProductionCategoryName(),
                        showShoppingCartDate.getActivityCode(),
                        showShoppingCartDate.getOrderCode(),
                        OrderConstant.UNPAID_STATE,
                        OrderConstant.SHOP_TYPE,
                        showShoppingCartDate.getDiscount()).getData();
                amount = amount + price;
                orderComment = orderComment + orderProductions.getProductionName() + " ";
                merchantCodeSet.add(orderProductions.getMerchantCode());
            }
        }
        Integer totalWayBillMoney = 0;
        if (orderWayBills != null && merchantCodeSet.size() > 0) {
            //根据商户拆派送
            orderWayBills.setUserId(showShoppingCartDateList.get(0).getUserId());
            orderWayBills.setOrderCode(orderCode);
            totalWayBillMoney = separateOrderWayBills(merchantCodeSet, orderWayBills);
        }
        //保存订单主表
        mallOrderClientService.saveOrderOrders(orderCode,
                OrderConstant.SHOP_TYPE,
                OrderConstant.UNPAID_STATE,
                merchantCode,
                showShoppingCartDateList.get(0).getUserId(),
                "-1",
                showShoppingCartDateList.size(),
                amount + totalWayBillMoney,
                orderComment,
                0);

        RetUnionOrderData retUnionOrderData = new RetUnionOrderData();
        retUnionOrderData.setOrderCode(orderCode);
        retUnionOrderData.setMerchantCode(merchantCode);
        return retUnionOrderData;
    }

    /**
     * 派送单 创建 或 拆分
     */
    private Integer separateOrderWayBills(Set<String> merchantCodeSet, OrderWayBills orderWayBills) {
        Integer totalWayBillsMoney = 0;
        orderWayBills.setType(OrderWayBillsTypeConstant.COURIER_SEND);
        orderWayBills.setState(OrderWayBillsStateConstant.UN_DELIVERED);
        if (merchantCodeSet != null && merchantCodeSet.size() > 0) {
            for (String merchantCode : merchantCodeSet) {
                //计算派送费
                Integer wayBillsMoney = 0;
                List<OrderWayBillFeeRules> orderWayBillFeeRulesList = orderWayBillsClientService.queryWayBillFeeRules(merchantCode).getData();
                if (orderWayBillFeeRulesList != null && orderWayBillFeeRulesList.size() > 0) {
                    for (OrderWayBillFeeRules orderWayBillFeeRules : orderWayBillFeeRulesList) {
                        if (orderWayBillFeeRules.getType().equals(orderWayBills.getType())) {
                            wayBillsMoney = orderWayBillFeeRules.getDefaultFee();
                        }
                    }
                }
                totalWayBillsMoney = totalWayBillsMoney + wayBillsMoney;
                orderWayBills.setBillFee(wayBillsMoney);
                orderWayBills.setMerchantCode(merchantCode);
                orderWayBills.setWayBillCode(OrderCodeFactory.getOrderCode(OrderEnum.WayBillOrder));
                orderWayBillsClientService.saveOrderWayBills(orderWayBills);
            }
        }
        return totalWayBillsMoney;
    }

    /**
     * 不根据商品品类拆单 不根据商户编码拆单 直接下单
     *
     * @param showShoppingCartDateList
     * @param orderWayBills
     * @return
     */
    private RetUnionOrderData createNoMoreCategoryOrder(List<ShowShoppingCartDate> showShoppingCartDateList, String merchantCode, OrderWayBills orderWayBills) {
//        String orderCode = IdWorker.getIdStr();
        String orderCode = OrderCodeFactory.getOrderCode(OrderEnum.ShopOrder);
        //创建订单明细
        Integer amount = 0;
        String orderComment = "";
        Set<String> merchantCodeSet = new HashSet<>();
        for (ShowShoppingCartDate showShoppingCartDate : showShoppingCartDateList) {
            OrderCategorys orderCategorys = orderCategorysServeice.queryLevelOneCode(showShoppingCartDate.getProductionCategoryCode(), showShoppingCartDate.getMerchantCode()).getData();
            if (ProductionsCategoryConstant.CARD_CARD_CATEGORY_CODE.equals(orderCategorys.getCategoryLevel01Code())) {
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardClientService.mallQueryCodeMerchantCodeType(showShoppingCartDate.getProductionCode(),
                        showShoppingCartDate.getMerchantCode(),
                        MerchantCardConstant.MALL_SELL_TYPE).getData();
                Integer totalPrice = cardMapMerchantCards.getPrice() * showShoppingCartDate.getQuantity().intValue();
                OrderOrderDetails orderOrderDetails = mallOrderClientService.saveOrderOrderDetails(orderCode,
                        cardMapMerchantCards.getMerchantCode(),
                        showShoppingCartDate.getUserId(),
                        showShoppingCartDate.getQuantity().intValue(),
                        totalPrice,
                        showShoppingCartDate.getProductionCode(),
                        showShoppingCartDate.getProductionName(),
                        showShoppingCartDate.getProductionCategoryCode(),
                        showShoppingCartDate.getProductionCategoryName(),
                        showShoppingCartDate.getActivityCode(),
                        showShoppingCartDate.getOrderCode(),
                        OrderConstant.UNPAID_STATE,
                        OrderConstant.SHOP_TYPE,
                        showShoppingCartDate.getDiscount()).getData();
                amount = amount + totalPrice;
                orderComment = orderComment + cardMapMerchantCards.getCardName() + " ";
            } else {
                //实体商品逻辑处理
                OrderProductions orderProductions = orderProductionsClientService.getByCode(showShoppingCartDate.getProductionCode(), showShoppingCartDate.getMerchantCode()).getData();
                Integer price = orderProductions.getPrice() * showShoppingCartDate.getQuantity().intValue();
                OrderOrderDetails orderOrderDetails = mallOrderClientService.saveOrderOrderDetails(orderCode,
                        orderProductions.getMerchantCode(),
                        showShoppingCartDate.getUserId(),
                        showShoppingCartDate.getQuantity().intValue(),
                        price,
                        showShoppingCartDate.getProductionCode(),
                        showShoppingCartDate.getProductionName(),
                        showShoppingCartDate.getProductionCategoryCode(),
                        showShoppingCartDate.getProductionCategoryName(),
                        showShoppingCartDate.getActivityCode(),
                        showShoppingCartDate.getOrderCode(),
                        OrderConstant.UNPAID_STATE,
                        OrderConstant.SHOP_TYPE,
                        showShoppingCartDate.getDiscount()).getData();
                amount = amount + price;
                orderComment = orderComment + orderProductions.getProductionName() + " ";
                merchantCodeSet.add(orderProductions.getMerchantCode());
            }
        }
        //按商户拆分派送单
        Integer totalWayBillMoney = 0;
        if (orderWayBills != null && merchantCodeSet.size() > 0) {
            //根据商户拆派送
            orderWayBills.setUserId(showShoppingCartDateList.get(0).getUserId());
            orderWayBills.setOrderCode(orderCode);
            totalWayBillMoney = separateOrderWayBills(merchantCodeSet, orderWayBills);
        }
        //保存订单主表
        mallOrderClientService.saveOrderOrders(orderCode,
                OrderConstant.SHOP_TYPE,
                OrderConstant.UNPAID_STATE,
                merchantCode,
                showShoppingCartDateList.get(0).getUserId(),
                "-1",
                showShoppingCartDateList.size(),
                amount + totalWayBillMoney,
                orderComment,
                0);

        RetUnionOrderData retUnionOrderData = new RetUnionOrderData();
        retUnionOrderData.setOrderCode(orderCode);
        retUnionOrderData.setIsToPay(true);
        retUnionOrderData.setUnionOrderMessage("下单成功,直接跳转支付,无需拆单");
        retUnionOrderData.setMerchantCode(merchantCode);
        return retUnionOrderData;
    }

    /**
     * 校验商户编码列表 是否有多个商户
     *
     * @param merchantCodeList
     * @return
     */
    public Boolean checkMoreMerchantCode(List<String> merchantCodeList) {
        long count = merchantCodeList.stream().distinct().count();
        System.out.println(merchantCodeList.size());
        System.out.println(count);
        if (count > 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 对 merchantCodeList 去重
     *
     * @param merchantCodeList
     * @return
     */
    public Set<String> distinctMerchantCodeList(List<String> merchantCodeList) {
        HashSet<String> merchantCodeSet = new HashSet<>(merchantCodeList);
        return merchantCodeSet;
    }

    /**
     * 查看券码 (新迭代)
     *
     * @param orderCode
     * @param id
     * @param mapUserCardNo
     * @return
     */
    public RetOrderQrCodeDetailData buyQrCodeDetail(String orderCode, String id, String mapUserCardNo) {
        OrderOrderDetails orderOrderDetails = mallOrderClientService.queryDetailById(id).getData();

        RetOrderQrCodeDetailData retOrderQrCodeDetailData = new RetOrderQrCodeDetailData();

        List<QrCodeDetailData> qrCodeDetailDataList = new ArrayList<>();

        CardMapUserCards cardMapUserCards = cardMapUserClientService.getByCardNo(mapUserCardNo).getData();
        if (!CardUserMallConstant.MALL_BUY_UN_USE_STATE.equals(cardMapUserCards.getState())){
            throw new CheckException(ResultTypeEnum.CARD_STATE_ERROR);
        }
        //券码数据
        CardCards cardCards = cardCardsClientService.getCardByCardCode(cardMapUserCards.getCardCode()).getData();

        String showDateStr = getShowValidityDateStr(cardCards, cardMapUserCards.getCreateAt());
        String romCode = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        stringRedisTemplate.opsForValue().set(cardMapUserCards.getCardNo(), romCode, 10, TimeUnit.MINUTES);
        String qrCode = getQrCode(cardMapUserCards.getCardNo() + romCode, 300, 300);
        QrCodeDetailData qrCodeDetailData = new QrCodeDetailData();
        qrCodeDetailData.setCardNo(cardMapUserCards.getCardNo());
        qrCodeDetailData.setQrCode(qrCode);
        qrCodeDetailData.setShowEndDate(showDateStr);
        qrCodeDetailData.setQuantity(Integer.parseInt(cardMapUserCards.getFaceValue()));
        qrCodeDetailData.setProductionName(cardMapUserCards.getCardName());
        qrCodeDetailDataList.add(qrCodeDetailData);

        retOrderQrCodeDetailData.setQrCodeDetailDataList(qrCodeDetailDataList);

        //商户信息
        Merchants merchants = merchantsClientService.getMerchantByCode(orderOrderDetails.getMerchantCode()).getData();
        MerchantsDetailData merchantsDetailData = new MerchantsDetailData();
        merchantsDetailData.setMerchants(merchants);
        merchantsDetailData.setProductionsName(cardMapUserCards.getCardName());
        merchantsDetailData.setQuantity(orderOrderDetails.getQuantity().intValue());
        merchantsDetailData.setAmount(orderOrderDetails.getAmount() - orderOrderDetails.getDiscount());
        retOrderQrCodeDetailData.setMerchantsDetailData(merchantsDetailData);
        //订单数据信息
        OrderQrShowData orderQrShowData = new OrderQrShowData();
        orderQrShowData.setOrderCode(orderCode);
        UserUsers userUsers = authClientService.getUserByIdTL(orderOrderDetails.getUserId().toString()).getData();
        if (StringUtils.isEmpty(userUsers.getTel())) {
            orderQrShowData.setTel("无手机号");
        } else {
            orderQrShowData.setTel(userUsers.getTel());
        }
        orderQrShowData.setPayTime(orderOrderDetails.getUpdateAt());
        orderQrShowData.setQuantity(1);

        //拆分订单数据展示
        Integer oneAmount = orderOrderDetails.getAmount()/orderOrderDetails.getQuantity().intValue();
        Integer oneDiscount = orderOrderDetails.getDiscount()/orderOrderDetails.getQuantity().intValue();
        orderQrShowData.setAmount(oneAmount);
        orderQrShowData.setDiscount(oneDiscount);
        orderQrShowData.setActualPayment(oneAmount - oneDiscount);

        retOrderQrCodeDetailData.setOrderQrShowData(orderQrShowData);
        //商品数据
        MallProductions mallProductions = new MallProductions();
        List<MallProductions> mallProductionsList = mallAppShowClientService.queryByCodeAndMerchant(orderOrderDetails.getProductionCode(), orderOrderDetails.getMerchantCode()).getData();
        if (mallProductionsList != null && mallProductionsList.size() > 0) {
            mallProductions = mallProductionsList.get(0);
        }
        retOrderQrCodeDetailData.setMallProductions(mallProductions);

        return retOrderQrCodeDetailData;
    }
}
