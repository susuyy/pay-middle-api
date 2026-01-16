package com.ht.feignapi.tongshangyun.client;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tongshangyun.entity.BizMemberData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "${custom.client.pay-prorate.name}",contextId = "tsyMember")
public interface TsyMemberClient {

    /**
     * 通商云创建会员,会员注册
     * @param bizUserId
     * @param memberType
     * @param source
     * @return
     */
    @PostMapping("/tsy/member/bizMemberRegister")
    Result<BizMemberData> bizMemberRegister(@RequestParam("bizUserId") String bizUserId,
                             @RequestParam("memberType")Long memberType,
                             @RequestParam("source")Long source);

    /**
     * 发送短信验证码
     * @param bizUserId
     * @param phone
     * @param verificationCodeType
     * @return
     */
    @GetMapping("/tsy/member/sendVerificationCode")
    Result<Boolean> sendVerificationCode(@RequestParam("bizUserId") String bizUserId,
                                 @RequestParam("phone")String phone,
                                 @RequestParam("verificationCodeType")Long verificationCodeType);

    /**
     * 根据短信验证码绑定手机
     * @param bizUserId
     * @param phone
     * @param verificationCode
     * @return
     */
    @PostMapping("/tsy/member/bindPhone")
    Result<Boolean> bindPhone(@RequestParam("bizUserId")String bizUserId,
                              @RequestParam("phone")String phone,
                              @RequestParam("verificationCode")String verificationCode);
}
