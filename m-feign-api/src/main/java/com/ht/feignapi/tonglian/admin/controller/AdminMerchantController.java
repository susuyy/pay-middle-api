package com.ht.feignapi.tonglian.admin.controller;

import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.config.DbConstantGroupConfig;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.service.MerchantService;
import com.ht.feignapi.tonglian.sysconstant.clientservice.DicConstantClientService;
import com.ht.feignapi.tonglian.sysconstant.clientservice.RegionClientService;
import com.ht.feignapi.tonglian.sysconstant.entity.DicConstant;
import com.ht.feignapi.tonglian.sysconstant.entity.DicRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/27 14:54
 */
@RequestMapping("/admin/merchant")
@RestController
public class AdminMerchantController {

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private DicConstantClientService dicConstantClientService;

    @Autowired
    private RegionClientService regionClientService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AuthClientService authClientService;

    /**
     * 店铺管理--公众号管理--公众号菜单管理
     * @param merchantCode
     * @return
     */
    @GetMapping("/miniProgramUrl/{merchantCode}")
    public String getMiniProgramUrl(@PathVariable("merchantCode") String merchantCode){
        List<DicConstant> list = dicConstantClientService.getKeyValue(DbConstantGroupConfig.MINI_PROGRAM_URL).getData();
        Assert.isTrue(!CollectionUtils.isEmpty(list),"参数有误");
        return list.get(0).getValue()+"?merchantCode="+merchantCode;
    }


    /**
     * 获取省市区下拉框，省直接传入regionId=10000
     * @param regionId
     * @return
     */
    @GetMapping("/regions/{regionId}")
    public List<DicRegion> getProvince(@PathVariable("regionId") Integer regionId){
        return regionClientService.getRegionList(regionId).getData();
    }

    /**
     * 获取收银下拉框
     * @return contants
     */
    @GetMapping("/chargeType")
    public List<DicConstant> getChargeType(){
        return dicConstantClientService.getListByGroupCode(DbConstantGroupConfig.MRC_CHARGE_TYPE).getData();
    }

    /**
     * pos端--验证管理员密码
     * @param merchantCode
     * @param password
     * @return
     */
    @PostMapping("/passwordCheck/{merchantCode}/{password}")
    public String checkPassword(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("password") String password){
        Result<Merchants> merchantByCode = merchantsClientService.getMerchantByCode(merchantCode);
        Merchants merchants = merchantByCode.getData();
        Boolean result = authClientService.checkPassword(merchants.getUserId(),password).getData();
        return result?"验证成功":"验证失败";
    }
}
