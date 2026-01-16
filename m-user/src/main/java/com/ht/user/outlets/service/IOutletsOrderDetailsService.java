package com.ht.user.outlets.service;

import com.ht.user.outlets.entity.OutletsOrderDetails;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单明细 服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface IOutletsOrderDetailsService extends IService<OutletsOrderDetails> {

    void updateStateByOrderCode(String orderCode, String state);

}
