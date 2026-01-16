package com.ht.user.card.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.*;
import com.ht.user.card.service.CardOrderDetailsService;
import com.ht.user.card.service.CardOrderPayTraceService;
import com.ht.user.card.vo.*;
import com.ht.user.card.service.CardOrdersService;
import com.ht.user.common.Result;
import com.ht.user.common.StatusCode;
import com.ht.user.config.CardOrdersStateConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * <p>
 * 订单主表 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin(allowCredentials = "true")
public class CardOrdersController {

    @Autowired
    private CardOrdersService cardOrdersService;
    @Autowired
    private CardOrderDetailsService cardOrderDetailsService;
    @Autowired
    private CardOrderPayTraceService cardOrderPayTraceService;


    /**
     * 根据订单号查询订单
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/queryByOrderCode/{orderCode}")
    public CardOrdersVO queryByOrderCode(@PathVariable("orderCode") String orderCode) {
        CardOrdersVO cardOrdersVO = cardOrdersService.queryByOrderCode(orderCode);
        return cardOrdersVO;
    }



    /**
     * 用户扫商户码支付下单
     *
     * @param merchantQrCodePayData
     * @return
     */
    @PostMapping("/merchantQrCodePay")
    public Result merchantQrCodePay(@RequestBody MerchantQrCodePayData merchantQrCodePayData) {
        try {
            PlaceOrderResult placeOrderResult = cardOrdersService.merchantQrCodePlaceOrder(merchantQrCodePayData.getAmount(),
                    merchantQrCodePayData.getMerchantCode(),
                    merchantQrCodePayData.getPaySource(),
                    merchantQrCodePayData.getPayType());
            return new Result(true, StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getDesc(), placeOrderResult);
        } catch (Exception e) {
            return new Result(false, StatusCode.ERROR.getCode(), StatusCode.ERROR.getDesc());
        }
    }

    /**
     * 用户扫商户码支付成功 修改订单数据
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/qrCodePaySuccess")
    public Result qrCodePaySuccess(@RequestBody PaySuccess paySuccess) {
        try {
            cardOrdersService.merchantQrCodePaySuccess(paySuccess);
            return new Result(true, StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getDesc());
        } catch (Exception e) {
            return new Result(false, StatusCode.ERROR.getCode(), StatusCode.ERROR.getDesc());
        }
    }


    /**
     * C端公众号 组合支付成功 修改订单状态
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/updateCashCardOrder")
    public Result updateCashCardOrder(@RequestBody PaySuccess paySuccess) {
        if (StringUtils.isEmpty(paySuccess.getUserId()) || "-1".equals(paySuccess.getUserId())) {
            //无user_id 非会员支付成功修改订单状态
            System.out.println("=====================无userid");
            try {
                cardOrdersService.merchantQrCodePaySuccess(paySuccess);
                return new Result(true, StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getDesc());
            } catch (Exception e) {
                return new Result(false, StatusCode.ERROR.getCode(), StatusCode.ERROR.getDesc());
            }
        }
        try {
            String orderCode = paySuccess.getOrderCode();
            CardOrdersVO ordersVO = cardOrdersService.queryByOrderCode(orderCode);
            String state = ordersVO.getState();
            if (CardOrdersStateConfig.PAID.equals(state)) {
                return new Result(false, StatusCode.ORDER_PAID.getCode(), StatusCode.ORDER_PAID.getDesc());
            }
            cardOrdersService.paySuccessCashCardOrder(paySuccess);
            return new Result(true, StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getDesc());
        } catch (Exception e) {
            return new Result(false, StatusCode.ERROR.getCode(), StatusCode.ERROR.getDesc(), e);
        }
    }

    //**************************************************************************************************************//

    /**
     * 获取某个用户在某个商户下的日消费总数
     *
     * @param merchantCode
     * @param userId
     * @return
     */
    @GetMapping("/{merchantCode}/{userId}/dailyExpenditure")
    public Integer getUserDailyExpenditure(@PathVariable("merchantCode") String merchantCode, @PathVariable("userId") Long userId) {
        return cardOrdersService.getUserDailyExpenditure(merchantCode, userId);
    }

