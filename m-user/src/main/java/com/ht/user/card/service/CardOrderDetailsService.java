package com.ht.user.card.service;

import com.ht.user.card.entity.CardOrderDetails;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.card.entity.CardOrders;
import com.ht.user.card.entity.PrimeBuyCardData;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单明细 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
public interface CardOrderDetailsService extends IService<CardOrderDetails> {

    /**
     * 订单明细 根据明细id 查询
     * @param detailId
     * @return
     */
    CardOrderDetails queryByDetailId(String detailId);

    /**
     * 根据订单号 查询订单明细
     * @param orderCode
     * @return
     */
    List<CardOrderDetails> queryByOrderCode(String orderCode);

    /**
     * 修改订单明细状态  通过orderCode
     * @param orderCode
     * @param paid
     * @param date
     */
    void updateStateByOrderCode(String orderCode, String paid, Date date);

    void createPrimeBuyCardOrderDetails(PrimeBuyCardData primeBuyCardData, CardOrders cardOrders);

    void updateProdCodeById(Long id, String proCodeListStr);

    List<CardOrderDetails> querySummaryDetails(String startTime, String endTime);

    List<CardOrderDetails> querySummaryDetailsForMerchantCode(String merchantCode,String startTime, String endTime);


}
