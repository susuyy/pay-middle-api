package com.ht.merchant.controller;


import com.ht.merchant.entity.MrcPrimePointsTrace;
import com.ht.merchant.service.IMrcPrimePointsTraceService;
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
 * @since 2020-09-29
 */
@RestController
@RequestMapping("/mrc-prime-points-trace")
public class MrcPrimePointsTraceController {

    @Autowired
    private IMrcPrimePointsTraceService mrcPrimePointsTraceService;

    /**
     * 保存积分流水
     * @param mrcPrimePointsTrace
     */
    @PostMapping("/save")
    public void save(@RequestBody MrcPrimePointsTrace mrcPrimePointsTrace){
        mrcPrimePointsTraceService.save(mrcPrimePointsTrace);
    }
}