    /**
     * 记录支付数据,流水,订单明细   (xxxxxxxxxx  CardOrderService类方法同名 )
     *
     * @param userId
     * @param amount
     * @param cardCouponMoney
     * @param userMoneyInt
     * @param orderMerchantCode
     * @param orderCode
     * @param cardNo
     * @param cardPayDetailData
     */
    @PostMapping("/orderDetail")
    public void accountPayCreateOrderAndDetailAndTrace(@RequestParam("userId") Long userId,
                                                       @RequestParam("amount") Integer amount,
                                                       @RequestParam("cardCouponMoney") Integer cardCouponMoney,
                                                       @RequestParam("userMoneyInt") Integer userMoneyInt,
                                                       @RequestParam("orderMerchantCode") String orderMerchantCode,
                                                       @RequestParam("orderCode") String orderCode,
                                                       @RequestParam("cardNo") String cardNo, @RequestBody CardPayDetailData cardPayDetailData) {
        if ("mis".equals(orderCode.substring(0, 3))) {
            cardOrdersService.misAccountPayCreateOrderAndDetailAndTrace(userId, amount, cardCouponMoney, userMoneyInt, orderMerchantCode, orderCode.substring(3), cardNo, cardPayDetailData);
        } else {
            cardOrdersService.accountPayCreateOrderAndDetailAndTrace(userId, amount, cardCouponMoney, userMoneyInt, orderMerchantCode, orderCode, cardNo, cardPayDetailData);
        }
    }

    /**
     * 获取order分页信息
     *
     * @param userId
     * @param merchantAndSon
     * @param type
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping
    public IPage selectOrderPage(@RequestParam Long userId, @RequestBody List<Merchants> merchantAndSon, @RequestParam String type,
                                 @RequestParam String state, @RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        return cardOrdersService.selectOrderPage(userId, merchantAndSon, type, state, pageNo, pageSize);
    }

    /**
     * 二维码支付
     *
     * @param amount
     * @param merchantCode
     * @param paySource
     * @param payType
     * @return
     */
    @PostMapping("/qrPay")
    public PlaceOrderResult merchantQrCodePlaceOrder(@RequestParam Integer amount, @RequestParam String merchantCode,
                                                     @RequestParam String paySource, @RequestParam String payType) {
        return cardOrdersService.merchantQrCodePlaceOrder(amount, merchantCode, paySource, payType);
    }

    /**
     * 支付成功
     *
     * @param paySuccess
     */
    @PostMapping("/paySuccess")
    public void merchantQrCodePaySuccess(@RequestBody PaySuccess paySuccess) {
        cardOrdersService.merchantQrCodePaySuccess(paySuccess);
    }

    /**
     * 组合支付下单
     *
     * @param userCashCardPayOrderData
     * @return
     */
    @PostMapping("/prePay")
    public UserCashCardPayOrderReturn userCashCardPayPlaceOrder(@RequestBody UserCashCardPayOrderData userCashCardPayOrderData) {
        return cardOrdersService.userCashCardPayPlaceOrder(userCashCardPayOrderData);
    }

    /**
     * C端公众号 组合支付成功 修改订单状态
     *
     * @param paySuccess
     */
    @PostMapping("/payCashSuccess")
    public void paySuccessCashCardOrder(@RequestBody PaySuccess paySuccess) {
        cardOrdersService.paySuccessCashCardOrder(paySuccess);
    }

    /**
     * C端买券下单接口
     *
     * @param userPlaceOrderData
     * @param userId
     * @return
     */
    @PostMapping("/cardPrePay")
    public PlaceOrderResult placeOrder(@RequestBody UserPlaceOrderData userPlaceOrderData, @RequestParam Long userId) {
        return cardOrdersService.placeOrder(userPlaceOrderData, userId);
    }

    /**
     * C端用户 购买卡券 支付成功 修改订单信息
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/paySuccessBuyCard")
    public List<CardOrderDetails> paySuccessBuyCard(@RequestBody PaySuccess paySuccess) {
        String orderCode = paySuccess.getOrderCode();
        CardOrdersVO ordersVO = cardOrdersService.queryByOrderCode(orderCode);
        String state = ordersVO.getState();
        if (CardOrdersStateConfig.PAID.equals(state)) {
            return null;
        }
        return cardOrdersService.paySuccess(paySuccess);
    }

    /**
     * C创建 充值订单
     *
     * @param userTopUpOrderData
     */
    @PostMapping("/createUserTopUpOrder")
    public String createUserTopUpOrder(@RequestBody UserTopUpOrderData userTopUpOrderData) {
        return cardOrdersService.createUserTopUpOrder(userTopUpOrderData);
    }

