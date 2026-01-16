package com.ht.user.outlets.service;

import com.ht.user.outlets.entity.OutletsOrderRefRefundCancel;
import com.baomidou.mybatisplus.extension.service.IService;

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
public interface IOutletsOrderRefRefundCancelService extends IService<OutletsOrderRefRefundCancel> {

    /**
     * 按照服务类型统计退款手续费
     * @param startCreateAt
     * @param endCreateAt
     * @return
     */
    List<Map<String, Object>> countRefundFeeByServiceType(String startCreateAt, String endCreateAt);

    /**
     * 按照扫码支付类型统计退款手续费
     * @param startCreateAt
     * @param endCreateAt
     * @return
     */
    List<Map<String, Object>> countRefundFeeByTrxcode(String startCreateAt, String endCreateAt);

    List<OutletsOrderRefRefundCancel> queryFeeNullPosTypeData();

    /**
     * 根据退款订单号，得到OutletsOrderRefRefundCancel
     * @param reqsn
     * @return
     */
    OutletsOrderRefRefundCancel getByReqsn(String reqsn);

}
