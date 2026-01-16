package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.card.common.CardConstant;
import com.ht.user.card.entity.Merchants;
import com.ht.user.mall.entity.OrderProductions;
import com.ht.user.mall.mapper.OrderProductionsMapper;
import com.ht.user.mall.service.OrderProductionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 卡定义 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@Service
public class OrderProductionsServiceImpl extends ServiceImpl<OrderProductionsMapper, OrderProductions> implements OrderProductionsService {

    /**
     * 根据 商品编号 查询商品信息
     * @param productionCode
     * @param storeMerchantCode
     * @return
     */
    @Override
    public OrderProductions queryByProductionCode(String productionCode, String storeMerchantCode) {
        QueryWrapper<OrderProductions> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("production_code",productionCode);
        queryWrapper.eq("merchant_code",storeMerchantCode);
        return this.baseMapper.selectOne(queryWrapper);
    }


    @Override
    public IPage<OrderProductions> selectPage(List<Merchants> merchants, IPage<OrderProductions> pageInfo, String productionName, String productionCode, String onSaleState){
        LambdaQueryWrapper<OrderProductions> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrderProductions::getMerchantCode, merchants.stream().map(Merchants::getMerchantCode).collect(Collectors.toList()));
        if (!StringUtils.isEmpty(productionName)){
            wrapper.like(OrderProductions::getProductionName,productionName);
        }
        if (!StringUtils.isEmpty(productionCode)){
            wrapper.eq(OrderProductions::getProductionCode,productionCode);
        }
        if (!(StringUtils.isEmpty(onSaleState) || CardConstant.ON_SALE_STATE_ALL.equals(onSaleState))){
            wrapper.eq(OrderProductions::getOnSaleState,onSaleState);
        }
        return this.baseMapper.selectPage(pageInfo,wrapper);
    }
}
