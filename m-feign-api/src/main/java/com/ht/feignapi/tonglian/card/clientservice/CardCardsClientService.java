package com.ht.feignapi.tonglian.card.clientservice;


import com.ht.feignapi.result.Result;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.sysconstant.entity.DicConstant;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "${custom.client.user.name}",contextId = "cardCards")
public interface CardCardsClientService {

    /**
     * 根据卡券编码,商户号,批次号 查询卡merchant-card信息 ( CardCardsService类 )
     * @param cardCode
     * @param merchantCode
     * @param batchCode
     * @return
     */
    @GetMapping("/cards/batchCard")
    Result<CardCards> getCard(@RequestParam("cardCode") String cardCode,
                              @RequestParam("merchantCode") String merchantCode,
                              @RequestParam("batchCode") String batchCode);

    /**
     * 根据卡号，获取card信息
     * @param cardCode
     * @return
     */
    @GetMapping("/cards/{cardCode}")
    Result<CardCards> getCardByCardCode(@PathVariable("cardCode") String cardCode);

    /**
     * 获取卡券类型列表
     */
    @GetMapping("/cards/cardTypeList")
    Result<List<DicConstant>> getListByGroupCode();


}
