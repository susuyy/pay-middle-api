package com.ht.feignapi.yz.service;

import com.ht.feignapi.yz.entity.WoWorkOrderDetails;
import com.ht.feignapi.yz.entity.WoWorkOrders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/14 17:18
 */
@Service
@FeignClient(url = "${custom.client.yz.url}", name = "${custom.client.yz.name}")
public interface WorkOrdersService {


    @PostMapping("/work/wo-work-orders")
    boolean saveOrders(@RequestBody WoWorkOrders workOrders);

    @PostMapping("/work/wo-work-orders/details")
    boolean saveOrderDetails(WoWorkOrderDetails workOrderDetails);

    @GetMapping("/work/wo-work-orders/{code}")
    List<WoWorkOrders> getOrders(@PathVariable("code") String code);
}
