package com.ht.feignapi.tongshangyun.service;

import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.clientservice.MapMerchantPrimesClientService;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tongshangyun.client.TsyMemberClient;
import com.ht.feignapi.tongshangyun.constant.MemberConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private TsyMemberClient tsyMemberClient;

    @Autowired
    private MapMerchantPrimesClientService mapMerchantPrimesClientService;

    /**
     * 通商云发送绑定手机号验证码
     * @param phoneNum
     * @param userId
     * @param merchantCode
     */
    public void sendVerificationCode(String phoneNum,String userId,String merchantCode) {
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByUserIdAndMerchantCode(Long.parseLong(userId), merchantCode).getData();
        Boolean flag = tsyMemberClient.sendVerificationCode(mrcMapMerchantPrimes.getBizUserId(), phoneNum, MemberConstant.CODE_TYPE_BIND).getData();
        if (!flag){
            throw new CheckException(ResultTypeEnum.SEND_CODE_ERROR);
        }
    }

    /**
     * 通商云会员绑定手机
     * @param userId
     * @param phoneNum
     * @param merchantCode
     * @param authCode
     */
    public void bindPhoneNum(Long userId, String phoneNum, String merchantCode,String authCode) {
        MrcMapMerchantPrimes mrcMapMerchantPrimes = mapMerchantPrimesClientService.queryByUserIdAndMerchantCode(userId, merchantCode).getData();
        Boolean flag = tsyMemberClient.bindPhone(mrcMapMerchantPrimes.getBizUserId(), phoneNum, authCode).getData();
        if (!flag){
            throw new CheckException(ResultTypeEnum.AUTH_CODE_ERROR);
        }
    }
}
