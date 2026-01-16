package com.ht.feignapi.mall.clientservice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.feignapi.mall.entity.OrderWayBillFeeRules;
import com.ht.feignapi.mall.entity.OrderWayBillMapUser;
import com.ht.feignapi.mall.entity.OrderWayBills;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${custom.client.order.name}",contextId = "orderWayBillsMapUser")
public interface OrderWayBillsMapUserClientService {

    /**
     * 保存
     * @param orderWayBillMapUser
     */
    @PostMapping("/mall/orderWayBillMapUser/save")
    void save(@RequestBody OrderWayBillMapUser orderWayBillMapUser);

    /**
     * 修改
     * @param orderWayBillMapUser
     */
    @PostMapping("/mall/orderWayBillMapUser/update")
    void updateById(@RequestBody OrderWayBillMapUser orderWayBillMapUser);

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/mall/orderWayBillMapUser/getOneById")
    Result<OrderWayBillMapUser> getById(@RequestParam("id") String id);

    /**
     * 查询列表
     * @param userId
     * @return
     */
    @GetMapping("/mall/orderWayBillMapUser/list")
    Result<List<OrderWayBillMapUser>> list(@RequestParam("userId")Long userId);

    /**
     * 删除
     * @param id
     */
    @DeleteMapping("/mall/orderWayBillMapUser/delById")
    void removeById(@RequestParam("id") Long id);
}
