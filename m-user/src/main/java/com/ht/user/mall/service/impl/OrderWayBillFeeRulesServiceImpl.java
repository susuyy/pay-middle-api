package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ht.user.mall.entity.OrderWayBillFeeRules;
import com.ht.user.mall.mapper.OrderWayBillFeeRulesMapper;
import com.ht.user.mall.service.OrderWayBillFeeRulesService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-23
 */
@Service
public class OrderWayBillFeeRulesServiceImpl extends ServiceImpl<OrderWayBillFeeRulesMapper, OrderWayBillFeeRules> implements OrderWayBillFeeRulesService {

    @Override
    public List<OrderWayBillFeeRules> queryWayBillFeeRules(String merchantCode) {
        QueryWrapper<OrderWayBillFeeRules> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("merchant_code",merchantCode);
        return this.baseMapper.selectList(queryWrapper);
    }
}
