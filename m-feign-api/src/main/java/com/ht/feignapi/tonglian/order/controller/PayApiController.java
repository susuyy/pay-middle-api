package com.ht.feignapi.tonglian.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import com.ht.feignapi.tonglian.order.client.OrderClientService;

import com.ht.feignapi.tonglian.order.client.PayClientService;
import com.ht.feignapi.tonglian.order.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 订单主表 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-22
 */
@RestController
@RequestMapping("/tonglian/pay")
@CrossOrigin(allowCredentials = "true")
public class PayApiController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private PayClientService payClient;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;


    /**
     * 获取通联调取 H5 支付数据 (组合支付)
     *
     * @param unionOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/unionorder")
    public Map unionorder(@RequestBody UnionOrderData unionOrderData) throws Exception {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(unionOrderData.getOrderCode()).getData();
        CardOrderPayTrace traceByOrderCode = orderClientService.getTraceByOrderCode(unionOrderData.getOrderCode()).getData();
        //获取配置的支付数据
        List<MerchantsConfigVO> merchantsConfigVOList = getMerchantsConfigListResult(unionOrderData.getMerchantCode());
        String mchId;
        String appId;
        String md5Key;
        for (MerchantsConfigVO merchantsConfig : merchantsConfigVOList) {
            if ("MCHID".equals(merchantsConfig.getKey())) {
                mchId = merchantsConfig.getValue();
                unionOrderData.setMchId(mchId);
            }
            if ("APPID".equals(merchantsConfig.getKey())) {
                appId = merchantsConfig.getValue();
                unionOrderData.setAppId(appId);
            }
            if ("MD5KEY".equals(merchantsConfig.getKey())) {
                md5Key = merchantsConfig.getValue();
                unionOrderData.setMD5Key(md5Key);
            }
        }
        unionOrderData.setTrxamt(traceByOrderCode.getAmount());
        unionOrderData.setBody(cardOrdersVO.getOrderDesc());
        unionOrderData.setMerchantCode(merchantsConfigVOList.get(0).getMerchantCode());
        logger.info("支付参数为" + unionOrderData);
        Map unionOrderMapData = payClient.unionorder(unionOrderData).getData();
        return unionOrderMapData;
    }


    /**
     * 获取通联调取 H5 支付数据(购买卡券)
     *
     * @param unionOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/unionorderBuyCard")
    public Map unionorderBuyCard(@RequestBody UnionOrderData unionOrderData) throws Exception {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(unionOrderData.getOrderCode()).getData();
        //获取支付数据
        List<MerchantsConfigVO> merchantsConfigVOList = getMerchantsConfigListResult(unionOrderData.getMerchantCode());
        String mchId;
        String appId;
        String md5Key;
        for (MerchantsConfigVO merchantsConfig : merchantsConfigVOList) {
            if ("MCHID".equals(merchantsConfig.getKey())) {
                mchId = merchantsConfig.getValue();
                unionOrderData.setMchId(mchId);
            }
            if ("APPID".equals(merchantsConfig.getKey())) {
                appId = merchantsConfig.getValue();
                unionOrderData.setAppId(appId);
            }
            if ("MD5KEY".equals(merchantsConfig.getKey())) {
                md5Key = merchantsConfig.getValue();
                unionOrderData.setMD5Key(md5Key);
            }
        }
        unionOrderData.setTrxamt(cardOrdersVO.getAmount());
        unionOrderData.setBody(cardOrdersVO.getOrderDesc());
        unionOrderData.setMerchantCode(merchantsConfigVOList.get(0).getMerchantCode());
        logger.info("支付参数为" + unionOrderData);
        Map unionOrderMapData = payClient.unionorderBuyCard(unionOrderData).getData();
        return unionOrderMapData;
    }

    /**
     * 获取 配置支付数据
     *
     * @param merchantCode
     * @return
     */
    public List<MerchantsConfigVO> getMerchantsConfigListResult(String merchantCode) {
        Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
        String chargeType = merchants.getChargeType();
        List<MerchantsConfigVO> result;
        if ("charge_by_entity".equals(chargeType)) {
            if (StringUtils.isEmpty(merchants.getBusinessSubjects())) {
                result = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
            } else {
                result = merchantsConfigClientService.getPayData(merchants.getBusinessSubjects()).getData();
            }
        } else if ("charge_by_store".equals(chargeType)) {
            result = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
        } else {
            throw new CheckException(ResultTypeEnum.CHARGE_TYPE_ERROR);
        }
        return result;
    }

    /**
     * 获取通联 当面付 支付数据 (组合支付)
     *
     * @param unionOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/cuspay")
    public Map cuspay(@RequestBody UnionOrderData unionOrderData) throws Exception {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(unionOrderData.getOrderCode()).getData();
        CardOrderPayTrace traceByOrderCode = orderClientService.getTraceByOrderCode(unionOrderData.getOrderCode()).getData();

        //获取配置的支付数据
        List<MerchantsConfigVO> merchantsConfigVOList = getMerchantsConfigListResult(unionOrderData.getMerchantCode());
        String payType = "CUS_PAY";
        String mchId = "";
        String appId = "";
        String md5Key = "";

        for (MerchantsConfigVO merchantsConfig : merchantsConfigVOList) {
            if ("MCHID".equals(merchantsConfig.getKey())) {
                mchId = merchantsConfig.getValue();
                unionOrderData.setMchId(mchId);
            }
            if ("APPID".equals(merchantsConfig.getKey())) {
                appId = merchantsConfig.getValue();
                unionOrderData.setAppId(appId);
            }
            if ("MD5KEY".equals(merchantsConfig.getKey())) {
                md5Key = merchantsConfig.getValue();
                unionOrderData.setMD5Key(md5Key);
            }
            //获取支付类别
//                if ("PAYTYPE".equals(merchantsConfigVO.getKey())){
//                    payType = merchantsConfigVO.getValue();
//                }
        }
        logger.info("支付参数为" + unionOrderData);

        if ("CUS_PAY".equals(payType)) {
            unionOrderData.setTrxamt(traceByOrderCode.getAmount());
            unionOrderData.setBody(cardOrdersVO.getOrderDesc());
            CuspayData cuspayData = new CuspayData();
            cuspayData.setAmt(traceByOrderCode.getAmount().toString());
            cuspayData.setOid(unionOrderData.getOrderCode());
            cuspayData.setAppId(unionOrderData.getAppId());
            cuspayData.setMerchantCode(merchantsConfigVOList.get(0).getMerchantCode());
            logger.info("传递到pay服务的支付参数为" + cuspayData);
            Map unionOrderMapData = payClient.cuspay(cuspayData).getData();
            return unionOrderMapData;
        } else {
            UnionOrderData H5UnionOrderData = new UnionOrderData();
            H5UnionOrderData.setOrderCode(cardOrdersVO.getOrderCode());
            H5UnionOrderData.setMerchantCode(cardOrdersVO.getMerchantCode());
            H5UnionOrderData.setTrxamt(traceByOrderCode.getAmount());
            H5UnionOrderData.setBody(cardOrdersVO.getOrderDesc());
            H5UnionOrderData.setMchId(mchId);
            H5UnionOrderData.setAppId(appId);
            H5UnionOrderData.setMD5Key(md5Key);
            H5UnionOrderData.setMerchantCode(merchantsConfigVOList.get(0).getMerchantCode());
            Map unionorder = payClient.unionorder(H5UnionOrderData).getData();
            return unionorder;
        }
    }


    /**
     * 获取通联调取 H5 支付数据(充值订单)
     *
     * @param unionOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/unionorderTopUp")
    public Map unionorderTopUp(@RequestBody UnionOrderData unionOrderData) throws Exception {
        CardOrdersVO cardOrdersVO = orderClientService.queryByOrderCode(unionOrderData.getOrderCode()).getData();
        //获取支付数据
        List<MerchantsConfigVO> merchantsConfigVOList = getMerchantsConfigListResult(unionOrderData.getMerchantCode());
        String mchId;
        String appId;
        String md5Key;
        for (MerchantsConfigVO merchantsConfig : merchantsConfigVOList) {
            if ("MCHID".equals(merchantsConfig.getKey())) {
                mchId = merchantsConfig.getValue();
                unionOrderData.setMchId(mchId);
            }
            if ("APPID".equals(merchantsConfig.getKey())) {
                appId = merchantsConfig.getValue();
                unionOrderData.setAppId(appId);
            }
            if ("MD5KEY".equals(merchantsConfig.getKey())) {
                md5Key = merchantsConfig.getValue();
                unionOrderData.setMD5Key(md5Key);
            }
        }
        unionOrderData.setTrxamt(cardOrdersVO.getAmount());
        unionOrderData.setBody(cardOrdersVO.getOrderDesc());
        unionOrderData.setMerchantCode(merchantsConfigVOList.get(0).getMerchantCode());
        logger.info("支付参数为" + unionOrderData);
        Map unionOrderMapData = payClient.unionorderTopUp(unionOrderData).getData();
        return unionOrderMapData;
    }
}

