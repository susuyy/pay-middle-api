package com.ht.user.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.mall.entity.OrderWayBills;
import com.ht.user.mall.mapper.OrderWayBillsMapper;
import com.ht.user.mall.service.OrderWayBillsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-11
 */
@Service
public class OrderWayBillsServiceImpl extends ServiceImpl<OrderWayBillsMapper, OrderWayBills> implements OrderWayBillsService {

    @Override
    public void saveOrderWayBills(Long userId,
                                  String orderCode,
                                  String wayBillCode,
                                  String province,
                                  String city,
                                  String county,
                                  String address,
                                  String tel,
                                  String name,
                                  Integer billFee,
                                  String type,
                                  String state, String merchantCode) {
        OrderWayBills orderWayBills = new OrderWayBills();
        orderWayBills.setUserId(userId);
        orderWayBills.setOrderCode(orderCode);
        orderWayBills.setWayBillCode(wayBillCode);
        orderWayBills.setProvince(province);
        orderWayBills.setCity(city);
        orderWayBills.setCounty(county);
        orderWayBills.setAddress(address);
        orderWayBills.setTel(tel);
        orderWayBills.setName(name);
        orderWayBills.setBillFee(billFee);
        orderWayBills.setType(type);
        orderWayBills.setState(state);
        orderWayBills.setMerchantCode(merchantCode);
        orderWayBills.setCreateAt(new Date());
        orderWayBills.setUpdateAt(new Date());
        this.baseMapper.insert(orderWayBills);
    }

    /**
     * 根据orderCode 查询派单信息
     * @param orderCode
     * @return
     */
    @Override
    public List<OrderWayBills> queryByOrderCode(String orderCode) {
        QueryWrapper<OrderWayBills> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_code",orderCode);
        return this.baseMapper.selectList(queryWrapper);
    }
}
