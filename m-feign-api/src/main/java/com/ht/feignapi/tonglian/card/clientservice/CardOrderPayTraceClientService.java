package com.ht.feignapi.tonglian.card.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.prime.entity.CardPhysical;
import com.ht.feignapi.prime.entity.SearchPosOrderListData;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.entity.*;
import com.ht.feignapi.tonglian.order.entity.CardOrderPayTrace;
import com.ht.feignapi.tonglian.order.entity.MisOrder;
import com.ht.feignapi.tonglian.order.entity.MisOrderData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${custom.client.user.name}",contextId = "cardOrdersTrace")
public interface CardOrderPayTraceClientService {

    /**
     * 账户余额充值,记录支付数据
     * @param posPayTraceData
     */
    @PostMapping("/orderPayTrace/posPayTrace")
    void createPosPayTrace(@RequestBody PosPayTraceData posPayTraceData);

    /**
     * pos 端组合支付,记录流水订单数据
     * @param posPayTraceData
     */
    @PostMapping("/orderPayTrace/settlementSuccess")
    Result<String> createPosPayTraceFromCashier(@RequestBody PosPayTraceData posPayTraceData);

    /**
     * 根据订单号查询 流水接口
     * @param orderCode
     */
    @GetMapping("/orderPayTrace/queryPayTrace")
    Result<List<CardOrderPayTrace>> queryPayTrace(@RequestParam("orderCode") String orderCode);

    /**
     * 创建云MIS订单待支付流水
     * @param misOrderData
     */
    @PostMapping("/orderPayTrace/createMisOrderPayTrace")
    Result<CardOrderPayTrace> createMisOrderPayTrace(@RequestBody MisOrderData misOrderData);

    /**
     * 云mis订单支付成功,修改订单状态
     * @param posPayTraceData
     */
    @PostMapping("/orderPayTrace/updateMisOrderState")
    void updateMisOrderState(@RequestBody PosPayTraceData posPayTraceData);

    /**
     * 云mis订单组合支付
     * @param posPayTraceData
     */
    @PostMapping("/orderPayTrace/updateVipMisOrderState")
    void updateVipMisOrderState(@RequestBody PosPayTraceData posPayTraceData);

    /**
     * 创建 优惠券 抵扣支付流水
     * @param userId
     * @param merchantCode
     * @param orderCode
     * @param cardPayDetailData
     */
    @PostMapping("/orderPayTrace/createCouponCardPayTrace")
    void createCouponCardPayTrace(@RequestParam("userId") Long userId,
                                  @RequestParam("merchantCode")String merchantCode,
                                  @RequestParam("orderCode")String orderCode,
                                  @RequestBody CardPayDetailData cardPayDetailData);

    /**
     * 创建电子卡支付 订单明细与流水
     * @param consumeMoney
     * @param orderCode
     * @param cardNo
     * @param userId
     */
    @PostMapping("/orderPayTrace/createCardElectronicPayTrace")
    void createCardElectronicPayTrace(@RequestParam("consumeMoney") int consumeMoney,
                                      @RequestParam("orderCode") String orderCode,
                                      @RequestParam("cardNo") String cardNo,
                                      @RequestParam("userId") long userId,
                                      @RequestParam("terId")String terId);


    /**
     * 创建电子卡支付 订单明细与流水
     * @param consumeMoney
     * @param orderCode
     * @param cardNo
     * @param userId
     */
    @PostMapping("/orderPayTrace/createCardPhysicalPayTrace")
    void createCardPhysicalPayTrace(@RequestParam("consumeMoney") int consumeMoney,
                                    @RequestParam("orderCode") String orderCode,
                                    @RequestParam("cardNo") String cardNo,
                                    @RequestParam("userId") long userId,
                                    @RequestParam("terId")String terId,
                                    @RequestBody CardPhysical cardPhysical);

