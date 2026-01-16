package com.ht.feignapi.tonglian.card.clientservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/17 12:17
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "cardTrace")
public interface CardDistributeTraceClientService {
    /**
     * 创建卡券发送记录
     * @param merchantCode
     * @param cardCode
     * @param size
     * @param description
     * @param nickName
     * @param s1
     * @param batchCode
     */
    @PostMapping("/card/distribute-trace")
    void createDistributeTrace(@RequestParam("merchantCode") String merchantCode,@RequestParam("cardCode") String cardCode,@RequestParam("size") int size,
                               @RequestParam("description") String description,@RequestParam("nickName") String nickName,@RequestParam("s1") String s1,
                               @RequestParam("batchCode") String batchCode);
}
