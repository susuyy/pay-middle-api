package com.ht.feignapi.appshow.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.appshow.entity.MallCoupon;
import com.ht.feignapi.appshow.entity.MallCouponSearch;
import com.ht.feignapi.appshow.service.MallCouponService;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.entity.Inventory;
import com.ht.feignapi.mall.service.InventoryService;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.card.entity.CardLimits;
import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.config.CardConstant;
import com.ht.feignapi.tonglian.config.CardLimitTypeConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.util.DateStrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zheng weiguang
 * @Date: 2020/10/21 14:45
 */
@RestController
@RequestMapping("/appshow/coupons")
public class MallCouponController {

    @Autowired
    MallAppShowClientService mallAppShowClientService;

    @Autowired
    MerchantsClientService merchantsClientService;

    @Autowired
    CardCardsClientService cardsClientService;

    @Autowired
    MallCouponService mallCouponService;

    @Autowired
    CardMapMerchantCardClientService merchantCardClientService;

    @Autowired
    InventoryService inventoryService;

    @GetMapping("/{mallCode}/{merchantCode}/{type}")
    public IPage<MallCoupon> getMallCouponList(
            @PathVariable("mallCode") String mallCode,
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("type") String type,
            MallCouponSearch mallCouponSearch,
            @RequestParam(value = "pageNo",required = false,defaultValue = "0") Long pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Long pageSize){
        List<String> merchantCodeList = new ArrayList<>();
        Result<List<Merchants>> result = merchantsClientService.getSubMerchants(merchantCode);
        if (result!=null && result.getData()!=null &&!CollectionUtils.isEmpty(result.getData())){
            merchantCodeList = result.getData().stream().map(Merchants::getMerchantCode).collect(Collectors.toList());
        }
        Result<Page<MallCoupon>> result1 = mallAppShowClientService.getMallCouponList(mallCode,merchantCodeList,type, mallCouponSearch, pageSize, pageNo);
        return result1.getData();
    }

    @PostMapping("/saveCoupons")
    public void saveMallCoupon(@RequestBody MallCoupon mallCoupon) throws Exception {
        Inventory inventory = inventoryService.createInventory(mallCoupon.getMerchantCode(),mallCoupon.getCouponCode(),mallCoupon.getInventory());
        saveMallCoupon(mallCoupon, inventory);
        decorateLimitForShow(mallCoupon);
        mallAppShowClientService.saveMallCoupon(mallCoupon);
    }

    private void decorateLimitForShow(MallCoupon mallCoupon) {
        if (!CollectionUtils.isEmpty(mallCoupon.getLimitsList())){
            for (CardLimits limit:mallCoupon.getLimitsList()) {
                if (limit.getType().equals(CardLimitTypeConfig.LIMIT_TOTAL)){
                    mallCoupon.setLimitForShow(limit.getLimitKey());
                }
            }
        }
    }

    private void saveMallCoupon(MallCoupon mallCoupon, Inventory inventory) {
        CardCards card = new CardCards();
        card.setValidFrom(mallCoupon.getValidFrom());
        card.setValidTo(mallCoupon.getValidTo());
        card.setCardName(mallCoupon.getCouponName());
        card.setFaceValue(mallCoupon.getFaceValue());
        card.setType(mallCoupon.getCouponType());
        card.setCardPicUrl(mallCoupon.getCouponUrl());
        card.setCardCode(mallCoupon.getCouponCode());
        card.setValidityType(CardConstant.BEGIN_TO_END);
        card.setPrice(0);
        card.setHaltSaleDate(mallCoupon.getEndTime());
        card.setOnSaleDate(mallCoupon.getBeginTime());
        card.setCategoryCode(mallCoupon.getCouponCode());
        card.setInventory(inventory.getInventory());
        card.setBatchCode(inventory.getBatchCode());
        card.setLimits(mallCoupon.getLimitsList());
        merchantCardClientService.saveMallCoupon(card,mallCoupon.getMerchantCode());
    }
}
