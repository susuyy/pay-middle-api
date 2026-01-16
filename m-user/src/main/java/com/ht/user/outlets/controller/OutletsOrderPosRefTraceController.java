package com.ht.user.outlets.controller;


import com.ht.user.outlets.service.IOutletsOrderPosRefTraceService;
import com.ht.user.outlets.service.IOutletsOrderRefTraceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-11-22
 */
@RestController
@RequestMapping("/tonglian/outlets/orderPosRefTrace")
public class OutletsOrderPosRefTraceController {

    private Logger logger = LoggerFactory.getLogger(OutletsOrderPosRefTraceController.class);

    @Autowired
    private IOutletsOrderPosRefTraceService outletsOrderPosRefTraceService;

    /**
     * 奥特莱斯 统计银行卡支付 日交易额
     * @return
     * @throws Exception
     */
    @PostMapping("/list/countDayAmount")
    public List<Map<String, Object>> countDayAmount() throws Exception {

        logger.info("countDayAmount");
        List<Map<String, Object>> result = outletsOrderPosRefTraceService.countLastSevenDaysAmount();
        return result;

    }

}

