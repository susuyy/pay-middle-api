package com.ht.user.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.mall.entity.OrderWayBills;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-11
 */
public interface OrderWayBillsService extends IService<OrderWayBills> {


    /**
     * 保存 OrderWayBills 派送相关
     * @param userId
     * @param orderCode
     * @param wayBillCode
     * @param province
     * @param city
     * @param county
     * @param address
     * @param tel
     * @param name
     * @param billFee
     * @param type
     * @param state
     * @param merchantCode
     */
    void saveOrderWayBills(Long userId,
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
                           String state, String merchantCode);

    /**
     * 根据orderCode 查询派单信息
     * @param orderCode
     * @return
     */
    List<OrderWayBills> queryByOrderCode(String orderCode);
}
