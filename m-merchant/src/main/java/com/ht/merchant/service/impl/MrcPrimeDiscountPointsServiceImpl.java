package com.ht.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.merchant.entity.MrcPrimeDiscountPoints;
import com.ht.merchant.entity.MrcPrimePointsTrace;
import com.ht.merchant.mapper.MrcPrimeDiscountPointsMapper;
import com.ht.merchant.service.IMrcPrimeDiscountPointsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.merchant.service.IMrcPrimePointsTraceService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-29
 */
@Service
public class MrcPrimeDiscountPointsServiceImpl extends ServiceImpl<MrcPrimeDiscountPointsMapper, MrcPrimeDiscountPoints> implements IMrcPrimeDiscountPointsService {

    @Autowired
    private IMrcPrimePointsTraceService primePointsTraceService;

    @SneakyThrows
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(MrcPrimeDiscountPoints mrcPrimeDiscountPoints){
        boolean res1 = super.saveOrUpdate(mrcPrimeDiscountPoints);

        MrcPrimePointsTrace pointsTrace = new MrcPrimePointsTrace();
        pointsTrace.setMerchantCode(mrcPrimeDiscountPoints.getMerchantCode());
        pointsTrace.setCredit(mrcPrimeDiscountPoints.getPoints());
        boolean res2 = primePointsTraceService.save(pointsTrace);
        if (!(res1&&res2)){
            throw new Exception("保存异常");
        }
        return true;
    }
    /**
     * 查询门店 商品 积分抵扣规则
     * @param merchantCode
     * @param productionCode
     * @return
     */
    @Override
    public MrcPrimeDiscountPoints queryPrimeDiscountPoints(String merchantCode, String productionCode) {
        QueryWrapper<MrcPrimeDiscountPoints> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_code",merchantCode);
        queryWrapper.eq("production_code",productionCode);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
