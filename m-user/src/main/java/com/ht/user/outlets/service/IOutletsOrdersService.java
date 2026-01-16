package com.ht.user.outlets.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.card.entity.MisOrderData;
import com.ht.user.common.Result;
import com.ht.user.outlets.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.outlets.vo.OutletsOrdersVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单主表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface IOutletsOrdersService extends IService<OutletsOrders> {

    /**
     * 创建 mis 刷卡支付订单
     * @param misOrderData
     * @return
     */
    OutletsOrderPayTrace createMisOrderPayTrace(MisOrderData misOrderData);

    /**
     * 根据订单号查询
     * @param orderCode
     * @return
     */
    OutletsOrders queryByOrderCode(String orderCode);

    OutletsOrders queryByOrderCodeNotMany(String orderCode);

    /**
     * 修改支付订单主表 明细表 流水表状态
     * @param orderCode
     * @param state
     */
    void updateAllState(String orderCode, String state);

    /**
     * 根据收银台号获取订单
     * @param cashId
     * @return
     */
    List<OutletsOrderPayTrace> checkHaveOrder(String cashId);

    /**
     * 根据订单号修改状态
     * @param orderCode
     * @param state
     */
    void updateStateByOrderCode(String orderCode, String state);

    void updateStateChannelByOrderCode(String orderCode, String state, String channelApi);

    /**
     * 创建 扫码支付 订单数据
     * @param outletsOrderRefTrace
     * @param qrPaymentData
     * @param channelApi
     */
    void createPaymentQrCodeOrder(OutletsOrderRefTrace outletsOrderRefTrace, QrPaymentData qrPaymentData,String channelApi);

    /**
     * 扫码支付 退款
     * @param outletsOrderPayTraces
     * @param outletsRefundData
     * @throws Exception
     */
    void refund(List<OutletsOrderPayTrace> outletsOrderPayTraces, OutletsRefundData outletsRefundData) throws Exception;

    /**
     * 扫码支付 撤销
     * @param cancelData
     */
    void orderCancel(OutletsOrderCancelData cancelData) throws Exception;

    /**
     * 订单主表 分页列表
     * @param page
     * @param paramsMap
     * @return
     */
    IPage<OutletsOrdersVO> findPageLeftJoinPayTrace(IPage<OutletsOrders> page, Map<String, String> paramsMap);
}
