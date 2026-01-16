package com.ht.feignapi.tonglian.card.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.prime.entity.PrimeBuyCardData;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.admin.entity.OrdersVo;
import com.ht.feignapi.tonglian.card.entity.CardOrders;
import com.ht.feignapi.tonglian.card.entity.CardPayDetailData;
import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${custom.client.user.name}",contextId = "cardOrders")
public interface CardOrderClientService {


    /**
     * 记录支付数据,流水,订单明细
     * @param userId
     * @param amount
     * @param cardCouponMoney
     * @param userMoneyInt
     * @param orderMerchantCode
     * @param orderCode
     * @param cardNo
     * @param cardPayDetailData
     */
    @PostMapping("/orders/orderDetail")
    void accountPayCreateOrderAndDetailAndTrace(@RequestParam("userId") Long userId,
                                                @RequestParam("amount")Integer amount,
                                                @RequestParam("cardCouponMoney")Integer cardCouponMoney,
                                                @RequestParam("userMoneyInt")Integer userMoneyInt,
                                                @RequestParam("orderMerchantCode")String orderMerchantCode,
                                                @RequestParam("orderCode")String orderCode,
                                                @RequestParam("cardNo")String cardNo,
                                                @RequestBody CardPayDetailData cardPayDetailData);


    /**
     * 获取充值列表
     * @param merchantCode
     * @param orderType
     * @param pageSize
     * @param pageNo
     * @return
     */
    @GetMapping("/admin/merchantsPrime/recharge/{merchantCode}/{orderType}")
    Result<Page<OrdersVo>> getRechargeOrders(@PathVariable("merchantCode") String merchantCode,
                                             @PathVariable("orderType") String orderType,
                                             @RequestParam("pageSize") Long pageSize,
                                             @RequestParam("pageNo") Long pageNo);

    /**
     * 获取订单列表
     * @param merchantCode
     * @param pageSize
     * @param pageNo
     * @return
     */
    @GetMapping("/admin/order/{merchantCode}")
    Result<Page<OrdersVo>> getOrderList(@PathVariable("merchantCode") String merchantCode,
                                        @RequestParam("pageNo") Long pageNo,
                                        @RequestParam("pageSize") Long pageSize);

    /**
     * 创建 免税用户买卡 订单数据
     * @param primeBuyCardData
     * @return
     */
    @PostMapping("/orders/createPrimeBuyCardOrder")
    Result<CardOrders> createPrimeBuyCardOrder(@RequestBody PrimeBuyCardData primeBuyCardData);

}
