package com.ht.feignapi.tonglian.card.clientservice;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCardsTrace;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/17 17:43
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "cardUserTrace")
public interface CardMapUserCardsTraceClientService {

    /**
     * 获取用户发券流水
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    @GetMapping("/card-use-trace/{merchantCode}")
    Result<Page<CardMapUserCardsTrace>> listPage(@PathVariable("merchantCode") String merchantCode,
                                                 @RequestParam(value = "type",required = false,defaultValue = "") String type,
                                                 @RequestParam(value = "pageNo",required = false,defaultValue = "0") Integer pageNo,
                                                 @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize);

    @PostMapping("/card-use-trace")
    void saveOrUpdateTrace(@RequestBody CardMapUserCardsTrace cardMapUserCardsTrace);
}
