package com.ht.user.outlets.jlpay.contants;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jlpay.ext.qrcode.common.service.CommonService;
import com.jlpay.ext.qrcode.trans.request.TransBaseRequest;
import com.jlpay.ext.qrcode.trans.response.TransBaseResponse;
import com.jlpay.ext.qrcode.trans.utils.ErrorConstants;
import com.jlpay.ext.qrcode.trans.utils.JlpayException;
import com.jlpay.ext.qrcode.trans.utils.RSA256Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ClientAddQrDeviceTransExecuteService {
    private static final Logger logger = LogManager.getLogger(ClientAddQrDeviceTransExecuteService.class);

    public ClientAddQrDeviceTransExecuteService() {

    }

    public static CustomClientAddQrDeviceResponse executor(TransBaseRequest request) {
        String sysPriKey = System.getProperty("qrcode.org.privatekey");
        String jlPubKey = System.getProperty("qrocde.jlpay.publickey");
        String tradeUrl = TransConstants.PRE_OTHER_URL;
        if (null != sysPriKey && null != jlPubKey && null != tradeUrl) {
            String requestStr = customClientAddQrDeviceSign(request, sysPriKey);
            String responseStr = CommonService.httpToInvoke(requestStr, tradeUrl + request.getService());
            return JSON.parseObject(responseStr, CustomClientAddQrDeviceResponse.class);
        } else {
            throw new JlpayException(ErrorConstants.VALIDATE_ERROR_1, "请设置系统参数");
        }
    }


    public static String customClientAddQrDeviceSign(TransBaseRequest request, String sysPriKey) {
        JSONObject contextJson = JSON.parseObject(JSON.toJSONString(request));
        //agentId,source,
        //merchNo,signMethod
        Object agentId = contextJson.get("agentId");
        Object source = contextJson.get("source");
        Object merchNo = contextJson.get("merchNo");
        Object signMethod = contextJson.get("signMethod");
//        String signContext = customClientAddQrDeviceGetSignContext(contextJson);
        String signContext = agentId.toString()+source+merchNo+signMethod;
        logger.info("签名前内容为：" + agentId+source+merchNo+signMethod);
        String sign = RSA256Utils.sign256(signContext, sysPriKey);
//        contextJson.put("sign", sign);
        contextJson.put("signData",sign);

//        String signData = RSA256Utils.sign256(TransConstants.WAIT_SIGN_STR, sysPriKey);
//        contextJson.put("signData",signData);

        return JSON.toJSONString(contextJson);
    }

    private static String customClientAddQrDeviceGetSignContext(JSONObject contextJson) {
        List<String> keys = new ArrayList(contextJson.keySet());
        Collections.sort(keys);
        Map<String, Object> treeMap = new TreeMap();
        Iterator var3 = keys.iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            if (!"sign".equals(key)) {
                treeMap.put(key, contextJson.get(key));
            }
        }

        return JSON.toJSONString(treeMap);
    }
}
