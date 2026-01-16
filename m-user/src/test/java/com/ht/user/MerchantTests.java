//package com.ht.user;
//
//import com.ht.user.card.service.CardLimitsService;
//import com.ht.user.mrc.service.MrcMapMerchantPrimesService;
//import com.ht.user.utils.TimeUtil;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///**
// * @author: zheng weiguang
// * @Date: 2020/6/19 17:02
// */
//@SpringBootTest
//public class MerchantTests {
//
//    @Autowired
//    private MrcMapMerchantPrimesService mrcMapMerchantPrimesService;
//
//    @Autowired
//    private CardLimitsService cardLimitsService;
//
//    @Test
//    public void testNone(){
//
//        String a = "08:00";
//        System.out.println(TimeUtil.format(TimeUtil.parseDate(a,"HH:mm"),"HH:mm"));
//    }
//
//    @Test
//    public void testCheckLimit(){
////        cardLimitsService.checkCardGetLimit();
//    }
//
//
//}
