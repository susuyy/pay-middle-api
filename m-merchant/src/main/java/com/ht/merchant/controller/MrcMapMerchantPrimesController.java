package com.ht.merchant.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.merchant.entity.MrcMapMerchantPrimes;
import com.ht.merchant.entity.vo.MerchantPrimeVo;
import com.ht.merchant.service.MerchantsService;
import com.ht.merchant.service.MrcMapMerchantPrimesService;
import com.ht.merchant.vo.VipSearch;
import com.ht.merchant.vo.VipVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商户-会员对应表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-06-15
 */
@RestController
@RequestMapping("/merchants-prime")
public class MrcMapMerchantPrimesController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MrcMapMerchantPrimesService merchantPrimesService;

    @Autowired
    private MerchantsService merchantsService;

    /**
     * 保存商户和会员关联信息
     * @param prime 保存prime
     * @return 返回保存的prime信息
     */
    @PostMapping
    public MrcMapMerchantPrimes save(@RequestBody MrcMapMerchantPrimes prime){
        merchantPrimesService.saveOrUpdate(prime);
        return prime;
    }

    /**
     * 查询商户和会员关联信息
     * @param userId
     * @param merchantCode
     * @return
     */
    @GetMapping("/vipInfo/{userId}/{merchantCode}")
    public MrcMapMerchantPrimes queryByUserIdAndMerchantCode(@PathVariable("userId") Long userId,@PathVariable("merchantCode") String merchantCode){
        MrcMapMerchantPrimes mrcMapMerchantPrimes = merchantPrimesService.queryByUserIdAndMerchantCode(userId, merchantCode);
        System.out.println(mrcMapMerchantPrimes);
        return merchantPrimesService.queryByUserIdAndMerchantCode(userId,merchantCode);
    }

    /**
     * 获取商户会员
     *
     * @param merchantCode 商户号
     * @param pageNo       页码
     * @param pageSize     每页展示数据条数
     * @return 列表
     */
    @GetMapping("/vip/{merchantCode}")
    public IPage<VipVo> getMerchantsPrimes(@PathVariable("merchantCode") String merchantCode,
                                           VipSearch vipSearch,
                                           @RequestParam(required = false, defaultValue = "0") Long pageNo,
                                           @RequestParam(required = false, defaultValue = "10") Long pageSize) {
        IPage<VipVo> page = new Page<>(pageNo, pageSize);
        List<VipVo> listVo = merchantPrimesService.getVipListByMerchantCodes(merchantCode, vipSearch, page);
        page.setRecords(listVo);
        return page;
    }

    /**
     * 获取所有的vip
     * @param vipSearch
     * @return
     */
    @PostMapping("/allVip")
    public Page<VipVo> getMerchantAllVipUsers(@RequestBody VipSearch vipSearch){
        Page<VipVo> page = new Page<>(vipSearch.getPageNo(), vipSearch.getPageSize());
        List<VipVo> listVo = merchantPrimesService.getVipListByMerchantCodes(vipSearch.getMerchantCode(), vipSearch, page);
        page.setRecords(listVo);
        return page;
    }

    /**
     * 用户成为商户会员
     * @param userId
     * @param merchantCode
     * @param normal
     * @param type
     * @param openid
     */
    @PostMapping("/add")
    public void add(@RequestParam("userId") Long userId, @RequestParam("merchantCode") String merchantCode, @RequestParam("normal") String normal,
             @RequestParam("type") String type, @RequestParam("openid") String openid){
        merchantPrimesService.add(userId,merchantCode,normal,type,openid);
    }

    /**
     * 获取对应merchantcode下的所有type类型的会员
     * @param adminMerchantCode
     * @param memberType
     * @return
     */
    @GetMapping("/{merchantCode}/primes/{memberType}")
    public List<MrcMapMerchantPrimes> getUserByMemberType(@PathVariable("merchantCode") String adminMerchantCode,@PathVariable("memberType") String memberType){
        logger.info("merchantCode:" + adminMerchantCode+ "*************  memberType:"+memberType);
        return merchantPrimesService.getUserByMemberType(adminMerchantCode,memberType);
    }

    /**
     * 保存vip状态
     * @param vipId 商户会员id
     * @return 保存结果
     */
    @PutMapping("/{vipId}")
    public String saveVipState(@PathVariable("vipId") Long vipId,
                               @RequestBody Map<String,String> map) {
        Assert.isTrue(map.containsKey("state"),"缺少state参数");
        MrcMapMerchantPrimes merchantPrimes = merchantPrimesService.getById(vipId);
        merchantPrimes.setState(map.get("state"));
        Boolean result = merchantPrimesService.updateById(merchantPrimes);
        Assert.isTrue(result,"保存失败");
        return "保存成功";
    }

    /**
     * 查询用户 在某个商户下的积分
     * @param userId
     * @param openId
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryUserPoint")
    public Integer queryUserPoint(@RequestParam("userId")Long userId,@RequestParam("openId")String openId,@RequestParam("merchantCode")String merchantCode){
        return merchantPrimesService.queryUserPoint(userId,openId,merchantCode);
    }

    /**
     * 查询用户 总积分
     * @param userId
     * @param openId
     * @return
     */
    @GetMapping("/queryMyTotalPoint")
    public Integer queryMyTotalPoint(@RequestParam("userId")String userId,@RequestParam("openId")String openId){
        return merchantPrimesService.queryMyTotalPoint(userId,openId);
    }

    /**
     * 查询用户 对应的 MrcMapMerchantPrimes
     * @param userId
     * @param openId
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryMyMrcMapMerchantPrimes")
    public MrcMapMerchantPrimes queryMyMrcMapMerchantPrimes(@RequestParam("userId")Long userId,
                                                             @RequestParam("openId")String openId,
                                                             @RequestParam("merchantCode")String merchantCode){
        return merchantPrimesService.queryMyMrcMapMerchantPrimes(userId,openId,merchantCode);
    }

    /**
     * 根据 merchants_prime表 id 扣除使用积分
     * @param id
     * @param usePoints
     */
    @PostMapping("/deductPointsById")
    public void deductPointsById(@RequestParam("id") String id,@RequestParam("usePoints") Integer usePoints){
        merchantPrimesService.deductPointsById(id,usePoints);
    }

    /**
     * 根据手机号和merchantCode 查询唯一会员信息
     * @param tel
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryByTelAndMerchantCode")
    public MrcMapMerchantPrimes queryByTelAndMerchantCode(@RequestParam("tel") String tel,@RequestParam("merchantCode") String merchantCode){
        return merchantPrimesService.queryByTelAndMerchantCode(tel,merchantCode);
    }

    /**
     * 获取主体每个月会员新增数目
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/monthlyIncrements")
    public List<MerchantPrimeVo> getMerchantPrimeCount(
            @RequestParam("beginDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        //获取每个主体的商户
        return merchantPrimesService.getPrimeMonthlyIncrements(beginDate,endDate,merchantsService.getObjectMerchantCodes());
    }

    /**
     * 获取主体会员截止到某个月总数统计
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/merchantPrimeTotalCount")
    public List<MerchantPrimeVo> getMerchantPrimeTotalCount(
            @RequestParam("beginDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date beginDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate){
        //获取每个主体的商户
        return merchantPrimesService.getPrimeTotalAmount(beginDate,endDate,merchantsService.getObjectMerchantCodes());
    }


    /**
     * 始终获取主体下的指定手机会员信息
     * @param tel
     * @param merchantCode
     * @return
     */
    @GetMapping("/queryByTelChangeObjectCode")
    public MrcMapMerchantPrimes queryByTelChangeObjectCode(@RequestParam("tel")String tel,@RequestParam("merchantCode")String merchantCode){
        return merchantPrimesService.queryByTelChangeObjectCode(tel, merchantCode);
    }

    /**
     * 添加主体会员信息 同时录入通商云会员相关数据
     * @param mrcMapMerchantPrimes
     */
    @PostMapping("/merchants-prime/addAndRegisterTsyMember")
    public void addAndRegisterTsyMember(@RequestBody MrcMapMerchantPrimes mrcMapMerchantPrimes){
        merchantPrimesService.addAndRegisterTsyMember(mrcMapMerchantPrimes);
    }
}

