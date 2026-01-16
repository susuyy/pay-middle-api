package com.ht.user.card.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.AdjustAccount;
import com.ht.user.admin.vo.OrdersVo;
import com.ht.user.admin.vo.Recharge;
import com.ht.user.card.common.PayTraceTypeSourceEnum;
import com.ht.user.card.entity.*;
import com.ht.user.card.service.*;
import com.ht.user.card.vo.*;
import com.ht.user.card.mapper.CardOrdersMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.config.*;
import com.ht.user.utils.CardMoneyAddUtil;
import com.ht.user.utils.CardMoneyPayUtil;

import com.ht.user.utils.OpenCardUtil;
import com.ht.user.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单主表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Service
public class CardOrdersServiceImpl extends ServiceImpl<CardOrdersMapper, CardOrders> implements CardOrdersService {


    @Autowired
    private CardOrderDetailsService cardOrderDetailsService;

    @Autowired
    private CardCardsService cardCardsService;

    private Logger logger = LoggerFactory.getLogger(CardOrdersServiceImpl.class);

    @Autowired
    private CardOrdersService cardOrdersService;

    @Autowired
    private CardOrderPayTraceService cardOrderPayTraceService;

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    @Autowired
    private CardMapMerchantCardsService cardMapMerchantCardsService;

    @Autowired
    private CardMapUserCardsTraceService cardMapUserCardsTraceService;

    @Override
    public IPage<CardOrders> selectOrderPage(Long userId, List<Merchants> merchantAndSon, String type, String state, Integer pageNo, Integer pageSize) {
        IPage<CardOrders> iPage = new Page<>(pageNo, pageSize);
        QueryWrapper<CardOrders> queryWrapper = new QueryWrapper();
        if (!"all".equals(type)) {
            queryWrapper.eq("type", type);
        }
        if (!"all".equals(state)) {
            queryWrapper.eq("state", state);
        }
        queryWrapper.eq("user_id", userId);
        List<String> merchantCodeList = new ArrayList<>();
        for (Merchants merchants : merchantAndSon) {
            merchantCodeList.add(merchants.getMerchantCode());
        }
        queryWrapper.in("merchant_code", merchantCodeList);
        queryWrapper.orderByDesc("create_at");
        return this.baseMapper.selectPage(iPage, queryWrapper);
    }

    @Override
    public Integer getUserDailyExpenditure(String merchantCode, Long userId) {
        List<CardOrders> list = getUserCardOrders(merchantCode, userId);
        Integer totalPayment = list.stream()
                .filter(e -> TimeUtil.format(e.getCreateAt(), "yyyy-MM-dd").equals(TimeUtil.format(new Date(), "yyyy-MM-dd")))
                .mapToInt(CardOrders::getAmount)
                .sum();
        return totalPayment;
    }

