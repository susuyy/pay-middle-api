package com.ht.feignapi.tonglian.order.client;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.entity.RetPageData;
import com.ht.feignapi.prime.entity.CardElectronic;
import com.ht.feignapi.prime.entity.CardOrderDetailsVo;
import com.ht.feignapi.prime.entity.PrimeBuyCardOrderExcelVo;
import com.ht.feignapi.prime.entity.UpdateCardNoData;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.entity.CardOrders;
import com.ht.feignapi.tonglian.card.entity.UserPlaceOrderData;
import com.ht.feignapi.tonglian.card.entity.UserTopUpOrderData;
import com.ht.feignapi.tonglian.config.PlaceOrderResult;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.order.entity.*;
import com.ht.feignapi.tonglian.user.entity.UserCashCardPayOrderData;
import com.ht.feignapi.tonglian.user.entity.UserCashCardPayOrderReturn;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.smartcardio.Card;
import java.util.Date;
import java.util.List;
import java.util.Map;

@FeignClient(name = "${custom.client.user.name}",contextId = "order")
public interface OrderClientService {


    /**
     * 根据订单号查询订单
     * @param orderCode
     * @return
     */
    @GetMapping("/orders/queryByOrderCode/{orderCode}")
    Result<CardOrdersVO> queryByOrderCode(@PathVariable("orderCode") String orderCode);

    /**
     * 支付成功 修改订单
     * @param paySuccess
     * @return
     */
    @PostMapping("/orders/updateCashCardOrder")
    Result<Boolean> updateCashCardOrder(@RequestBody PaySuccess paySuccess);

    /**
     * 根据订单号查询支付流水 ,获取现金金额
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/orderPayTrace/getTraceByOrderCode/{orderCode}")
    Result<CardOrderPayTrace> getTraceByOrderCode(@PathVariable(value = "orderCode", required = true) String orderCode);


    /**
     * 购买卡券支付成功修改数据
     * @param paySuccess
     * @return
     */
    @PostMapping("/orders/paySuccessBuyCard")
    Result<List<CardOrderDetails>> buyCardPaySuccess(@RequestBody PaySuccess paySuccess);





    /**
     * 获取某个用户在某个商户下的日消费总数
     * @param merchantCode
     * @param userId
     * @return
     */
    @GetMapping("/orders/{merchantCode}/{userId}/dailyExpenditure")
    Integer getUserDailyExpenditure(@PathVariable("merchantCode") String merchantCode,@PathVariable("userId") Long userId);

    /**
     * 获取用户的order列表
     * @param userId
     * @param merchantAndSon
     * @param type
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/orders")
    Result<RetPageData> selectOrderPage(@RequestParam("userId") Long userId, @RequestBody List<Merchants> merchantAndSon, @RequestParam("type") String type,
                                        @RequestParam("state") String state, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize);



    /**
     * 扫码支付
     * @param amount
     * @param merchantCode
     * @param paySource
     * @param payType
     * @return
     */
    @PostMapping("/orders/qrPay")
    Result<PlaceOrderResult> merchantQrCodePlaceOrder(@RequestParam("amount") Integer amount, @RequestParam("merchantCode") String merchantCode,
                                              @RequestParam("paySource") String paySource, @RequestParam("payType") String payType);

    /**
     * 支付成功
     * @param paySuccess
     */
    @PostMapping("/orders/paySuccess")
    void merchantQrCodePaySuccess(@RequestBody PaySuccess paySuccess);

    /**
     * 组合支付下单
     * @param userCashCardPayOrderData
     * @return
     */
    @PostMapping("/orders/prePay")
    Result<UserCashCardPayOrderReturn> userCashCardPayPlaceOrder(@RequestBody UserCashCardPayOrderData userCashCardPayOrderData);

    /**
     * C端公众号 组合支付成功 修改订单状态
     * @param paySuccess
     */
    @PostMapping("/orders/payCashSuccess")
    void paySuccessCashCardOrder(@RequestBody PaySuccess paySuccess);

