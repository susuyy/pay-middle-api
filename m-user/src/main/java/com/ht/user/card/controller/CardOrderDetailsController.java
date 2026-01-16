package com.ht.user.card.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.user.card.entity.CardOrderDetails;
import com.ht.user.card.service.CardOrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.smartcardio.Card;
import javax.xml.ws.Action;
import java.util.List;

/**
 * <p>
 * 订单明细 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@RestController
@RequestMapping("/orderDetails")
@CrossOrigin(allowCredentials = "true")
public class CardOrderDetailsController {

    @Autowired
    private CardOrderDetailsService cardOrderDetailsService;

    /**
     * 订单明细查询 根据order_detail_id 查询
     *
     * @param detailId
     * @return
     */
    @GetMapping("/details/{detailId}")
    public CardOrderDetails getByDetailId(@PathVariable(value = "detailId", required = true) String detailId) {
        return cardOrderDetailsService.queryByDetailId(detailId);
    }

    @GetMapping("/querySummaryDetails")
    public List<CardOrderDetails> querySummaryDetails(@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime){
        return cardOrderDetailsService.querySummaryDetails(startTime,endTime);
    }

    @GetMapping("/querySummaryDetailsForMerchantCode")
    public List<CardOrderDetails> querySummaryDetailsForMerchantCode(@RequestParam("merchantCode") String merchantCode,@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime){
        return cardOrderDetailsService.querySummaryDetailsForMerchantCode(merchantCode,startTime,endTime);
    }



    @GetMapping("/queryByOrderCode")
    public List<CardOrderDetails> queryByOrderCode(@RequestParam("orderCode") String orderCode){
        return cardOrderDetailsService.queryByOrderCode(orderCode);
    }

    @GetMapping("/allDetailsBuyCard")
    public List<CardOrderDetails> allDetailsBuyCard(){
        QueryWrapper<CardOrderDetails> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("merchant_code","HLMSD");
        queryWrapper.isNotNull("production_code");
        return cardOrderDetailsService.list(queryWrapper);
    }

    @PostMapping("/fixOrderDetails")
    public void fixOrderDetails(@RequestBody List<CardOrderDetails> data){
        for (CardOrderDetails datum : data) {
            cardOrderDetailsService.updateById(datum);
        }
    }
}