    /**
     * 充值订单 支付成功 处理业务逻辑
     *
     * @param paySuccess
     */
    @PostMapping("/topUpPaySuccess")
    public void topUpPaySuccess(@RequestBody PaySuccess paySuccess) throws Exception {
        String orderCode = paySuccess.getOrderCode();
        CardOrdersVO ordersVO = cardOrdersService.queryByOrderCode(orderCode);
        String state = ordersVO.getState();
        if (CardOrdersStateConfig.PAID.equals(state)) {
            return;
        }
        cardOrdersService.topUpPaySuccess(paySuccess, ordersVO);
    }

    /**
     * 查询数据库中是否存在待支付的mis订单
     *
     * @param cashId
     * @return
     */
    @GetMapping("/checkHaveOrder")
    public List<CardOrderPayTrace> checkHaveOrder(@RequestParam("sourceId") String cashId) {
//        Date now = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(now);
//        cal.add(cal.MINUTE, -5);
//        Date beforeDate = cal.getTime();
        return cardOrdersService.checkHaveOrder(cashId);
    }


    /**
     * 创建 免税用户买卡 订单数据
     *
     * @param primeBuyCardData
     * @return
     */
    @PostMapping("/createPrimeBuyCardOrder")
    public CardOrders createPrimeBuyCardOrder(@RequestBody PrimeBuyCardData primeBuyCardData) {
        return cardOrdersService.createPrimeBuyCardOrder(primeBuyCardData);
    }

    /**
     * 修改免税用户买卡 订单状态
     *
     * @param orderCode
     */
    @PostMapping("/updatePrimeBuyCardState")
    public List<CardOrderDetails> updatePrimeBuyCardState(@RequestParam("orderCode") String orderCode,
                                                          @RequestParam("payCode") String payCode) {
        return cardOrdersService.updatePrimeBuyCardState(orderCode,payCode);
    }

    @PostMapping("/primeBuyCardUpdateCardNo")
    public void primeBuyCardUpdateCardNo(@RequestBody UpdateCardNoData updateCardNoData) {
        cardOrdersService.primeBuyCardUpdateCardNo(updateCardNoData);
    }

