package com.ht.feignapi.mall.service;

import com.ht.feignapi.mall.clientservice.OrderCategoriesClientService;
import com.ht.feignapi.mall.entity.OrderCategorys;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class OrderCategorysServeice {

    @Autowired
    private OrderCategoriesClientService orderCategoriesClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;


    public Result<OrderCategorys> queryLevelOneCode(String categoryThreeCode, String merchantCode){
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        return orderCategoriesClientService.queryLevelOneCode(categoryThreeCode, merchants.getBusinessSubjects());
    }
}
