package com.ht.feignapi.tonglian.merchant.clientservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.admin.entity.VipSearch;
import com.ht.feignapi.tonglian.admin.entity.VipVo;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${custom.client.merchant.name}",contextId = "mapMerchantPrimesClient")
public interface MapMerchantPrimesClientService {

    /**
     * 查询商户和会员关联信息(xxxxxxx)
     * @param userId
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants-prime/vipInfo/{userId}/{merchantCode}")
    Result<MrcMapMerchantPrimes> queryByUserIdAndMerchantCode(@PathVariable("userId") Long userId, @PathVariable("merchantCode") String merchantCode);

    /**
     * 用户成为商户会员(xxxxxxx)
     * @param userId
     * @param merchantCode
     * @param normal
     * @param type
     * @param openid
     */
    @PostMapping("/merchants-prime/add")
    void add(@RequestParam("userId") Long userId,
             @RequestParam("merchantCode") String merchantCode,
             @RequestParam("normal") String normal,
             @RequestParam("type") String type,
             @RequestParam("openid") String openid);

    /**
     * 保存或更新(xxxxxxx)
     * @param mrcMapMerchantPrimes
     * @return 返回保存的prime信息
     */
    @PostMapping("/merchants-prime")
    Result<MrcMapMerchantPrimes> saveOrUpdate(@RequestBody MrcMapMerchantPrimes mrcMapMerchantPrimes);

    /**
     * 获取对应merchantcode下的所有type类型的会员
     * @param adminMerchantCode
     * @param memberType
     * @return
     */
    @GetMapping("/merchants-prime/{merchantCode}/primes/{memberType}")
    Result<List<MrcMapMerchantPrimes>> getUserByMemberType(@PathVariable("merchantCode") String adminMerchantCode,@PathVariable("memberType") String memberType);

    @PostMapping("/merchants-prime/allVip")
    Result<Page<VipVo>> getMerchantAllVipUsers(@RequestBody VipSearch vipSearch);

    /**
     * 查询用户 在某个商户下的积分
     * @param userId
     * @param openId
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants-prime/queryUserPoint")
    Result<Integer> queryUserPoint(@RequestParam("userId")Long userId,@RequestParam("openId")String openId,@RequestParam("merchantCode")String merchantCode);

    /**
     * 查询用户 总积分
     * @param userId
     * @param openId
     * @return
     */
    @GetMapping("/merchants-prime/queryMyTotalPoint")
    Result<Integer> queryMyTotalPoint(@RequestParam("userId")String userId,@RequestParam("openId")String openId);

    /**
     * 查询用户 对应的 MrcMapMerchantPrimes
     * @param userId
     * @param openId
     * @return
     */
    @GetMapping("/merchants-prime/queryMyMrcMapMerchantPrimes")
    Result<MrcMapMerchantPrimes> queryMyMrcMapMerchantPrimes(@RequestParam("userId")Long userId,
                                                             @RequestParam("openId")String openId,
                                                             @RequestParam("merchantCode")String merchantCode);

    /**
     * 根据 merchants_prime表 id 扣除使用积分
     * @param id
     * @param usePoints
     */
    @PostMapping("/merchants-prime/deductPointsById")
    void deductPointsById(@RequestParam("id") String id,@RequestParam("usePoints") Integer usePoints);

    /**
     * 根据手机号和merchantCode 查询唯一会员信息
     * @param tel
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants-prime/queryByTelAndMerchantCode")
    Result<MrcMapMerchantPrimes> queryByTelAndMerchantCode(@RequestParam("tel") String tel,@RequestParam("merchantCode") String merchantCode);

    /**
     * 始终获取主体下的指定手机会员信息
     * @param tel
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchants-prime/queryByTelChangeObjectCode")
    Result<MrcMapMerchantPrimes> queryByTelChangeObjectCode(@RequestParam("tel")String tel,@RequestParam("merchantCode")String merchantCode);

    /**
     * 添加主体会员信息 同时录入通商云会员相关数据
     * @param mrcMapMerchantPrimes
     */
    @PostMapping("/merchants-prime/addAndRegisterTsyMember")
    void addAndRegisterTsyMember(@RequestBody MrcMapMerchantPrimes mrcMapMerchantPrimes);
}
