package com.ht.feignapi.tonglian.merchant.clientservice;

import com.ht.feignapi.mall.entity.BizMerchantUserData;
import com.ht.feignapi.mall.entity.MrcPrimeDiscountPoints;
import com.ht.feignapi.mall.entity.OrderProductions;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.admin.entity.MerchantCardEditVo;
import com.ht.feignapi.tonglian.merchant.entity.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.POST;
import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 11:42
 */
@FeignClient(name = "${custom.client.merchant.name}",contextId = "merchantClient")
public interface MerchantsClientService {

    /**
     * 通过openId和商户号，获取商户用户的信息
     * @param openId
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants/{openId}/{merchantCode}")
    Result<MrcMapMerchantPrimes> getPrimeByOpenId(@PathVariable("openId") String openId, @PathVariable("merchantCode") String merchantCode);

    /**
     * 通过商户号获取商户信息
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants/{merchantCode}")
    Result<Merchants> getMerchantByCode(@PathVariable("merchantCode") String merchantCode);

    /**
     * 查询父级商户编码 (xxxxxxxxxx)
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants/parentMerchant/{merchantCode}")
    Result<String> queryObjectMerchantCode(@PathVariable("merchantCode") String merchantCode);

    /**
     * 通过商户号，获取到子商户列表
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants/subMerchants/{merchantCode}")
    Result<List<Merchants>> getSubMerchants(@PathVariable("merchantCode") String merchantCode);

    /**
     * 根据userId 获取商户信息
     * @param userId
     * @return
     */
    @GetMapping("/merchants/queryByUserId/{userId}")
    Result<Merchants> queryByUserId(@PathVariable("userId") Long userId);

    /**
     * 保存merchants
     * @param merchants
     * @return
     */
    @PostMapping("/merchants")
    Result<Boolean> save(@RequestBody Merchants merchants);

    /**
     * 保存primeDiscountPoints产品积分
     * @param primeDiscountPoints
     */
    @PostMapping("/mrc-prime-discount-points")
    void savePrimeDiscountPoints(@RequestBody MrcPrimeDiscountPoints primeDiscountPoints);

    /**
     * 保存商户用户关系
     * @param mapMerchantUser
     */
    @PostMapping("/merchants/user")
    void saveMerchantUser(@RequestBody MrcMapMerchantUser mapMerchantUser);

    /**
     * 获取商户对应的用户管理员列表
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants/merchantAdminUserList/{merchantCode}")
    Result<List<MrcMapMerchantUser>> getMerchantAdminUserList(@PathVariable("merchantCode") String merchantCode);

    /**
     * 获取商户对应的用户管理员列表
     * @param merchantCode
     * @param userId
     * @return
     */
    @GetMapping("/merchants/merchantAdmin/{userId}/{merchantCode}")
    Result<MrcMapMerchantUser> getMerchantAdminUser(
            @PathVariable("merchantCode") String merchantCode,
            @PathVariable("userId") Long userId);

    /**
     * 保存和关联用户
     * @param saveAndMapUser
     */
    @PostMapping("/merchants/saveAndMapUser")
    Result<String> saveAndMapUser(@RequestBody SaveAndMapUser saveAndMapUser);

    /**
     * 获取所有的主体商户号
     * @return
     */
    @GetMapping("/merchants/objectMerchantCodes")
    Result<List<String>> getObjMerchantCodes();

    /**
     * 保存用户和(商户号关联)
     * @param mrcMapMerchantUser
     */
    @PostMapping("/merchants/saveMerchantMapUser")
    void saveMerchantMapUser(@RequestBody MrcMapMerchantUser mrcMapMerchantUser);

    /**
     * 查询通商云 的商户bizUserId 和 用户对应的 bizUserId
     * @param merchantCode
     * @param userId
     */
    @GetMapping("/merchants/queryBizUserData")
    Result<BizMerchantUserData> queryMerchantBizUserIdAndPayerBizUserId(@RequestParam("merchantCode") String merchantCode, @RequestParam("userId") Long userId);
}
