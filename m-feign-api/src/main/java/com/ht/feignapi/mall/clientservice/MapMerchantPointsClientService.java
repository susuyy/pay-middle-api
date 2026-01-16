package com.ht.feignapi.mall.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.mall.entity.MrcPrimeDiscountPoints;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.admin.entity.VipSearch;
import com.ht.feignapi.tonglian.admin.entity.VipVo;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${custom.client.merchant.name}",contextId = "mapMerchantPointsClient")
public interface MapMerchantPointsClientService {


    /**
     * 查询商品积分抵扣
     * @param merchantCode
     * @param productionCode
     */
    @GetMapping("/mrc-prime-discount-points/queryPrimeDiscountPoints")
    Result<MrcPrimeDiscountPoints> queryPrimeDiscountPoints(@RequestParam("merchantCode") String merchantCode,
                                                            @RequestParam("productionCode") String productionCode);
}
