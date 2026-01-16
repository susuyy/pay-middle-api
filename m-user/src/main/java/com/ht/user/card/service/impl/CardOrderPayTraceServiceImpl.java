package com.ht.user.card.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.*;
import com.ht.user.card.excel.ConsumeCardOrderExcelVo;
import com.ht.user.card.mapper.CardOrderPayTraceMapper;
import com.ht.user.card.service.CardMapUserCardsService;
import com.ht.user.card.service.CardOrderDetailsService;
import com.ht.user.card.service.CardOrderPayTraceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.card.service.CardOrdersService;
import com.ht.user.card.vo.CardOrdersVO;
import com.ht.user.card.vo.PosPayTraceData;
import com.ht.user.card.vo.PosSelectCardNo;
import com.ht.user.config.*;
import com.ht.user.ordergoods.entity.CardOrdersGoods;
import com.ht.user.ordergoods.entity.UploadOrderDetails;
import com.ht.user.ordergoods.service.CardOrdersGoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.smartcardio.Card;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单支付流水 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
@Service
public class CardOrderPayTraceServiceImpl extends ServiceImpl<CardOrderPayTraceMapper, CardOrderPayTrace> implements CardOrderPayTraceService {

    private Logger logger = LoggerFactory.getLogger(CardOrderPayTraceServiceImpl.class);

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    @Autowired
    private CardOrdersService cardOrdersService;

    @Autowired
    private CardOrderDetailsService cardOrderDetailsService;

    @Autowired
    private CardOrdersGoodsService cardOrdersGoodsService;

    /**
     * 根据pos机串号查询消费流水记录
     * @param posSerialNum
     * @return
     */
    @Override
    public List<CardOrderPayTrace> queryListByPosSerialNum(String posSerialNum) {
        return this.baseMapper.selectListByPosSerialNum(posSerialNum);
    }

    /**
     * 根据会员卡号-payCode查询消费交易流水记录
     * @param payCode
     * @return
     */
    @Override
    public List<CardOrderPayTrace> queryListByPayCode(String payCode) {
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("pay_code",payCode);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void createCouponCardPayTrace(Long userId,
                                         String merchantCode,
                                         String orderCode,
                                         CardPayDetailData cardPayDetailData) {
        CardOrdersVO cardOrdersVO = cardOrdersService.queryByOrderCode(orderCode);
        cardOrdersService.updateStateByOrderCode(orderCode,CardOrderPayTraceStateConfig.PAID);
        cardOrderDetailsService.updateStateByOrderCode(orderCode,CardOrderPayTraceStateConfig.PAID,new Date());

        List<PosSelectCardNo> cardNoList = cardPayDetailData.getCardNoList();
        Map<String, Integer> couponMoneyMap = cardPayDetailData.getCouponMoneyMap();

        Integer saveAmount = cardOrdersVO.getAmount();

        CardOrderPayTrace cardOrderPayTraceOriMis = queryTraceByOrderCode(orderCode).get(0);
        for (PosSelectCardNo posSelectCardNo : cardNoList) {
            Integer couponMoney = couponMoneyMap.get(posSelectCardNo.getCardNo());
            CardOrderPayTrace cardOrderPayTraceCard = new CardOrderPayTrace();
            cardOrderPayTraceCard.setOrderCode(orderCode);
            cardOrderPayTraceCard.setOrderDetailId(cardOrderPayTraceOriMis.getOrderDetailId());
            cardOrderPayTraceCard.setPayCode(cardOrderPayTraceOriMis.getPayCode());
            cardOrderPayTraceCard.setType(CardOrderPayTraceTypeConfig.POS_COUPON_PAY);
            cardOrderPayTraceCard.setState(CardOrderPayTraceStateConfig.PAID);
            cardOrderPayTraceCard.setSource(cardOrderPayTraceOriMis.getSource());
            cardOrderPayTraceCard.setSourceId(posSelectCardNo.getCardNo());

            if (saveAmount>=couponMoney) {
                cardOrderPayTraceCard.setAmount(couponMoney);
                saveAmount = saveAmount - couponMoney;
            }else {
                cardOrderPayTraceCard.setAmount(saveAmount);
            }

            cardOrderPayTraceCard.setUserFlag(userId+"");
            cardOrderPayTraceCard.setMerchantCode(merchantCode);
            cardOrderPayTraceCard.setPosSerialNum(cardOrderPayTraceOriMis.getPosSerialNum());
            cardOrderPayTraceCard.setMerchId(cardOrderPayTraceOriMis.getMerchId());
            cardOrderPayTraceCard.setMerchName(cardOrderPayTraceOriMis.getMerchName());
            cardOrderPayTraceCard.setCreateAt(new Date());
            cardOrderPayTraceCard.setUpdateAt(new Date());
            save(cardOrderPayTraceCard);
        }

//        if (CardOrderPayTraceStateConfig.UNPAID.equals(cardOrderPayTraceOriMis.getState())){
//            removeById(cardOrderPayTraceOriMis);
//        }else {
//            cardOrderPayTraceOriMis.setUserFlag(userId+"");
//            updateById(cardOrderPayTraceOriMis);
//        }
    }

    /**
     * pos端成功后创建支付流水
     * @param posPayTraceData
     */
    @Override
    public void createPosPayTrace(PosPayTraceData posPayTraceData) {
        String orderCode = IdWorker.getIdStr();
        CardOrders cardOrders=new CardOrders();
        cardOrders.setAmount(posPayTraceData.getAmount());
        cardOrders.setMerchantCode(posPayTraceData.getMerchantCode());
        cardOrders.setOrderCode(orderCode);
        cardOrders.setDiscount(0);
        cardOrders.setQuantity(BigDecimal.ONE);
        cardOrders.setComments(CardOrderCommentConfig.POS_USER_TOP_UP);
        cardOrders.setCreateAt(new Date());
        cardOrders.setUpdateAt(new Date());
        cardOrders.setState(CardOrdersStateConfig.PAID);
        cardOrders.setType(CardOrdersTypeConfig.POS_USER_TOP_UP);
        cardOrders.setSaleId("POS_ADMIN");
        cardOrders.setUserId(posPayTraceData.getUserId());
        cardOrdersService.save(cardOrders);

        CardOrderDetails cardOrderDetails=new CardOrderDetails();
        cardOrderDetails.setOrderCode(orderCode);
        cardOrderDetails.setUpdateAt(new Date());
        cardOrderDetails.setCreateAt(new Date());
        cardOrderDetails.setProductionName(CardOrderCommentConfig.POS_USER_TOP_UP);
        cardOrderDetails.setAmount(posPayTraceData.getAmount());
        cardOrderDetails.setState(CardOrdersStateConfig.PAID);
        cardOrderDetails.setType(CardOrdersTypeConfig.POS_USER_TOP_UP);
        cardOrderDetails.setQuantity(BigDecimal.ONE);
        cardOrderDetails.setDisccount(0);
        cardOrderDetailsService.save(cardOrderDetails);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
        cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setPayCode(posPayTraceData.getTraceNo());
        cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
        cardOrderPayTrace.setSource(CardOrderPayTraceSourceDescConfig.POS_VIP_TOP_UP);
        cardOrderPayTrace.setSourceId(posPayTraceData.getCardNo());
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.POS);
        this.baseMapper.insert(cardOrderPayTrace);

    }

