package com.ht.merchant.service;

import com.ht.merchant.entity.MrcMapMerchant;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-03-23
 */
public interface MrcMapMerchantService extends IService<MrcMapMerchant> {

    void addMrcMapMerchant(String subMerchantCode, String objMerchantCode);

    List<MrcMapMerchant> queryBySubMerchantCode(String subMerchantCode);

}
