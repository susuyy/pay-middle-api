package com.ht.user.admin.service;

import com.ht.user.admin.vo.AdjustAccount;
import com.ht.user.admin.vo.Recharge;
import com.ht.user.card.entity.CardOrders;
import com.ht.user.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/28 15:09
 */
//@Service
//@FeignClient(url = "http://localhost:13020/", name = "M-ORDER")
public interface OrderService {
    @RequestMapping(value = "/card/orders/adjustAccount",method = RequestMethod.POST)
    Result adjustAccount(@RequestBody AdjustAccount adjustAccount);

    @GetMapping(value = "/adjustAccount/{merchantCode}")
    Result getAdjustAccountOrders(@PathVariable("merchantCode") String merchantCode);

    @GetMapping(value = "/recharge/{merchantCode}")
    Result getRechargeOrders(@PathVariable("merchantCode") String merchantCode);

    @PutMapping(value = "/card/orders/recharge")
    Result recharge(@RequestBody Recharge recharge);



}
