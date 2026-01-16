package com.ht.feignapi.mall.service;

import com.ht.feignapi.mall.clientservice.MapMerchantPointsClientService;
import com.ht.feignapi.mall.clientservice.OrderProductionsClientService;
import com.ht.feignapi.mall.entity.MrcPrimeDiscountPoints;
import com.ht.feignapi.mall.entity.OrderProductions;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: zheng weiguang
 * @Date: 2020/9/29 14:49
 */
@Service
public class MerchantMallService {

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MapMerchantPointsClientService pointsClientService;

    public void createProductionPoints(OrderProductions productions) {
        MrcPrimeDiscountPoints primeDiscountPoints = new MrcPrimeDiscountPoints();
        BeanUtils.copyProperties(productions,primeDiscountPoints);
        if (productions.getLimitAmountTotal()==null){
            productions.setLimitAmountTotal(0);
        }
        if (productions.getLimitAmountPerOrder()==null){
            productions.setLimitAmountPerOrder(0);
        }
        if (productions.getPoints()==null){
            productions.setPoints(0);
        }
        merchantsClientService.savePrimeDiscountPoints(primeDiscountPoints);
    }

    public void updatePoints(OrderProductions productions) {
        Result<MrcPrimeDiscountPoints> primeDiscountPointsResult = pointsClientService.queryPrimeDiscountPoints(productions.getMerchantCode(),productions.getProductionCode());
        if (ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(primeDiscountPointsResult.getCode()) &&
                primeDiscountPointsResult.getData()!=null){
            primeDiscountPointsResult.getData().setPoints(productions.getPoints());
            primeDiscountPointsResult.getData().setLimitAmountPerOrder(productions.getLimitAmountPerOrder());
            primeDiscountPointsResult.getData().setLimitAmountTotal(productions.getLimitAmountTotal());
        }
        if (productions.getLimitAmountTotal()==null){
            primeDiscountPointsResult.getData().setLimitAmountTotal(0);
        }
        if (productions.getLimitAmountPerOrder() == null) {
            primeDiscountPointsResult.getData().setLimitAmountPerOrder(0);
        }
        if (productions.getPoints() == null) {
            primeDiscountPointsResult.getData().setPoints(0);
        }
        merchantsClientService.savePrimeDiscountPoints(primeDiscountPointsResult.getData());
    }
}
