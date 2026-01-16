package com.ht.feignapi.mall.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.entity.vo.ObjectIncomeSearch;
import com.ht.feignapi.mall.entity.vo.SalesVolume;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.order.entity.OrderSearch;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.ht.feignapi.mall.entity.PaySuccess;

import java.util.List;

@FeignClient(name = "${custom.client.order.name}",contextId = "mallOrderOrders")
public interface MallOrderClientService {

    /**
     * 商品详情页 直接下单购买
     * @param mallUnionOrderData
     * @return
     */
    @PostMapping("/mall/orderOrders/mallUnionOrder")
    Result<RetUnionOrderData> saveOrderAllData(@RequestBody MallUnionOrderData mallUnionOrderData);

    /**
     * 展示用户的 订单信息
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/mall/orderOrders/showMyOrder")
    Result<Page<OrderOrderDetails>> queryMyOrderDetail(@RequestBody ShowMyOrderData showMyOrderData);

    /**
     * 点击更多 获取商户下的 订单明细数据
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/mall/orderOrders/moreMyOrder")
    Result<Page<OrderOrderDetails>> moreMyOrder(@RequestBody ShowMyOrderData showMyOrderData);

    /**
     * 根据订单号查询订单主表数据
     * @param orderCode
     */
    @GetMapping("/mall/orderOrders/queryByOrderCode")
    Result<OrderOrders> queryOrderByOrderCode(@RequestParam("orderCode") String orderCode);

    /**
     * 查询订单明细表集合
     * @param orderCode
     * @return
     */
    @GetMapping("/mall/orderOrderDetails/queryByOrderCode")
    Result<List<OrderOrderDetails>> queryDetailByOrderCode(@RequestParam("orderCode") String orderCode);

    /**
     * 查询 支付流水集合
     * @param orderCode
     * @return
     */
    @GetMapping("/mall/orderPayTrace/queryByOrderCode")
    Result<List<OrderPayTrace>> getTraceByOrderCode(@RequestParam("orderCode") String orderCode);

    /**
     * 更新订单所有数据为已支付
     * @param paysuccess
     */
    @PostMapping("/mall/orderOrders/updateMallOrderAllPaid")
    void updateMallOrderAllPaid(@RequestBody PaySuccess paysuccess);

    /**
     * 根据 id 查询订单明细
     * @param id
     * @return
     */
    @GetMapping("/mall/orderOrderDetails/queryDetailById")
    Result<OrderOrderDetails> queryDetailById(@RequestParam("id") String id);

    /**
     * 保存订单详情
     * @param orderCode
     * @param merchantCode
     * @param userId
     * @param quantity
     * @param amount
     * @param productionCode
     * @param productionName
     * @param productionCategoryCode
     * @param productionCategoryName
     * @param activityCode
     * @param shoppingCartOrderCode
     * @param state
     * @param type
     * @param discount
     * @return
     */
    @PostMapping("/mall/orderOrderDetails/saveOrderOrderDetails")
    Result<OrderOrderDetails> saveOrderOrderDetails(@RequestParam("orderCode") String orderCode,
                                            @RequestParam("merchantCode")String merchantCode,
                                            @RequestParam("userId")Long userId,
                                            @RequestParam("quantity")Integer quantity,
                                            @RequestParam("amount")Integer amount,
                                            @RequestParam("productionCode")String productionCode,
                                            @RequestParam("productionName")String productionName,
                                            @RequestParam("productionCategoryCode")String productionCategoryCode,
                                            @RequestParam("productionCategoryName")String productionCategoryName,
                                            @RequestParam("activityCode")String activityCode,
                                            @RequestParam("shoppingCartOrderCode")String shoppingCartOrderCode,
                                            @RequestParam("state")String state,
                                            @RequestParam("type")String type,
                                            @RequestParam("discount")Integer discount);

    /**
     * 保存未支付流水数据
     * @param orderCode
     * @param orderDetailId
     * @param payCode
     * @param type
     * @param state
     * @param source
     * @param sourceId
     * @param amount
     * @param posSerialNum
     * @return
     */
    @PostMapping("/mall/orderPayTrace/saveOrderPayTrace")
    Result<OrderPayTrace> saveOrderPayTrace(@RequestParam("orderCode")String orderCode,
                                    @RequestParam("orderDetailId")Long orderDetailId,
                                    @RequestParam("payCode")String payCode,
                                    @RequestParam("type")String type,
                                    @RequestParam("state")String state,
                                    @RequestParam("source")String source,
                                    @RequestParam("sourceId")String sourceId,
                                    @RequestParam("amount")Integer amount,
                                    @RequestParam("posSerialNum")String posSerialNum);

