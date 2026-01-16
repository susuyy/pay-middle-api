package com.ht.user.outlets.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ht.user.card.entity.MisOrderData;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.vo.OutletsOrdersVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 收银台商户对应 服务类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
public interface IOutletsMerchantCashService extends IService<OutletsMerchantCash> {


    Page<RetSummaryMerchantCashPayData> summaryMerchantCashPayData(ReqSummaryMerchantCashPayData reqSummaryMerchantCashPayData);

}
