package com.ht.feignapi.pay.client;

import com.ht.feignapi.mall.entity.OrderRefundData;
import com.ht.feignapi.mall.entity.RetRefundOrderData;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "${custom.client.pay.name}",contextId = "payProject")
public interface PayProjectClient {

    /**
     * 查询商户的二维码id 编码
     * @param merchantCode
     * @return
     */
    @GetMapping("/payconfig/pay-config/queryC")
    Result<String> queryC(@RequestParam("merchantCode") String merchantCode);

    /**
     * 调用通联订单查询接口 反查订单
     * @param orderCode
     * @param trxId
     * @param cusId
     * @param appId
     * @param merchantCode
     * @return
     */
    @GetMapping("/allinpay/queryAllInPayOrder")
    Result<Map> queryAllInPayOrder(@RequestParam("orderCode")String orderCode, @RequestParam("trxId")String trxId,
                                  @RequestParam("cusId")String cusId, @RequestParam("appId")String appId, @RequestParam("merchantCode") String merchantCode);


    /**
     * 调用退款
     * @param orderRefundData
     * @return
     * @throws Exception
     */
    @PostMapping("/mallAllinpay/mallOrderRefund")
    Result<RetRefundOrderData> mallOrderRefund(@RequestBody OrderRefundData orderRefundData);

}