    /**
     * C端买券下单接口
     * @param userPlaceOrderData
     * @param userId
     * @return
     */
    @PostMapping("/orders/cardPrePay")
    Result<PlaceOrderResult> placeOrder(@RequestBody UserPlaceOrderData userPlaceOrderData, @RequestParam("userId") Long userId);

    /**
     * C创建 充值订单
     * @param userTopUpOrderData
     */
    @PostMapping("/orders/createUserTopUpOrder")
    Result<String> createUserTopUpOrder(@RequestBody UserTopUpOrderData userTopUpOrderData);

    /**
     * 充值订单 支付成功 处理业务逻辑
     * @param paySuccess
     */
    @PostMapping("/orders/topUpPaySuccess")
    void topUpPaySuccess(@RequestBody PaySuccess paySuccess);

    /**
     * 查询数据库中是否存在待支付的mis订单
     * @param cashId
     * @return
     */
    @GetMapping("/orders/checkHaveOrder")
    Result<List<CardOrderPayTrace>> checkHaveOrder(@RequestParam("sourceId") String cashId);

    /**
     * 修改免税用户买卡 订单状态
     * @param orderCode
     */
    @PostMapping("/orders/updatePrimeBuyCardState")
    Result<List<CardOrderDetails>> updatePrimeBuyCardState(@RequestParam("orderCode") String orderCode,
                                                           @RequestParam("payCode")String payCode);

    /**
     * 回写订单卡号
     * @param updateCardNoData
     */
    @PostMapping("/orders/primeBuyCardUpdateCardNo")
    void primeBuyCardUpdateCardNo(@RequestBody UpdateCardNoData updateCardNoData);


    /**
     * 获取订单列表
     * @param pageNo
     * @param pageSize
     * @param orderNo
     * @param startTime
     * @param endTime
     * @param state
     * @param userId
     * @param type
     * 订单类型linkcom.ht.user.config.CardOrdersTypeConfig.PRIME_BUY_CARD
     * @return
     */
    @GetMapping("/orders/ms/list")
    Result<Page<CardOrders>> getOrderList(@RequestParam Long pageNo, @RequestParam Long pageSize, @RequestParam String orderNo,
                                          @RequestParam String startTime, @RequestParam String endTime, @RequestParam String state,
                                          @RequestParam Long userId,@RequestParam String phone,@RequestParam String type,@RequestParam String cardNo,
                                          @RequestParam String traceNo);

    /**
     * 获取订单列表
     * @param pageNo
     * @param pageSize
     * @param orderNo
     * @param startTime
     * @param endTime
     * @param state
     * @param phone
     * @param type
     * 订单类型linkcom.ht.user.config.CardOrdersTypeConfig.PRIME_BUY_CARD
     * @return
     */
    @GetMapping("/orders/ms/listPhone")
    Result<Page<CardOrders>> getOrderListPhone(@RequestParam Long pageNo, @RequestParam Long pageSize, @RequestParam String orderNo,
                                          @RequestParam String startTime, @RequestParam String endTime, @RequestParam String state,
                                          @RequestParam String phone,@RequestParam String type,@RequestParam String cardNo,
                                          @RequestParam String traceNo);
    /**
     * 获取订单列表
     * @param pageNo
     * @param pageSize
     * @param phoneNum
     * @param userId
     * @return
     */
    @GetMapping("/orders/ms/order/{userId}")
    Result<Page<CardOrders>> getOrderList(@RequestParam Long pageNo,
                                          @RequestParam Long pageSize,
                                          @RequestParam("phoneNum") String phoneNum,
                                          @PathVariable("userId") Long userId);

    /**
     * 修改退款单状态
     * @param orderCode
     */
    @PostMapping("/orders/updateBuyCardOrderRefundState")
    void updateBuyCardOrderRefundState(@RequestParam("orderCode") String orderCode);


