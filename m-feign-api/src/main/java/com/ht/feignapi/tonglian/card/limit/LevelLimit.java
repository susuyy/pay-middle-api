package com.ht.feignapi.tonglian.card.limit;

import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 10:18
 */
public class LevelLimit implements LimitStrategy{

    private final Long userId;
    private final String merchantCode;
    private final String memberType;

    private MapMerchantPrimesClientService merchantPrimesService;

    public LevelLimit(Long userId,String merchantCode,String memberType,MapMerchantPrimesClientService merchantPrimesService){
        this.userId = userId;
        this.merchantCode = merchantCode;
        this.memberType = memberType;
        this.merchantPrimesService = merchantPrimesService;
    }

    @Override
    public Boolean checkLimit() {
        List<MrcMapMerchantPrimes> list = merchantPrimesService.getUserByMemberType(merchantCode,memberType).getData();
        return list.stream().anyMatch(e->e.getUserId().equals(userId));
    }
}
