package com.ht.feignapi.util;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.ht.feignapi.result.CodeSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/26 10:01
 */
public class KtMsg {

    private final static Logger logger = LoggerFactory.getLogger(KtMsg.class);

    public static void main(String[] args) {
        sendMsg("15067089660","111111");
    }

    public static void sendMsg(String phone,String code){
        String serverIp = "app.cloopen.com";
        //请求端口
        String serverPort = "8883";
        //主账号,登陆云通讯网站后,可在控制台首页看到开发者主账号ACCOUNT SID和主账号令牌AUTH TOKEN
        String accountSId = "8aaf0708732220a6017356bf668916c5";
        String accountToken = "8cc096928fea4f90827718b05da7c3df";
        //请使用管理控制台中已创建应用的APPID
        String appId = "8aaf0708732220a6017356bf676c16cc";
        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(serverIp, serverPort);
        sdk.setAccount(accountSId, accountToken);
        sdk.setAppId(appId);
        sdk.setBodyType(BodyType.Type_JSON);
        String to = phone;
        String templateId= "900143";
        String[] datas = {code};
        //HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        HashMap<String, Object> result = sdk.sendTemplateSMS(to,templateId,datas);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            logger.error("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            throw new CodeSendException("发送短信失败！");
        }
    }
}
