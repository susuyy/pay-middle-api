package com.ht.feignapi.tonglian.merchant.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.appconstant.AppConstant;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetServiceData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.mall.constant.MerchantChargeTypeConstant;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.config.TongLianUserConstant;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.*;
import com.ht.feignapi.tonglian.merchant.service.MerchantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
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
@RequestMapping("/tonglian/merchants")
@CrossOrigin(allowCredentials = "true")
public class MerchantsController {

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AuthClientService authClientService;

    private final static Logger logger = LoggerFactory.getLogger(MerchantsController.class);


    @Autowired
    private InventoryClientService inventoryClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    /**
     * 通过merchantCode获取商户
     *
     * @param merchantCode
     * @return
     */
    @GetMapping("/getOne/{merchantCode}")
    public Merchants getByMerchantCode(@PathVariable("merchantCode") String merchantCode) {
        return merchantsClientService.getMerchantByCode(merchantCode).getData();
    }

    /**
     * 新增主体
     *
     * @param map
     * @return
     */
    @PostMapping("/object")
    public Boolean saveObject(@RequestBody HashMap<String, String> map) {
        Assert.isTrue(checkParam(map), "非法参数");
        UserUsers users = this.registerUser(map.get("userName"), map.get("password"));

        Merchants merchants = new Merchants();
        merchants.setMerchantCode(map.get("merchantCode"));
        merchants.setUserId(users.getId());
        merchants.setMerchantName(map.get("merchantName"));
        merchants.setType(map.get("merchantType"));
        merchants.setBusinessSubjects(map.get("businessSubject"));
        merchants.setChargeType(map.get("charge_type"));

        inventoryClientService.createDefaultMerchantWarehouse(map.get("merchantCode"));
        merchantsClientService.save(merchants);
        this.saveAllMerchantConfig(map);
        return true;
    }

    /**
     * 新增主体
     *
     * @param map
     * @return
     */
    @PutMapping("/object")
    public Boolean registerUserForMerchant(@RequestBody HashMap<String, String> map) {
        Assert.isTrue(checkParam(map), "非法参数");
        UserUsers users = this.registerUser(map.get("userName"), map.get("password"));

        Result<Merchants> merchantsResult = merchantsClientService.getMerchantByCode(map.get("merchantCode"));
        merchantsResult.getData().setUserId(users.getId());
        merchantsClientService.save(merchantsResult.getData());
        Merchants merchants = new Merchants();
        merchants.setMerchantCode(map.get("merchantCode"));
        merchants.setUserId(users.getId());
        merchants.setMerchantName(map.get("merchantName"));
        merchants.setType(map.get("merchantType"));
        merchants.setBusinessSubjects(map.get("businessSubject"));
        merchants.setChargeType(map.get("charge_type"));

        inventoryClientService.createDefaultMerchantWarehouse(map.get("merchantCode"));
        merchantsClientService.save(merchants);
        this.saveAllMerchantConfig(map);
        return true;
    }

    /**
     * 新增商户
     *
     * @param map
     * @return
     */
    @PostMapping("/merchant")
    public Boolean saveMerchant(@RequestBody HashMap<String, String> map) {
        Assert.isTrue(checkParam(map), "非法参数");
        UserUsers users = this.registerUser(map.get("userName"), map.get("password"));

        Merchants merchants = new Merchants();
        merchants.setMerchantCode(map.get("merchantCode"));
        merchants.setUserId(users.getId());
        merchants.setMerchantName(map.get("merchantName"));
        merchants.setType(map.get("merchantType"));
        merchants.setBusinessSubjects(map.get("businessSubject"));
        merchants.setChargeType(map.get("charge_type"));
        merchantsClientService.save(merchants);
        inventoryClientService.createDefaultMerchantWarehouse(map.get("merchantCode"));
        merchantService.saveBaseMerchantConfig(map);
        return true;
    }


