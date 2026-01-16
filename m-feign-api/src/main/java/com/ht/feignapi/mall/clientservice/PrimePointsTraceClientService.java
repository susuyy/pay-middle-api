package com.ht.feignapi.mall.clientservice;

import com.ht.feignapi.mall.entity.MrcPrimeDiscountPoints;
import com.ht.feignapi.mall.entity.MrcPrimePointsTrace;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${custom.client.merchant.name}",contextId = "primePointsTraceClient")
public interface PrimePointsTraceClientService {

    /**
     * 保存积分流水
     * @param mrcPrimePointsTrace
     */
    @PostMapping("/mrc-prime-points-trace/save")
    void save(@RequestBody MrcPrimePointsTrace mrcPrimePointsTrace);

}
