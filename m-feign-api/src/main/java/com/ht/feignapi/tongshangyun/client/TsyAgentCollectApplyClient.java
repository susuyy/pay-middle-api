package com.ht.feignapi.tongshangyun.client;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tongshangyun.entity.RequestAgentCollectApply;
import com.ht.feignapi.tongshangyun.entity.ResponseAgentCollectApplyData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "${custom.client.pay-prorate.name}",contextId = "tsyAgentCollectApply")
public interface TsyAgentCollectApplyClient {

    /**
     * 托管代收_确认支付 返回支付页面地址跳转支付
     * @param requestAgentCollectApply
     * @return
     */
    @PostMapping("/tsy/agentCollectApply/agentCollectApplyAndCheckPay")
    Result<ResponseAgentCollectApplyData> agentCollectApplyAndCheckPay(@RequestBody RequestAgentCollectApply requestAgentCollectApply);

}
