package com.ht.feignapi.higo.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.feignapi.higo.constant.WxConstant;
import com.ht.feignapi.higo.entity.HiGoReqUserData;
import com.ht.feignapi.higo.entity.WXReturnAuthData;
import com.ht.feignapi.higo.utils.WXUtil;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.clientservice.ShoppingCartClientService;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.prime.entity.VipUser;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.user.entity.WXData;
import com.ht.feignapi.util.OrderCodeFactory;
import com.ht.feignapi.util.OrderEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class HiGoUserService {

    @Autowired
    private MSPrimeClient msPrimeClient;

    public VipUser userInfo(HiGoReqUserData hiGoReqUserData) {
        String code = hiGoReqUserData.getCode();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + WxConstant.appid + "&secret=" + WxConstant.secret + "&js_code=" + code + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WXReturnAuthData> forEntity = restTemplate.getForEntity(url, WXReturnAuthData.class);
        WXReturnAuthData wxRet = forEntity.getBody();
        if (wxRet == null) {
            throw new CheckException(ResultTypeEnum.WX_AUTH_ERROR);
        }
        if (0 != wxRet.getErrcode()) {
            throw new CheckException(wxRet.getErrcode(), WxConstant.WX_CODE_MSG_MAP.get(wxRet.getErrcode()+""));
        }

        // 解密用户数据
        String userPhone = getAuthUserPhone(hiGoReqUserData.getEncryptedData(), wxRet.getSession_key(), hiGoReqUserData.getIv());
        if ("null".equals(userPhone) || org.springframework.util.StringUtils.isEmpty(userPhone)){
            throw new CheckException(ResultTypeEnum.WX_AUTH_ERROR);
        }

        VipUser vipUser = new VipUser();
        vipUser.setPhoneNum(userPhone);
        vipUser.setOpenid(wxRet.getOpenid());
        vipUser.setVipLevel(0);
        vipUser.setPoint(0);
        vipUser.setNickName(hiGoReqUserData.getNickName());
        vipUser.setHeadImg(hiGoReqUserData.getAvatarUrl());
        vipUser.setCreateAt(new Date());
        vipUser.setUpdateAt(new Date());
        return msPrimeClient.hiGoUserInfo(vipUser).getData();
    }

    public String getAuthUserPhone(String encryptedData, String sessionKey, String iv) {
        String result = WXUtil.wxDecrypt(encryptedData, sessionKey, iv);
        JSONObject json = JSONObject.parseObject(result);
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
