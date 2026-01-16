package com.ht.merchant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ht.merchant.entity.MrcMapMerchantUser;
import com.ht.merchant.mapper.MrcMapMerchantUserMapper;
import com.ht.merchant.service.IMrcMapMerchantUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-17
 */
@Service
public class MrcMapMerchantUserServiceImpl extends ServiceImpl<MrcMapMerchantUserMapper, MrcMapMerchantUser> implements IMrcMapMerchantUserService {

    @Override
    public List<MrcMapMerchantUser> getMerchantUserList(String merchantCode) {
        LambdaQueryWrapper<MrcMapMerchantUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MrcMapMerchantUser::getMerchantCode,merchantCode);
        return this.list(wrapper);
    }

    @Override
    public MrcMapMerchantUser getMerchantUser(String userId, String merchantCode) {
        LambdaQueryWrapper<MrcMapMerchantUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MrcMapMerchantUser::getMerchantCode,merchantCode);
        wrapper.eq(MrcMapMerchantUser::getUserId,userId);
        return this.getOne(wrapper,false);
    }
}