    /**
     * 保存订单主表数据
     * @param orderCode
     * @param type
     * @param state
     * @param merchantCode
     * @param userId
     * @param saleId
     * @param quantity
     * @param amount
     * @param comments
     * @param discount
     * @return
     */
    @PostMapping("/mall/orderOrders/saveOrderOrders")
    Result<OrderOrders> saveOrderOrders(@RequestParam("orderCode")String orderCode,
                                @RequestParam("type")String type,
                                @RequestParam("state")String state,
                                @RequestParam("merchantCode")String merchantCode,
                                @RequestParam("userId")Long userId,
                                @RequestParam("saleId")String saleId,
                                @RequestParam("quantity")Integer quantity,
                                @RequestParam("amount")Integer amount,
                                @RequestParam("comments")String comments,
                                @RequestParam("discount")Integer discount);

    /**
     * 修改明细折扣
     * @param useDetailList
     * @param oneDetailDiscount
     */
    @PostMapping("/mall/orderOrderDetails/updateDetailDiscount")
    void updateDetailDiscount(@RequestBody List<OrderOrderDetails> useDetailList,@RequestParam("oneDetailDiscount") Integer oneDetailDiscount);

    /**
     * 修改订单主表折扣数据
     * @param orderCode
     * @param discount
     */
    @PostMapping("/mall/orderOrders/updateOrderDiscount")
    void updateOrderDiscount(@RequestParam("orderCode") String orderCode,@RequestParam("discount") Integer discount);

    /**
     * 查询用户订单主表数据 展示
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/mall/orderOrders/queryMyOrderMaster")
    Result<Page<OrderOrders>> queryMyOrderMaster(@RequestBody ShowMyOrderData showMyOrderData);

    /**
     * 查询用户 商户下的更多订单
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/mall/orderOrders/moreMyOrderMaster")
    Result<Page<OrderOrders>> moreMyOrderMaster(ShowMyOrderData showMyOrderData);

    /**
     * 根据id 修改订单明细状态
     * @param orderDetailId
     * @param state
     */
    @PostMapping("/mall/orderOrderDetails/updateOrderDetailState")
    void updateOrderDetailState(@RequestParam("orderDetailId") String orderDetailId, @RequestParam("state") String state);

    /**
     * 根据orderCode source 查询 trace
     * @param orderCode
     * @param source
     * @return
     */
    @GetMapping("/mall/orderPayTrace/queryOrderTraceSource")
    Result<OrderPayTrace> queryOrderTraceSource(@RequestParam("orderCode") String orderCode,
                                                @RequestParam("source") String source);

    /**
     * 修改一个明细的折扣
     * @param id
     * @param discount
     */
    @PostMapping("/mall/orderOrderDetails/updateOneDetailDiscount")
    void updateOneDetailDiscount(@RequestParam("id") Long id, @RequestParam("discount") Integer discount);

    /**
     * 获取用户的order列表
     * @param merchantCodeList
     * @param pageNo
     * @param pageSize
     * @param orderSearch
     * @return
     */
    @GetMapping("/mall/orderOrders/merchantsOrders/{merchantCodeList}")
    Result<Page<OrderOrders>> selectOrderPage(@PathVariable("merchantCodeList") List<String> merchantCodeList,
                                              @RequestParam("pageNo") Long pageNo,
                                              @RequestParam("pageSize") Long pageSize,
                                              OrderSearch orderSearch);

    /**
     * 获取子商户的订单派送单
     * @param orderCode
     * @param merchantCode
     * @return
     */
    @GetMapping("/mall/orderWayBills/{orderCode}/{merchantCode}")
    Result<OrderWayBills> getOrderDetailWayBillState(@PathVariable("orderCode") String orderCode, @PathVariable("merchantCode") String merchantCode);

    /**
     * 获取入驻主体日收入统计数据
     * @param objectIncomeSearch
     * @return
     */
    @PostMapping("/mall/orderOrders/objectIncome")
    Result<List<SalesVolume>> getMerchantSalesVolume(@RequestBody ObjectIncomeSearch objectIncomeSearch);

    /**
     * 获取入驻主体日收入统计数据
     * @param objectIncomeSearch
     * @return
     */
    @PostMapping("/mall/orderOrders/subMerchantIncome")
    Result<List<SalesVolume>> getSubMerchantSalesVolume(@RequestBody ObjectIncomeSearch objectIncomeSearch);

}
