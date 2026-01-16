package com.ht.user.mall.controller;


import com.ht.user.mall.entity.OrderWayBills;
import com.ht.user.mall.service.OrderWayBillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-11
 */
@RestController
@RequestMapping("/mall/orderWayBills")
@CrossOrigin(allowCredentials = "true")
public class OrderWayBillsController {

    @Autowired
    private OrderWayBillsService orderWayBillsService;

    /**
     * 根据orderCode 查询 派单信息
     * @param orderCode
     * @return
     */
    @GetMapping("/getByOrderCode")
    public List<OrderWayBills> queryByOrderCode(@RequestParam("orderCode") String orderCode){
        return orderWayBillsService.queryByOrderCode(orderCode);
    }

    /**
     * 保存 OrderWayBills 派送相关
     * @param orderWayBills
     */
    @PostMapping("/saveOrderWayBills")
    public void saveOrderWayBills(@RequestBody OrderWayBills orderWayBills){
        System.out.println("===============================创建派送数据");
        orderWayBillsService.saveOrderWayBills(orderWayBills.getUserId(),orderWayBills.getOrderCode(),orderWayBills.getWayBillCode(),
                orderWayBills.getProvince(),orderWayBills.getCity(),orderWayBills.getCounty(),
                orderWayBills.getAddress(),orderWayBills.getTel(),orderWayBills.getName(),orderWayBills.getBillFee(),orderWayBills.getType(),orderWayBills.getState(),
                orderWayBills.getMerchantCode());
    }

}

