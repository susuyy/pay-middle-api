package com.ht.user.outlets.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.outlets.entity.OutletsOrderRefundCancel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.outlets.entity.OutletsOrders;
import com.ht.user.outlets.entity.SearchTraceData;
import com.ht.user.outlets.excel.OutletsOrderRefundCancelExcelVO;
import com.ht.user.outlets.vo.OutletsOrderRefundCancelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-17
 */
public interface IOutletsOrderRefundCancelService extends IService<OutletsOrderRefundCancel> {

    /**
     * 对接方查询 退款 撤销数据
     * @param searchTraceData
     * @return
     */
    List<OutletsOrderRefundCancel> queryRefundCancelData(SearchTraceData searchTraceData);

    /**
     * 分页列表
     * @param page
     * @param paramsMap
     * @return
     */
    IPage<OutletsOrderRefundCancelVO> findPage(IPage<OutletsOrderRefundCancel> page, Map<String, String> paramsMap);

    List<OutletsOrderRefundCancelVO> findlist(Map<String, String> paramsMap);

    /**
     * 包装退款订单数据
     * @param result
     * @return
     */
    List<OutletsOrderRefundCancelExcelVO> packageOutletsOrderRefundCancel(List<OutletsOrderRefundCancelVO> result);

    /**
     * 按照支付类型统计退款金额
     * @param startCreateAt
     * @param endCreateAt
     * @return
     */
    List<Map<String, Object>> countRefundAmountByServiceType(String startCreateAt, String endCreateAt);

    /**
     * 按照交易类型统计退款金额
     * @param startCreateAt
     * @param endCreateAt
     * @return
     */
    List<Map<String, Object>> countRefundAmountByPayTrxcode(String startCreateAt, String endCreateAt);

    /**
     * 统计 收银台号 刷卡退款金额，退款手续费
     * @return
     */
    Map<String, Object> countCardRefundAmountByPayTrxcodeAndCashId(Map<String, String> paramsMap);

    /**
     * 统计 收银台号 扫码退款金额，退款手续费
     * @return
     */
    Map<String, Object> countCardRefundAmountByPayTrxcodeDescribeAndCashId(Map<String, String> paramsMap);


}
