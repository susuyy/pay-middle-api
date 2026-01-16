package com.ht.user.card.controller;


import com.ht.user.card.service.DistributeTraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-07-23
 */
@RestController
@RequestMapping("/card/distribute-trace")
public class DistributeTraceController {

    @Autowired
    private DistributeTraceService distributeTraceService;

    /**
     * 创建卡券发送记录
     * @param merchantCode
     * @param cardCode
     * @param size
     * @param description
     * @param nickName
     * @param s1
     * @param batchCode
     */
    @PostMapping
    public void createDistributeTrace(@RequestParam String merchantCode, @RequestParam String cardCode, @RequestParam int size,
                               @RequestParam String description, @RequestParam String nickName, @RequestParam String s1,
                               @RequestParam String batchCode){
        distributeTraceService.createDistributeTrace(merchantCode,cardCode,size,description,nickName,s1,batchCode);
    }


}

