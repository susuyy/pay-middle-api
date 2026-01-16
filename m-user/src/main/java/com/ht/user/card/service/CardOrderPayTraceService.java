package com.ht.user.card.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.card.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.card.excel.ConsumeCardOrderExcelVo;
import com.ht.user.card.vo.PosPayTraceData;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单支付流水 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
public interface CardOrderPayTraceService extends IService<CardOrderPayTrace> {

    /**
     * 根据pos机串号查询消费流水记录
     * @param posSerialNum
     * @return
     */
    List<CardOrderPayTrace> queryListByPosSerialNum(String posSerialNum);

    /**
     * 根据会员卡号 查询消费流水记录
     * @param payCode
     * @return
     */
    List<CardOrderPayTrace> queryListByPayCode(String payCode);

    /**
     * 创建 优惠券抵扣金额流水
     * @param userId
     * @param merchantCode
     * @param orderCode
     * @param cardPayDetailData
     */
    void createCouponCardPayTrace(Long userId,
                            String merchantCode,
                            String orderCode, CardPayDetailData cardPayDetailData);

    /**
     * pos端支付成功过后创建支付流水
     * @param posPayTraceData
     */
    void createPosPayTrace(PosPayTraceData posPayTraceData);

    /**
     * 根据订单号 修改支付流水状态
     * @param orderCode
     * @param paid
     * @param date
     * @param payCode
     */
    void updateStateByOrderCode(String orderCode, String paid, Date date, String payCode);


    /**
     * 创建pos收银的支付流水
     * @param posPayTraceData
     */
    String createPosPayTraceFromCashier(PosPayTraceData posPayTraceData);

    /**
     * 创建 余额支出 支付流水
     * @param orderCode
     * @param orderDetailId
     * @param type
     * @param state
     * @param source
     * @param sourceId
     * @param userMoneyInt
     * @param payCode
     */
    void createPayTrace(String orderCode, Long orderDetailId, String type, String state, String source, String sourceId, int userMoneyInt, String payCode);

    /**
     * 根据订单号和支付来源标识 查询
     * @param orderCode
     * @param sourceId
     */
    CardOrderPayTrace queryByOrderCodeAndSourceId(String orderCode, String sourceId);

    /**
     * 查询现金支付流水 获取现金金额
     * @param orderCode
     * @return
     */
    CardOrderPayTrace queryTraceByOrderCodeAndCashPay(String orderCode);

    /**
     * 创建非会员 , 直接支付的订单流水
     * @param posPayTraceData
     */
    void createUsuallyUserPayTrace(PosPayTraceData posPayTraceData);

    /**
     * 创建云mis订单未支付数据
     * @param misOrderData
     */
    String createMisOrderPayTrace(MisOrderData misOrderData);

    /**
     * 云mis订单支付成功,修改订单状态 (非会员支付)
     * @param posPayTraceData
     */
    void updateMisOrderState(PosPayTraceData posPayTraceData);

    /**
     * 云mis订单支付成功,修改订单状态 (会员组合支付)
     * @param posPayTraceData
     */
    void updateVipMisOrderState(PosPayTraceData posPayTraceData);

    /**
     * 根据订单编号查询流水
     * @param orderCode
     * @return
     */
    List<CardOrderPayTrace> queryTraceByOrderCode(String orderCode);

    /**
     * 创建现金支付mis流水
     * @param posPayTraceData
     */
    void createCashMisPayTrace(PosPayTraceData posPayTraceData);

    /**
     * 创建电子卡支付 订单明细与流水 (基于云mis订单支付)
     * @param consumeMoney
     * @param orderCode
     * @param cardNo
     * @param userId
     * @param terId
     */
    void createCardElectronicPayTrace(int consumeMoney, String orderCode, String cardNo, long userId, String terId);

    /**
     * 根据订单号和商户号查询流水
     * @param orderCode
     * @param merchantCode
     * @return
     */
    List<CardOrderPayTrace> queryPayTraceByOrderCodeAndMerchantCode(String orderCode, String merchantCode);

    /**
     * 创建 富基对接的 组合支付流水
     * @param saveCardPayTraceList
     * @param totalAmount
     * @param userId
     * @param storeCode
     * @param actualPhone
     * @param idCardNo
     */
    void createPosCombinationPayTrace(List<CardOrderPayTrace> saveCardPayTraceList, Integer totalAmount, Long userId, String storeCode, String actualPhone, String idCardNo);

    /**
     * 创建富基 实际支付流水
     * @param posCombinationPaySuccess
     */
    void createPosCombinationCashPay(PosCombinationPaySuccess posCombinationPaySuccess);

    /**
     * 更新退款状态
     * @param refundCardOrderPayTraces
     */
    void updateRefundData(List<CardOrderPayTrace> refundCardOrderPayTraces);

    /**
     * 查询用户流水
     * @param userFlag
     * @param pageNo
     * @param pageSize
     */
    Page<CardOrderPayTrace> queryUserPayTrace(String userFlag, String pageNo, String pageSize);

    void createPrimeBuyCardOrderPayTrace(PrimeBuyCardData primeBuyCardData, CardOrders cardOrders);

    void updateStateByOrderCodeNotPayCode(String orderCode, String paid, Date date);

    void updateStateAndPayCodeByOrderCode(String orderCode, String paid, Date date, String payCode);

    Page<CardOrderPayTrace> posOrderList(SearchPosOrderListData searchPosOrderListData);

    List<CardOrderPayTrace> querySummaryData(String startTime, String endTime);

    List<CardOrderPayTrace> querySummaryDataForMerChantCode(String startTime, String endTime,String merchantCode);


    void createCardPhysicalPayTrace(int consumeMoney, String orderCode, String cardNo, long userId, String terId, CardPhysical cardPhysical);

    Page<CardOrderPayTrace> queryUserConsumeOrder(String openId, String phoneNum, Long pageNo, Long pageSize);

    List<ConsumeCardOrderExcelVo> batchConsumeExcel(String batchCode);

}