    @GetMapping("/orders/ms/consumeOrdersExcelList")
    Result<List<CardOrderPayTrace>> getConsumeOrdersExcelData(@RequestParam String orderNo,
                                                       @RequestParam String startTime,
                                                       @RequestParam String endTime,
                                                       @RequestParam String state,
                                                       @RequestParam String openId,
                                                       @RequestParam String type,
                                                       @RequestParam String cardNo,
                                                       @RequestParam String traceNo);
    /**
     * 获取订单列表
     * @param orderNo
     * @param startTime
     * @param endTime
     * @param state
     * @param userId
     * @param type
     * @param cardNo
     * @param traceNo
     * 订单类型linkcom.ht.user.config.CardOrdersTypeConfig.PRIME_BUY_CARD
     * @return
     */
    @GetMapping("/orders/ms/download")
    Result<List<PrimeBuyCardOrderExcelVo>> getOrderExcelList(@RequestParam String orderNo, @RequestParam String startTime,
                                                             @RequestParam String endTime, @RequestParam String state,
                                                             @RequestParam Long userId,@RequestParam String phone, @RequestParam String type, @RequestParam String cardNo,
                                                             @RequestParam String traceNo);

    /**
     * 获取订单列表
     * @param orderNo
     * @param startTime
     * @param endTime
     * @param state
     * @param phone
     * @param type
     * @param cardNo
     * @param traceNo
     * 订单类型linkcom.ht.user.config.CardOrdersTypeConfig.PRIME_BUY_CARD
     * @return
     */
    @GetMapping("/orders/ms/getOrderExcelListPhone")
    Result<List<PrimeBuyCardOrderExcelVo>> getOrderExcelListPhone(@RequestParam String orderNo, @RequestParam String startTime,
                                                             @RequestParam String endTime, @RequestParam String state,
                                                             @RequestParam String phone, @RequestParam String type, @RequestParam String cardNo,
                                                             @RequestParam String traceNo);

    @GetMapping("/orders/queryByOrderCodeNotDetail")
    Result<CardOrders> queryByOrderCodeNotDetail(@RequestParam("orderCode") String orderCode);

    /**
     * 创建 管理员发卡订单
     * @param cardElectronic
     * @param payType
     * @param payAmount
     */
    @PostMapping("/orders/createAdminSetUserCardOrder")
    void createAdminSetUserCardOrder(@RequestBody CardElectronic cardElectronic,
                                     @RequestParam("payType") String payType,
                                     @RequestParam("payAmount") String payAmount);

    @PostMapping("/orders/updateState")
    void updateState(@RequestParam("orderCode") String orderCode,
                     @RequestParam("state")String state);

    @PostMapping("/orders/updateStateCancel")
    void updateStateCancel(@RequestBody List<CardOrderPayTrace> cardOrderPayTraces,
                     @RequestParam("orderCode") String orderCode,
                     @RequestParam("state")String state);


    @GetMapping("/orderDetails/querySummaryDetails")
    Result<List<CardOrderDetails>> querySummaryDetails(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime);

    @GetMapping("/orderDetails/querySummaryDetailsForMerchantCode")
    Result<List<CardOrderDetails>> querySummaryDetailsForMerchantCode(@RequestParam("merchantCode") String merchantCode,@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime);

    @GetMapping("/orderDetails/queryByOrderCode")
    Result<List<CardOrderDetails>> queryListByOrderCode(@RequestParam("orderCode") String orderCode);

    @GetMapping("/orders/consumeOrdersMasterExcel")
    Result<List<CardOrders>> getConsumeOrdersMasterExcelData(@RequestParam("orderNo")String orderNo,
                                         @RequestParam("startTime")String startTime,
                                         @RequestParam("endTime")String endTime);

    @PostMapping("/orders/createAdminSetUserCardOrderBatch")
    void createAdminSetUserCardOrderBatch(@RequestBody Map<String, CardElectronic> cardElectronicMap, @RequestParam("payType") String payType);

    @GetMapping("/orderDetails/allDetailsBuyCard")
    Result<List<CardOrderDetails>> allDetailsBuyCard();

    @PostMapping("/orderDetails/fixOrderDetails")
    void fixOrderDetails(@RequestBody List<CardOrderDetails> data);

}
