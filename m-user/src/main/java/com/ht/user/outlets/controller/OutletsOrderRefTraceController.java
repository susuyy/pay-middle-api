package com.ht.user.outlets.controller;


import com.ht.user.outlets.service.IOutletsOrderPayTraceService;
import com.ht.user.outlets.service.IOutletsOrderRefTraceService;
import com.ht.user.outlets.vo.QueryCountSumVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-11-16
 */
@RestController
@RequestMapping("/tonglian/outlets/orders/orderRefTrace")
public class OutletsOrderRefTraceController {

    private Logger logger = LoggerFactory.getLogger(OutletsOrderRefTraceController.class);

    @Autowired
    private IOutletsOrderRefTraceService outletsOrderRefTraceService;

    /**
     * 奥特莱斯 根据交易类型统计 实际交易金额总和
     *
     * @param queryCountSumVO
     * @return
     */
    @PostMapping("/list/countTrxamt")
    public List<Map<String, Object>> countTrxamt(@RequestBody QueryCountSumVO queryCountSumVO) throws Exception {
        String startCreateAt = queryCountSumVO.getStartCreateAt();
        String endCreateAt = queryCountSumVO.getEndCreateAt();

        Map<String,String> paramsMap = new HashMap<>();
        paramsMap.put("startCreateAt", startCreateAt);
        paramsMap.put("endCreateAt", endCreateAt);

        List<Map<String, Object>> result = outletsOrderRefTraceService.countTrxamt(paramsMap);
        return result;
    }

    /**
     * 奥特莱斯 统计扫码支付日销售额（暂时为三种类型扫码支付）
     * @return
     * @throws Exception
     */
    @PostMapping("/list/countDayAmount")
    public List<Map<String, Object>> countDayAmount() throws Exception {

        logger.info("countDayAmount");
        List<Map<String, Object>> result = outletsOrderRefTraceService.countLastSevenDaysAmount();
        return result;

    }


}

