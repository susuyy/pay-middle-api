package com.ht.feignapi.pay.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ht.feignapi.tonglian.user.entity.WXData;
import com.ht.feignapi.tonglian.utils.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class WeChatService {


    Logger logger = LoggerFactory.getLogger(WeChatService.class);

    /**
     * 获取用户openid
     *
     * @param code
     * @param wxAppId
     * @param wxAppSecret
     * @return
     */
    public WXData getOpenid(String code, String wxAppId, String wxAppSecret) {
        String content = "";
        String openId = "";
        String unionId = "";
        String accessToken = "";
        String errmsg = "";
        String errcode = "";
        String refreshToken = "";
        //封装获取openId的微信API
        StringBuffer url = new StringBuffer();
        url.append("https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
                .append(wxAppId)
                .append("&secret=")
                .append(wxAppSecret)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HttpClient httpClient = new HttpClient(url.toString());
            httpClient.get();
            content = httpClient.getContent();
            System.out.println("openid信息"+openId);
            Map map = objectMapper.readValue(content, Map.class);
            System.out.println("openid信息"+map);
            openId = String.valueOf(map.get("openid"));
            accessToken = String.valueOf(map.get("access_token"));
            refreshToken = String.valueOf(map.get("refresh_token"));
            unionId = String.valueOf(map.get("unionid"));
            errcode = (String) map.get("errcode");
            errmsg = (String) map.get("errmsg");
            logger.info("获取的openID：" + openId);
        } catch (JsonParseException e) {
            logger.error("json解析失败：", e);
        } catch (JsonMappingException e) {
            logger.error("map转换成json失败：", e);
        } catch (Exception e) {
            logger.error("http获取openId请求失败：", e);
        }
        WXData wxData = new WXData();
        wxData.setOpenid(openId);
        wxData.setAccessToken(accessToken);
        wxData.setRefreshToken(refreshToken);
        wxData.setErrCode(errcode);
        wxData.setErrMsg(errmsg);
        return wxData;
    }
}
