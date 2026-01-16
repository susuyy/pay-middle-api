package com.ht.user.outlets.service.impl;

import com.ht.user.outlets.entity.OutletsOrderDetails;
import com.ht.user.outlets.mapper.OutletsOrderDetailsMapper;
import com.ht.user.outlets.service.IOutletsOrderDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 订单明细 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Service
public class OutletsOrderDetailsServiceImpl extends ServiceImpl<OutletsOrderDetailsMapper, OutletsOrderDetails> implements IOutletsOrderDetailsService {

    @Override
    public void updateStateByOrderCode(String orderCode, String state) {
        this.baseMapper.updateStateByOrderCode(orderCode,state,new Date());
    }
}
