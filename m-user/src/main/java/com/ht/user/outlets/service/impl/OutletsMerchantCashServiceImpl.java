package com.ht.user.outlets.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ht.user.card.entity.MisOrderData;
import com.ht.user.config.CardOrderPayTraceStateConfig;
import com.ht.user.config.CardOrderPayTraceTypeConfig;
import com.ht.user.config.CardOrdersStateConfig;
import com.ht.user.config.CardOrdersTypeConfig;
import com.ht.user.ordergoods.entity.UploadOrderDetails;
import com.ht.user.outlets.allinpay.SybPayService;
import com.ht.user.outlets.config.TrxCodeDescribeEnum;
import com.ht.user.outlets.entity.*;
import com.ht.user.outlets.exception.CheckException;
import com.ht.user.outlets.mapper.OutletsMerchantCashMapper;
import com.ht.user.outlets.mapper.OutletsOrdersMapper;
import com.ht.user.outlets.service.*;
import com.ht.user.outlets.util.MoneyUtils;
import com.ht.user.outlets.util.StringGeneralUtil;
import com.ht.user.outlets.vo.OutletsOrderPayTraceVO;
import com.ht.user.outlets.vo.OutletsOrdersVO;
import com.ht.user.result.ResultTypeEnum;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 收银台商户对应 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Service
public class OutletsMerchantCashServiceImpl extends ServiceImpl<OutletsMerchantCashMapper, OutletsMerchantCash> implements IOutletsMerchantCashService {

    @Override
    public Page<RetSummaryMerchantCashPayData> summaryMerchantCashPayData(ReqSummaryMerchantCashPayData reqSummaryMerchantCashPayData) {
        IPage<RetSummaryMerchantCashPayData> page = new Page<>(reqSummaryMerchantCashPayData.getPageNo(),reqSummaryMerchantCashPayData.getPageSize());
        QueryWrapper<RetSummaryMerchantCashPayData> queryWrapper= new QueryWrapper<>();
        if (StringGeneralUtil.checkNotNull(reqSummaryMerchantCashPayData.getMerchName())) {
            queryWrapper.like("merch_name",reqSummaryMerchantCashPayData.getMerchName());
        }
        if (StringGeneralUtil.checkNotNull(reqSummaryMerchantCashPayData.getCashId())) {
            queryWrapper.eq("cash_id",reqSummaryMerchantCashPayData.getCashId());
        }
        return this.baseMapper.summaryMerchantCashPayDataNotName(page,
                reqSummaryMerchantCashPayData.getStartTime(),
                reqSummaryMerchantCashPayData.getEndTime(),queryWrapper);
    }


}
