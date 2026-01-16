package com.ht.merchant.service;

import com.ht.merchant.entity.MrcMapMerchantUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-17
 */
public interface IMrcMapMerchantUserService extends IService<MrcMapMerchantUser> {

    /**
     * 获取商户管理员list
     * @param merchantCode
     * @return
     */
    List<MrcMapMerchantUser> getMerchantUserList(String merchantCode);

    /**
     * 获取商户固定管理员
     * @param userId
     * @param merchantCode
     * @return
     */
    MrcMapMerchantUser getMerchantUser(String userId, String merchantCode);
}
