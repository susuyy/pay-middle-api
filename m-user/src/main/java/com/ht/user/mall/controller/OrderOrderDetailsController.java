package com.ht.user.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.mall.entity.OrderOrderDetails;
import com.ht.user.mall.entity.OrderOrders;
import com.ht.user.mall.entity.RetOrderQrCodeDetailData;
import com.ht.user.mall.service.OrderOrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单明细 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@RestController
@RequestMapping("/mall/orderOrderDetails")
@CrossOrigin(allowCredentials = "true")
public class OrderOrderDetailsController {

    @Autowired
    private OrderOrderDetailsService orderOrderDetailsService;

    /**
     * 根据orderCode 查询订单明细表数据
     * @param orderCode
     * @return
     */
    @GetMapping("/queryByOrderCode")
    public List<OrderOrderDetails> queryByOrderCode(@RequestParam("orderCode")String orderCode){
        QueryWrapper<OrderOrderDetails> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return orderOrderDetailsService.list(queryWrapper);
    }

    /**
     * 根据 id 查询订单明细
     * @param id
     * @return
     */
    @GetMapping("/queryDetailById")
    public OrderOrderDetails queryDetailById(@RequestParam("id") String id){
        return orderOrderDetailsService.getById(Long.parseLong(id));
    }

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
    @PostMapping("/saveOrderOrderDetails")
    public OrderOrderDetails saveOrderOrderDetails(@RequestParam("orderCode") String orderCode,
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
                                            @RequestParam("discount")Integer discount){
        return orderOrderDetailsService.saveOrderOrderDetails(orderCode,merchantCode,userId,quantity,amount,productionCode,productionName,
                productionCategoryCode,productionCategoryName,activityCode,shoppingCartOrderCode,state,type,discount);
    }


    /**
     * 修改明细折扣
     * @param useDetailList
     * @param oneDetailDiscount
     */
    @PostMapping("/updateDetailDiscount")
    public void updateDetailDiscount(@RequestBody List<OrderOrderDetails> useDetailList,@RequestParam("oneDetailDiscount") Integer oneDetailDiscount){
        for (OrderOrderDetails orderOrderDetails : useDetailList) {
            orderOrderDetailsService.updateDiscountById(orderOrderDetails.getId(),oneDetailDiscount);
        }
    }


    /**
     * 根据id 修改订单明细状态
     * @param orderDetailId
     * @param state
     */
    @PostMapping("/updateOrderDetailState")
    public void updateOrderDetailState(@RequestParam("orderDetailId") String orderDetailId, @RequestParam("state") String state){
        orderOrderDetailsService.updateStateById(orderDetailId,state);
    }
}

