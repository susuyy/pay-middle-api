package com.ht.feignapi.prime.controller;

import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.*;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.OpenIdException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.util.UserFlagCodeSubUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ms/prime")
@CrossOrigin(allowCredentials = "true")
public class PrimeUserConsumerController {

    @Autowired
    private MSPrimeClient msPrimeClient;

    private Logger logger = LoggerFactory.getLogger(PrimeUserConsumerController.class);

    /**
     * 会员消费接口
     * @param primeConsumerData
     * @return
     */
    @PostMapping("/consumer")
    public ReturnPrimeConsumerData consumer(@RequestBody PrimeConsumerData primeConsumerData) {
        String userFlagCode = primeConsumerData.getUserFlagCode();
        QrUserMessageData qrUserMessageData = UserFlagCodeSubUtil.userFlagCodeSub(userFlagCode);

        Boolean checkResult = msPrimeClient.checkQrAuthCode(qrUserMessageData).getData();
        if (!checkResult){
            throw new CheckException(ResultTypeEnum.QR_AUTH_CODE_ERROR);
        }

        String ext1 = primeConsumerData.getExt1();
        if (StringUtils.isEmpty(ext1)){
            ext1 = "消费未上送扩展数据";
        }

        String ext2 = primeConsumerData.getExt2();
        if (StringUtils.isEmpty(ext2)){
            ext2 = "Is not enabled";
        }
        String ext3 = primeConsumerData.getExt3();
        if (StringUtils.isEmpty(ext3)){
            ext3 = "Is not enabled";
        }

        //查询用户余额
        Result<MsUserAccount> msUserAccountResult = msPrimeClient.queryMsUserAccount(qrUserMessageData.getOpenId(), qrUserMessageData.getUserAccountFlagCode());
        if (msUserAccountResult.getCode().equals(ResultTypeEnum.USER_CARD_NOT.getCode())){
            throw new CheckException(ResultTypeEnum.USER_MONEY_NOT_ENOUGH);
        }else if (msUserAccountResult.getCode().equals(ResultTypeEnum.USER_NULL.getCode())){
            throw new CheckException(ResultTypeEnum.USER_NULL);
        }else if (msUserAccountResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode())){
            MsUserAccount msUserAccount = msUserAccountResult.getData();
            if (msUserAccount.getAmount()<primeConsumerData.getAmount()){
                throw new CheckException(ResultTypeEnum.USER_MONEY_NOT_ENOUGH);
            }
            //消费用户余额
            Result<PayResultInfo> result = msPrimeClient.consumerUserAccount(msUserAccount.getCardId(),
                    primeConsumerData.getAmount(),
                    qrUserMessageData.getUserAccountFlagCode(),
                    ext1,
                    ext2,
                    ext3);
            PayResultInfo payResultInfo = result.getData();
            if (!result.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode())){
                if (StringUtils.isEmpty(payResultInfo.getSubMsg()) || "null".equals(payResultInfo.getSubMsg())){
                    payResultInfo.setSubMsg(ResultTypeEnum.CARD_PAY_ERROR.getMessage());
                }
                throw new OpenIdException(ResultTypeEnum.CARD_PAY_ERROR.getCode(),payResultInfo.getSubMsg());
            }
            //封装数据返回
            ReturnPrimeConsumerData returnPrimeConsumerData = new ReturnPrimeConsumerData();
            returnPrimeConsumerData.setConsumerFlag(true);
            returnPrimeConsumerData.setUsedAmount(Integer.parseInt(payResultInfo.getAmount()));
            MsUserAccount userAccount = msPrimeClient.queryMsUserAccount(qrUserMessageData.getOpenId(), qrUserMessageData.getUserAccountFlagCode()).getData();
            returnPrimeConsumerData.setAfterUserMoney(userAccount.getAmount());
            returnPrimeConsumerData.setMerOrderId(payResultInfo.getMerOrderId());
            return returnPrimeConsumerData;
        }else {
            throw new CheckException(ResultTypeEnum.CARD_PAY_ERROR);
        }
    }


}
