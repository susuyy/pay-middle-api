package com.ht.feignapi.mall.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${custom.client.order.name}",contextId = "orderWayBills")
public interface OrderWayBillsClientService {

    /**
     * 根据orderCode 查询 派单信息
     * @param orderCode
     * @return
     */
    @GetMapping("/mall/orderWayBills/getByOrderCode")
    Result<List<OrderWayBills>> queryByOrderCode(@RequestParam("orderCode") String orderCode);


    /**
     * 保存 OrderWayBills 派送相关
     * @param orderWayBills
     */
    @PostMapping("/mall/orderWayBills/saveOrderWayBills")
    void saveOrderWayBills(@RequestBody OrderWayBills orderWayBills);


    /**
     * 根据商户编码查询运费计算规则
     * @param merchantCode
     * @return
     */
    @GetMapping("/mall/orderWayBillFeeRules/queryWayBillFeeRules")
    Result<List<OrderWayBillFeeRules>> queryWayBillFeeRules(@RequestParam("merchantCode") String merchantCode);

    /**
     * 分页展示用户 派送单
     * @param showMyWayBillsData
     * @return
     */
    @PostMapping("/mall/orderWayBills/showMyOrderWayBills")
    Result<Page<OrderWayBills>> showMyOrderWayBills(@RequestBody ShowMyWayBillsData showMyWayBillsData);

    /**
     * 查询派送单下的实体商品
     * @param id
     * @param wayBillCode
     * @return
     */
    @GetMapping("/mall/orderWayBills/queryWayBillsProduction")
    Result<WayBillPageData> queryWayBillsProduction(@RequestParam("id") String id, @RequestParam("wayBillCode") String wayBillCode);
}
