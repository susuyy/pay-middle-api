package com.ht.feignapi.shoppingmall.service;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.shoppingmall.client.ShoppingMallClient;
import com.ht.feignapi.shoppingmall.constant.WXConfigConstant;
import com.ht.feignapi.shoppingmall.entity.Merchant;
import com.ht.feignapi.shoppingmall.utils.WXUtil;
import com.ht.feignapi.shoppingmall.vo.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class ShoppingMallUserService {

    @Autowired
    private ShoppingMallClient shoppingMallClient;

    @Autowired
    private AuthClientService authClientService;

    private Logger logger = LoggerFactory.getLogger(ShoppingMallUserService.class);


    public void addMerchant(MerchantUserVo merchantUserVo) {
        String merName = merchantUserVo.getMerName();
        String tel = merchantUserVo.getTel();
        String password = merchantUserVo.getPassword();
        Integer merStoreType = merchantUserVo.getMerStoreType();

        MerchantVo merchantVo = new MerchantVo();
        merchantVo.setMerName(merName);
        merchantVo.setTel(tel);
        merchantVo.setMerStoreType(merStoreType);
        shoppingMallClient.addMerchant(merchantVo);

        UserUsers userUsers = new UserUsers();
        userUsers.setAccount(tel);
        userUsers.setPassword(password);
        userUsers.setTel(tel);
        userUsers.setAppCode("m-zd-mall");
        userUsers.setAppName("扎堆项目");
        userUsers.setNickName(merName);
        userUsers.setRealName(merName);
        authClientService.register(userUsers);
    }

    public void modifyMerchant(MerchantUserVo merchantUserVo) {
        Long id = merchantUserVo.getId();
        String merName = merchantUserVo.getMerName();
        String tel = merchantUserVo.getTel();
        String password = merchantUserVo.getPassword();

        MerchantDetailVo merchantDetailVo = shoppingMallClient.queryById(id).getData();
        String tel1 = merchantDetailVo.getTel();
        UserUsers userUsers = authClientService.queryUserByAccount(tel1, "m-zd-mall").getData();
        userUsers.setAccount(tel);
        userUsers.setTel(tel);
        userUsers.setNickName(merName);
        userUsers.setRealName(merName);
        authClientService.updateUser(userUsers);
        if (password != null && password.length() != 0) {
            userUsers.setPassword(password);
            authClientService.updatePasswordByUserId(userUsers);
        }

        MerchantVo merchantVo = new MerchantVo();
        merchantVo.setId(id);
        merchantVo.setMerName(merName);
        merchantVo.setTel(tel);
        shoppingMallClient.modifyMerchant(merchantVo);

    }


    public JSONObject userInfo( JSONObject postParams) {

        logger.info("===小程序端上送的用户数据为===:"+postParams);
        String code = postParams.getString("code");
        String iv = postParams.getString("iv");
        String encryptedData = postParams.getString("encrypted_data");
        JSONObject rawDataJson = postParams.getJSONObject("raw_data");
        String wxNickName = rawDataJson.getString("nickName");
        String wxAvatarUrl = rawDataJson.getString("avatarUrl");

        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WXConfigConstant.appid + "&secret=" + WXConfigConstant.secret + "&js_code=" + code + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();

        String res = restTemplate.getForObject(url,String.class);

        //微信返回的数据为:{"session_key":"JB6CcXJBxRpilU92UEDkdQ==","openid":"oV6Px4p-ITTj6uWBdjXebtERqH2I"}
        logger.info("微信返回的数据为:"+res);

        WXReturnAuthData wxRet = JSONObject.parseObject(res, WXReturnAuthData.class);
        logger.info("微信数据转化json:"+wxRet);
//        ResponseEntity<WXReturnAuthData> forEntity = restTemplate.getForEntity(url, WXReturnAuthData.class);
//        WXReturnAuthData wxRet = forEntity.getBody();
        if (wxRet == null) {
            throw new CheckException(ResultTypeEnum.WX_AUTH_ERROR);
        }
        if (0 != wxRet.getErrcode()) {
            throw new CheckException(wxRet.getErrcode(), WXConfigConstant.WX_CODE_MSG_MAP.get(wxRet.getErrcode()+""));
        }

        // 解密用户数据
        String userPhone = getAuthUserPhone(encryptedData, wxRet.getSession_key(), iv);
//        if ("null".equals(userPhone) || org.springframework.util.StringUtils.isEmpty(userPhone)){
//            throw new CheckException(ResultTypeEnum.WX_AUTH_ERROR);
//        }

        VipUser vipUser = new VipUser();
        vipUser.setPhoneNum(userPhone);
        vipUser.setOpenid(wxRet.getOpenid());
        vipUser.setVipLevel(0);
        vipUser.setPoint(0);
        vipUser.setNickName(wxNickName);
        vipUser.setHeadImg(wxAvatarUrl);
        vipUser.setCreateAt(new Date());
        vipUser.setUpdateAt(new Date());
        VipUser data = shoppingMallClient.vipUserInfo(vipUser).getData();


        JSONObject resultJson = new JSONObject();
        JSONObject userJson = new JSONObject();
        resultJson.put("token",data.getOpenid());

        userJson.put("id",data.getId());
        userJson.put("avatar",data.getHeadImg());
        userJson.put("openid",data.getOpenid());
        userJson.put("user_nickname",data.getNickName());
        userJson.put("mobile",data.getPhoneNum());

        resultJson.put("user",userJson);


        return  resultJson;
    }

    public String getAuthUserPhone(String encryptedData, String session_key, String iv) {
        String result = WXUtil.wxDecrypt(encryptedData, session_key, iv);

        logger.info("微信用户数据解密:"+result);

        JSONObject json = JSONObject.parseObject(result);

        logger.info("微信用户数据解密转化json:"+json);

        if (json.containsKey("phoneNumber")) {
            String phone = json.getString("phoneNumber");
//            String appid = json.getJSONObject("watermark").getString("appid");
            if (StringUtils.isNoneBlank(phone)) {
                return phone;
            }
        }
        return "";
    }


}
