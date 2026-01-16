package com.ht.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.merchant.entity.*;
import com.ht.merchant.entity.vo.MerchantCountVo;
import com.ht.merchant.result.Result;
import com.ht.merchant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * <p>
 * 商户 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-06-18
 */
@RestController
@RequestMapping(value = "/merchants",produces={"application/json; charset=UTF-8"})
@CrossOrigin(allowCredentials = "true")
public class MerchantsController {

    @Autowired
    private MerchantsService merchantsService;

    @Autowired
    private MrcMapMerchantPrimesService merchantPrimesService;

    @Autowired
    private MerchantsConfigService merchantsConfigService;

    @Autowired
    private IMrcMapMerchantUserService mapMerchantUserService;

    @Autowired
    private MrcMapMerchantService mrcMapMerchantService;

    /**
     * 查看店铺详情
     * @param id
     */
    @GetMapping("/info/{id}")
    public Merchants getMerchantInfo(@PathVariable("id") Long id){
        return merchantsService.getById(id);
    }

    /**
     * 店铺管理--店铺管理--新建店铺
     *
     * @param merchants
     * @return
     */
    @PostMapping("/sonMerchant")
    public String saveSonMerchant(@RequestBody Merchants merchants) {
        if (merchantsService.save(merchants)) {
            merchantsConfigService.saveMerchantQrCode(merchants.getMerchantCode());
        }
        return "保存成功";
    }

    /**
     * 店铺管理--店铺管理--新建店铺
     *
     * @param merchants
     * @return
     */
    @PostMapping
    public Boolean save(@RequestBody Merchants merchants) {
        return merchantsService.saveOrUpdate(merchants);
    }

    /**
     * 通过merchantCode获取商户
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/{merchantCode}")
    public Merchants getMerchantByCode(@PathVariable("merchantCode") String merchantCode) {
        return merchantsService.getMerchantByCode(merchantCode);
    }

    /**
     * 通过openId和商户号，获取商户用户的信息
     *
     * @param openId
     * @param merchantCode
     * @return
     */
    @GetMapping("/{openId}/{merchantCode}")
    public MrcMapMerchantPrimes getPrimeByOpenId(@PathVariable("openId") String openId, @PathVariable("merchantCode") String merchantCode) {
        return merchantPrimesService.getPrimeByOpenId(openId, merchantCode);
    }

    /**
     * 通过merchantCode获取其父商户的code
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/parentMerchant/{merchantCode}")
    public String getParentMerchantCode(@PathVariable("merchantCode") String merchantCode) {
        Merchants merchants = merchantsService.getMerchantByCode(merchantCode);
        if ("OBJECT".equals(merchants.getType())) {
            return merchants.getMerchantCode();
        } else {
            return merchants.getBusinessSubjects();
        }
    }

    /**
     * 获取 subMerchants
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/subMerchants/{merchantCode}")
    public List<Merchants> getSubMerchants(@PathVariable("merchantCode") String merchantCode) {
        List<Merchants> subMerchants = merchantsService.getSubMerchants(merchantCode);
        System.out.println(subMerchants);
        return subMerchants;
    }

    /**
     * 根据userId 获取商户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/queryByUserId/{userId}")
    public Merchants queryByUserId(@PathVariable("userId") Long userId) {
        return merchantsService.getByUserId(userId);
    }

    /**
     * 获取所有主体
     * @param pageNo
     * @param pageSize
     * @param merchantName
     * @return
     */
    @GetMapping("/object")
    public IPage<Merchants> getAllObjectMerchants(
            @RequestParam(value = "pageNo",defaultValue = "0",required = false) Long pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) Long pageSize,
            @RequestParam(value = "merchantName",defaultValue = "",required = false) String merchantName){
        IPage<Merchants> merchantsPage = merchantsService.getObjectMerchants(pageNo,pageSize,merchantName);
        if (!CollectionUtils.isEmpty(merchantsPage.getRecords())){
            merchantsPage.getRecords().forEach(e->{
                e.setSubjectMerchantsList(merchantsService.getSubMerchants(e.getMerchantCode()));
            });
        }
        return merchantsPage;
    }

    /**
     * 获取所有主体下拉框
     * @return
     */
    @GetMapping("/allObject")
    public List<Merchants> getAllObjectMerchants(){
        return merchantsService.getObjectMerchant();
    }

    /**
     * 保存商户用户关系
     * @param mapMerchantUser
     */
    @PostMapping("/user")
    public void saveMerchantUser(@RequestBody MrcMapMerchantUser mapMerchantUser){
        mapMerchantUserService.saveOrUpdate(mapMerchantUser);
    }

    /**
     * 获取商户对应的用户管理员列表
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchantAdminUserList/{merchantCode}")
    public List<MrcMapMerchantUser> getMerchantAdminUserList(@PathVariable("merchantCode") String merchantCode){
        return mapMerchantUserService.getMerchantUserList(merchantCode);
    }

    /**
     * 获取商户对应的用户管理员
     * @param merchantCode
     * @param userId
     * @return
     */
    @GetMapping("/merchantAdmin/{userId}/{merchantCode}")
    public MrcMapMerchantUser getMerchantAdminUser(
            @PathVariable("userId") String userId,
            @PathVariable("merchantCode") String merchantCode){
        return mapMerchantUserService.getMerchantUser(userId,merchantCode);
    }

    /**
     * 获取入驻的主体数目
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/objectCount")
    public List<MerchantCountVo> getMerchantCount(
            @RequestParam(value = "beginDate",defaultValue = "2020-01-01") String beginDate,
            @RequestParam(value = "endDate",defaultValue = "2099-12-31") String endDate){
        return merchantsService.getMerchantCount(beginDate,endDate);
    }

    /**
     * 保存和关联用户
     * @param saveAndMapUser
     */
    @PostMapping("/saveAndMapUser")
    public String saveAndMapUser(@RequestBody SaveAndMapUser saveAndMapUser){
        return merchantsService.saveAndMapUser(saveAndMapUser);
    }

    /**
     * 保存用户和(商户号关联)
     * @param mrcMapMerchantUser
     */
    @PostMapping("/saveMerchantMapUser")
    public void saveMerchantMapUser(@RequestBody MrcMapMerchantUser mrcMapMerchantUser){
        mapMerchantUserService.save(mrcMapMerchantUser);
    }

    /**
     * 获取所有的主体商户号
     * @return
     */
    @GetMapping("/objectMerchantCodes")
    public List<String> getObjMerchantCodes(){
        return merchantsService.getObjectMerchantCodes();
    }

    /**
     * 根据合作机构 获取绑定的主体商户
     * @return
     */
    @GetMapping("/objectMerchantCodesData")
    public List<Merchants> objectMerchantCodesData(@RequestParam("subMerchantCode")String subMerchantCode){
        return merchantsService.getObjectMerchantCodesData(subMerchantCode);
    }


    /**
     * 分页 获取 subMerchants 包含主体(平台),自身
     *
     * @param searchSubMerchantsData
     * @return
     */
    @PostMapping("/searchSubMerchants")
    public Result searchSubMerchants(@RequestBody SearchSubMerchantsData searchSubMerchantsData) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<SearchSubMerchantsData> request = new HttpEntity<>(searchSubMerchantsData, headers);
