package com.ht.feignapi.mall.timer;
//
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.ht.feignapi.mall.clientservice.InventoryClientService;
//import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
//import com.ht.feignapi.mall.clientservice.OrderProductionsClientService;
//import com.ht.feignapi.mall.constant.MallConstant;
//import com.ht.feignapi.mall.constant.MerchantCardConstant;
//import com.ht.feignapi.mall.entity.MallProductions;
//import com.ht.feignapi.mall.entity.OrderProductions;
//import com.ht.feignapi.mall.service.MallProductionService;
//import com.ht.feignapi.result.Result;
//import com.ht.feignapi.tonglian.card.clientservice.CardMapMerchantCardClientService;
//import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
//import com.ht.feignapi.util.DateStrUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.util.Assert;
//import org.springframework.util.CollectionUtils;
//
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * @author: zheng weiguang
// * @Date: 2020/11/18 11:32
// */
//@Configuration
//@EnableScheduling
public class CardScheduleTask {
//    @Autowired
//    private MallAppShowClientService mallAppShowClientService;
//
//    @Autowired
//    private MallProductionService mallProductionService;
//
//    @Autowired
//    private CardMapMerchantCardClientService merchantCardClientService;
//
//    @Autowired
//    private OrderProductionsClientService orderProductionsClientService;
//
//    @Autowired
//    private InventoryClientService inventoryClientService;
//
//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    private final static Logger logger = LoggerFactory.getLogger(CardScheduleTask.class);
//
//    //每天凌晨3点，存储所有的当天的production到redis
////    @Scheduled(cron = "0 0 3 * *")
////    public void saveProductionToRedis(){
////        Result<List<MallProductions>> productionsResult = mallAppShowClientService.getAllProduction();
////        // todo 获取当前时间
////        // todo 获取当前时间后一天
////        // todo 获取endDate在这两个之间的数据
////        // todo 将数据存入redis
////        for (MallProductions production : productionsResult.getData()) {
////            mallProductionService.parseEndDate(production);
////            Calendar cal = Calendar.getInstance();
////            cal.setTime(production.getEndDate());
//////            if (production)
////            redisTemplate.opsForList().leftPush("productions",production);
////        }
//
////    }
//
//    //半个小时更新一次
//    @Scheduled(fixedRate=1800000)
////    @Scheduled(fixedRate=120000)
//    public void configureTasks() {
//        //todo 查找所有的卡券类和非卡券类产品
//        logger.info("执行定时任务，更新卡券排序");
//        logger.info("当前时间：" + DateStrUtil.dateToStr(new Date()));
//        Result<List<MallProductions>> productionsResult = mallAppShowClientService.getAllProduction();
//        Assert.notNull(productionsResult,"获取产品列表信息出错!");
//        Assert.notNull(productionsResult.getData(),"获取产品列表信息出错!");
//        List<MallProductions> productionList = productionsResult.getData();
//        for (MallProductions mallProduction:productionList){
//            Date endDate;
//            Result<Integer> inventoryResult = inventoryClientService.getInventory(mallProduction.getMerchantCode(),mallProduction.getProductionCode());
//            if (inventoryResult==null||inventoryResult.getData()==null) {
//                logger.info(mallProduction.getProductionCode()+ "商品不存在库存*******");
//                continue;
//            }
//
//            if(mallProductionService.isCardProduction(mallProduction.getCategoryCode(),mallProduction.getMerchantCode())){
//                Result<CardMapMerchantCards> cardsResult = merchantCardClientService.mallQueryCodeMerchantCodeType(
//                        mallProduction.getProductionCode(),mallProduction.getMerchantCode(), MerchantCardConstant.MALL_SELL_TYPE);
//                if (cardsResult.getData()==null){
//                    logger.info("非法卡券号:" + mallProduction.getProductionCode());
//                    continue;
//                }
//                endDate = cardsResult.getData().getHaltSaleDate();
//            }else{
//                Result<OrderProductions> orderProductionsResult =  orderProductionsClientService.getByCode(
//                        mallProduction.getProductionCode(),mallProduction.getMerchantCode());
//                if (orderProductionsResult.getData()==null){
//                    logger.info("非法产品号:" + mallProduction.getProductionCode());
//                    continue;
//                }
//                endDate = orderProductionsResult.getData().getValidTo();
//            }
//            if (endDate.before(new Date()) || inventoryResult.getData()<=0){
//                //固定排序到999
//                mallProduction.setSortNum(MallConstant.SOLD_OUT);
//                //更新mallProductin排序
//                mallAppShowClientService.saveMallProduction(mallProduction);
//            }
//        }
//    }
}
