package com.ht.feignapi.tonglian.card.clientservice;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.entity.CardProfiles;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/17 16:16
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "cardProfiles")
public interface CardProfilesClientService {
    /**
     * 获取卡的profiles
     * @param cardCode
     * @return
     */
    @GetMapping("/card-profile/{cardCode}")
    Result<List<CardProfiles>> queryByCardCode(@PathVariable("cardCode") String cardCode);
}
