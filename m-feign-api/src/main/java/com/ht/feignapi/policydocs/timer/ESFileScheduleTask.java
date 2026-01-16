package com.ht.feignapi.policydocs.timer;

import com.ht.feignapi.policydocs.service.PolicyDocsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


/**
 * @author: wang.hy
 * @Date: 2021/01/07
 */
@Configuration
@EnableScheduling
public class ESFileScheduleTask {


//    @Autowired
//    private PolicyDocsService policyDocsService;
//
//    private final static Logger log = LoggerFactory.getLogger(ESFileScheduleTask.class);
//
//    //每天凌晨3点，存储所有的当天的production到redis
////    @Scheduled(cron = "0/50 * * * * ?")//每50秒
//    @Scheduled(cron = "0 0 2 * * ?")//凌晨2点
//    public void saveFileContentToEs(){
//        log.info("saveFileContentToEs start.......");
//        policyDocsService.saveFileContentToEsTask();
//        log.info("saveFileContentToEs end.......");
//    }


}
