package com.ht.feignapi.yz.controller;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tencent.service.TencentCosService;

import com.ht.feignapi.tonglian.card.entity.DESDataStr;
import com.ht.feignapi.util.DESUtil;
import com.ht.feignapi.yz.entity.WoWorkOrderDetails;
import com.ht.feignapi.yz.entity.WoWorkOrders;
import com.ht.feignapi.yz.service.WorkOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/14 17:17
 */
@RequestMapping("/yz/work")
public class WorkController {

    @Autowired
    private WorkOrdersService workOrdersService;

    @Autowired
    private TencentCosService tencentCosService;

    @Autowired
    private DESUtil desUtil;

    @PostMapping
    public boolean saveOrder(@RequestBody WoWorkOrders workOrders){
        return workOrdersService.saveOrders(workOrders);
    }

    @PostMapping("/details")
    public boolean updateOrder(@RequestBody WoWorkOrderDetails workOrderDetails){
        return workOrdersService.saveOrderDetails(workOrderDetails);
    }

    @GetMapping("/orderDetails/{code}")
    public List<WoWorkOrders> getOrders(@PathVariable("code") String code){
        return workOrdersService.getOrders(code);
    }

}