    @Override
    public List<CardOrders> getUserCardOrders(String merchantCode, Long userId) {
        LambdaQueryWrapper<CardOrders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardOrders::getMerchantCode, merchantCode);
        wrapper.eq(CardOrders::getUserId, userId);
        return this.list(wrapper);
    }


    /**
     * 用户买券下单
     *
     * @param userPlaceOrderData
     * @return
     */
    @Override
    public PlaceOrderResult placeOrder(UserPlaceOrderData userPlaceOrderData, Long userId) {
        try {
            String orderCode = IdWorker.getIdStr();
            CardOrders orders = new CardOrders();
            BeanUtils.copyProperties(userPlaceOrderData, orders);
            orders.setOrderCode(orderCode);
            orders.setState(CardOrdersStateConfig.UNPAID);
            orders.setUserId(userId);
            orders.setCreateAt(new Date());
            orders.setUpdateAt(new Date());

            orders.setType(CardOrdersTypeConfig.SHOP);
            orders.setQuantity(new BigDecimal(userPlaceOrderData.getQuantity()));
            String productionCode = userPlaceOrderData.getProductionCode();
            CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(productionCode, userPlaceOrderData.getMerchantCode(), userPlaceOrderData.getBatchCode());
            orders.setComments(cardMapMerchantCards.getCardName());

            orders.setAmount(cardMapMerchantCards.getPrice() * userPlaceOrderData.getQuantity());
            orders.setDiscount(0);
            this.baseMapper.insert(orders);

            CardOrderDetails orderDetails = new CardOrderDetails();
            BeanUtils.copyProperties(userPlaceOrderData, orderDetails);
            orderDetails.setOrderCode(orderCode);
            orderDetails.setState(CardOrdersStateConfig.UNPAID);
            orderDetails.setCreateAt(new Date());
            orderDetails.setUpdateAt(new Date());
            orderDetails.setDisccount(0);
            orderDetails.setQuantity(new BigDecimal(userPlaceOrderData.getQuantity()));
            orderDetails.setAmount(cardMapMerchantCards.getPrice() * userPlaceOrderData.getQuantity());
            orderDetails.setProductionCode(cardMapMerchantCards.getCardCode());
            orderDetails.setProductionName(cardMapMerchantCards.getCardName());
            orderDetails.setBatchCode(userPlaceOrderData.getBatchCode());
            orderDetails.setType(CardOrdersTypeConfig.SHOP);
            cardOrderDetailsService.save(orderDetails);

            CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
            cardOrderPayTrace.setType(userPlaceOrderData.getType());
            cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.UNPAID);
            cardOrderPayTrace.setAmount(orders.getAmount());
            cardOrderPayTrace.setOrderCode(orders.getOrderCode());
            cardOrderPayTrace.setOrderDetailId(orderDetails.getId());
            cardOrderPayTrace.setUpdateAt(new Date());
            cardOrderPayTrace.setUpdateAt(new Date());
            cardOrderPayTraceService.save(cardOrderPayTrace);

            PlaceOrderResult placeOrderResult = new PlaceOrderResult();
            placeOrderResult.setOrderCode(orderCode);
            placeOrderResult.setAmount(cardMapMerchantCards.getPrice() * userPlaceOrderData.getQuantity());
            placeOrderResult.setDiscount(0);
            placeOrderResult.setMessage("下单成功");
            return placeOrderResult;
        } catch (BeansException e) {
            logger.info("下单失败" + e);
            PlaceOrderResult placeOrderResult = new PlaceOrderResult();
            placeOrderResult.setMessage("下单失败");
            return placeOrderResult;
        }
    }

    @Override
    public void updateStateByOrderCode(String orderCode, String state) {
        //修改订单支付状态
        this.baseMapper.updateStateByOrderCode(orderCode, state, new Date());
    }

    @Override
    public void updateStateAndUserIdByOrderCode(String orderCode, String state, Long userId) {
        this.baseMapper.updateStateAndUserIdByOrderCode(orderCode, state, userId, new Date());
    }

    @Override
    public void misAccountPayCreateOrderAndDetailAndTrace(Long userId, Integer amount, Integer payMoney, Integer needPayMoney, String merchantCode, String orderCode, String accountCardNo, CardPayDetailData cardPayDetailData) {

        //更新订单状态
        updateStateAndUserIdByOrderCode(orderCode, CardOrdersStateConfig.PAID, userId);

        //更新订单明细状态
        cardOrderDetailsService.updateStateByOrderCode(orderCode, CardOrdersStateConfig.PAID, new Date());

        CardOrderPayTrace cardOrderPayTraceOriMis = cardOrderPayTraceService.queryTraceByOrderCode(orderCode).get(0);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setType(CardOrdersTypeConfig.POS_ACCOUNT_PAY);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setPayCode(cardOrderPayTraceOriMis.getPayCode());
        cardOrderPayTrace.setAmount(needPayMoney);
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(cardOrderPayTraceOriMis.getOrderDetailId());
        cardOrderPayTrace.setSource(cardOrderPayTraceOriMis.getSource());
        cardOrderPayTrace.setSourceId(accountCardNo);
        cardOrderPayTrace.setMerchantCode(merchantCode);
        cardOrderPayTrace.setUserFlag(userId + "");
        cardOrderPayTrace.setPosSerialNum(cardOrderPayTraceOriMis.getPosSerialNum());
        cardOrderPayTrace.setMerchId(cardOrderPayTraceOriMis.getMerchId());
        cardOrderPayTrace.setMerchName(cardOrderPayTraceOriMis.getMerchName());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTraceService.save(cardOrderPayTrace);
    }

    @Override
    public String createUserTopUpOrder(UserTopUpOrderData userTopUpOrderData) {
        String orderCode = IdWorker.getIdStr();

        CardOrders cardOrders = new CardOrders();
        cardOrders.setOrderCode(orderCode);
        cardOrders.setType(CardOrdersTypeConfig.C_USER_TOP_UP);
        cardOrders.setSaleId("");
        cardOrders.setState(CardOrdersStateConfig.UNPAID);
        cardOrders.setMerchantCode(userTopUpOrderData.getObjectMerchantCode());
        cardOrders.setUserId(userTopUpOrderData.getUserId());
        cardOrders.setQuantity(BigDecimal.ONE);
        cardOrders.setAmount(userTopUpOrderData.getAmount());
        cardOrders.setDiscount(0);
        cardOrders.setComments("充值订单");
        cardOrders.setCreateAt(new Date());
        cardOrders.setUpdateAt(new Date());
        this.baseMapper.insert(cardOrders);

        CardOrderDetails cardOrderDetails = new CardOrderDetails();
        cardOrderDetails.setOrderCode(orderCode);
        cardOrderDetails.setMerchantCode(userTopUpOrderData.getObjectMerchantCode());
        cardOrderDetails.setQuantity(BigDecimal.ONE);
        cardOrderDetails.setAmount(userTopUpOrderData.getAmount());
        cardOrderDetails.setProductionCode("");
        cardOrderDetails.setProductionName("充值订单");
        cardOrderDetails.setProductionCategoryCode("");
        cardOrderDetails.setProductionCategoryName("");
        cardOrderDetails.setState(CardOrdersStateConfig.UNPAID);
        cardOrderDetails.setType(CardOrdersTypeConfig.C_USER_TOP_UP);
        cardOrderDetails.setDisccount(0);
        cardOrderDetails.setCreateAt(new Date());
        cardOrderDetails.setUpdateAt(new Date());
        cardOrderDetails.setBatchCode("");
        cardOrderDetailsService.save(cardOrderDetails);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
        cardOrderPayTrace.setPayCode("");
        cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.ALLINPAY_H5);
        cardOrderPayTrace.setState(CardOrdersStateConfig.UNPAID);
        cardOrderPayTrace.setSource("");
        cardOrderPayTrace.setSourceId("");
        cardOrderPayTrace.setAmount(userTopUpOrderData.getAmount());
        cardOrderPayTrace.setPosSerialNum("");
        cardOrderPayTrace.setUserFlag(userTopUpOrderData.getUserId() + "");
        cardOrderPayTrace.setMerchantCode(userTopUpOrderData.getObjectMerchantCode());
        cardOrderPayTrace.setMerchId("");
        cardOrderPayTrace.setMerchName("");
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTraceService.save(cardOrderPayTrace);

        return orderCode;
    }

    @Override
    public void topUpPaySuccess(PaySuccess paySuccess, CardOrdersVO ordersVO) throws Exception {
        //修改订单状态
        updateStateByOrderCode(ordersVO.getOrderCode(), CardOrdersStateConfig.PAID);
        cardOrderDetailsService.updateStateByOrderCode(ordersVO.getOrderCode(), CardOrdersStateConfig.PAID, new Date());
        cardOrderPayTraceService.updateStateByOrderCode(ordersVO.getOrderCode(), CardOrdersStateConfig.PAID, new Date(), paySuccess.getPayCode());
        //加钱
        CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByUserIdAndAccountAndMerchantCode(ordersVO.getUserId(), ordersVO.getMerchantCode());
        if (cardMapUserCards != null) {
            CardMoneyAddUtil.cardMoneyAdd(cardMapUserCards.getCardNo(), ordersVO.getAmount());
        } else {
            tongLianCardOpenAndTopUp(paySuccess.getUserTel(), ordersVO.getAmount(), ordersVO);
        }
        boolean sendFlag = cardMapUserCardsService.sendCardForFulfilQuota(ordersVO.getMerchantCode(), ordersVO.getAmount(), ordersVO.getUserId());
        if (!sendFlag) {
            logger.info("发放用户卡券失败,充值订单号为:" + ordersVO.getOrderCode());

        }
    }

    /**
     * 通联开卡开通账户余额
     *
     * @param userPhone
     * @param amount
     * @param ordersVO
     * @return
     * @throws Exception
     */
    public String tongLianCardOpenAndTopUp(String userPhone, Integer amount, CardOrdersVO ordersVO) throws Exception {
        String content = OpenCardUtil.callOpenCard(userPhone);
        Map map = JSONObject.parseObject(content, Map.class);
        Map errorResponse = (Map) map.get("error_response");
        if (errorResponse == null) {
            PpcsCloudCardOpenReturnData ppcsCloudCardOpenReturnData = JSONObject.parseObject(content, PpcsCloudCardOpenReturnData.class);
            PpcsCloudCardOpenResponse ppcsCloudCardOpenResponse = ppcsCloudCardOpenReturnData.getPpcs_cloud_card_open_response();
            //创建用户余额账户关联
            CardMapUserCards cardMapUserCards = new CardMapUserCards();
            cardMapUserCards.setUserId(ordersVO.getUserId());
            cardMapUserCards.setMerchantCode(ordersVO.getMerchantCode());
            cardMapUserCards.setCardCode(TongLianCardState.CARD_CODE.getCode() + "");
            cardMapUserCards.setCardNo(ppcsCloudCardOpenResponse.getCard_id());
            cardMapUserCards.setCardName(TongLianCardState.CARD_NAME.getDesc());
            cardMapUserCards.setCategoryCode(TongLianCardState.CATEGORY.getCode() + "");
            cardMapUserCards.setCategoryName(TongLianCardState.CATEGORY.getDesc());
            cardMapUserCards.setState(TongLianCardState.STATE_NORMAL.getDesc());
            cardMapUserCards.setType(TongLianCardState.TYPE.getDesc());
            cardMapUserCards.setCreateAt(new Date());
            cardMapUserCards.setUpdateAt(new Date());
            cardMapUserCardsService.save(cardMapUserCards);

            if (ppcsCloudCardOpenResponse.getResult().equals(0)) {
                //云卡开卡,开卡后一定个要调用充值，否则这个电子卡是没有账户的
                String moneyAddContent = CardMoneyAddUtil.cardMoneyAdd(ppcsCloudCardOpenResponse.getCard_id(), amount);
                logger.info("卡号:" + ppcsCloudCardOpenResponse.getCard_id() + "初次初始化数据充值信息为:" + moneyAddContent);
                return ppcsCloudCardOpenResponse.getCard_id();
            } else {
                logger.info("手机号为:" + userPhone + ",开通账户余额失败,响应数据为:" + ppcsCloudCardOpenResponse);
            }
        } else {
            logger.info("手机号为:" + userPhone + ",开通账户余额失败,响应的sub_msg为:" + errorResponse.get("sub_msg") + ",msg为:" + errorResponse.get("msg") + ",响应数据为:" + errorResponse);
        }
        return null;
    }

    @Override
    public List<CardOrderPayTrace> checkHaveOrder(String cashId) {
        QueryWrapper<CardOrderPayTrace> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cash_id", cashId);
        queryWrapper.orderByDesc("create_at");
        queryWrapper.last("limit 1");
        return cardOrderPayTraceService.list(queryWrapper);
    }

    @Override
    public CardOrders createPrimeBuyCardOrder(PrimeBuyCardData primeBuyCardData) {
        int totalAmount = 0;

        String orderComments = "";
        List<CardElectronicSell> cardElectronicSellList = primeBuyCardData.getCardElectronicSellList();
        for (CardElectronicSell cardElectronicSell : cardElectronicSellList) {
            totalAmount = (int) (totalAmount + (cardElectronicSell.getSellAmount() * cardElectronicSell.getQuantity()));
            orderComments = orderComments + " " + cardElectronicSell.getCardName();
        }

        String orderCode = IdWorker.getIdStr();
        CardOrders cardOrders = new CardOrders();
        cardOrders.setOrderCode(orderCode);
        cardOrders.setType(CardOrdersTypeConfig.PRIME_BUY_CARD);
        cardOrders.setState(CardOrdersStateConfig.UNPAID);
        cardOrders.setMerchantCode(cardElectronicSellList.get(0).getBrhId());
        cardOrders.setUserId(Long.parseLong(primeBuyCardData.getUserId()));
        cardOrders.setQuantity(new BigDecimal(cardElectronicSellList.size()));
        cardOrders.setAmount(totalAmount);
        cardOrders.setDiscount(0);
        cardOrders.setComments(orderComments);
        cardOrders.setCreateAt(new Date());
        cardOrders.setUpdateAt(new Date());
        this.baseMapper.insert(cardOrders);

        cardOrderDetailsService.createPrimeBuyCardOrderDetails(primeBuyCardData, cardOrders);

        cardOrderPayTraceService.createPrimeBuyCardOrderPayTrace(primeBuyCardData, cardOrders);

        return cardOrders;
    }

    @Override
    public List<CardOrderDetails> updatePrimeBuyCardState(String orderCode, String payCode) {
        updateStateByOrderCode(orderCode, CardOrdersStateConfig.PAID);
        cardOrderDetailsService.updateStateByOrderCode(orderCode, CardOrdersStateConfig.PAID, new Date());
//        cardOrderPayTraceService.updateStateByOrderCodeNotPayCode(orderCode,CardOrdersStateConfig.PAID,new Date());
        cardOrderPayTraceService.updateStateAndPayCodeByOrderCode(orderCode, CardOrdersStateConfig.PAID, new Date(), payCode);
        List<CardOrderDetails> cardOrderDetails = cardOrderDetailsService.queryByOrderCode(orderCode);
        return cardOrderDetails;
    }

    @Override
    public void primeBuyCardUpdateCardNo(UpdateCardNoData updateCardNoData) {
        List<CardElectronic> cardElectronicList = updateCardNoData.getCardElectronicList();
        List<CardOrderDetails> cardOrderDetailsList = updateCardNoData.getCardOrderDetailsList();
        for (CardOrderDetails cardOrderDetails : cardOrderDetailsList) {
            List<String> proCodeList = new ArrayList<>();
            for (CardElectronic cardElectronic : cardElectronicList) {
                if (cardOrderDetails.getBatchCode().equals(cardElectronic.getBatchCode())) {
                    proCodeList.add(cardElectronic.getCardNo());
                }
            }
            cardOrderDetailsService.updateProdCodeById(cardOrderDetails.getId(), JSONObject.toJSONString(proCodeList));
        }
    }

    @Override
    public void updateBuyCardOrderRefundState(String orderCode) {
        updateStateByOrderCode(orderCode, CardOrdersStateConfig.REFUND);
        cardOrderDetailsService.updateStateByOrderCode(orderCode, CardOrdersStateConfig.REFUND, new Date());
        cardOrderPayTraceService.updateStateByOrderCodeNotPayCode(orderCode, CardOrdersStateConfig.REFUND, new Date());
    }

    @Override
    public CardOrders getByOrderCode(String orderCode) {
        QueryWrapper<CardOrders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_code", orderCode);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<CardOrders> getOrderPage(Page<CardOrders> page, String orderNo, String startTime, String endTime,
                                         String state, Long userId, String phone, String type, String cardNo, String traceNo) {
        return this.baseMapper.getOrderPage(page, orderNo, startTime, endTime, state, userId, phone, type, cardNo, traceNo);
    }

    @Override
    public Page<CardOrders> getOrderPagePhone(Page<CardOrders> page, String orderNo, String startTime, String endTime,
                                              String state, String phone, String type, String cardNo, String traceNo) {
        return this.baseMapper.getOrderPagePhone(page, orderNo, startTime, endTime, state, phone, type, cardNo, traceNo);
    }

    @Override
    public List<CardOrderPayTrace> getConsumeOrdersExcelData(String orderNo, String startTime, String endTime, String state, String openId, String type, String cardNo, String traceNo) {
        QueryWrapper<CardOrderPayTrace> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(orderNo)) {
            queryWrapper.eq("order_code", orderNo);
        }
        if (!StringUtils.isEmpty(state)) {
            queryWrapper.eq("state", state);
        }
        if (!StringUtils.isEmpty(openId)) {
            queryWrapper.eq("user_flag", openId);
        }
        if (!StringUtils.isEmpty(type)) {
            queryWrapper.eq("type", type);
        } else {
            queryWrapper.ne("type", CardOrderPayTraceTypeConfig.ALLINPAY_H5);
            queryWrapper.ne("type", PayTraceTypeSourceEnum.TL_POS.getValue());
            queryWrapper.ne("type", PayTraceTypeSourceEnum.ACTUAL_CASH.getValue());
            queryWrapper.ne("type", PayTraceTypeSourceEnum.OTHER_PAY.getValue());
            queryWrapper.ne("type", PayTraceTypeSourceEnum.REMITTANCE_PAY.getValue());
            queryWrapper.ne("type", PayTraceTypeSourceEnum.COMPANY_PAY.getValue());
            queryWrapper.ne("type", PayTraceTypeSourceEnum.FREE.getValue());
        }
        if (!StringUtils.isEmpty(cardNo)) {
            queryWrapper.eq("source_id", cardNo);
        }
        if (!StringUtils.isEmpty(traceNo)) {
            queryWrapper.eq("pay_code", traceNo);
        }
        queryWrapper.ge("create_at", startTime);
        queryWrapper.le("create_at", endTime);
        queryWrapper.orderByDesc("create_at");
        List<CardOrderPayTrace> list = cardOrderPayTraceService.list(queryWrapper);

        QueryWrapper<CardOrders> queryWrapperOrder =new QueryWrapper<>();
        queryWrapperOrder.eq("type",CardOrdersTypeConfig.CONSUME);
        queryWrapperOrder.ge("create_at", startTime);
        queryWrapperOrder.le("create_at", endTime);
        List<CardOrders> cardOrdersList = this.baseMapper.selectList(queryWrapperOrder);
        Map<String,CardOrders> ordersMap = new HashMap<>();
        for (CardOrders cardOrders : cardOrdersList) {
            if (cardOrders!=null) {
                ordersMap.put(cardOrders.getOrderCode(), cardOrders);
            }
        }
        for (CardOrderPayTrace cardOrderPayTrace : list) {
            CardOrders cardOrders = ordersMap.get(cardOrderPayTrace.getOrderCode());
            if (cardOrders!=null){
                cardOrderPayTrace.setOrderMasterAmount(cardOrders.getAmount()+"");
                cardOrderPayTrace.setOrderMasterDesc(cardOrders.getComments());
                cardOrderPayTrace.setOrderMasterUserId(cardOrders.getUserId());
            }
        }
        return list;
    }

    @Override
    public List<PrimeBuyCardOrderExcelVo> getExcelList(String startTime, String endTime, String state, Long userId, String phone, String type, String cardNo, String traceNo, String orderNo) {
        List<PrimeBuyCardOrderExcelVo> excelList = this.baseMapper.getExcelList(startTime, endTime, state, userId, phone, type, cardNo, traceNo, orderNo);
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.ge("create_at",startTime);
        queryWrapper.le("create_at",endTime);
        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceService.list(queryWrapper);
        Map<String,CardOrderPayTrace> traceMap =  new HashMap<>();
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            if (cardOrderPayTrace!=null) {
                traceMap.put(cardOrderPayTrace.getOrderCode(), cardOrderPayTrace);
            }
        }

        for (PrimeBuyCardOrderExcelVo primeBuyCardOrderExcelVo : excelList) {
            String orderCode = primeBuyCardOrderExcelVo.getOrderCode();
            CardOrderPayTrace cardOrderPayTrace = traceMap.get(orderCode);
            if (cardOrderPayTrace == null) {
                primeBuyCardOrderExcelVo.setPayType("未确定收款方式");
            } else {
                if (PayTraceTypeSourceEnum.TL_POS.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.ACTUAL_CASH.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.FREE.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.COMPANY_PAY.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.REMITTANCE_PAY.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.OTHER_PAY.getValue().equals(cardOrderPayTrace.getType())) {
                    primeBuyCardOrderExcelVo.setPhone(cardOrderPayTrace.getUserFlag());
                }

                primeBuyCardOrderExcelVo.setPayType(cardOrderPayTrace.getSource());
                if (PayTraceTypeSourceEnum.FREE.getValue().equals(cardOrderPayTrace.getType())) {
                    primeBuyCardOrderExcelVo.setDetailAmount(Integer.parseInt("0"));
                    primeBuyCardOrderExcelVo.setReceiveAmount("0");
                }
            }
        }
        return excelList;
    }

    @Override
    public List<PrimeBuyCardOrderExcelVo> getExcelListPhone(String startTime, String endTime, String state, String phone, String type, String cardNo, String traceNo, String orderNo) {
        List<PrimeBuyCardOrderExcelVo> excelListPhone = this.baseMapper.getExcelListPhone(startTime, endTime, state, phone, type, cardNo, traceNo, orderNo);

        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.ge("create_at",startTime);
        queryWrapper.le("create_at",endTime);
        List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceService.list(queryWrapper);
        Map<String,CardOrderPayTrace> traceMap =  new HashMap<>();
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            if (cardOrderPayTrace!=null) {
                traceMap.put(cardOrderPayTrace.getOrderCode(), cardOrderPayTrace);
            }
        }

        for (PrimeBuyCardOrderExcelVo primeBuyCardOrderExcelVo : excelListPhone) {
            String orderCode = primeBuyCardOrderExcelVo.getOrderCode();
            CardOrderPayTrace cardOrderPayTrace = traceMap.get(orderCode);
            if (cardOrderPayTrace == null) {
                primeBuyCardOrderExcelVo.setPayType("未确定收款方式");
            } else {
                if (PayTraceTypeSourceEnum.TL_POS.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.ACTUAL_CASH.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.FREE.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.COMPANY_PAY.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.REMITTANCE_PAY.getValue().equals(cardOrderPayTrace.getType())
                        || PayTraceTypeSourceEnum.OTHER_PAY.getValue().equals(cardOrderPayTrace.getType())) {
                    primeBuyCardOrderExcelVo.setPhone(cardOrderPayTrace.getUserFlag());
                }

                primeBuyCardOrderExcelVo.setPayType(cardOrderPayTrace.getSource());
                if (PayTraceTypeSourceEnum.FREE.getValue().equals(cardOrderPayTrace.getType())) {
                    primeBuyCardOrderExcelVo.setDetailAmount(Integer.parseInt("0"));
                    primeBuyCardOrderExcelVo.setReceiveAmount("0");
                }
            }
        }
        return excelListPhone;
    }


    @Override
    public void createAdminSetUserCardOrder(CardElectronic cardElectronic, String payType, String payAmount) {
        String orderCode = IdWorker.getIdStr();
        CardOrders cardOrders = new CardOrders();
        cardOrders.setOrderCode(orderCode);
        cardOrders.setType(CardOrdersTypeConfig.PRIME_BUY_CARD);
        cardOrders.setState(CardOrdersStateConfig.PAID);
        cardOrders.setMerchantCode(cardElectronic.getMerchantCode());

        cardOrders.setUserId(cardElectronic.getUserId());

        String sellAmount = cardElectronic.getSellAmount();
        if (StringUtils.isEmpty(sellAmount)){
            sellAmount = "0";
        }

        cardOrders.setQuantity(BigDecimal.ONE);
        cardOrders.setAmount(Integer.parseInt(sellAmount));
        cardOrders.setDiscount(0);
        cardOrders.setComments(cardElectronic.getCardName());
        cardOrders.setCreateAt(new Date());
        cardOrders.setUpdateAt(new Date());
        save(cardOrders);

        CardOrderDetails cardOrderDetails = new CardOrderDetails();
        cardOrderDetails.setOrderCode(orderCode);
        cardOrderDetails.setMerchantCode(cardElectronic.getMerchantCode());
        cardOrderDetails.setQuantity(BigDecimal.ONE);
        cardOrderDetails.setAmount(Integer.parseInt(sellAmount));
        List<String> list = new ArrayList<>();
        list.add(cardElectronic.getCardNo());
        cardOrderDetails.setProductionCode(JSONObject.toJSONString(list));
        cardOrderDetails.setProductionName(cardElectronic.getCardName());
        cardOrderDetails.setState(CardOrdersStateConfig.PAID);
        cardOrderDetails.setType(CardOrdersTypeConfig.PRIME_BUY_CARD);
        cardOrderDetails.setDisccount(0);
        cardOrderDetails.setCreateAt(new Date());
        cardOrderDetails.setUpdateAt(new Date());
        cardOrderDetails.setBatchCode(cardElectronic.getBatchCode());

        cardOrderDetails.setCardType(cardElectronic.getCardType());
        cardOrderDetails.setUserPhone(cardElectronic.getUserPhone());

        cardOrderDetailsService.save(cardOrderDetails);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
        cardOrderPayTrace.setPayCode(IdWorker.getIdStr());

        // 转换类型
        cardOrderPayTrace.setType(payType);

        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);

        // 转换类型中文名字
        cardOrderPayTrace.setSource(PayTraceTypeSourceEnum.getDescByValueKey(payType));

        cardOrderPayTrace.setAmount(Integer.parseInt(payAmount));
        cardOrderPayTrace.setUserFlag(cardElectronic.getUserId() != null ? cardElectronic.getUserId() + "" : cardElectronic.getUserPhone());
        cardOrderPayTrace.setMerchantCode(cardElectronic.getMerchantCode());
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTraceService.save(cardOrderPayTrace);

    }

    @Override
    public List<CardOrders> getConsumeOrdersMasterExcelData(String orderNo, String startTime, String endTime) {
        QueryWrapper<CardOrders> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(orderNo)) {
            queryWrapper.eq("order_code", orderNo);
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.ge("create_at", startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            queryWrapper.le("create_at", endTime);
        }
        List<CardOrders> cardOrdersList = this.baseMapper.selectList(queryWrapper);
        for (CardOrders cardOrders : cardOrdersList) {
            List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceService.queryTraceByOrderCode(cardOrders.getOrderCode());
            cardOrders.setPayTraceList(cardOrderPayTraces);
        }
        return cardOrdersList;
    }

    @Override
    public void createAdminSetUserCardOrderBatch(Map<String, CardElectronic> cardElectronicMap, String payType) {
        List<CardOrders> cardOrderList = new ArrayList<>();
        List<CardOrderDetails> cardDetailList = new ArrayList<>();
        List<CardOrderPayTrace> cardOrderPayTraceList = new ArrayList<>();

        for (String cardNo : cardElectronicMap.keySet()) {
            CardElectronic cardElectronic = cardElectronicMap.get(cardNo);
            String orderCode = IdWorker.getIdStr();
            CardOrders cardOrders = new CardOrders();
            cardOrders.setOrderCode(orderCode);
            cardOrders.setType(CardOrdersTypeConfig.PRIME_BUY_CARD);
            cardOrders.setState(CardOrdersStateConfig.PAID);
            cardOrders.setMerchantCode(cardElectronic.getMerchantCode());

            cardOrders.setUserId(cardElectronic.getUserId());

            String sellAmount = cardElectronic.getSellAmount();
            if (StringUtils.isEmpty(sellAmount)){
                sellAmount = "0";
            }

            cardOrders.setQuantity(BigDecimal.ONE);
            cardOrders.setAmount(Integer.parseInt(sellAmount));
            cardOrders.setDiscount(0);
            cardOrders.setComments(cardElectronic.getCardName());
            cardOrders.setCreateAt(new Date());
            cardOrders.setUpdateAt(new Date());
            cardOrderList.add(cardOrders);

            CardOrderDetails cardOrderDetails = new CardOrderDetails();
            cardOrderDetails.setOrderCode(orderCode);
            cardOrderDetails.setMerchantCode(cardElectronic.getMerchantCode());
            cardOrderDetails.setQuantity(BigDecimal.ONE);
            cardOrderDetails.setAmount(Integer.parseInt(sellAmount));
            List<String> list = new ArrayList<>();
            list.add(cardElectronic.getCardNo());
            cardOrderDetails.setProductionCode(JSONObject.toJSONString(list));
            cardOrderDetails.setProductionName(cardElectronic.getCardName());
            cardOrderDetails.setState(CardOrdersStateConfig.PAID);
            cardOrderDetails.setType(CardOrdersTypeConfig.PRIME_BUY_CARD);
            cardOrderDetails.setDisccount(0);
            cardOrderDetails.setCreateAt(new Date());
            cardOrderDetails.setUpdateAt(new Date());
            cardOrderDetails.setBatchCode(cardElectronic.getBatchCode());

            cardOrderDetails.setUserPhone(cardElectronic.getUserPhone());
            cardOrderDetails.setCardType(cardElectronic.getCardType());

            cardDetailList.add(cardOrderDetails);

            CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
            cardOrderPayTrace.setOrderCode(orderCode);
            cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
            cardOrderPayTrace.setPayCode(IdWorker.getIdStr());

            // 转换类型
            cardOrderPayTrace.setType(payType);

            cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);

            // 转换类型中文名字
            cardOrderPayTrace.setSource(PayTraceTypeSourceEnum.getDescByValueKey(payType));

            cardOrderPayTrace.setAmount(Integer.parseInt(sellAmount));
            cardOrderPayTrace.setUserFlag(cardElectronic.getUserId() != null ? cardElectronic.getUserId() + "" : cardElectronic.getUserPhone());
            cardOrderPayTrace.setMerchantCode(cardElectronic.getMerchantCode());
            cardOrderPayTrace.setCreateAt(new Date());
            cardOrderPayTrace.setUpdateAt(new Date());
            cardOrderPayTraceList.add(cardOrderPayTrace);
        }

        saveBatch(cardOrderList);
        cardOrderDetailsService.saveBatch(cardDetailList);
        cardOrderPayTraceService.saveBatch(cardOrderPayTraceList);
    }

    @Override
    public Page<CardOrders> queryUserOrderListPage(Long pageNo, Long pageSize, String phoneNum, Long userId) {
        Page<CardOrders> page = new Page<>(pageNo, pageSize);
        Page<CardOrders> ordersPage = this.baseMapper.selectUserOrderListPage(page,phoneNum,userId);
        return ordersPage;
    }

    /**
     * 根据订单号查询订单详情
     *
     * @param orderCode
     * @return
     */
    @Override
    public CardOrdersVO queryByOrderCode(String orderCode) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("order_code", orderCode);
        CardOrders cardOrders = this.baseMapper.selectOne(queryWrapper);
        List<CardOrderDetails> cardOrderDetails = cardOrderDetailsService.queryByOrderCode(cardOrders.getOrderCode());
        CardOrdersVO cardOrdersVO = new CardOrdersVO();
        BeanUtils.copyProperties(cardOrders, cardOrdersVO);
        cardOrdersVO.setCardOrderDetailsList(cardOrderDetails);
        cardOrdersVO.setOrderDesc(cardOrders.getComments());
        return cardOrdersVO;
    }

    /**
     * 支付成功修改 订单状态
     *
     * @param paySuccess
     * @return
     */
    @Override
    public List<CardOrderDetails> paySuccess(PaySuccess paySuccess) {
        //修改订单支付状态
        this.baseMapper.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date());

        //修改订单明细支付状态
        cardOrderDetailsService.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date());

        //修改支付流水状态
        cardOrderPayTraceService.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date(), paySuccess.getPayCode());

        //查询用户购买的商品,与用户建立绑定关系
        List<CardOrderDetails> cardOrderDetails = cardOrderDetailsService.queryByOrderCode(paySuccess.getOrderCode());
        for (CardOrderDetails cardOrderDetail : cardOrderDetails) {
            CardCards cardCards = cardCardsService.queryByCardCode(cardOrderDetail.getProductionCode());
            CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardOrderDetail.getProductionCode(), paySuccess.getMerchantCode(), cardOrderDetail.getBatchCode());
            cardMapUserCardsService.createCardMapUserCards(cardMapMerchantCards.getCardCode(),
                    Long.parseLong(paySuccess.getUserId()),
                    paySuccess.getMerchantCode(),
                    cardMapMerchantCards.getCardName(),
                    cardCards.getCategoryCode(),
                    cardCards.getCategoryName(),
                    UserCardsStateConfig.UN_USE,
                    UserCardsTypeConfig.VIRTUAL,
                    IdWorker.getIdStr(),
                    UserCardsTraceActionTypeConfig.BUY,
                    UserCardsTraceStateConfig.NORMAL,
                    cardCards.getFaceValue().toString(),
                    cardOrderDetail.getBatchCode(),
                    cardMapMerchantCards.getCardType());
        }
        return cardOrderDetails;
    }

    /**
     * 用户扫码支付下单
     *
     * @param amount
     * @param merchantCode
     * @param paySource
     * @param payType
     */
    @Override
    public PlaceOrderResult merchantQrCodePlaceOrder(Integer amount, String merchantCode, String paySource, String payType) {
        String orderCode = IdWorker.getIdStr();
        CardOrders cardOrders = new CardOrders();
        cardOrders.setComments(CardOrderCommentConfig.MERCHANT_QR_CODE);
        cardOrders.setAmount(amount);
        cardOrders.setOrderCode(orderCode);
        cardOrders.setMerchantCode(merchantCode);
        cardOrders.setUserId(-1L);
        cardOrders.setType(CardOrdersTypeConfig.QR_PAY);
        cardOrders.setState(CardOrdersStateConfig.UNPAID);
        cardOrders.setQuantity(BigDecimal.ZERO);
        cardOrders.setDiscount(0);
        cardOrdersService.save(cardOrders);

        CardOrderDetails cardOrderDetails = new CardOrderDetails();
        cardOrderDetails.setOrderCode(orderCode);
        cardOrderDetails.setAmount(amount);
        cardOrderDetails.setProductionName(CardOrderCommentConfig.MERCHANT_QR_CODE);
        cardOrderDetails.setCreateAt(new Date());
        cardOrderDetails.setUpdateAt(new Date());
        cardOrderDetails.setType(CardOrdersTypeConfig.QR_PAY);
        cardOrderDetails.setState(CardOrdersStateConfig.UNPAID);
        cardOrderDetails.setQuantity(BigDecimal.ONE);
        cardOrderDetails.setDisccount(0);

        cardOrderDetailsService.save(cardOrderDetails);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setType(CardOrdersTypeConfig.QR_PAY);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.UNPAID);
        cardOrderPayTrace.setAmount(amount);
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
        cardOrderPayTrace.setSource(paySource);
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setSourceId("cash_pay");
        cardOrderPayTraceService.save(cardOrderPayTrace);


        PlaceOrderResult placeOrderResult = new PlaceOrderResult();
        placeOrderResult.setAmount(amount);
        placeOrderResult.setMessage("下单成功");
        placeOrderResult.setDiscount(0);
        placeOrderResult.setOrderCode(orderCode);
        return placeOrderResult;
    }

    /**
     * 用户扫商家二维码  支付成功 修改订单数据
     *
     * @param paySuccess
     */
    @Override
    public void merchantQrCodePaySuccess(PaySuccess paySuccess) {
        //修改订单支付状态
        this.baseMapper.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date());

        //修改订单明细支付状态
        cardOrderDetailsService.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date());

        //修改支付流水状态
        cardOrderPayTraceService.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date(), paySuccess.getPayCode());
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean saveOrder(AdjustAccount adjustAccount) {
        CardOrders orders = getOrdersEntity(adjustAccount.getAmount(), adjustAccount.getMerchantCode(),
                adjustAccount.getUserId(), adjustAccount.getComments(), adjustAccount.getOperatorId());
        orders.setType("admin_adjust");
        Boolean result = this.save(orders);

        CardOrderPayTrace trace = getOrderPayTrace(adjustAccount.getAmount(), orders.getOrderCode());
        Boolean traceResult = cardOrderPayTraceService.save(trace);
        return result && traceResult;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean recharge(Recharge recharge) {
        List<CardOrders> orders = new ArrayList<>();
        List<CardOrderPayTrace> traces = new ArrayList<>();
        recharge.getUserIds().forEach(e -> {
            CardOrders orderEntity = this.getOrdersEntity(recharge.getAmount(), recharge.getMerchantCode(), e, recharge.getOperatorId());
            orderEntity.setType("admin_recharge");
            orders.add(orderEntity);
            CardOrderPayTrace trace = this.getOrderPayTrace(recharge.getAmount(), orderEntity.getOrderCode());
            traces.add(trace);
        });
        return this.saveBatch(orders) && cardOrderPayTraceService.saveBatch(traces);
    }

    @Override
    public List<OrdersVo> getAdjustAccountOrders(String merchantCode, IPage<OrdersVo> page) {
        return this.baseMapper.getAdminOrderList(merchantCode, "admin_adjust", page);
    }

    @Override
    public List<OrdersVo> getRechargeOrders(String merchantCode, String orderType, IPage<OrdersVo> page) {
        return this.baseMapper.getAdminOrderList(merchantCode, orderType, page);
    }

    /**
     * 组合支付创建订单,流水
     *
     * @param userId
     * @param amount
     * @param couponPayMoney
     * @param needPayMoney
     * @param merchantCode
     * @param orderCode
     * @param accountCardNo
     * @param cardPayDetailData
     */
    @Override
    public void accountPayCreateOrderAndDetailAndTrace(Long userId, Integer amount, Integer couponPayMoney,
                                                       Integer needPayMoney, String merchantCode, String orderCode,
                                                       String accountCardNo, CardPayDetailData cardPayDetailData) {
        List<PosSelectCardNo> cardNoList = cardPayDetailData.getCardNoList();
        Map<String, Integer> couponMoneyMap = cardPayDetailData.getCouponMoneyMap();

        CardOrders cardOrders = new CardOrders();
        cardOrders.setComments(CardOrderCommentConfig.POS_VIP_PAY);
//        cardOrders.setAmount(needPayMoney);
        cardOrders.setAmount(amount);
        cardOrders.setOrderCode(orderCode);
        cardOrders.setMerchantCode(merchantCode);
        cardOrders.setUserId(userId);
        cardOrders.setType(CardOrdersTypeConfig.POS_VIP_PAY);
        cardOrders.setState(CardOrdersStateConfig.PAID);
        cardOrders.setQuantity(BigDecimal.ONE);
//        cardOrders.setDiscount(couponPayMoney);
        cardOrders.setDiscount(0);
        cardOrdersService.save(cardOrders);

        CardOrderDetails cardOrderDetails = new CardOrderDetails();
        cardOrderDetails.setOrderCode(orderCode);
//        cardOrderDetails.setAmount(needPayMoney);
        cardOrderDetails.setAmount(amount);
        cardOrderDetails.setProductionName(CardOrderCommentConfig.POS_VIP_PAY);
        cardOrderDetails.setCreateAt(new Date());
        cardOrderDetails.setUpdateAt(new Date());
        cardOrderDetails.setType(CardOrdersTypeConfig.POS_VIP_PAY);
        cardOrderDetails.setState(CardOrdersStateConfig.PAID);
        cardOrderDetails.setQuantity(BigDecimal.ONE);
//        cardOrderDetails.setDisccount(couponPayMoney);
        cardOrderDetails.setDisccount(0);
        cardOrderDetails.setProductionCode(JSONObject.toJSONString(cardNoList));
        cardOrderDetailsService.save(cardOrderDetails);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setType(CardOrdersTypeConfig.POS_ACCOUNT_PAY);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setPayCode(accountCardNo);
        cardOrderPayTrace.setAmount(needPayMoney);
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
        cardOrderPayTrace.setSource("pos端会员收银账户余额支出");
        cardOrderPayTrace.setMerchantCode(merchantCode);
        cardOrderPayTrace.setUserFlag(userId + "");
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTraceService.save(cardOrderPayTrace);

        for (PosSelectCardNo posSelectCardNo : cardNoList) {
            Integer couponMoney = couponMoneyMap.get(posSelectCardNo.getCardNo());
            CardOrderPayTrace cardOrderPayTraceCard = new CardOrderPayTrace();
            cardOrderPayTraceCard.setOrderCode(orderCode);
            cardOrderPayTraceCard.setOrderDetailId(cardOrderDetails.getId());
            cardOrderPayTraceCard.setPayCode(posSelectCardNo.getCardNo());
            cardOrderPayTraceCard.setType(CardOrderPayTraceTypeConfig.POS_COUPON_PAY);
            cardOrderPayTraceCard.setState(CardOrderPayTraceStateConfig.PAID);
            cardOrderPayTraceCard.setSource("优惠券抵扣");
            cardOrderPayTraceCard.setSourceId(posSelectCardNo.getCardNo());
            cardOrderPayTraceCard.setAmount(couponMoney);
            cardOrderPayTraceCard.setUserFlag(userId + "");
            cardOrderPayTraceCard.setMerchantCode(merchantCode);
            cardOrderPayTraceCard.setCreateAt(new Date());
            cardOrderPayTraceCard.setUpdateAt(new Date());
            cardOrderPayTraceService.save(cardOrderPayTraceCard);
        }
    }

    /**
     * C端公众号 扫码支付支付下单
     *
     * @param userCashCardPayOrderData
     * @return
     */
    @Override
    public UserCashCardPayOrderReturn userCashCardPayPlaceOrder(UserCashCardPayOrderData userCashCardPayOrderData) {

        if (userCashCardPayOrderData.getIsAccountPay()) {
            //使用余额支付
            UserCashCardPayOrderReturn userCashCardPayOrderReturn = userAccountPay(userCashCardPayOrderData);
            return userCashCardPayOrderReturn;
        } else {
            //不使用余额支付
            UserCashCardPayOrderReturn userCashCardPayOrderReturn = notUserAccountPay(userCashCardPayOrderData);
            return userCashCardPayOrderReturn;
        }
    }

    /**
     * C端卡券现金 组合支付成功 修改数据
     *
     * @param paySuccess
     */
    @Override
    public void paySuccessCashCardOrder(PaySuccess paySuccess) {
        //修改订单支付状态
        this.baseMapper.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date());

        //修改订单明细支付状态
        cardOrderDetailsService.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date());

        //修改支付流水状态
        cardOrderPayTraceService.updateStateByOrderCode(paySuccess.getOrderCode(), CardOrdersStateConfig.PAID, new Date(), paySuccess.getPayCode());


        //查询用户余额需支付多少钱,调取通联支付
        CardOrderPayTrace cardOrderPayTraceAccountPay = cardOrderPayTraceService.queryByOrderCodeAndSourceId(paySuccess.getOrderCode(), CardOrderPayTraceSourceIdConfig.USER_ACCOUNT);

        CardOrderPayTrace cardOrderPayTraceNotAccount = cardOrderPayTraceService.queryByOrderCodeAndSourceId(paySuccess.getOrderCode(), CardOrderPayTraceSourceIdConfig.CASH_PAY);

        CardOrderPayTrace cardOrderPayTrace;
        if (cardOrderPayTraceAccountPay != null) {
            cardOrderPayTrace = cardOrderPayTraceAccountPay;
            try {
                CardMoneyPayUtil.CardMoneyPay(cardOrderPayTrace.getAmount() + "", cardOrderPayTrace.getPayCode(), cardOrderPayTrace.getOrderCode());
            } catch (IOException e) {
                logger.info("通联卡号:" + cardOrderPayTrace.getPayCode() + ",账户卡券支付失败" + e.getMessage());
            }
        } else {
            cardOrderPayTrace = cardOrderPayTraceNotAccount;
        }

        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setState(CardOrdersStateConfig.PAID);
        cardOrderPayTraceService.updateById(cardOrderPayTrace);

        //查询用户使用卡券 修改卡券状态
        CardOrderDetails cardOrderDetails = cardOrderDetailsService.queryByDetailId(cardOrderPayTrace.getOrderDetailId().toString());
        String productionCode = cardOrderDetails.getProductionCode();
        List<String> list = JSONObject.parseObject(productionCode, List.class);
        for (String cardNo : list) {
            cardMapUserCardsService.updateStateByCardNo(cardNo, UserCardsStateConfig.USED);
            //记录卡券使用流水
            CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(cardNo);
            cardMapUserCardsTraceService.createCardMapUserCardsTrace(cardMapUserCards.getUserId(),
                    cardMapUserCards.getMerchantCode(),
                    cardMapUserCards.getCardCode(), cardNo, UserCardsTraceActionTypeConfig.C_QR_USED,
                    new Date(), UserCardsTraceStateConfig.NORMAL, cardMapUserCards.getBatchCode());
        }
    }

    @Override
    public CardOrders getOrder(String orderCode) {
        LambdaQueryWrapper<CardOrders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CardOrders::getOrderCode, orderCode);
        return this.getOne(wrapper);
    }

    /**
     * C端公众号 扫码支付下单 不使用余额支付
     *
     * @param userCashCardPayOrderData
     * @return
     */
    private UserCashCardPayOrderReturn notUserAccountPay(UserCashCardPayOrderData userCashCardPayOrderData) {
        List<String> cardNoList = userCashCardPayOrderData.getCardNoList();
        //创建返回数据
        UserCashCardPayOrderReturn userCashCardPayOrderReturn = new UserCashCardPayOrderReturn();
        Integer payMoney = 0;
        if (cardNoList != null && cardNoList.size() > 0) {
            for (String cardNo : cardNoList) {
                CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(cardNo);
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(),
                        cardMapUserCards.getMerchantCode(),
                        cardMapUserCards.getBatchCode());
                if (cardMapMerchantCards == null) {
                    CardCards cardCards = cardCardsService.queryByCardCode(cardMapUserCards.getCardCode());
                    if ("discount".equals(cardCards.getType())) {
                        int discount = cardCards.getFaceValue();
                        Double d = (100 - discount) * 0.01;
                        Double v = userCashCardPayOrderData.getAmount() * d;
                        payMoney = v.intValue();
                    } else {
                        int parseIntCardValue = cardCards.getFaceValue();
                        payMoney = payMoney + parseIntCardValue;
                    }
                } else {
                    if ("discount".equals(cardMapMerchantCards.getCardType())) {
                        String cardFaceValue = cardMapMerchantCards.getCardFaceValue();
                        int discount = Integer.parseInt(cardFaceValue);
                        Double d = (100 - discount) * 0.01;
                        Double v = userCashCardPayOrderData.getAmount() * d;
                        payMoney = v.intValue();
                    } else {
                        int parseIntCardValue = Integer.parseInt(cardMapMerchantCards.getCardFaceValue());
                        payMoney = payMoney + parseIntCardValue;
                    }
                }
            }
            userCashCardPayOrderReturn.setUseCardFlag(true);
            userCashCardPayOrderReturn.setUseCardMessage("使用卡券");
        }

        Integer needPayMoney = userCashCardPayOrderData.getAmount() - payMoney;
        if (needPayMoney > 0) {
            String orderCode = IdWorker.getIdStr();
            // 创建未支付订单
            userCashCardPayCreateOrder(
                    userCashCardPayOrderData.getUserId(),
                    needPayMoney,
                    payMoney,
                    needPayMoney,
                    userCashCardPayOrderData.getMerchantCode(),
                    orderCode,
                    cardNoList,
                    CardOrdersStateConfig.UNPAID,
                    CardOrdersTypeConfig.CONSUME,
                    CardOrderCommentConfig.CONSUME,
                    CardOrderPayTraceSourceDescConfig.CONSUME,
                    CardOrderPayTraceSourceIdConfig.CASH_PAY
            );


            //返回数据
            userCashCardPayOrderReturn.setAmount(needPayMoney);
            userCashCardPayOrderReturn.setIsToPay(true);
            userCashCardPayOrderReturn.setOrderCode(orderCode);
            return userCashCardPayOrderReturn;
        } else {
            String orderCode = IdWorker.getIdStr();
            // 创建已支付订单
            userCashCardPayCreateOrder(
                    userCashCardPayOrderData.getUserId(),
                    0,
                    payMoney,
                    0,
                    userCashCardPayOrderData.getMerchantCode(),
                    orderCode,
                    cardNoList,
                    CardOrdersStateConfig.PAID,
                    CardOrdersTypeConfig.CONSUME,
                    CardOrderCommentConfig.CONSUME,
                    CardOrderPayTraceSourceDescConfig.CONSUME,
                    CardOrderPayTraceSourceIdConfig.CASH_PAY
            );

            //修改卡券状态
            List<PosSelectCardNo> posSelectCardNOList = new ArrayList<>();
            for (String cardNo : cardNoList) {
                PosSelectCardNo posSelectCardNO = new PosSelectCardNo();
                posSelectCardNO.setCardNo(cardNo);
                posSelectCardNOList.add(posSelectCardNO);
            }
            cardMapUserCardsService.updateUserCardsState(posSelectCardNOList, UserCardsStateConfig.USED);

            //返回数据
            userCashCardPayOrderReturn.setAmount(0);
            userCashCardPayOrderReturn.setIsToPay(false);
            userCashCardPayOrderReturn.setOrderCode(orderCode);
            return userCashCardPayOrderReturn;
        }
    }

    /**
     * C端公众号 扫码支付支付下单 使用余额支付
     *
     * @param userCashCardPayOrderData
     * @return
     */
    public UserCashCardPayOrderReturn userAccountPay(UserCashCardPayOrderData userCashCardPayOrderData) {
        List<String> cardNoList = userCashCardPayOrderData.getCardNoList();
        //创建返回数据
        UserCashCardPayOrderReturn userCashCardPayOrderReturn = new UserCashCardPayOrderReturn();
        Integer payMoney = 0;
        if (cardNoList != null && cardNoList.size() > 0) {
            for (String cardNo : cardNoList) {
                CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(cardNo);
                CardMapMerchantCards cardMapMerchantCards = cardMapMerchantCardsService.queryByCardCodeAndMerchantCodeBatchCode(cardMapUserCards.getCardCode(),
                        cardMapUserCards.getMerchantCode(),
                        cardMapUserCards.getBatchCode());
                if (cardMapMerchantCards == null) {
                    CardCards cardCards = cardCardsService.queryByCardCode(cardMapUserCards.getCardCode());
                    if ("discount".equals(cardCards.getType())) {
                        int discount = cardCards.getFaceValue();
                        Double d = (100 - discount) * 0.01;
                        Double v = userCashCardPayOrderData.getAmount() * d;
                        payMoney = v.intValue();
                    } else {
                        int parseIntCardValue = cardCards.getFaceValue();
                        payMoney = payMoney + parseIntCardValue;
                    }
                } else {
                    if ("discount".equals(cardMapMerchantCards.getCardType())) {
                        String cardFaceValue = cardMapMerchantCards.getCardFaceValue();
                        int discount = Integer.parseInt(cardFaceValue);
                        Double d = (100 - discount) * 0.01;
                        Double v = userCashCardPayOrderData.getAmount() * d;
                        payMoney = v.intValue();
                    } else {
                        int parseIntCardValue = Integer.parseInt(cardMapMerchantCards.getCardFaceValue());
                        payMoney = payMoney + parseIntCardValue;
                    }
                }

            }
            userCashCardPayOrderReturn.setUseCardFlag(true);
            userCashCardPayOrderReturn.setUseCardMessage("使用卡券");
        }

        Integer needPayMoney = userCashCardPayOrderData.getAmount() - payMoney;
        if (needPayMoney < 0) {
            needPayMoney = 0;
        }
        BigDecimal userMoney = cardMapUserCardsService.queryUserMoney(userCashCardPayOrderData.getUserId());
        int userMoneyInt = Integer.parseInt(userMoney.toString());
        CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByUserIdAndAccount(userCashCardPayOrderData.getUserId(), UserCardsTypeConfig.ACCOUNT);
        if (userMoneyInt - needPayMoney >= 0) {
            String orderCode = IdWorker.getIdStr();
            try {
                CardMoneyPayUtil.CardMoneyPay(needPayMoney.toString(), cardMapUserCards.getCardNo(), orderCode);
            } catch (IOException e) {
                logger.info("通联卡号:" + cardMapUserCards.getCardCode() + ",账户卡券支付失败" + e.getMessage());
            }
            // 创建已支付订单
            OrderRet orderRet = userCashCardPayCreateOrder(
                    userCashCardPayOrderData.getUserId(),
                    needPayMoney,
                    payMoney,
                    needPayMoney,
                    userCashCardPayOrderData.getMerchantCode(),
                    orderCode,
                    cardNoList,
                    CardOrdersStateConfig.PAID,
                    CardOrdersTypeConfig.CONSUME,
                    CardOrderCommentConfig.CONSUME,
                    CardOrderPayTraceSourceDescConfig.CONSUME,
                    CardOrderPayTraceSourceIdConfig.USER_ACCOUNT
            );


            //修改卡券状态
            List<PosSelectCardNo> posSelectCardNOList = new ArrayList<>();
            for (String cardNo : cardNoList) {
                PosSelectCardNo posSelectCardNO = new PosSelectCardNo();
                posSelectCardNO.setCardNo(cardNo);
                posSelectCardNOList.add(posSelectCardNO);
            }
            cardMapUserCardsService.updateUserCardsState(posSelectCardNOList, UserCardsStateConfig.USED);

            //返回数据
            userCashCardPayOrderReturn.setAmount(0);
            userCashCardPayOrderReturn.setIsToPay(false);
            userCashCardPayOrderReturn.setOrderCode(orderCode);
            return userCashCardPayOrderReturn;
        } else {
            Integer cashPayAmount = (userMoneyInt - needPayMoney) * -1;
            String orderCode = IdWorker.getIdStr();

            // 创建未支付订单
            OrderRet orderRet = userCashCardPayCreateOrder(
                    userCashCardPayOrderData.getUserId(),
                    needPayMoney,
                    payMoney,
                    cashPayAmount,
                    userCashCardPayOrderData.getMerchantCode(),
                    orderCode,
                    cardNoList,
                    CardOrdersStateConfig.UNPAID,
                    CardOrdersTypeConfig.CONSUME,
                    CardOrderCommentConfig.CONSUME,
                    CardOrderPayTraceSourceDescConfig.CONSUME,
                    CardOrderPayTraceSourceIdConfig.CASH_PAY
            );

            cardOrderPayTraceService.createPayTrace(orderRet.getOrderCode(),
                    orderRet.getOrderDetailId(),
                    CardOrdersTypeConfig.CONSUME,
                    CardOrdersStateConfig.UNPAID,
                    CardOrderPayTraceSourceDescConfig.USER_ACCOUNT,
                    CardOrderPayTraceSourceIdConfig.USER_ACCOUNT,
                    userMoneyInt,
                    cardMapUserCards.getCardNo());

            //返回数据
            userCashCardPayOrderReturn.setAmount(cashPayAmount);
            userCashCardPayOrderReturn.setIsToPay(true);
            userCashCardPayOrderReturn.setOrderCode(orderCode);
            return userCashCardPayOrderReturn;
        }
    }

    private CardOrderPayTrace getOrderPayTrace(Integer amount, String orderCode) {
        CardOrderPayTrace trace = new CardOrderPayTrace();
        trace.setAmount(amount);
        trace.setOrderCode(orderCode);
        trace.setSource("后台充值");
        return trace;
    }

    private CardOrders getOrdersEntity(Integer amount, String merchantCode, Long userId, String comments, Long adminId) {
        CardOrders orders = this.getOrdersEntity(amount, merchantCode, userId, adminId);
        orders.setComments(comments);
        return orders;
    }

    private CardOrders getOrdersEntity(Integer amount, String merchantCode, Long userId, Long adminId) {
        CardOrders orders = new CardOrders();
        orders.setAmount(amount);
        orders.setSaleId(String.valueOf(adminId));
        orders.setState("待审核");
        orders.setMerchantCode(merchantCode);
        orders.setUserId(userId);
        orders.setOrderCode(IdWorker.getIdStr());
        return orders;
    }


    /**
     * C端 组合支付 创建订单
     *
     * @param userId
     * @param amount
     * @param discount
     * @param needPayMoney
     * @param merchantCode
     * @param orderCode
     * @param cardNoList
     * @param orderState
     * @param orderType
     * @param comment
     */
    private OrderRet userCashCardPayCreateOrder(Long userId,
                                                Integer amount,
                                                Integer discount,
                                                Integer needPayMoney,
                                                String merchantCode,
                                                String orderCode,
                                                List cardNoList,
                                                String orderState,
                                                String orderType,
                                                String comment,
                                                String source,
                                                String sourceId) {

        CardOrders cardOrders = new CardOrders();
        cardOrders.setComments(comment);
        cardOrders.setAmount(amount);
        cardOrders.setOrderCode(orderCode);
        cardOrders.setMerchantCode(merchantCode);
        cardOrders.setUserId(userId);
        cardOrders.setType(orderType);
        cardOrders.setState(orderState);
        cardOrders.setQuantity(BigDecimal.ONE);
        cardOrders.setDiscount(discount);
        cardOrdersService.save(cardOrders);

        CardOrderDetails cardOrderDetails = new CardOrderDetails();
        cardOrderDetails.setProductionCode(JSONObject.toJSONString(cardNoList));
        cardOrderDetails.setOrderCode(orderCode);
        cardOrderDetails.setAmount(amount);
        cardOrderDetails.setProductionName(comment);
        cardOrderDetails.setCreateAt(new Date());
        cardOrderDetails.setUpdateAt(new Date());
        cardOrderDetails.setType(orderType);
        cardOrderDetails.setState(orderState);
        cardOrderDetails.setQuantity(BigDecimal.ONE);
        cardOrderDetails.setDisccount(discount);
        cardOrderDetailsService.save(cardOrderDetails);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setType(orderType);
        cardOrderPayTrace.setState(orderState);
        cardOrderPayTrace.setAmount(needPayMoney);
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setSource(source);
        cardOrderPayTrace.setSourceId(sourceId);
        cardOrderPayTraceService.save(cardOrderPayTrace);

        OrderRet orderRet = new OrderRet();
        orderRet.setOrderCode(orderCode);
        orderRet.setOrderDetailId(cardOrderDetails.getId());
        orderRet.setPayTraceId(cardOrderPayTrace.getId());
        return orderRet;
    }

    @Override
    public List<OrdersVo> getOrderList(String merchantCode, IPage<OrdersVo> page) {
        return this.baseMapper.getOrderList(merchantCode, page);
    }

}
