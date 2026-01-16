package com.ht.user.card.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ht.user.card.entity.DistributeTrace;
import com.ht.user.card.mapper.DistributeTraceMapper;
import com.ht.user.card.service.DistributeTraceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-09
 */
@Service
public class DistributeTraceServiceImpl extends ServiceImpl<DistributeTraceMapper, DistributeTrace> implements DistributeTraceService {

    @Override
    public void createDistributeTrace(String merchantCode, String cardCode, int amount, String comments, String source, String activityCode, String batchCode) {
        DistributeTrace distributeTrace = new DistributeTrace();
        distributeTrace.setMerchantCode(merchantCode);
        distributeTrace.setCardCode(cardCode);
        distributeTrace.setAmount(amount);
        distributeTrace.setComments(comments);
        distributeTrace.setSource(source);
        distributeTrace.setActivityCode(activityCode);
        distributeTrace.setBatchCode(batchCode);
        this.save(distributeTrace);
    }

    @Override
    public List<DistributeTrace> getList(String traceType, String merchantCode, IPage<DistributeTrace> page) {
        return this.baseMapper.getList(traceType,merchantCode,page);
    }
}
