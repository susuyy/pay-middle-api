package com.ht.merchant.controller;


import com.ht.merchant.entity.MrcMerchantCandidates;
import com.ht.merchant.service.IMrcMerchantCandidatesService;
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
 * @since 2020-11-03
 */
@RestController
@RequestMapping("/merchant/candidates")
public class MrcMerchantCandidatesController {

    @Autowired
    private IMrcMerchantCandidatesService merchantCandidatesService;

    /**
     * 保存用户门店申请
     * @param merchantCandidates
     */
    @PostMapping
    public Boolean save(@RequestBody MrcMerchantCandidates merchantCandidates){
        return merchantCandidatesService.save(merchantCandidates);
    }
}

