package com.ht.user.mall.controller;


import com.ht.user.mall.entity.OrderWayBillFeeRules;
import com.ht.user.mall.service.OrderWayBillFeeRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-23
 */
@RestController
@RequestMapping("/mall/orderWayBillFeeRules")
public class OrderWayBillFeeRulesController {


    @Autowired
    private OrderWayBillFeeRulesService orderWayBillFeeRulesService;

    /**
     * 根据商户编码查询运费计算规则
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryWayBillFeeRules")
    public List<OrderWayBillFeeRules> queryWayBillFeeRules(@RequestParam("merchantCode") String merchantCode){
        return orderWayBillFeeRulesService.queryWayBillFeeRules(merchantCode);
    }
}

