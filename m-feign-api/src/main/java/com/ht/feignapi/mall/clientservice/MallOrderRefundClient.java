package com.ht.feignapi.mall.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.entity.OrderRefund;
import com.ht.feignapi.mall.entity.OrderRefundDetails;
import com.ht.feignapi.mall.entity.RefundListData;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${custom.client.order.name}",contextId = "mallOrderRefund")
public interface MallOrderRefundClient {

    /**
     * 提交退款单
     * @param refundListData
     */
    @PostMapping("/mall/orderRefund/addOrderRefund")
    void addOrderRefund(@RequestBody RefundListData refundListData);

        /**
     * 获取主体的退款列表信息
     * @param merchantCodes
     * @param pageNo
     * @param pageSize
     * @return
     */
    @PostMapping("/mall/orderRefund/list")
    Result<Page<OrderRefund>> getRefundList(
            @RequestBody List<String> merchantCodes,
            @RequestParam("pageNo") Long pageNo,
            @RequestParam("pageSize") Long pageSize);

    /**
     * 执行退款修改订单状态流程
     * @param orderRefundDetails
     */
    @PutMapping("/mall/orderRefund")
    void doRefundOrders(@RequestBody OrderRefundDetails orderRefundDetails);

    /**
     * 退款某个退款详情单
     * @param refundDetailId
     * @return
     */
    @GetMapping("/mall/orderRefund/info/{id}")
    Result<OrderRefundDetails> getRefundDetailById(@PathVariable("id") Long refundDetailId);
}
