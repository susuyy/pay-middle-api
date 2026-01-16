package com.ht.user.ordergoods.controller;


import com.ht.user.ordergoods.entity.CardOrdersGoods;
import com.ht.user.ordergoods.service.CardOrdersGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-09-13
 */
@RestController
@RequestMapping("/card-orders-goods")
public class CardOrdersGoodsController {

    @Autowired
    private CardOrdersGoodsService cardOrdersGoodsService;

    @PostMapping("/saveOrderGoods")
    public void saveOrderGoods(@RequestBody CardOrdersGoods cardOrdersGoods){
        cardOrdersGoodsService.save(cardOrdersGoods);
    }
}

