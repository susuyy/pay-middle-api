package com.ht.feignapi.tencent.service;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tencent.entity.WxJsApiResponse;
import com.ht.feignapi.tencent.entity.WxOfficialJSJDKConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/4 11:30
 */
@Service
public class TonglianWxService {
    @Autowired
    private WxOfficialPlatformService wxOfficialPlatformService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TonglianWxService.class);

    public WxOfficialJSJDKConfig getJsJDKConfig(String merchantCode, String url) throws NoSuchAlgorithmException, URISyntaxException {

        Result<String> appIdResult =  merchantsConfigClientService.getConfigByKey(merchantCode,"WX_APPID");
        Assert.notNull(appIdResult,"获取商户WX_APPID出错");
        Result<String> appSecretResult =  merchantsConfigClientService.getConfigByKey(merchantCode,"WX_APPSECRET");
        Assert.notNull(appIdResult,"获取商户WX_APPSECRET出错");
        String tokenAccess = this.getWxAccessToken(merchantCode,appIdResult.getData(),appSecretResult.getData());
        String ticket = this.getJsApiTicket(tokenAccess,merchantCode);

        WxOfficialJSJDKConfig config = new WxOfficialJSJDKConfig();
        String nonceStr = "js_" + IdWorker.getIdStr();
        String timeStamp = String.valueOf(Calendar.getInstance().getTimeInMillis());
        config.setTimestamp(timeStamp);
        config.setAppId(appIdResult.getData());
        config.setNonceStr(nonceStr);
        config.setJsApiList(new String[]{"scanQRCode"});
        config.setSignature(getWxSignature(ticket,nonceStr,timeStamp,url));
        return config;
    }

    public String getWxSignature(String ticket,String nonceStr,String timestamp,String url) throws NoSuchAlgorithmException {
        String signature = String.format("jsapi_ticket=%s&noncestr=%s&timestamp=%s&url=%s",ticket,nonceStr,timestamp,url);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA"); // 此处的sha代表sha1
        // 调用digest方法，进行加密操作
        byte[] cipherBytes = messageDigest.digest(signature.getBytes());
        return Hex.encodeHexString(cipherBytes);
    }

    public String getWxAccessToken(String merchantCode,String appId,String appSecret) throws URISyntaxException {
        String key = merchantCode+"_tokenAccess";
        logger.info("********************进入获取accessToken方法*********************");
        if (redisTemplate.hasKey(key)){
            logger.info("********************从redis获取到token*********************");
            logger.info(redisTemplate.opsForValue().get(key));
            return redisTemplate.opsForValue().get(key);
        }else {
            logger.info("********************从wx获取token*********************");
            String token = wxOfficialPlatformService.getWxAccessToken(appId,appSecret);
            redisTemplate.opsForValue().set(key,token,90, TimeUnit.MINUTES);
            logger.info(token);
            return token;
        }
    }

    public String getJsApiTicket(String accessToken,String merchantCode) throws URISyntaxException {
        String key = merchantCode+"_jsApiTicket";

        if (redisTemplate.hasKey(key)){
            logger.info(redisTemplate.opsForValue().get(key));
            return redisTemplate.opsForValue().get(key);
        }else {
            WxJsApiResponse response = wxOfficialPlatformService.getJsApiTicket(accessToken);
            redisTemplate.opsForValue().set(key,response.getTicket(),90, TimeUnit.MINUTES);
            logger.info(response.getTicket());
            return response.getTicket();
        }
    }
}
