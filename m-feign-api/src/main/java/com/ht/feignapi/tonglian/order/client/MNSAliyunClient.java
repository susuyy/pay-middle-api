package com.ht.feignapi.tonglian.order.client;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.order.entity.MessageData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "${custom.client.aliyun.name}",contextId = "mnsAliyun")
public interface MNSAliyunClient {

    /**
     * 发送消息至消息队列
     * @param messageData
     */
    @PostMapping("/mns/producer/sendMessage")
    Result<Boolean> sendMessage(@RequestBody MessageData messageData);


    @PostMapping("/mns/consumer/consumerMessage")
    Result<String> consumerMessage(@RequestParam("queueName") String queueName);
}
