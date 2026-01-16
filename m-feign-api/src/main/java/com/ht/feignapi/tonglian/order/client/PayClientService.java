package com.ht.feignapi.tonglian.order.client;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.order.entity.CuspayData;
import com.ht.feignapi.tonglian.order.entity.PayConfig;
import com.ht.feignapi.tonglian.order.entity.UnionOrderData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "${custom.client.pay.name}",contextId = "tonglianPay")
public interface PayClientService {


    /**
     * H5支付下单(组合支付)
     * @param unionOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/allinpay/unionorder")
    Result<Map> unionorder(@RequestBody UnionOrderData unionOrderData) throws Exception;


    /**
     * H5支付下单(购买卡券)
     * @param unionOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/allinpay/unionorderBuyCard")
    Result<Map> unionorderBuyCard(@RequestBody UnionOrderData unionOrderData) throws Exception;

    /**
     * 保存支付数据
     * @param payConfig
     */
    @PostMapping("/payconfig/pay-config")
    void savePayConfig(@RequestBody PayConfig payConfig);

    /**
     * 获取当面付参数
     * @param cuspayData
     * @return
     */
    @PostMapping("/allinpay/cuspay")
    Result<Map> cuspay(@RequestBody CuspayData cuspayData);


    /**
     * H5支付下单(充值订单)
     * @param unionOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/allinpay/unionorderTopUp")
    Result<Map> unionorderTopUp(@RequestBody UnionOrderData unionOrderData) throws Exception;
}
