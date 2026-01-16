package com.ht.user.card.controller;

import com.ht.user.card.entity.CardProfiles;
import com.ht.user.card.service.CardProfilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: zheng weiguang
 * @Date: 2020/8/19 11:00
 */
@RestController
@RequestMapping(value = "/card-profile",produces = "application/json")
public class CardProfilesController {

    @Autowired
    private CardProfilesService cardProfilesService;

    /**
     * 获取卡的profiles
     * @param cardCode
     * @return
     */
    @GetMapping("/{cardCode}")
    public List<CardProfiles> queryByCardCode(@PathVariable("cardCode") String cardCode){
        List<CardProfiles> cardProfiles = cardProfilesService.queryByCardCode(cardCode);
        return cardProfiles;
    }
}
