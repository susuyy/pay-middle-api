package com.ht.user.card.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.user.admin.vo.AdjustAccount;
import com.ht.user.admin.vo.OrdersVo;
import com.ht.user.admin.vo.Recharge;
import com.ht.user.card.entity.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.card.vo.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单主表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-06-22
 */
public interface CardOrdersService extends IService<CardOrders> {


    /**
     * 根据订单号查询订单详情
     * @param orderCode
     * @return
     */
    CardOrdersVO queryByOrderCode(String orderCode);

    /**
     * 支付成功 修改订单状态
     * @param paySuccess
     * @return
     */
    List<CardOrderDetails> paySuccess(PaySuccess paySuccess);

    /**
     * 用户扫码支付下单
     * @param money
     * @param merchantCode
     * @param paySource
     * @param payType
     */
    PlaceOrderResult merchantQrCodePlaceOrder(Integer money, String merchantCode, String paySource, String payType);

    /**
     * 用户扫商家二维码 支付成功,修改订单数据
     * @param paySuccess
     */
    void merchantQrCodePaySuccess(PaySuccess paySuccess);

    /**
     * 通过传入的adjustAccount账号信息，添加order
     *
     * @param adjustAccount
     * @return
     */
    Boolean saveOrder(AdjustAccount adjustAccount);

    /**
     * 充值
     * @param recharge
     * @return
     */
    Boolean recharge(Recharge recharge);

    /**
     * 获取调账订单
     * @param merchantCode
     * @param page
     * @return
     */
    List<OrdersVo> getAdjustAccountOrders(String merchantCode, IPage<OrdersVo> page);

    /**
     * 获取充值订单
     * @param merchantCode
     * @param orderType
     * @param page
     * @return
     */
    List<OrdersVo> getRechargeOrders(String merchantCode, String orderType, IPage<OrdersVo> page);

    /**
     * 创建 组合支付 优惠,流水,订单
     * @param userId
     * @param amount
     * @param payMoney
     * @param needPayMoney
     * @param merchantCode
     * @param orderCode
     * @param accountCardNo
     * @param cardPayDetailData
     */
    void accountPayCreateOrderAndDetailAndTrace(Long userId, Integer amount, Integer payMoney, Integer needPayMoney, String merchantCode, String orderCode,String accountCardNo,CardPayDetailData cardPayDetailData);

    /**
     * C端用户组合支付下单
     * @param userCashCardPayOrderData
     */
    UserCashCardPayOrderReturn userCashCardPayPlaceOrder(UserCashCardPayOrderData userCashCardPayOrderData);

    /**
     * C端用户卡券 支付成功
     * @param paySuccess
     */
    void paySuccessCashCardOrder(PaySuccess paySuccess);

    CardOrders getOrder(String orderCode);

    List<OrdersVo> getOrderList(String merchantCode, IPage<OrdersVo> page);

    /**
     * 分页展示订单列表
     * @param userId
     * @param merchantAndSon
     * @param type
     * @param state
     * @param pageNo
     * @param pageSize
     * @return
     */
    IPage selectOrderPage(Long userId, List<Merchants> merchantAndSon, String type, String state, Integer pageNo, Integer pageSize);

    /**
     * 获取用户在某个商户下，一天的花费
     * @param merchantCode
     * @param userId
     * @return
     */
    Integer getUserDailyExpenditure(String merchantCode, Long userId);

    /**
     * 获取用户在某个商户下的所有订单，不包含详细信息
     * @param merchantCode
     * @param userId
     * @return
     */
    List<CardOrders> getUserCardOrders(String merchantCode, Long userId);

    /**
     * 用户买券下单
     * @param userPlaceOrderData
     * @param userId
     * @return
     */
    PlaceOrderResult placeOrder(UserPlaceOrderData userPlaceOrderData, Long userId);

    /**
     * 根据订单号修改订单状态
     * @param orderCode
     * @param state
     */
    void updateStateByOrderCode(String orderCode, String state);

    /**
     * 根据订单号更新 用户id 和 state
     * @param orderCode
     * @param state
     * @param userId
     */
    void updateStateAndUserIdByOrderCode(String orderCode, String state, Long userId);

    /**
     * 创建 组合支付 优惠,流水,订单
     * @param userId
     * @param amount
     * @param payMoney
     * @param needPayMoney
     * @param merchantCode
     * @param orderCode
     * @param accountCardNo
     * @param cardPayDetailData
     */
    void misAccountPayCreateOrderAndDetailAndTrace(Long userId, Integer amount, Integer payMoney, Integer needPayMoney, String merchantCode, String orderCode,String accountCardNo,CardPayDetailData cardPayDetailData);

    /**
     * 创建充值订单
     * @param userTopUpOrderData
     */
    String createUserTopUpOrder(UserTopUpOrderData userTopUpOrderData);

    /**
     * 充值订单 支付成功
     * @param paySuccess
     * @param ordersVO
     */
    void topUpPaySuccess(PaySuccess paySuccess,CardOrdersVO ordersVO) throws Exception;

    /**
     * 确认数据库中 mis 的 待支付订单
     * @param cashId
     * @return
     */
    List<CardOrderPayTrace> checkHaveOrder(String cashId);

    CardOrders createPrimeBuyCardOrder(PrimeBuyCardData primeBuyCardData);

    List<CardOrderDetails> updatePrimeBuyCardState(String orderCode, String payCode);

    void primeBuyCardUpdateCardNo(UpdateCardNoData updateCardNoData);

    void updateBuyCardOrderRefundState(String orderCode);

    CardOrders getByOrderCode(String orderCode);

    Page<CardOrders> getOrderPage(Page<CardOrders> page, String orderNo, String startTime, String endTime,
                                  String state, Long userId,String phone, String type, String cardNo, String traceNo);

    Page<CardOrders> getOrderPagePhone(Page<CardOrders> page, String orderNo, String startTime, String endTime,
                                  String state, String userId, String type, String cardNo, String traceNo);

    List<CardOrderPayTrace> getConsumeOrdersExcelData(String orderNo, String startTime, String entTime, String state, String openId, String type, String cardNo, String traceNo);

    List<PrimeBuyCardOrderExcelVo> getExcelList(String startTime, String endTime, String state, Long userId,String phone, String type, String cardNo, String traceNo, String orderNo);

    List<PrimeBuyCardOrderExcelVo> getExcelListPhone(String startTime, String endTime, String state, String phone, String type, String cardNo, String traceNo, String orderNo);

    void createAdminSetUserCardOrder(CardElectronic cardElectronic, String payType, String payAmount);

    List<CardOrders> getConsumeOrdersMasterExcelData(String orderNo, String startTime, String endTime);

    void createAdminSetUserCardOrderBatch(Map<String, CardElectronic> cardElectronicMap, String payType);

    Page<CardOrders> queryUserOrderListPage(Long pageNo, Long pageSize, String phoneNum, Long userId);

}
