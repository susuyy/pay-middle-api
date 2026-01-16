package com.ht.user.card.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.card.entity.CardMapUserCardsTrace;
import com.ht.user.card.service.CardMapUserCardsService;
import com.ht.user.card.service.CardMapUserCardsTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(allowCredentials = "true")
@RestController
@RequestMapping("/card-use-trace")
public class CardMapUserCardsTraceController {

    @Autowired
    private CardMapUserCardsTraceService cardMapUserCardsTraceService;

    @Autowired
    private CardMapUserCardsService cardMapUserCardsService;

    /**
     * 获取用户发券流水
     * @param merchantCode
     * @param pageNo
     * @param pageSize
     * @param type
     * @return
     */
    @GetMapping("/{merchantCode}")
    public IPage<CardMapUserCardsTrace> listPage(
            @PathVariable("merchantCode") String merchantCode,
            @RequestParam(value = "type",required = false,defaultValue = "") String type,
            @RequestParam(value = "pageNo",required = false,defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize){
        return cardMapUserCardsTraceService.listPage(merchantCode,pageNo,pageSize,type);
    }

    /**
     * 保存trace
     * @param cardMapUserCardsTrace
     */
    @PostMapping
    public void saveOrUpdateTrace(@RequestBody CardMapUserCardsTrace cardMapUserCardsTrace){
        cardMapUserCardsTraceService.save(cardMapUserCardsTrace);
    }
}
