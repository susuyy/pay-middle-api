package com.ht.feignapi.tongshangyun.service;

import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.entity.MrcMapMerchantPrimes;
import com.ht.feignapi.tongshangyun.client.TsyAgentCollectApplyClient;
import com.ht.feignapi.tongshangyun.entity.RequestAgentCollectApply;
import com.ht.feignapi.tongshangyun.entity.ResponseAgentCollectApplyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AgentCollectApplyService {

    @Autowired
    private TsyAgentCollectApplyClient tsyAgentCollectApplyClient;

    /**
     * 托管代收 申请 加确认支付
     * @param amount
     * @param collectionBizUserId
     * @param payerId
     * @param goodsName
     * @param goodsDesc
     * @param orderCode
     * @return
     */
    public ResponseAgentCollectApplyData agentCollectApplyAndCheckPay(Long amount,String collectionBizUserId,String payerId,String goodsName,String goodsDesc,String orderCode) {
        RequestAgentCollectApply requestAgentCollectApply = new RequestAgentCollectApply();
        requestAgentCollectApply.setAmount(amount);
        requestAgentCollectApply.setCollectionBizUserId(collectionBizUserId);
        requestAgentCollectApply.setPayerId(payerId);
        requestAgentCollectApply.setGoodsName(goodsName);
        requestAgentCollectApply.setGoodsDesc(goodsDesc);
        requestAgentCollectApply.setOrderCode(orderCode);
        return tsyAgentCollectApplyClient.agentCollectApplyAndCheckPay(requestAgentCollectApply).getData();
    }
}