    /**
     * 根据订单号修改支付流水状态
     * @param orderCode
     * @param state
     * @param date
     * @param payCode
     */
    @Override
    public void updateStateByOrderCode(String orderCode, String state, Date date, String payCode) {
        this.baseMapper.updateStateByOrderCode(orderCode,state,date,payCode);
    }

    /**
     * pos 会员收银创建支付流水
     * @param posPayTraceData
     */
    @Override
    public String createPosPayTraceFromCashier(PosPayTraceData posPayTraceData) {
        String orderCode = posPayTraceData.getOrderCode();
        if (StringUtils.isEmpty(orderCode) || "null".equals(orderCode)){
            orderCode = IdWorker.getIdStr();
        }


        CardOrders cardOrders=new CardOrders();

        cardOrders.setUserId(posPayTraceData.getUserId());
        cardOrders.setAmount(posPayTraceData.getAmount());
        cardOrders.setMerchantCode(posPayTraceData.getMerchantCode());
        cardOrders.setOrderCode(orderCode);
        cardOrders.setDiscount(0);
        cardOrders.setQuantity(BigDecimal.ONE);
        cardOrders.setComments(CardOrderCommentConfig.POS_CASH);
        cardOrders.setCreateAt(new Date());
        cardOrders.setUpdateAt(new Date());
        cardOrders.setState(CardOrdersStateConfig.PAID);
        cardOrders.setType(CardOrdersTypeConfig.POS_CASH);
        cardOrders.setSaleId("POS_ADMIN");
        cardOrdersService.save(cardOrders);

        CardOrderDetails cardOrderDetails=new CardOrderDetails();
        cardOrderDetails.setOrderCode(orderCode);
        cardOrderDetails.setUpdateAt(new Date());
        cardOrderDetails.setCreateAt(new Date());
        cardOrderDetails.setProductionName(CardOrderCommentConfig.POS_CASH);
        cardOrderDetails.setAmount(posPayTraceData.getAmount());
        cardOrderDetails.setState(CardOrdersStateConfig.PAID);
        cardOrderDetails.setType(CardOrdersTypeConfig.POS_CASH);
        cardOrderDetails.setQuantity(BigDecimal.ONE);
        cardOrderDetails.setDisccount(0);
        cardOrderDetailsService.save(cardOrderDetails);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(-1L);
        cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        cardOrderPayTrace.setPayCode(posPayTraceData.getTraceNo());
        cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
        cardOrderPayTrace.setSource(CardOrderPayTraceSourceDescConfig.POS_CASH);
        cardOrderPayTrace.setSourceId(posPayTraceData.getCardNo());
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.POS);
        cardOrderPayTrace.setUserFlag(posPayTraceData.getUserId()+"");
        cardOrderPayTrace.setMerchId(posPayTraceData.getMerchId());
        cardOrderPayTrace.setMerchName(posPayTraceData.getMerchName());
        cardOrderPayTrace.setMerchantCode(posPayTraceData.getMerchantCode());
        this.baseMapper.insert(cardOrderPayTrace);
        return orderCode;
    }

    /**
     * C 端组合支付创建流水
     * @param orderCode
     * @param orderDetailId
     * @param type
     * @param state
     * @param source
     * @param sourceId
     * @param userMoneyInt
     * @param payCode
     */
    @Override
    public void createPayTrace(String orderCode, Long orderDetailId, String type, String state, String source, String sourceId, int userMoneyInt, String payCode) {
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setSource(source);
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(orderDetailId);
        cardOrderPayTrace.setType(type);
        cardOrderPayTrace.setState(state);
        cardOrderPayTrace.setAmount(userMoneyInt);
        cardOrderPayTrace.setSourceId(sourceId);
        cardOrderPayTrace.setPayCode(payCode);
        this.baseMapper.insert(cardOrderPayTrace);
    }

    @Override
    public CardOrderPayTrace queryByOrderCodeAndSourceId(String orderCode, String sourceId) {
        return this.baseMapper.selectByOrderCodeAndSourceId(orderCode,sourceId);
    }

    @Override
    public CardOrderPayTrace queryTraceByOrderCodeAndCashPay(String orderCode) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("order_code",orderCode);
        queryWrapper.eq("source_id","cash_pay");
        return this.baseMapper.selectOne(queryWrapper);
    }

    /**
     * 创建直接支付的订单流水
     * @param posPayTraceData
     */
    @Override
    public void createUsuallyUserPayTrace(PosPayTraceData posPayTraceData) {
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        String orderCode = IdWorker.getIdStr();
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setOrderDetailId(-1L);
        cardOrderPayTrace.setPayCode(posPayTraceData.getTraceNo());
        cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.POS);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setSource("非会员用户直接支付");
        cardOrderPayTrace.setSourceId(posPayTraceData.getCardNo());
        cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
        cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
        cardOrderPayTrace.setUserFlag("normal user");
        cardOrderPayTrace.setMerchantCode(posPayTraceData.getMerchantCode());
        cardOrderPayTrace.setMerchId(posPayTraceData.getMerchId());
        cardOrderPayTrace.setMerchName(posPayTraceData.getMerchName());
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        this.baseMapper.insert(cardOrderPayTrace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createMisOrderPayTrace(MisOrderData misOrderData) {
        QueryWrapper<CardOrders> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",misOrderData.getOrderCode());
        CardOrders cardOrdersVO = cardOrdersService.getOne(queryWrapper);

        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        if (cardOrdersVO==null){
            CardOrders cardOrders = new CardOrders();
            cardOrders.setOrderCode(misOrderData.getOrderCode());
            cardOrders.setType(CardOrdersTypeConfig.CONSUME);
            cardOrders.setState(CardOrderPayTraceStateConfig.UNPAID);
            cardOrders.setMerchantCode(misOrderData.getMerchantCode());
            cardOrders.setUserId(-1L);
            cardOrders.setQuantity(BigDecimal.ONE);
            cardOrders.setAmount(Integer.parseInt(misOrderData.getAmount()));
            cardOrders.setDiscount(0);
            cardOrders.setComments("海旅云mis订单");
            cardOrders.setLimitPayType(misOrderData.getLimitPayType());
            cardOrders.setCreateAt(new Date());
            cardOrders.setUpdateAt(new Date());
            cardOrders.setStoreCode(misOrderData.getStoreCode());
            cardOrders.setIdCardNo(misOrderData.getIdCardNo());
            cardOrders.setActualPhone(misOrderData.getActualPhone());
            cardOrdersService.save(cardOrders);

            //记录订单上送的商品数据
            if (!StringUtils.isEmpty(misOrderData.getOrderDetail()) && !"null".equals(misOrderData.getOrderDetail())){
                String orderGoodsStr = misOrderData.getOrderDetail();
                List list = JSONObject.parseObject(orderGoodsStr, List.class);
                for (Object data : list) {
                    UploadOrderDetails uploadOrderDetails = JSONObject.parseObject(JSONObject.toJSONString(data), UploadOrderDetails.class);
                    CardOrdersGoods cardOrdersGoods = new CardOrdersGoods();
                    cardOrdersGoods.setOrderCode(misOrderData.getOrderCode());
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
                    cardOrdersGoodsService.save(cardOrdersGoods);
                }
            }

            CardOrderDetails cardOrderDetails=new CardOrderDetails();
            cardOrderDetails.setOrderCode(misOrderData.getOrderCode());
            cardOrderDetails.setMerchantCode(misOrderData.getMerchantCode());
            cardOrderDetails.setQuantity(BigDecimal.ONE);
            cardOrderDetails.setAmount(Integer.parseInt(misOrderData.getAmount()));
            cardOrderDetails.setState(CardOrderPayTraceStateConfig.UNPAID);
            cardOrderDetails.setType(CardOrdersTypeConfig.CONSUME);
            cardOrderDetails.setDisccount(0);
            cardOrderDetails.setCreateAt(new Date());
            cardOrderDetails.setUpdateAt(new Date());
            cardOrderDetailsService.save(cardOrderDetails);

            cardOrderPayTrace.setOrderDetailId(cardOrderDetails.getId());
        }

        cardOrderPayTrace.setOrderCode(misOrderData.getOrderCode());
        String cloudMisTrxSsn = IdWorker.getIdStr();
        cardOrderPayTrace.setPayCode(StringUtils.isEmpty(misOrderData.getPayCode()) ? cloudMisTrxSsn : misOrderData.getPayCode());

        cardOrderPayTrace.setType(CardOrdersTypeConfig.POS_MIS_ORDER);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.UNPAID);
        cardOrderPayTrace.setSource("云mis订单流水");
        cardOrderPayTrace.setAmount(Integer.parseInt(misOrderData.getAmount()));
        cardOrderPayTrace.setPosSerialNum(misOrderData.getCashId());
        cardOrderPayTrace.setCashId(misOrderData.getCashId());
        cardOrderPayTrace.setUserFlag("normal user");
        cardOrderPayTrace.setMerchantCode(misOrderData.getMerchantCode());
        cardOrderPayTrace.setMerchId(misOrderData.getStoreId());
        cardOrderPayTrace.setMerchName("海旅免税店");
        cardOrderPayTrace.setRefTraceNo(StringUtils.isEmpty(misOrderData.getPayCode()) ? cloudMisTrxSsn : misOrderData.getPayCode());
        cardOrderPayTrace.setTraceNo(IdWorker.getIdStr());
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        this.baseMapper.insert(cardOrderPayTrace);

        return cloudMisTrxSsn;
    }

    @Override
    public void updateMisOrderState(PosPayTraceData posPayTraceData) {
        cardOrdersService.updateStateByOrderCode(posPayTraceData.getOrderCode(),CardOrderPayTraceStateConfig.PAID);
        cardOrderDetailsService.updateStateByOrderCode(posPayTraceData.getOrderCode(),CardOrderPayTraceStateConfig.PAID,new Date());

        List<CardOrderPayTrace> cardOrderPayTraces = queryTraceByOrderCodeUnPaidType(posPayTraceData.getOrderCode(),CardOrderPayTraceStateConfig.UNPAID, CardOrdersTypeConfig.POS_MIS_ORDER);
//        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("order_code",posPayTraceData.getOrderCode());
//        List<CardOrderPayTrace> cardOrderPayTraces = this.baseMapper.selectList(queryWrapper);

//        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
//            cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
//            cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
//            cardOrderPayTrace.setSource(posPayTraceData.getTraceNo());
//            cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
//            this.baseMapper.updateById(cardOrderPayTrace);
//        }

        // 创建金额支付流水
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(posPayTraceData.getOrderCode());
        cardOrderPayTrace.setOrderDetailId(cardOrderPayTraces.get(0).getOrderDetailId());
        cardOrderPayTrace.setPayCode(cardOrderPayTraces.get(0).getPayCode());
        cardOrderPayTrace.setType("cash_pay");
        cardOrderPayTrace.setState("paid");
        cardOrderPayTrace.setSource(cardOrderPayTraces.get(0).getSource());
        cardOrderPayTrace.setSourceId(posPayTraceData.getTraceNo());
        cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
        cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
        cardOrderPayTrace.setUserFlag(cardOrderPayTraces.get(0).getUserFlag());
        cardOrderPayTrace.setMerchantCode(cardOrderPayTraces.get(0).getMerchantCode());
        cardOrderPayTrace.setMerchId(cardOrderPayTraces.get(0).getMerchId());
        cardOrderPayTrace.setMerchName(cardOrderPayTraces.get(0).getMerchName());
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        this.baseMapper.insert(cardOrderPayTrace);
    }


    @Override
    public void updateVipMisOrderState(PosPayTraceData posPayTraceData) {
        cardOrdersService.updateStateAndUserIdByOrderCode(posPayTraceData.getOrderCode(),CardOrderPayTraceStateConfig.PAID,posPayTraceData.getUserId());
        cardOrderDetailsService.updateStateByOrderCode(posPayTraceData.getOrderCode(),CardOrderPayTraceStateConfig.PAID,new Date());
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",posPayTraceData.getOrderCode());
        List<CardOrderPayTrace> cardOrderPayTraces = this.baseMapper.selectList(queryWrapper);
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
            cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
            cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
            cardOrderPayTrace.setSource(posPayTraceData.getTraceNo());
            cardOrderPayTrace.setUserFlag(posPayTraceData.getUserId()+"");
            this.baseMapper.updateById(cardOrderPayTrace);
        }
        CardOrderPayTrace cardOrderPayTraceOne = cardOrderPayTraces.get(0);
        List<PosSelectCardNo> cardNoList = posPayTraceData.getCardNoList();
        for (PosSelectCardNo posSelectCardNo : cardNoList) {
            CardMapUserCards cardMapUserCards = cardMapUserCardsService.queryByCardNo(posSelectCardNo.getCardNo());
            CardOrderPayTrace cardOrderPayTrace=new CardOrderPayTrace();
            cardOrderPayTrace.setOrderCode(posPayTraceData.getOrderCode());
            cardOrderPayTrace.setOrderDetailId(cardOrderPayTraceOne.getOrderDetailId());
            cardOrderPayTrace.setPayCode(posSelectCardNo.getCardNo());
            cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.POS_COUPON_PAY);
            cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
            cardOrderPayTrace.setSource(cardOrderPayTraceOne.getSource());
            cardOrderPayTrace.setSourceId(cardOrderPayTraceOne.getSourceId());
            //金额计算
            String cardType = cardMapUserCards.getCardType();
            if ("discount".equals(cardType)){
                CardOrdersVO cardOrdersVO = cardOrdersService.queryByOrderCode(posPayTraceData.getOrderCode());
                int discountMoney=cardMapUserCardsService.calculateDiscountMoney(cardOrdersVO.getAmount(),cardMapUserCards);
                cardOrderPayTrace.setAmount(discountMoney);
            }else {
                cardOrderPayTrace.setAmount(Integer.parseInt(cardMapUserCards.getFaceValue()));
            }
            cardOrderPayTrace.setPosSerialNum(cardOrderPayTraceOne.getPosSerialNum());
            cardOrderPayTrace.setUserFlag(cardMapUserCards.getUserId()+"");
            cardOrderPayTrace.setMerchantCode(cardOrderPayTraceOne.getMerchantCode());
            cardOrderPayTrace.setMerchId(cardOrderPayTraceOne.getMerchId());
            cardOrderPayTrace.setMerchName(cardOrderPayTraceOne.getMerchName());
            cardOrderPayTrace.setCreateAt(new Date());
            cardOrderPayTrace.setUpdateAt(new Date());
            this.baseMapper.insert(cardOrderPayTrace);
        }
    }

    @Override
    public List<CardOrderPayTrace> queryTraceByOrderCode(String orderCode) {
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void createCashMisPayTrace(PosPayTraceData posPayTraceData) {
        cardOrdersService.updateStateByOrderCode(posPayTraceData.getOrderCode(),CardOrderPayTraceStateConfig.PAID);
        cardOrderDetailsService.updateStateByOrderCode(posPayTraceData.getOrderCode(),CardOrderPayTraceStateConfig.PAID,new Date());

        List<CardOrderPayTrace> cardOrderPayTraces = queryTraceByOrderCode(posPayTraceData.getOrderCode());
        CardOrderPayTrace cardOrderPayTrace = cardOrderPayTraces.get(0);
        cardOrderPayTrace.setType("cash_pay");
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setPayCode(posPayTraceData.getTraceNo());
        cardOrderPayTrace.setAmount(posPayTraceData.getAmount());
        cardOrderPayTrace.setPosSerialNum(posPayTraceData.getTerId());
        cardOrderPayTrace.setUserFlag(posPayTraceData.getUserId()+"");
        cardOrderPayTrace.setUpdateAt(new Date());
        this.baseMapper.updateById(cardOrderPayTrace);
    }

    @Override
    @Transactional
    public void createCardElectronicPayTrace(int consumeMoney, String orderCode, String cardNo, long userId, String terId) {
        //更新订单状态
        cardOrdersService.updateStateByOrderCode(orderCode,CardOrdersStateConfig.PAID);

        //更新订单明细状态
        cardOrderDetailsService.updateStateByOrderCode(orderCode,CardOrdersStateConfig.PAID,new Date());

        List<CardOrderPayTrace> cardOrderPayTraces = queryTraceByOrderCodeUnPaidType(orderCode, CardOrderPayTraceStateConfig.UNPAID, CardOrdersTypeConfig.POS_MIS_ORDER);
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        if (cardOrderPayTraces!=null && cardOrderPayTraces.size()>0){
            CardOrderPayTrace cardOrderPayTraceOriMis = cardOrderPayTraces.get(0);

            String payCode = IdWorker.getIdStr();
            cardOrderPayTrace.setPayCode(StringUtils.isEmpty(cardOrderPayTraceOriMis.getPayCode())? payCode : cardOrderPayTraceOriMis.getPayCode());
            cardOrderPayTrace.setRefTraceNo(StringUtils.isEmpty(cardOrderPayTraceOriMis.getPayCode())? payCode : cardOrderPayTraceOriMis.getPayCode());

            cardOrderPayTrace.setOrderDetailId(cardOrderPayTraceOriMis.getOrderDetailId());

            cardOrderPayTrace.setPosSerialNum(terId);
            cardOrderPayTrace.setCashId(cardOrderPayTraceOriMis.getCashId());

            cardOrderPayTrace.setMerchId(cardOrderPayTraceOriMis.getMerchId());
            cardOrderPayTrace.setMerchName(cardOrderPayTraceOriMis.getMerchName());
            cardOrderPayTrace.setMerchantCode(cardOrderPayTraceOriMis.getMerchantCode());

            this.baseMapper.deleteById(cardOrderPayTraceOriMis);
        }else {
            CardOrderPayTrace orderPayTrace = queryTraceByOrderCode(orderCode).get(0);

            String payCode = IdWorker.getIdStr();
            cardOrderPayTrace.setPayCode(StringUtils.isEmpty(orderPayTrace.getPayCode())? payCode : orderPayTrace.getPayCode());
            cardOrderPayTrace.setRefTraceNo(StringUtils.isEmpty(orderPayTrace.getPayCode())? payCode : orderPayTrace.getPayCode());

            cardOrderPayTrace.setOrderDetailId(orderPayTrace.getOrderDetailId());

            cardOrderPayTrace.setPosSerialNum(terId);
            cardOrderPayTrace.setCashId(orderPayTrace.getCashId());

            cardOrderPayTrace.setMerchId(orderPayTrace.getMerchId());
            cardOrderPayTrace.setMerchName(orderPayTrace.getMerchName());
            cardOrderPayTrace.setMerchantCode(orderPayTrace.getMerchantCode());
        }

        cardOrderPayTrace.setTraceNo(IdWorker.getIdStr());

        cardOrderPayTrace.setSource(CardOrderPayTraceSourceDescConfig.CARD_PHYSICAL);
        cardOrderPayTrace.setType(CardOrdersTypeConfig.CARD_PHYSICAL);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setAmount(consumeMoney);
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setSourceId(cardNo);
        cardOrderPayTrace.setUserFlag(userId+"");
//        cardOrderPayTrace.setCreateAt(new Date());
//        cardOrderPayTrace.setUpdateAt(new Date());
        logger.info("创建支付流水定单,订单号为:"+orderCode);
        this.baseMapper.insert(cardOrderPayTrace);
    }

    @Override
    public List<CardOrderPayTrace> queryPayTraceByOrderCodeAndMerchantCode(String orderCode, String merchantCode) {
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        queryWrapper.eq("merchant_code",merchantCode);
        queryWrapper.ge("amount",0);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public void createPosCombinationPayTrace(List<CardOrderPayTrace> saveCardPayTraceList, Integer totalAmount, Long userId, String storeCode, String actualPhone, String idCardNo) {
        for (CardOrderPayTrace cardOrderPayTrace : saveCardPayTraceList) {
            cardOrderPayTrace.setSource(CardOrderPayTraceSourceDescConfig.CARD_ELECTRONIC);
            cardOrderPayTrace.setType(CardOrdersTypeConfig.CARD_ELECTRONIC);
            cardOrderPayTrace.setMerchId("HLMSD");
            this.baseMapper.insert(cardOrderPayTrace);
        }
        String orderCode = saveCardPayTraceList.get(0).getOrderCode();
        CardOrders cardOrdersQuery = cardOrdersService.getByOrderCode(orderCode);
        if (cardOrdersQuery==null) {
            CardOrders cardOrdersSave = new CardOrders();
            cardOrdersSave.setOrderCode(saveCardPayTraceList.get(0).getOrderCode());
            cardOrdersSave.setType(CardOrdersTypeConfig.CONSUME);
            cardOrdersSave.setState(saveCardPayTraceList.get(0).getState());
            cardOrdersSave.setMerchantCode(saveCardPayTraceList.get(0).getMerchantCode());
            cardOrdersSave.setUserId(userId);
            cardOrdersSave.setAmount(totalAmount);
            cardOrdersSave.setDiscount(0);
            cardOrdersSave.setComments(CardOrderCommentConfig.POS_VIP_PAY);
            cardOrdersSave.setCreateAt(new Date());
            cardOrdersSave.setUpdateAt(new Date());
            cardOrdersSave.setStoreCode(storeCode);
            cardOrdersSave.setActualPhone(actualPhone);
            cardOrdersSave.setIdCardNo(idCardNo);
            cardOrdersService.save(cardOrdersSave);



            CardOrderDetails cardOrderDetails = new CardOrderDetails();
            cardOrderDetails.setOrderCode(saveCardPayTraceList.get(0).getOrderCode());
            cardOrderDetails.setMerchantCode(saveCardPayTraceList.get(0).getMerchantCode());
            cardOrderDetails.setAmount(totalAmount);
            cardOrderDetails.setState(saveCardPayTraceList.get(0).getState());
            cardOrderDetails.setType(CardOrdersTypeConfig.CONSUME);
            cardOrderDetails.setDisccount(0);
            cardOrderDetails.setCreateAt(new Date());
            cardOrderDetails.setUpdateAt(new Date());
            cardOrderDetailsService.save(cardOrderDetails);
        }else {
            cardOrdersService.updateStateByOrderCode(orderCode,CardOrderPayTraceStateConfig.PAID);
            cardOrderDetailsService.updateStateByOrderCode(orderCode,CardOrderPayTraceStateConfig.PAID,new Date());
            QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("type",CardOrdersTypeConfig.POS_MIS_ORDER);
            queryWrapper.eq("order_code",orderCode);
            queryWrapper.eq("state",CardOrderPayTraceStateConfig.UNPAID);
            this.baseMapper.delete(queryWrapper);
        }

    }

    @Override
    public void createPosCombinationCashPay(PosCombinationPaySuccess posCombinationPaySuccess) {
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(posCombinationPaySuccess.getOrderCode());
        cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.CASH_PAY);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setSource(CardOrderPayTraceSourceDescConfig.USER_CASH_PAY);
        cardOrderPayTrace.setAmount(Integer.parseInt(posCombinationPaySuccess.getAmount()));
        cardOrderPayTrace.setMerchantCode(posCombinationPaySuccess.getMerchantCode());
        cardOrderPayTrace.setPayCode(posCombinationPaySuccess.getPayCode());
        cardOrderPayTrace.setMerchId("HLMSD");
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        this.baseMapper.insert(cardOrderPayTrace);
        updateStateByOrderCodeNotPayCode(posCombinationPaySuccess.getOrderCode(),CardOrderPayTraceStateConfig.PAID,new Date());
        cardOrdersService.updateStateByOrderCode(posCombinationPaySuccess.getOrderCode(),CardOrdersStateConfig.PAID);
        cardOrderDetailsService.updateStateByOrderCode(posCombinationPaySuccess.getOrderCode(),CardOrdersStateConfig.PAID,new Date());
    }

    public void updateStateByOrderCodeNotPayCode(String orderCode, String state, Date date) {
        this.baseMapper.updateStateByOrderCodeNotPayCode(orderCode,state,date);
    }

    @Override
    public void updateStateAndPayCodeByOrderCode(String orderCode, String paid, Date date, String payCode) {
        this.baseMapper.updateStateAndPayCodeByOrderCode(orderCode,paid,date,payCode);
    }

    @Override
    public Page<CardOrderPayTrace> posOrderList(SearchPosOrderListData searchPosOrderListData) {
        Page<CardOrderPayTrace> page=new Page<>(searchPosOrderListData.getPageNo(),searchPosOrderListData.getPageSize());
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        if (!StringUtils.isEmpty(searchPosOrderListData.getStartTime())){
            String startTime = searchPosOrderListData.getStartTime();
            startTime = startTime + " 00:00:01";
            queryWrapper.ge("create_at", startTime);
        }
        if (!StringUtils.isEmpty(searchPosOrderListData.getEndTime())){
            String endTime = searchPosOrderListData.getEndTime();
            endTime = endTime + " 23:59:59";
            queryWrapper.le("create_at", endTime);
        }

        if (!StringUtils.isEmpty(searchPosOrderListData.getOrderCode())){
            queryWrapper.like("order_code",searchPosOrderListData.getOrderCode())
            .or()
            .like("order_code",searchPosOrderListData.getOrderCode()).or()
            .like("pay_code",searchPosOrderListData.getOrderCode()).or()
            .like("ref_trace_no",searchPosOrderListData.getOrderCode()).or()
            .like("trace_no",searchPosOrderListData.getOrderCode())
            ;
        }

        if (!StringUtils.isEmpty(searchPosOrderListData.getAmount())){
            queryWrapper.eq("amount",searchPosOrderListData.getAmount());
        }
        queryWrapper.eq("cash_id",searchPosOrderListData.getCashId());
        queryWrapper.ne("state",CardOrdersStateConfig.UNPAID);
        queryWrapper.orderByDesc("create_at");
        return page(page, queryWrapper);
    }

    @Override
    public List<CardOrderPayTrace> querySummaryData(String startTime, String endTime) {
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.ne("state",CardOrderPayTraceStateConfig.UNPAID);
        queryWrapper.ne("state","close");
        queryWrapper.ge("create_at", startTime);
        queryWrapper.le("create_at", endTime);
        queryWrapper.last(" AND  (type = 'card_electronic' OR type = 'card_physical') ");
        return list(queryWrapper);
    }

    @Override
    public List<CardOrderPayTrace> querySummaryDataForMerChantCode(String startTime, String endTime, String merchantCode) {
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.ne("state",CardOrderPayTraceStateConfig.UNPAID);
        queryWrapper.ne("state","close");
        queryWrapper.eq("merchant_code", merchantCode);
        queryWrapper.ge("create_at", startTime);
        queryWrapper.le("create_at", endTime);
        queryWrapper.last(" AND  (type = 'card_electronic' OR type = 'card_physical') ");
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public void createCardPhysicalPayTrace(int consumeMoney, String orderCode, String cardNo, long userId, String terId, CardPhysical cardPhysical) {
        //更新订单状态
        cardOrdersService.updateStateByOrderCode(orderCode,CardOrdersStateConfig.PAID);

        //更新订单明细状态
        cardOrderDetailsService.updateStateByOrderCode(orderCode,CardOrdersStateConfig.PAID,new Date());

        List<CardOrderPayTrace> cardOrderPayTraces = queryTraceByOrderCodeUnPaidType(orderCode, CardOrderPayTraceStateConfig.UNPAID, CardOrdersTypeConfig.POS_MIS_ORDER);
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        if (cardOrderPayTraces!=null && cardOrderPayTraces.size()>0){
            CardOrderPayTrace cardOrderPayTraceOriMis = cardOrderPayTraces.get(0);

            String payCode = IdWorker.getIdStr();
            cardOrderPayTrace.setPayCode(StringUtils.isEmpty(cardOrderPayTraceOriMis.getPayCode())? payCode : cardOrderPayTraceOriMis.getPayCode());
            cardOrderPayTrace.setRefTraceNo(StringUtils.isEmpty(cardOrderPayTraceOriMis.getPayCode())? payCode : cardOrderPayTraceOriMis.getPayCode());

            cardOrderPayTrace.setOrderDetailId(cardOrderPayTraceOriMis.getOrderDetailId());

            cardOrderPayTrace.setPosSerialNum(terId);
            cardOrderPayTrace.setCashId(cardOrderPayTraceOriMis.getCashId());

            cardOrderPayTrace.setMerchId(cardOrderPayTraceOriMis.getMerchId());
            cardOrderPayTrace.setMerchName(cardOrderPayTraceOriMis.getMerchName());
            cardOrderPayTrace.setMerchantCode(cardOrderPayTraceOriMis.getMerchantCode());

            this.baseMapper.deleteById(cardOrderPayTraceOriMis);
        }else {
            CardOrderPayTrace orderPayTrace = queryTraceByOrderCode(orderCode).get(0);

            String payCode = IdWorker.getIdStr();
            cardOrderPayTrace.setPayCode(StringUtils.isEmpty(orderPayTrace.getPayCode())? payCode : orderPayTrace.getPayCode());
            cardOrderPayTrace.setRefTraceNo(StringUtils.isEmpty(orderPayTrace.getPayCode())? payCode : orderPayTrace.getPayCode());

            cardOrderPayTrace.setOrderDetailId(orderPayTrace.getOrderDetailId());

            cardOrderPayTrace.setPosSerialNum(terId);
            cardOrderPayTrace.setCashId(orderPayTrace.getCashId());

            cardOrderPayTrace.setMerchId(orderPayTrace.getMerchId());
            cardOrderPayTrace.setMerchName(orderPayTrace.getMerchName());
            cardOrderPayTrace.setMerchantCode(orderPayTrace.getMerchantCode());
        }

        cardOrderPayTrace.setTraceNo(IdWorker.getIdStr());

        cardOrderPayTrace.setSource(CardOrderPayTraceSourceDescConfig.CARD_PHYSICAL);
        cardOrderPayTrace.setType(CardOrdersTypeConfig.CARD_PHYSICAL);
        cardOrderPayTrace.setState(CardOrderPayTraceStateConfig.PAID);
        cardOrderPayTrace.setAmount(consumeMoney);
        cardOrderPayTrace.setOrderCode(orderCode);
        cardOrderPayTrace.setSourceId(cardNo);
        cardOrderPayTrace.setUserFlag(userId+"");

        cardOrderPayTrace.setRefBatchCode(cardPhysical.getBatchCode());
        cardOrderPayTrace.setRefCardType(cardPhysical.getType());
        cardOrderPayTrace.setRefCardName(cardPhysical.getCardName());
        cardOrderPayTrace.setRefRemainFaceValue(cardPhysical.getFaceValue());
        cardOrderPayTrace.setRefCardBrhId(cardPhysical.getRefMerchantCode());
//        cardOrderPayTrace.setCreateAt(new Date());
//        cardOrderPayTrace.setUpdateAt(new Date());
        logger.info("创建支付流水定单,订单号为:"+orderCode);
        this.baseMapper.insert(cardOrderPayTrace);
    }



    @Override
    public void updateRefundData(List<CardOrderPayTrace> refundCardOrderPayTraces) {
        for (CardOrderPayTrace refundCardOrderPayTrace : refundCardOrderPayTraces) {
            refundCardOrderPayTrace.setState("refund");
            this.baseMapper.updateById(refundCardOrderPayTrace);
        }
        cardOrderDetailsService.updateStateByOrderCode(refundCardOrderPayTraces.get(0).getOrderCode(),"refund",new Date());
        cardOrdersService.updateStateByOrderCode(refundCardOrderPayTraces.get(0).getOrderCode(),"refund");
    }

    /**
     * 查询用户卡支付流水
     * @param userFlag
     * @param pageNo
     * @param pageSize
     */
    @Override
    public Page<CardOrderPayTrace> queryUserPayTrace(String userFlag, String pageNo, String pageSize) {
        Page<CardOrderPayTrace> page = new Page<>(Integer.parseInt(pageNo),Integer.parseInt(pageSize));
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("user_flag",userFlag);
        queryWrapper.eq("type",CardOrdersTypeConfig.CARD_ELECTRONIC);
        queryWrapper.gt("amount",0);
        queryWrapper.eq("state","paid");
        queryWrapper.orderByDesc("create_at");
        Page<CardOrderPayTrace> cardOrderPayTracePage = this.baseMapper.selectPage(page, queryWrapper);
//        Page<CardOrderPayTrace> cardOrderPayTracePage = this.baseMapper.selectUserPayTrace(page,userFlag,CardOrdersTypeConfig.CARD_ELECTRONIC);
        return cardOrderPayTracePage;
    }

    @Override
    public Page<CardOrderPayTrace> queryUserConsumeOrder(String openId, String phoneNum, Long pageNo, Long pageSize) {
        Page<CardOrderPayTrace> page = new Page<>(pageNo,pageSize);
        Page<CardOrderPayTrace> cardOrderPayTracePage = this.baseMapper.selectUserPayTrace(page,openId,phoneNum,CardOrdersTypeConfig.CARD_ELECTRONIC);
        return cardOrderPayTracePage;
    }

    @Override
    public List<ConsumeCardOrderExcelVo> batchConsumeExcel(String batchCode) {
        return this.baseMapper.batchConsumeExcel(batchCode);
    }

    @Override
    public void createPrimeBuyCardOrderPayTrace(PrimeBuyCardData primeBuyCardData, CardOrders cardOrders) {
        CardOrderPayTrace cardOrderPayTrace = new CardOrderPayTrace();
        cardOrderPayTrace.setOrderCode(cardOrders.getOrderCode());
        cardOrderPayTrace.setType(CardOrderPayTraceTypeConfig.ALLINPAY_H5);
        cardOrderPayTrace.setState(cardOrders.getState());
        cardOrderPayTrace.setAmount(cardOrders.getAmount());
        cardOrderPayTrace.setUserFlag(cardOrders.getUserId()+"");
        cardOrderPayTrace.setMerchantCode(cardOrders.getMerchantCode());
        cardOrderPayTrace.setPayCode(IdWorker.getIdStr());
        cardOrderPayTrace.setSource("线上支付");
        cardOrderPayTrace.setCreateAt(new Date());
        cardOrderPayTrace.setUpdateAt(new Date());
        this.baseMapper.insert(cardOrderPayTrace);
    }

    private List<CardOrderPayTrace> queryTraceByOrderCodeUnPaidType(String orderCode, String state, String type) {
        QueryWrapper<CardOrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        queryWrapper.eq("state",state);
        queryWrapper.eq("type",type);
        return this.baseMapper.selectList(queryWrapper);
    }


}
