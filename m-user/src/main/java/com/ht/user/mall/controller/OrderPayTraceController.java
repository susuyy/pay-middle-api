package com.ht.user.mall.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.mall.entity.OrderPayTrace;
import com.ht.user.mall.service.OrderPayTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单支付流水 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@RestController
@RequestMapping("/mall/orderPayTrace")
@CrossOrigin(allowCredentials = "true")
public class OrderPayTraceController {


    @Autowired
    private OrderPayTraceService orderPayTraceService;

    /**
     * 根据orderCode 查询订单流水表 数据
     * @param orderCode
     * @return
     */
    @GetMapping("/queryByOrderCode")
    public List<OrderPayTrace> queryByOrderCode(@RequestParam("orderCode")String orderCode){
        QueryWrapper<OrderPayTrace> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return orderPayTraceService.list(queryWrapper);
    }

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
    @PostMapping("/saveOrderPayTrace")
    public OrderPayTrace saveOrderPayTrace(@RequestParam("orderCode")String orderCode,
                                    @RequestParam("orderDetailId")Long orderDetailId,
                                    @RequestParam("payCode")String payCode,
                                    @RequestParam("type")String type,
                                    @RequestParam("state")String state,
                                    @RequestParam("source")String source,
                                    @RequestParam("sourceId")String sourceId,
                                    @RequestParam("amount")Integer amount,
                                    @RequestParam("posSerialNum")String posSerialNum){
        return orderPayTraceService.saveOrderPayTrace(orderCode,orderDetailId,payCode,type,state,source,sourceId,amount,posSerialNum);
    }

}

