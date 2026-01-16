package com.ht.feignapi.aliyun.client;


import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.aliyun.entity.AliMsgResponseEntity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient("${custom.client.aliyun.name}")
public interface AliyunClientService {

    /**
     * 考试通知
     * @param phone
     * @param msg
     * @return
     */
    @PostMapping("/alimsg/sendExamMsg")
    AliMsgResponseEntity sendExamMsg(@RequestParam("phone") String phone, @RequestBody JSONObject msg);

    /**
     * 报名审核通知
     * @param phone
     * @param msg
     * @return
     */
    @PostMapping("/alimsg/sendRegExamMsg")
    AliMsgResponseEntity sendRegExamMsg(@RequestParam("phone") String phone, @RequestBody JSONObject msg);


}
