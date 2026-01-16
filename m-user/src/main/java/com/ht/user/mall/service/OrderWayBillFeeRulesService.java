package com.ht.user.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.mall.entity.OrderWayBillFeeRules;

import java.util.List;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-09-23
 */
public interface OrderWayBillFeeRulesService extends IService<OrderWayBillFeeRules> {

    /**
     * 根据商户编码查询 派单费用计算规则
     * @param merchantCode
     * @return
     */
    List<OrderWayBillFeeRules> queryWayBillFeeRules(String merchantCode);
}
