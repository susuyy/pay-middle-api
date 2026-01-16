package com.ht.feignapi.mall.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetServiceData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.entity.MallShops;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantUser;
import com.ht.feignapi.tonglian.merchant.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.ht.feignapi.result.ResultTypeEnum.SERVICE_SUCCESS;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/27 10:03
 */
@RestController
@RequestMapping("/mall/shops")
public class MallShopsController {

    @Autowired
    MallAppShowClientService appShowClientService;

    @Autowired
    MerchantsClientService merchantsClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    private InventoryClientService inventoryClientService;

    @PostMapping("/{ojbMerchantCode}")
    public boolean addStore(
            @PathVariable("ojbMerchantCode") String ojbMerchantCode,
            @RequestBody MallShops mallShops){
        UserUsers users = this.registerUser(IdWorker.getIdStr(),"123456");
        mallShops.setMerchantCode(IdWorker.getIdStr());
        Merchants merchants = new Merchants();
        merchants.setMerchantCode(mallShops.getMerchantCode());
        merchants.setMerchantName(mallShops.getMerchantName());
        merchants.setType("MERCHANT");
        merchants.setUserId(users.getId());
        merchants.setBusinessSubjects(ojbMerchantCode);
        merchants.setChargeType("charge_by_entity");
        merchants.setLocation(mallShops.getLocation()+" "+mallShops.getAddress());
        merchants.setMerchantContact(mallShops.getMerchantPhone());
        merchants.setMerchantPicUrl(mallShops.getMainPicUrl());
        Result<Boolean> result = merchantsClientService.save(merchants);

        merchantService.saveMerchantUser(merchants.getMerchantCode(),users.getId());
        if (SERVICE_SUCCESS.getCode().equals(result.getCode()) || result.getData()){
            inventoryClientService.createDefaultMerchantWarehouse(mallShops.getMerchantCode());
            if (mallShops.getConfig()!=null&&mallShops.getConfig().getMchId()!=null){
                merchantService.saveBaseMerchantShopConfig(mallShops.getConfig(),mallShops.getMerchantCode(),ojbMerchantCode);
            }
            appShowClientService.saveMallShop(mallShops);
            return true;
        }
        return false;
    }

    private UserUsers registerUser(String username,String password){
        UserUsers userUsers = new UserUsers();
        userUsers.setAppName(AppConstant.TONGLIAN_APP_NAME);
        userUsers.setAppCode(AppConstant.TONGLIAN_APP_CODE);
        userUsers.setAccount(username);
        userUsers.setPassword(password);
        Result<RetServiceData> result = authClientService.register(userUsers);
        RetServiceData resultData = result.getData();
        System.out.println("****************user**************" + resultData);
        return resultData.getData();
    }
}
