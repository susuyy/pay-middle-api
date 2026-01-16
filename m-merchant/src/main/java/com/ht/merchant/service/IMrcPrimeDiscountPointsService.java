package com.ht.merchant.service;

import com.ht.merchant.entity.MrcPrimeDiscountPoints;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-29
 */
public interface IMrcPrimeDiscountPointsService extends IService<MrcPrimeDiscountPoints> {

    /**
     * 覆盖saveOrUpdate方法
     * @param mrcPrimeDiscountPoints
     * @return
     */
    @Override
    boolean saveOrUpdate(MrcPrimeDiscountPoints mrcPrimeDiscountPoints);
    /**
     * 查询 门店 商品积分 抵扣规则
     * @param merchantCode
     * @param productionCode
     * @return
     */
    MrcPrimeDiscountPoints queryPrimeDiscountPoints(String merchantCode, String productionCode);
}
