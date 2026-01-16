package com.ht.merchant.controller;


import com.ht.merchant.entity.MrcMapMerchantPrimes;
import com.ht.merchant.entity.MrcPrimeDiscountPoints;
import com.ht.merchant.service.IMrcPrimeDiscountPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-09-29
 */
@RestController
@RequestMapping("/mrc-prime-discount-points")
public class MrcPrimeDiscountPointsController {

    @Autowired
    private IMrcPrimeDiscountPointsService primeDiscountPointsService;

    @Autowired
    private IMrcPrimeDiscountPointsService mrcPrimeDiscountPointsService;

    /**
     * 保存商户和会员关联信息
     * @param primeDiscountPoints 保存prime
     * @return 返回保存的prime信息
     */
    @PostMapping
    public void save(@RequestBody MrcPrimeDiscountPoints primeDiscountPoints){
        primeDiscountPointsService.saveOrUpdate(primeDiscountPoints);
    }

    /**
     * 查询商品积分抵扣
     * @param merchantCode
     * @param productionCode
     */
    @GetMapping("/queryPrimeDiscountPoints")
    public MrcPrimeDiscountPoints queryPrimeDiscountPoints(@RequestParam("merchantCode") String merchantCode,
                                                    @RequestParam("productionCode") String productionCode){
        return mrcPrimeDiscountPointsService.queryPrimeDiscountPoints(merchantCode,productionCode);
    }
}

