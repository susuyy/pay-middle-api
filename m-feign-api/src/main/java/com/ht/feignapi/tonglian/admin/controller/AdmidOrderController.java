package com.ht.feignapi.tonglian.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.feignapi.tonglian.admin.entity.OrdersVo;
import com.ht.feignapi.tonglian.card.service.CardMapMerchantCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/31 17:33
 */
@RestController
@RequestMapping("/admin/order")
public class AdmidOrderController {

    @Autowired
    private CardMapMerchantCardService cardMapMerchantCardService;

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
            @RequestParam(required = false, defaultValue = "0",value = "pageNo") Long pageNo,
            @RequestParam(required = false, defaultValue = "10",value = "pageSize") Long pageSize){
        return cardMapMerchantCardService.getOrderList(merchantCode,pageNo,pageSize);
    }
}