    /**
     * 获取所有订单列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/ms/list")
    public Page<CardOrders> getOrderList(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Long pageSize,
            @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
            @RequestParam(value = "traceNo", defaultValue = "", required = false) String traceNo,
            @RequestParam(value = "state", defaultValue = "", required = false) String state,
            @RequestParam(value = "userId", defaultValue = "", required = false) Long userId,
            @RequestParam(value = "phone", defaultValue = "", required = false) String phone,
            @RequestParam(value = "type", defaultValue = "", required = false) String type,
            @RequestParam(value = "cardNo", defaultValue = "", required = false) String cardNo,
            @RequestParam(value = "startTime", defaultValue = "1970-01-01", required = false) String startTime,
            @RequestParam(value = "endTime", defaultValue = "2200-01-01", required = false)  String endTime
    ) {
        Page<CardOrders> page = new Page<>(pageNo, pageSize);
        return cardOrdersService.getOrderPage(page, orderNo, startTime, endTime, state, userId,phone, type, cardNo, traceNo);
    }

    /**
     * 获取所有订单列表
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/ms/listPhone")
    public Page<CardOrders> getOrderListPhone(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Long pageSize,
            @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
            @RequestParam(value = "traceNo", defaultValue = "", required = false) String traceNo,
            @RequestParam(value = "state", defaultValue = "", required = false) String state,
            @RequestParam(value = "phone", defaultValue = "", required = false) String phone,
            @RequestParam(value = "type", defaultValue = "", required = false) String type,
            @RequestParam(value = "cardNo", defaultValue = "", required = false) String cardNo,
            @RequestParam(value = "startTime", defaultValue = "1970-01-01", required = false) String startTime,
            @RequestParam(value = "endTime", defaultValue = "2200-01-01", required = false)  String endTime
    ) {
        Page<CardOrders> page = new Page<>(pageNo, pageSize);
        return cardOrdersService.getOrderPagePhone(page, orderNo, startTime, endTime, state, phone, type, cardNo, traceNo);
    }


    /**
     * 获取订单导出的list，每个cardNo，对应一条
     *
     * @param orderNo
     * @param traceNo
     * @param state
     * @param userId
     * @param type
     * @param cardNo
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/ms/download")
    public List<PrimeBuyCardOrderExcelVo> getOrderList(
            @RequestParam(value = "orderNo",defaultValue = "",required = false) String orderNo,
            @RequestParam(value = "startTime", defaultValue = "1970-01-01", required = false) String startTime,
            @RequestParam(value = "endTime", defaultValue = "2200-01-01", required = false)  String endTime,
            @RequestParam(value = "state",defaultValue = "",required = false) String state,
            @RequestParam(value = "userId",defaultValue = "",required = false) Long userId,
            @RequestParam(value = "phone",defaultValue = "",required = false) String phone,
            @RequestParam(value = "type",defaultValue = "",required = false) String type,
            @RequestParam(value = "cardNo",defaultValue = "",required = false) String cardNo,
            @RequestParam(value = "traceNo",defaultValue = "",required = false) String traceNo
    ) {
        List<PrimeBuyCardOrderExcelVo> list = cardOrdersService.getExcelList(startTime,endTime, state,userId,phone,type,cardNo,traceNo,orderNo);
        return list;
    }

    /**
     * 获取订单导出的list，每个cardNo，对应一条
     *
     * @param orderNo
     * @param traceNo
     * @param state
     * @param phone
     * @param type
     * @param cardNo
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/ms/getOrderExcelListPhone")
    public List<PrimeBuyCardOrderExcelVo> getOrderExcelListPhone(
            @RequestParam(value = "orderNo",defaultValue = "",required = false) String orderNo,
            @RequestParam(value = "startTime", defaultValue = "1970-01-01", required = false) String startTime,
            @RequestParam(value = "endTime", defaultValue = "2200-01-01", required = false)  String endTime,
            @RequestParam(value = "state",defaultValue = "",required = false) String state,
            @RequestParam(value = "userId",defaultValue = "",required = false) String phone,
            @RequestParam(value = "type",defaultValue = "",required = false) String type,
            @RequestParam(value = "cardNo",defaultValue = "",required = false) String cardNo,
            @RequestParam(value = "traceNo",defaultValue = "",required = false) String traceNo
    ) {
        List<PrimeBuyCardOrderExcelVo> list = cardOrdersService.getExcelListPhone(startTime,endTime, state,phone,type,cardNo,traceNo,orderNo);
        return list;
    }

    /**
     * 获取用户的订单
     *
     * @param pageNo
     * @param pageSize
     * @param userId   用户id
     * @return
     */
    @GetMapping("/ms/order/{userId}")
    public Page<CardOrders> getOrderList(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) Long pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Long pageSize,
            @RequestParam("phoneNum") String phoneNum,
            @PathVariable("userId") Long userId
    ) {
//        Page<CardOrders> page = new Page<>(pageNo, pageSize);

        Page<CardOrders> ordersPage = cardOrdersService.queryUserOrderListPage(pageNo,pageSize,phoneNum,userId);

//        LambdaQueryWrapper<CardOrders> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(CardOrders::getUserId, userId);
//        wrapper.eq(CardOrders::getType, "prime_buy_card");
//        wrapper.orderByDesc(CardOrders::getCreateAt);
//        Page<CardOrders> ordersPage = cardOrdersService.page(page, wrapper);
        List<CardOrders> records = ordersPage.getRecords();
        for (CardOrders record : records) {
            if (CardOrdersStateConfig.PAID.equals(record.getState())) {
                List<String> cardNoList = new ArrayList<>();
                List<CardOrderDetails> cardOrderDetails = cardOrderDetailsService.queryByOrderCode(record.getOrderCode());
                List<CardOrderDetailsVo> cardOrderDetailsVos = new ArrayList<>();
                for (CardOrderDetails cardOrderDetail : cardOrderDetails) {

                    CardOrderDetailsVo cardOrderDetailsVo = new CardOrderDetailsVo();

                    String jsonProductionCode = cardOrderDetail.getProductionCode();
                    if (!StringUtils.isEmpty(jsonProductionCode)) {
                        List list = JSONObject.parseObject(jsonProductionCode, List.class);
                        for (Object o : list) {
                            cardNoList.add(o + "");
                        }
                    }
                    cardOrderDetailsVo.setProductionCode(jsonProductionCode);
                    cardOrderDetailsVos.add(cardOrderDetailsVo);
                }
                record.setCardNoList(cardNoList);
                record.setOrderDetailsList(cardOrderDetailsVos);

                List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceService.queryTraceByOrderCode(record.getOrderCode());
                record.setPayTraceList(cardOrderPayTraces);

            } else {
                record.setCardNoList(new ArrayList<>());
                record.setPayTraceList(new ArrayList<>());
            }

        }
        return ordersPage;
    }

    /**
     * 修改退款单状态
     *
     * @param orderCode
     */
    @PostMapping("/updateBuyCardOrderRefundState")
    public void updateBuyCardOrderRefundState(@RequestParam("orderCode") String orderCode) {
        cardOrdersService.updateBuyCardOrderRefundState(orderCode);
    }


    @GetMapping("/ms/consumeOrdersExcelList")
    public List<CardOrderPayTrace> getConsumeOrdersExcelData(
                                                      @RequestParam(value = "orderNo", defaultValue = "", required = false) String orderNo,
                                                      @RequestParam(value = "traceNo", defaultValue = "", required = false) String traceNo,
                                                      @RequestParam(value = "state", defaultValue = "", required = false) String state,
                                                      @RequestParam(value = "openId", defaultValue = "", required = false) String openId,
                                                      @RequestParam(value = "type", defaultValue = "", required = false) String type,
                                                      @RequestParam(value = "cardNo", defaultValue = "", required = false) String cardNo,
                                                      @RequestParam(value = "startTime", defaultValue = "1970-01-01", required = false) String startTime,
                                                      @RequestParam(value = "endTime", defaultValue = "2200-01-01", required = false)  String endTime
    ) {
        return cardOrdersService.getConsumeOrdersExcelData(orderNo, startTime, endTime, state, openId, type, cardNo, traceNo);
    }


    @GetMapping("/queryByOrderCodeNotDetail")
    public CardOrders queryByOrderCodeNotDetail(@RequestParam("orderCode") String orderCode){
        QueryWrapper<CardOrders> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return this.cardOrdersService.getOne(queryWrapper);
    }

    /**
     * 创建 管理员发卡订单
     * @param cardElectronic
     * @param payType
     * @param payAmount
     */
    @PostMapping("/createAdminSetUserCardOrder")
    public void createAdminSetUserCardOrder(@RequestBody CardElectronic cardElectronic,
                                     @RequestParam("payType") String payType,
                                     @RequestParam("payAmount") String payAmount){
        cardOrdersService.createAdminSetUserCardOrder(cardElectronic,payType,payAmount);
    }

    /**
     * 更新所有关联订单状态
     * @param orderCode
     * @param state
     */
    @PostMapping("/updateState")
    public void updateState(@RequestParam("orderCode") String orderCode,
                     @RequestParam("state")String state){
        cardOrdersService.updateStateByOrderCode(orderCode,state);
        cardOrderDetailsService.updateStateByOrderCode(orderCode,state,new Date());
        cardOrderPayTraceService.updateStateByOrderCodeNotPayCode(orderCode,state,new Date());
    }

    @PostMapping("/updateStateCancel")
    public void updateStateCancel(@RequestBody List<CardOrderPayTrace> cardOrderPayTraces,
                           @RequestParam("orderCode") String orderCode,
                           @RequestParam("state")String state){
        cardOrdersService.updateStateByOrderCode(orderCode,state);
        cardOrderDetailsService.updateStateByOrderCode(orderCode,state,new Date());
        for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
            cardOrderPayTrace.setState(state);
            cardOrderPayTraceService.updateById(cardOrderPayTrace);
        }
    }

    @PostMapping("/fixOrderPayTraceAmount")
    public void fixOrderPayTraceAmount(@RequestBody List<String> proCodeList){
        for (String proCode : proCodeList) {
            QueryWrapper<CardOrderDetails> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("production_code",proCode);
            CardOrderDetails cardOrderDetails = cardOrderDetailsService.getOne(queryWrapper);

            if (cardOrderDetails!=null){
                List<CardOrderPayTrace> cardOrderPayTraces = cardOrderPayTraceService.queryTraceByOrderCode(cardOrderDetails.getOrderCode());
                for (CardOrderPayTrace cardOrderPayTrace : cardOrderPayTraces) {
                    cardOrderPayTrace.setAmount(cardOrderDetails.getAmount());
                    cardOrderPayTraceService.updateById(cardOrderPayTrace);
                }
            }
        }
    }

    @GetMapping("/consumeOrdersMasterExcel")
    public List<CardOrders> getConsumeOrdersMasterExcelData(@RequestParam("orderNo")String orderNo,
                                                             @RequestParam("startTime")String startTime,
                                                             @RequestParam("endTime")String endTime){
        return cardOrdersService.getConsumeOrdersMasterExcelData(orderNo,startTime,endTime);
    }

    @PostMapping("/createAdminSetUserCardOrderBatch")
    public void createAdminSetUserCardOrderBatch(@RequestBody Map<String, CardElectronic> cardElectronicMap, @RequestParam("payType") String payType){
        cardOrdersService.createAdminSetUserCardOrderBatch(cardElectronicMap,payType);
    }
}

