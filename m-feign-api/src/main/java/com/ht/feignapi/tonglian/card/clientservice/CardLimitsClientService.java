package com.ht.feignapi.tonglian.card.clientservice;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.entity.CardLimits;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/17 10:47
 */
@FeignClient(name = "${custom.client.user.name}",contextId = "cardLimits")
public interface CardLimitsClientService {

    /**
     * 获取批次号卡号对应的卡规则
     * @param cardCode
     * @param batchCode
     * @return
     */
    @GetMapping("/card-limits/{cardCode}/{batchCode}")
    Result<List<CardLimits>> queryCardGetLimit(@PathVariable("cardCode") String cardCode,@PathVariable("batchCode") String batchCode);

    /**
     * 获取批次号卡号对应类型的卡规则
     * @param cardCode
     * @param batchCode
     * @return
     */
    @GetMapping("/card-limits/withType/{cardCode}/{batchCode}/{type}")
    Result<List<CardLimits>> getLimits(@PathVariable("cardCode") String cardCode,@PathVariable("type") String type,@PathVariable("batchCode") String batchCode);

    /**
     * 获取卡模板的规则
     * @param cardCode
     * @return
     */
    @GetMapping("/card-limits/withoutBatchCode/{cardCode}")
    Result<List<CardLimits>> getLimitsByCardCodeWithOutBatchCode(@PathVariable("cardCode") String cardCode);
}
