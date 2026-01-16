//package com.ht.feignapi.prime.task;
//
//import com.ht.feignapi.mall.constant.MallConstant;
//import com.ht.feignapi.mall.constant.MerchantCardConstant;
//import com.ht.feignapi.mall.entity.MallProductions;
//import com.ht.feignapi.mall.entity.OrderProductions;
//import com.ht.feignapi.prime.client.MSPrimeClient;
//import com.ht.feignapi.prime.controller.PrimePayController;
//import com.ht.feignapi.prime.entity.CardElectronicSell;
//import com.ht.feignapi.prime.utils.SXLHttpClient;
//import com.ht.feignapi.prime.utils.SXLOrderRequest;
//import com.ht.feignapi.result.Result;
//import com.ht.feignapi.tonglian.card.clientservice.CardOrderClientService;
//import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
//import com.ht.feignapi.tonglian.order.client.OrderClientService;
//import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
//import com.ht.feignapi.util.DateStrUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.util.Assert;
//
//import java.time.Instant;
//import java.time.temporal.ChronoUnit;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//@Configuration
//@EnableScheduling
//public class RollBackVirtualInventoryTask {
//
//    private Logger logger = LoggerFactory.getLogger(RollBackVirtualInventoryTask.class);
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Autowired
//    private MSPrimeClient msPrimeClient;
//
//    @Autowired
//    private OrderClientService orderClientService;
//
//    //6分钟更新一次 释放虚拟库存
//    @Scheduled(fixedRate=360000)
//    public void rollBackVirtualInventory() {
//        Map<String,List<CardElectronicSell>> unpaidOrderInventoryMap = (Map<String,List<CardElectronicSell>>) redisTemplate.opsForHash().entries("orderInventory");
//        Set<String> orderCodeSet = unpaidOrderInventoryMap.keySet();
//        //当前时间
//        Date nowDate = new Date();
//        for (String orderCodeKey : orderCodeSet) {
//            //检验订单创建时间
//            CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(orderCodeKey).getData();
//            Date createAt = cardOrdersVO.getCreateAt();
//            long minutes = ChronoUnit.MINUTES.between(Instant.ofEpochMilli(createAt.getTime()), Instant.ofEpochMilli(nowDate.getTime()));
//            if (minutes>=6) {
//                //清空未支付订单列表
//                redisTemplate.opsForHash().delete("orderInventory", orderCodeKey);
//                //释放未支付订单锁定的库存
//                List<CardElectronicSell> cardElectronicSells = unpaidOrderInventoryMap.get(orderCodeKey);
//                for (CardElectronicSell cardElectronicSell : cardElectronicSells) {
//                    Integer oriRedisInventory = (Integer) redisTemplate.opsForValue().get(cardElectronicSell.getBatchCode());
//                    if (oriRedisInventory != null) {
//                        redisTemplate.opsForValue().set(cardElectronicSell.getBatchCode(), oriRedisInventory + cardElectronicSell.getQuantity());
//                    }
//                }
//            }
//        }
//
////        List<CardElectronicSell> cardElectronicSellList = msPrimeClient.noPageSellAll().getData();
////        for (CardElectronicSell electronicSell : cardElectronicSellList) {
////            redisTemplate.opsForValue().set(electronicSell.getBatchCode(), electronicSell.getQuantity());
////        }
//    }
//
//    //4个小时同步一次 真实库存
//    @Scheduled(fixedRate=14400000)
//    public void rollBackInventory() {
//        Map<String,List<CardElectronicSell>> unpaidOrderInventoryMap = (Map<String,List<CardElectronicSell>>) redisTemplate.opsForHash().entries("orderInventory");
//        Set<String> orderCodeSet = unpaidOrderInventoryMap.keySet();
//        for (String orderCodeKey : orderCodeSet) {
//            //清空未支付订单列表
//            redisTemplate.opsForHash().delete("orderInventory",orderCodeKey);
//        }
//        List<CardElectronicSell> cardElectronicSellList = msPrimeClient.noPageSellAll().getData();
//        for (CardElectronicSell electronicSell : cardElectronicSellList) {
//            redisTemplate.opsForValue().set(electronicSell.getBatchCode(), electronicSell.getQuantity());
//        }
//    }
//
//}
