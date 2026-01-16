package com.ht.user.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.mall.constant.OrderConstant;
import com.ht.user.mall.entity.*;
import com.ht.user.mall.service.OrderOrderDetailsService;
import com.ht.user.mall.service.OrderOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单主表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@RestController
@RequestMapping("/mall/orderOrders")
@CrossOrigin(allowCredentials = "true")
public class OrderOrdersController {

    @Autowired
    private OrderOrdersService orderOrdersService;

    @Autowired
    private OrderOrderDetailsService orderOrderDetailsService;

    /**
     * 商品详情页 直接下单购买
     * @param mallUnionOrderData
     * @return
     */
    @PostMapping("/mallUnionOrder")
    public RetUnionOrderData mallUnionOrder(@RequestBody MallUnionOrderData mallUnionOrderData){
        RetUnionOrderData retUnionOrderData = orderOrdersService.saveOrderAllData(mallUnionOrderData.getUserId(),
                mallUnionOrderData.getProductionCode(),
                mallUnionOrderData.getQuantity(),
                mallUnionOrderData.getStoreMerchantCode(),
                mallUnionOrderData.getObjectMerchantCode(),
                mallUnionOrderData.getOrderWayBills(),
                mallUnionOrderData.getCategoryLevel01Code(),
                mallUnionOrderData.getCardMapMerchantCards(),
                mallUnionOrderData.getDiscount());
        return retUnionOrderData;
    }


    /**
     * 分页展示用户订单明细  分页
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/showMyOrder")
    public IPage<OrderOrderDetails> showMyOrder(@RequestBody ShowMyOrderData showMyOrderData){
        IPage<OrderOrderDetails> retPageOrderData = orderOrderDetailsService.queryMyOrderDetail(showMyOrderData.getUserId(),
                showMyOrderData.getPageNo(),
                showMyOrderData.getPageSize(),
                showMyOrderData.getState());
        return retPageOrderData;
    }

    /**
     * 获取更多 某个商家下的 订单 数据 (目前不用 )
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/moreMyOrder")
    public IPage<OrderOrderDetails> moreMyOrder(@RequestBody ShowMyOrderData showMyOrderData){
        return orderOrderDetailsService.moreMyOrder(showMyOrderData.getMerchantCode(),
                showMyOrderData.getUserId(),
                showMyOrderData.getPageNo(),
                showMyOrderData.getPageSize(),
                showMyOrderData.getState());
    }

    /**
     * 根据orderCode 查询订单主表数据
     * @param orderCode
     * @return
     */
    @GetMapping("/queryByOrderCode")
    public OrderOrders queryByOrderCode(@RequestParam("orderCode")String orderCode){
        QueryWrapper<OrderOrders> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return orderOrdersService.getOne(queryWrapper);
    }

    /**
     * 更新所有的订单状态
     * @param paySuccess
     */
    @PostMapping("/updateMallOrderAllPaid")
    public void updateMallOrderAllPaid(@RequestBody PaySuccess paySuccess){
        orderOrdersService.updateMallOrderAllPaid(paySuccess);
    }


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
    @PostMapping("/saveOrderOrders")
    public OrderOrders saveOrderOrders(@RequestParam("orderCode")String orderCode,
                                @RequestParam("type")String type,
                                @RequestParam("state")String state,
                                @RequestParam("merchantCode")String merchantCode,
                                @RequestParam("userId")Long userId,
                                @RequestParam("saleId")String saleId,
                                @RequestParam("quantity")Integer quantity,
                                @RequestParam("amount")Integer amount,
                                @RequestParam("comments")String comments,
                                @RequestParam("discount")Integer discount){
        return orderOrdersService.saveOrderOrders(orderCode,type,state,merchantCode,userId,saleId,quantity,amount,comments,discount);
    }

    /**
     * 修改订单主表折扣数据
     * @param orderCode
     * @param discount
     */
    @PostMapping("/updateOrderDiscount")
    public void updateOrderDiscount(@RequestParam("orderCode") String orderCode,@RequestParam("discount") Integer discount){
        orderOrdersService.updateDiscountByOrderCode(orderCode,discount);
    }


    /**
     * 查询用户订单主表数据 展示 分页
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/queryMyOrderMaster")
    public IPage<OrderOrders> queryMyOrderMaster(@RequestBody ShowMyOrderData showMyOrderData){
        return orderOrdersService.queryMyOrderMaster(showMyOrderData.getUserId(),
                showMyOrderData.getPageNo(),
                showMyOrderData.getPageSize(),
                showMyOrderData.getState());
    }

    /**
     * 查询用户 商户下的更多订单 (目前不用 )
     * @param showMyOrderData
     * @return
     */
    @PostMapping("/moreMyOrderMaster")
    public Page<OrderOrders> moreMyOrderMaster(@RequestBody ShowMyOrderData showMyOrderData){
        return orderOrdersService.moreMyOrderMaster(showMyOrderData.getMerchantCode(),
                showMyOrderData.getUserId(),
                showMyOrderData.getPageNo(),
                showMyOrderData.getPageSize(),
                showMyOrderData.getState());
    }
}

