package com.ht.feignapi.tonglian.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;


public class AliMsgSendUtil {
    private static final String sign = "123123";
    private static final String templateCode = "123123123";

    /**
     * 不带参数的发送
     *
     * @param phone
     * @param tempCode
     * @return
     * @throws ClientException
     */
    public static String sendNotifyMsg(String phone,String tempCode) throws ClientException {
//		Assert.state(true, "The session has already been invalidated");
//		Assert.hasText(phone,"手机号不能为空");

        DefaultProfile profile = DefaultProfile.getProfile("default",
                "123123123123", "123123123");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = createCommonRequest(phone, tempCode);
        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }

    /**
     * 不带参数的发送
     *
     * @param phone
     * @param code
     * @return
     * @throws ClientException
     */
    public static String sendMsg(String phone, String code) throws ClientException {
//		Assert.state(true, "The session has already been invalidated");
//		Assert.hasText(phone,"手机号不能为空");

        DefaultProfile profile = DefaultProfile.getProfile("default",
                "123123", "123123123123");
        IAcsClient client = new DefaultAcsClient(profile);
        CommonRequest request = createCodeRequest(phone, code);
        CommonResponse response = client.getCommonResponse(request);
        return response.getData();
    }


    /**
     * 不带参数
     *
     * @param phoneNum
     * @return
     * @Description 装配CommonRequest的必要参数：
     * TemplateCode，PhoneNumber，SignName，Code
     */
    private static CommonRequest createCodeRequest(String phoneNum, String code) {
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", sign);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":" + code + "}");
//        request.putQueryParameter("TemplateParam", "{'"+paramName+"': '"+data+"' }");
        return request;
    }

    /**
     * 不带参数
     *
     * @param phoneNum
     * @param tempCode
     * @return
     * @Description 装配CommonRequest的必要参数：
     * TemplateCode，PhoneNumber，SignName，Code
     */
    private static CommonRequest createCommonRequest(String phoneNum, String tempCode) {
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phoneNum);
        request.putQueryParameter("SignName", sign);
        request.putQueryParameter("TemplateCode", tempCode);
        return request;
    }

}