    /**
     * 给主体/商户分配用户
     *
     * @param user
     * @param merchantCode
     * @return
     */
    @PostMapping("/merchantAdmin/{merchantCode}")
    public boolean saveMerchantAdminUser(@PathVariable("merchantCode") String merchantCode, @RequestBody UserUsers user) {
        Result<RetServiceData> result = authClientService.register(user);
        Assert.isTrue(result.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()) && result.getData() != null, "注册用户出错");
        try {
            merchantService.saveMerchantUser(merchantCode, result.getData().getData().getId());
        } catch (Exception e) {
            logger.error("*****MerchantUserCreateError*******" + e.getMessage());
        }
        return true;
    }

    /**
     * 获取商户对应的用户管理员列表
     * @param merchantCode
     * @return
     */
    @GetMapping("/merchantAdmin/{merchantCode}")
    public Result<List<MrcMapMerchantUser>> getMerchantAdminUsers(@PathVariable("merchantCode") String merchantCode){
        Result<List<MrcMapMerchantUser>> merchantUserResult = merchantsClientService.getMerchantAdminUserList(merchantCode);
        Assert.isTrue(merchantUserResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()) && !CollectionUtils.isEmpty(merchantUserResult.getData()), "注册用户出错");
        merchantUserResult.getData().forEach(e->{
            Result<UserUsers> userUsersResult = authClientService.getUserByIdTL(String.valueOf(e.getUserId()));
            Assert.isTrue(userUsersResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()) && userUsersResult.getData()!=null, "获取商户管理员账号出错");
            e.setUser(userUsersResult.getData());
        });
        return merchantUserResult;
    }

    /**
     * 不能修改密码
     * @param userUsers
     * @return
     */
    @PutMapping("/merchantAdmin")
    public void updateMerchantUser(@RequestBody UserUsers userUsers){
        authClientService.updateUserTL(userUsers);
    }

    /**
     * 重置管理员密码为123456
     * @param userId
     */
    @PutMapping("/resetPassword/{userId}")
    public void resetPasswordForUser(@PathVariable("userId") Long userId){
        Result<UserUsers> userUsersResult = authClientService.getUserByIdTL(String.valueOf(userId));
        Assert.isTrue(ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(userUsersResult.getCode())&&userUsersResult.getData()!=null,"获取用户出错");
        userUsersResult.getData().setPassword(TongLianUserConstant.INIT_PASSWORD);
        authClientService.updatePasswordByUserId(userUsersResult.getData());
    }

    /**
     * 修改管理员状态
     * @param merchantCode
     * @param state
     * @param id
     */
    @PutMapping("/state/{id}/{merchantCode}")
    public void changeUserState(
            @PathVariable("id") Long id,
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam("state") String state){
        Result<MrcMapMerchantUser> mrcMapMerchantUserResult = merchantsClientService.getMerchantAdminUser(merchantCode,id);
        Assert.isTrue(ResultTypeEnum.SERVICE_SUCCESS.getCode().equals(
                mrcMapMerchantUserResult.getCode())&&mrcMapMerchantUserResult.getData()!=null,
                "获取商户管理员出错");
        mrcMapMerchantUserResult.getData().setState(state);
        merchantsClientService.saveMerchantUser(mrcMapMerchantUserResult.getData());
    }

    private void saveAllMerchantConfig(HashMap<String, String> map) {
        merchantService.saveBaseMerchantConfig(map);
        merchantService.saveMerchantConfig("SHOW-IMG-ONE", "http://allinpay.hualta.com/public/member/images/banner.jpg", map.get("merchantCode"), "slide_show");
        merchantService.saveMerchantConfig("SHOW-IMG-TWO", "http://agent.sy.hualta.com/static/home/images/banner2.jpg", map.get("merchantCode"), "slide_show");
    }

    private boolean checkParam(HashMap<String, String> map) {
        return map.containsKey("APPID") && map.containsKey("MD5KEY") && map.containsKey("MCHID")
                && map.containsKey("WX_APPID") && map.containsKey("WX_APPSECRET")
                && map.containsKey("merchantCode") && map.containsKey("userName")
                && map.containsKey("RSA_PUBLIC") && map.containsKey("RSA_PRIVATE")
                && map.containsKey("password") && map.containsKey("merchantName")
                && map.containsKey("businessSubject") && map.containsKey("merchantType");
    }

    private UserUsers registerUser(String username, String password) {
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

    /**
     * 合作机构入驻
     * @param registerBrhMerchantData
     * @return
     */
    @PostMapping("/registerBrhMerchant")
    public BrhMerchantData registerBrhMerchant(@RequestBody RegisterBrhMerchantData registerBrhMerchantData){
        UserUsers userUsers = new UserUsers();
        userUsers.setAppName(AppConstant.MS_APP_NAME);
        userUsers.setAppCode(AppConstant.MS_APP_CODE+"-brh");
        userUsers.setAccount(registerBrhMerchantData.getAccount());
        userUsers.setPassword(registerBrhMerchantData.getPassword());
        userUsers.setTel(registerBrhMerchantData.getMerchantContact());
        userUsers.setNickName(registerBrhMerchantData.getMerchantName());
        userUsers.setRealName(registerBrhMerchantData.getMerchantName());
        RetServiceData retServiceData = authClientService.register(userUsers).getData();

        if (!retServiceData.getFlag()){
            throw new CheckException(ResultTypeEnum.REGISTER_BRH_ERROR.getCode(),retServiceData.getMessage());
        }

        Merchants merchants=new Merchants();
        merchants.setMerchantName(registerBrhMerchantData.getMerchantName());
        merchants.setUserId(retServiceData.getData().getId());
        merchants.setLocation(registerBrhMerchantData.getLocation());
        merchants.setType("MERCHANT");
        merchants.setBusinessSubjects(registerBrhMerchantData.getBusinessSubjects());
        merchants.setCreateAt(new Date());
        merchants.setUpdateAt(new Date());
        merchants.setChargeType(MerchantChargeTypeConstant.CHARGE_BY_ENTITY);
        merchants.setMerchantContact(registerBrhMerchantData.getMerchantContact());

        SaveAndMapUser saveAndMapUser = new SaveAndMapUser();
        saveAndMapUser.setMerchants(merchants);
        saveAndMapUser.setUserUsers(retServiceData.getData());
        String brhMerchantCode = merchantsClientService.saveAndMapUser(saveAndMapUser).getData();

        BrhMerchantData brhMerchantData = new BrhMerchantData();
        brhMerchantData.setAccount(registerBrhMerchantData.getAccount());
        brhMerchantData.setPassword(registerBrhMerchantData.getPassword());
        brhMerchantData.setBrhMerchantCode(brhMerchantCode);
        return brhMerchantData;
    }


}

