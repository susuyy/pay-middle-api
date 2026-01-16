package com.ht.user.card.controller;


import com.ht.user.card.entity.CardLimits;
import com.ht.user.card.service.CardLimitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-06-23
 */
@RestController
@RequestMapping("/card-limits")
public class CardLimitsController {

    @Autowired
    private CardLimitsService cardLimitsService;

    /**
     * 获取批次号卡号对应的卡规则
     * @param cardCode
     * @param batchCode
     * @return
     */
    @GetMapping("/{cardCode}/{batchCode}")
    public List<CardLimits> queryCardGetLimit(
            @PathVariable("cardCode") String cardCode,
            @PathVariable("batchCode") String batchCode){
        return cardLimitsService.queryCardGetLimit(cardCode,batchCode);
    }

    /**
     * 获取批次号卡号对应类型的卡规则
     * @param type
     * @param cardCode
     * @param batchCode
     * @return
     */
    @GetMapping("/withType/{cardCode}/{batchCode}/{type}")
    public List<CardLimits> getLimits(
            @PathVariable("cardCode") String cardCode,
            @PathVariable("type") String type,
            @PathVariable("batchCode") String batchCode){
        return cardLimitsService.getLimits(cardCode,type,batchCode);
    }

    /**
     * 获取卡模板的规则
     * @param cardCode
     * @return
     */
    @GetMapping("/withoutBatchCode/{cardCode}")
    public List<CardLimits> getLimitsByCardCodeWithOutBatchCode(@PathVariable("cardCode") String cardCode){
        return cardLimitsService.getLimitsByCardCodeWithOutBatchCode(cardCode);
    }
}