//        ResponseEntity<Result> result = restTemplate.postForEntity("https://gateway.hualta.com/m-merchant/merchants/searchSubMerchantsHlta",
//                request, Result.class);
////        ResponseEntity<Result> result = restTemplate.postForEntity("http://localhost:14030/m-merchant/merchants/searchSubMesearchSubMerchantsHltarchantsHlta",
////                request, Result.class);

//        String objectMerchantCode = searchSubMerchantsData.getObjectMerchantCode();
//        if("THSZ".equals(objectMerchantCode)){
//                    ResponseEntity<Result> result = restTemplate.postForEntity("http://localhost:14030/m-merchant/merchants/searchSubMerchantsHlta",
//                request, Result.class);
//            return result.getBody();
//        }

        ResponseEntity<Result> result = restTemplate.postForEntity("https://gateway.hualta.com/m-merchant/merchants/searchSubMerchantsHlta",
                request, Result.class);

        return result.getBody();


    }

    /**
     * 分页 获取 subMerchants 包含主体(平台),自身
     *
     * @param searchSubMerchantsData
     * @return
     */
    @PostMapping("/searchSubMerchantsHlta")
    public Page<Merchants> searchSubMerchantsHlta(@RequestBody SearchSubMerchantsData searchSubMerchantsData) {
        return merchantsService.searchSubMerchants(searchSubMerchantsData);
    }

    /**
     * 分页 获取 所有的机构列表
     *
     * @param searchSubMerchantsData
     * @return
     */
    @PostMapping("/searchAllMerchants")
    public Object searchAllMerchants(@RequestBody SearchSubMerchantsData searchSubMerchantsData) {
        if (searchSubMerchantsData.getPageNo()==null || searchSubMerchantsData.getPageSize() ==null){
            QueryWrapper<Merchants> queryWrapper=new QueryWrapper<>();
            if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchCode())){
                queryWrapper.like("merchant_code",searchSubMerchantsData.getSearchCode());
            }
            if (!StringUtils.isEmpty(searchSubMerchantsData.getSearchName())){
                queryWrapper.like("merchant_name",searchSubMerchantsData.getSearchName());
            }
            return merchantsService.list(queryWrapper);
        }
        return merchantsService.searchAllMerchants(searchSubMerchantsData);
    }

    /**
     * 解除合作机构
     *
     * @param brhMerchantCode
     * @return
     */
    @PostMapping("/removeBrhMerchant")
    public void removeBrhMerchant(@RequestParam("brhMerchantCode")String brhMerchantCode) {
        merchantsService.removeBrhMerchant(brhMerchantCode);
    }


    /**
     * 查询通商云 的商户bizUserId 和 用户对应的 bizUserId
     * @param merchantCode
     * @param userId
     */
    @GetMapping("/queryBizUserData")
    public BizMerchantUserData queryMerchantBizUserIdAndPayerBizUserId(@RequestParam("merchantCode") String merchantCode,@RequestParam("userId") Long userId){
        return merchantsService.queryMerchantBizUserIdAndPayerBizUserId(merchantCode,userId);
    }

}

