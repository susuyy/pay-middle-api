package com.ht.feignapi.tencent.service;

import com.alibaba.fastjson.JSON;
import com.ht.feignapi.tencent.entity.WxAccessToken;
import com.ht.feignapi.tencent.entity.WxJsApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: zheng weiguang
 * @Date: 2020/11/4 10:51
 */
@Service
public class WxOfficialPlatformService {

    private static final Logger logger = LoggerFactory.getLogger(WxOfficialPlatformService.class);

    @Autowired
    private RestTemplate restTemplate;

    public String getWxAccessToken(String appId, String appSecret) throws URISyntaxException {
        ResponseEntity<WxAccessToken> responseEntity;
        URI uri = new URI(String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", appId, appSecret));
        try {
            HashMap<String,String> response = restTemplate.getForObject(uri, HashMap.class);
            logger.info("***************tokenResponse**************" + JSON.toJSONString(response));
            logger.info("*********-----------*********为什么不显示log**************");
            if (response.containsKey("errcode")){
                logger.error("*********-----------*********"+JSON.toJSONString(response)+"**************");
//                throw new Exception(response.get("errmsg"));
            }else {
                return response.get("access_token");
            }
        } catch (RestClientException e) {
            logger.error("转换WxAccessToken出错");
            e.printStackTrace();
        }

        return null;
    }

    public WxJsApiResponse getJsApiTicket(String accessToken) throws URISyntaxException {
        ResponseEntity<WxJsApiResponse> jsApiResponse;
        URI uri = new URI(String.format("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi",accessToken));
        try {
            HashMap<String,String> response = restTemplate.getForObject(uri,HashMap.class);
            jsApiResponse = restTemplate.getForEntity(uri, WxJsApiResponse.class);
            logger.info("*****************jsApiResponse***************:"+ JSON.toJSONString(jsApiResponse));
            Assert.notNull(jsApiResponse.getBody(), "获取token失败");
            Assert.isTrue(jsApiResponse.getBody().getErrcode() == 0, jsApiResponse.getBody().getErrmsg());
            return jsApiResponse.getBody();
        } catch (RestClientException e) {
            logger.error("转换WxJsApiResponse出错");
            e.printStackTrace();
        }
        return null;
    }

}
