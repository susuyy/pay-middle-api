package com.ht.feignapi.prime.client;

import com.ht.feignapi.mall.entity.PayOrderData;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "${custom.client.pay.name}",contextId = "primePay")
public interface PrimePayClient {

    /**
     * 免税店买卡 调取支付
     * @param payOrderData
     * @return
     */
    @PostMapping("/primeAllinpay/buyCard")
    Result<Map> buyCard(@RequestBody PayOrderData payOrderData);
}
