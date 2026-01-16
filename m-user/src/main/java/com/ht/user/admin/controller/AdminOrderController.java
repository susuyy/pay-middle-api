package com.ht.user.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.OrdersVo;
import com.ht.user.card.service.CardOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/13 17:37
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {
    @Autowired
    private CardOrdersService ordersService;

    /**
     * 订单管理--列表
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/{merchantCode}")
    public IPage<OrdersVo> getOrderList(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(required = false, defaultValue = "10",value = "pageNo") Long pageNo,
            @RequestParam(required = false, defaultValue = "10",value = "pageSize") Long pageSize){
        IPage<OrdersVo> page = new Page<>(pageNo,pageSize);
        List<OrdersVo> list = ordersService.getOrderList(merchantCode,page);
        page.setRecords(list);
        return page;
    }



}
