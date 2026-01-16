package com.ht.user.card.controller;

import com.ht.user.card.entity.CardCards;
import com.ht.user.card.service.CardCardsService;
import com.ht.user.common.Result;
import com.ht.user.sysconstant.entity.DicConstant;
import com.ht.user.sysconstant.service.DicConstantService;
import com.ht.user.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 卡定义 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@RestController
@RequestMapping("/cards")
@CrossOrigin(allowCredentials = "true")
public class CardCardsController {

    @Autowired
    private CardCardsService cardCardsService;

    @Autowired
    private DicConstantService dicConstantService;

    /**
     * 根据卡号获取卡信息
     * @param cardCode
     * @return
     */
    @GetMapping("/{cardCode}")
    public CardCards getCardMsg(@PathVariable(value = "cardCode", required = true) String cardCode) {
        return cardCardsService.queryByCardCode(cardCode);
    }

    /**
     * 根据卡券编码,商户号,批次号 查询卡merchant-card信息 ( CardCardsService类 )
     * @param cardCode
     * @param merchantCode
     * @param batchCode
     * @return
     */
    @GetMapping("/batchCard")
    public CardCards getCard(@RequestParam("cardCode") String cardCode, @RequestParam("merchantCode") String merchantCode,
                      @RequestParam("batchCode") String batchCode){
        return cardCardsService.queryByCardCode(cardCode);
    }

    /**
     * 获取卡券类别列表
     * @return
     */
    @GetMapping("/cardTypeList")
    public List<DicConstant> getCardTypeList() {
        List<DicConstant> dicConstantList = dicConstantService.getListByGroupCode("card_type");
        for (DicConstant dicConstant : dicConstantList) {
            if (dicConstant.getKey().equals("credit")){
                dicConstantList.remove(dicConstant);
            }
            if (dicConstant.getKey().equals("number")){
                dicConstantList.remove(dicConstant);
            }
        }
        return dicConstantList;
    }



}