    /**
     * 根据订单号和商户号查询流水
     * @param orderCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/orderPayTrace/queryPayTraceByOrderCodeAndMerchantCode")
    Result<List<CardOrderPayTrace>> queryPayTraceByOrderCodeAndMerchantCode(@RequestParam("orderCode")String orderCode,
                                                                            @RequestParam("merchantCode")String merchantCode);

    /**
     * 创建 富基对接的 组合支付流水
     * @param saveCardPayTraceList
     * @param totalAmount
     * @param userId
     */
    @PostMapping("/orderPayTrace/createPosCombinationPayTrace")
    void createPosCombinationPayTrace(@RequestBody List<CardOrderPayTrace> saveCardPayTraceList, @RequestParam("totalAmount") Integer totalAmount,
                                      @RequestParam("userId")Long userId,
                                      @RequestParam(value = "storeCode",required = false)String getStoreCode,
                                      @RequestParam(value = "actualPhone",required = false)String actualPhone,
                                      @RequestParam(value = "idCardNo",required = false)String idCardNo);

    /**
     * 创建 富基对接的 现金支付 支付流水
     * @param posCombinationPaySuccess
     */
    @PostMapping("/orderPayTrace/createPosCombinationCashPay")
    void createPosCombinationCashPay(@RequestBody PosCombinationPaySuccess posCombinationPaySuccess);

    /**
     * 修改退款状态
     * @param refundCardOrderPayTraces
     */
    @PostMapping("/orderPayTrace/updateRefundData")
    void updateRefundData(@RequestBody List<CardOrderPayTrace> refundCardOrderPayTraces);

    /**
     * 根据流水号查询支付流水
     * @param payCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/orderPayTrace/queryTraceByPayCode")
    Result<List<CardOrderPayTrace>> queryTraceByPayCode(@RequestParam("payCode") String payCode,
                                                        @RequestParam(value = "merchantCode",required = false,defaultValue = "HLSC")String merchantCode);

    /**
     * 创建其他支付方式的支付成功流水
     * @param posPayTraceData
     */
    @PostMapping("/orderPayTrace/createMisSuccessTrace")
    Result<String> createMisSuccessTrace(@RequestBody PosPayTraceData posPayTraceData);

    @PostMapping("/orderPayTrace/posOrderList")
    Result<Page<CardOrderPayTrace>> posOrderList(@RequestBody SearchPosOrderListData searchPosOrderListData);

    @GetMapping("/orderPayTrace/querySummaryData")
    Result<List<CardOrderPayTrace>> querySummaryData(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime);

    @GetMapping("/orderPayTrace/querySummaryDataForMerchantCode")
    Result<List<CardOrderPayTrace>> querySummaryDataForMerchantCode(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime,@RequestParam("merchantCode") String merchantCode);

    @PostMapping("/orderPayTrace/fixCardOrderTraceUser")
    void fixCardOrderTraceUser(@RequestParam("orderCode") String orderCode,@RequestParam("userPhone") String userPhone);

    @GetMapping("/orderPayTrace/queryConsumeAll")
    Result<List<CardOrderPayTrace>> queryConsumeAll();

    @PostMapping("/orderPayTrace/fixOrderTraceRefUser")
    void fixOrderTraceRefUser(@RequestBody List<CardOrderPayTrace> cardOrderPayTraces);

    @GetMapping("/orderPayTrace/queryUserConsumeOrder")
    Result<Page<CardOrderPayTrace>> queryUserConsumeOrder(@RequestParam("pageNo") Long pageNo,@RequestParam("pageSize") Long pageSize,
                                                          @RequestParam("phoneNum") String phoneNum,@RequestParam("openId") String openId);

    /**
     * 保存上送的订单数据
     * @param cardOrdersGoods
     */
    @PostMapping("/card-orders-goods/saveOrderGoods")
    void saveOrderGoods(@RequestBody CardOrdersGoods cardOrdersGoods);

}
