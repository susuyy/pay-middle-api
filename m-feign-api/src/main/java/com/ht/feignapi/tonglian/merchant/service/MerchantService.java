package com.ht.feignapi.tonglian.merchant.service;


import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetServiceData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.constant.MerchantConstant;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfig;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantUser;
import com.ht.feignapi.tonglian.order.client.PayClientService;
import com.ht.feignapi.tonglian.order.entity.PayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/13 11:37
 */
@Service
public class MerchantService {

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private PayClientService payClientService;

    @Autowired
    private AuthClientService userService;

    public UserUsers getMerchantAdminUser(String merchantCode) {
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        Assert.notNull(merchants,"非法商户号");
        Result result=userService.getUserByIdTL(merchants.getUserId().toString());
        String string = JSONObject.toJSONString(result.getData());
        UserUsers userUsers = JSONObject.parseObject(string, UserUsers.class);
        return userUsers;
    }

    public Boolean checkMerchantPassword(Merchants merchant, String password) {
        UserUsers objectUser;
        System.out.println(userService.getUserByIdTL(merchant.getUserId().toString()));
        objectUser =  userService.getUserByIdTL(merchant.getUserId().toString()).getData();
        if (!"OBJECT".equals(merchant.getType())){
        Merchants objectMerchant = merchantsClientService.getMerchantByCode(merchant.getBusinessSubjects()).getData();
            Assert.notNull(objectMerchant,"获取商户主体出错");
        }
        return objectUser.getPassword().equals(password);
    }

    public void saveMerchantConfig(String key, String value, String merchantCode, String groupCode){
        MerchantsConfig merchantsConfig = new MerchantsConfig();
        merchantsConfig.setKey(key);
        merchantsConfig.setValue(value);
        merchantsConfig.setMerchantCode(merchantCode);
        merchantsConfig.setGroupCode(groupCode);
        merchantsConfigClientService.save(merchantsConfig);
    }

    public void saveBaseMerchantShopConfig(MerchantsConfigVO config,String merchantCode,String adminMerchantCode) {
        this.saveMerchantConfig("APPID",config.getAppId(),merchantCode, "pay_data");
        this.saveMerchantConfig("MD5KEY",config.getMd5key(),merchantCode, "pay_data");
        this.saveMerchantConfig("MCHID",config.getMchId(),merchantCode, "pay_data");
        this.saveMerchantConfig("WX_APPID",config.getWxAppid(),merchantCode, "get_openid_data");
        this.saveMerchantConfig("WX_APPSECRET",config.getWxAppsecret(),merchantCode, "get_openid_data");
        if (config.getChargeType().equals("charge_by_store")){
            this.saveMerchantConfig("QR_CODE_URL","https://gateway.hualta.com/m-feign-api/tonglian/wxRedirect/getWXCode/"+merchantCode,merchantCode,"qr_code_url");
        }else {
            this.saveMerchantConfig("QR_CODE_URL","https://gateway.hualta.com/m-feign-api/tonglian/wxRedirect/getWXCode/"+adminMerchantCode,merchantCode,"qr_code_url");
        }
        this.saveMerchantConfig("POS_PRINT_CONFIG","1",merchantCode,"print_config");
        this.saveMerchantConfig("PER_PAYMENT_LIMIT","100000",merchantCode,"payment_limit");
        this.saveMerchantConfig("DAILY_PAYMENT_LIMIT","500000",merchantCode,"payment_limit");
        this.saveMerchantConfig("PAY_TYPE",config.getPayType(),merchantCode,"pay_data");
        savePayConfig("MD5KEY",config.getMd5key(),merchantCode,"pay_data");
        savePayConfig("RSA_PUBLIC",config.getRsaPublic(),merchantCode,"pay_data");
        savePayConfig("RSA_PRIVATE",config.getRsaPrivate(),merchantCode,"pay_data");
        savePayConfig("C",config.getC(),merchantCode,"pay_data");
        savePayConfig("PAY_TYPE",config.getPayType(),merchantCode,"pay_data");
    }

    public void saveBaseMerchantConfig(HashMap<String, String> map) {
        this.saveMerchantConfig("VIP-GOLDEN",map.get("VIP-GOLDEN"),map.get("merchantCode"), "pay_data");
        this.saveMerchantConfig("VIP-PLATINUM",map.get("VIP-PLATINUM"),map.get("merchantCode"), "pay_data");
        this.saveMerchantConfig("VIP-CROWN",map.get("VIP-CROWN"),map.get("merchantCode"), "pay_data");
        this.saveMerchantConfig("APPID",map.get("APPID"),map.get("merchantCode"), "pay_data");
        this.saveMerchantConfig("MD5KEY",map.get("MD5KEY"),map.get("merchantCode"), "pay_data");
        this.saveMerchantConfig("MCHID",map.get("MCHID"),map.get("merchantCode"), "pay_data");
        this.saveMerchantConfig("WX_APPID",map.get("WX_APPID"),map.get("merchantCode"), "get_openid_data");
        this.saveMerchantConfig("WX_APPSECRET",map.get("WX_APPSECRET"),map.get("merchantCode"), "get_openid_data");
        if (map.get("charge_type").equals("charge_by_store")){
            this.saveMerchantConfig("QR_CODE_URL","https://gateway.hualta.com/m-feign-api/tonglian/wxRedirect/getWXCode/"+map.get("merchantCode"),map.get("merchantCode"),"qr_code_url");
        }else {
            this.saveMerchantConfig("QR_CODE_URL","https://gateway.hualta.com/m-feign-api/tonglian/wxRedirect/getWXCode/"+map.get("merchantCode"),map.get("businessSubject"),"qr_code_url");
        }
        this.saveMerchantConfig("POS_PRINT_CONFIG","1",map.get("merchantCode"),"print_config");
        this.saveMerchantConfig("PER_PAYMENT_LIMIT","100000",map.get("merchantCode"),"payment_limit");
        this.saveMerchantConfig("DAILY_PAYMENT_LIMIT","500000",map.get("merchantCode"),"payment_limit");
        this.saveMerchantConfig("PAY_TYPE",map.get("PAY_TYPE"),map.get("merchantCode"),"pay_data");
        savePayConfig("MD5KEY",map.get("MD5KEY"),map.get("merchantCode"),"pay_data");
        savePayConfig("RSA_PUBLIC",map.get("RSA_PUBLIC"),map.get("merchantCode"),"pay_data");
        savePayConfig("RSA_PRIVATE",map.get("RSA_PRIVATE"),map.get("merchantCode"),"pay_data");
        savePayConfig("C",map.get("C"),map.get("merchantCode"),"pay_data");
        savePayConfig("PAY_TYPE",map.get("PAY_TYPE"),map.get("merchantCode"),"pay_data");
    }

    private void savePayConfig(String key,String value,String merchantCode,String groupCoude){
        PayConfig payConfigRsaPublic = new PayConfig();
        payConfigRsaPublic.setKey(key);
        payConfigRsaPublic.setValue(value);
        payConfigRsaPublic.setGroupCode(groupCoude);
        payConfigRsaPublic.setMerchantCode(merchantCode);
        payClientService.savePayConfig(payConfigRsaPublic);
    }

     public void saveMerchantUser(String merchantCode,Long userId) {
        MrcMapMerchantUser mapMerchantUser = new MrcMapMerchantUser();
        mapMerchantUser.setUserId(userId);
        mapMerchantUser.setState(MerchantConstant.MERCHANT_ADMIN_ENABLE);
        mapMerchantUser.setMerchantCode(merchantCode);
        merchantsClientService.saveMerchantUser(mapMerchantUser);
    }
}
