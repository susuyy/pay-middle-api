package com.ht.feignapi.mall.clientservice;

import com.ht.feignapi.mall.entity.PayOrderData;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.order.entity.UnionOrderData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "${custom.client.pay.name}",contextId = "mallPay")
public interface MallPayClientService {

    /**
     * 商城 购物 调取支付 H5
     * @param payOrderData
     * @return
     */
    @PostMapping("/mallAllinpay/mallUnionOrderBuy")
    Result<Map> mallUnionOrderBuy(@RequestBody PayOrderData payOrderData);

    /**
     * higo 购物 调取支付 小程序-api web
     * @param payOrderData
     * @return
     */
    @PostMapping("/mallAllinpay/mallUnionOrderBuyApiWeb")
    Result<Map> mallUnionOrderBuyApiWeb(@RequestBody PayOrderData payOrderData);
}
