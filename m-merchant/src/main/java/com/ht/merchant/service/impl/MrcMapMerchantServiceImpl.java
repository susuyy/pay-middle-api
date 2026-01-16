package com.ht.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ht.merchant.entity.Merchants;
import com.ht.merchant.entity.MrcMapMerchant;
import com.ht.merchant.mapper.MrcMapMerchantMapper;
import com.ht.merchant.service.MerchantsService;
import com.ht.merchant.service.MrcMapMerchantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-03-23
 */
@Service
public class MrcMapMerchantServiceImpl extends ServiceImpl<MrcMapMerchantMapper, MrcMapMerchant> implements MrcMapMerchantService {

    @Autowired
    private MerchantsService merchantsService;

    @Override
    public void addMrcMapMerchant(String subMerchantCode, String objMerchantCode) {
        Merchants subMerchant = merchantsService.getMerchantByCode(subMerchantCode);
        Merchants objMerchant = merchantsService.getMerchantByCode(objMerchantCode);
        MrcMapMerchant mrcMapMerchant = new MrcMapMerchant();
        mrcMapMerchant.setSubMerchantCode(subMerchant.getMerchantCode());
        mrcMapMerchant.setSubMerchantName(subMerchant.getMerchantName());
        mrcMapMerchant.setObjMerchantCode(objMerchant.getMerchantCode());
        mrcMapMerchant.setObjMerchantName(objMerchant.getMerchantName());
        mrcMapMerchant.setCreateAt(new Date());
        mrcMapMerchant.setUpdateAt(new Date());
        save(mrcMapMerchant);
    }

    @Override
    public List<MrcMapMerchant> queryBySubMerchantCode(String subMerchantCode) {
        QueryWrapper<MrcMapMerchant> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("sub_merchant_code",subMerchantCode);
        return list(queryWrapper);
    }

}
