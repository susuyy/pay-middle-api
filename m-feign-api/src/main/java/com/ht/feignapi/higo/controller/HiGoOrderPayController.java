package com.ht.feignapi.higo.controller;

import com.ht.feignapi.higo.constant.WxConstant;
import com.ht.feignapi.higo.entity.ShowPayOrderMessage;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.clientservice.MallPayClientService;
import com.ht.feignapi.mall.constant.MerchantChargeTypeConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.prime.entity.PrimeBuyCardData;
import com.ht.feignapi.prime.entity.RetCheckQuantityData;
import com.ht.feignapi.prime.entity.VipUser;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.entity.CardCards;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import com.ht.feignapi.tonglian.user.controller.UserUsersController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/higo/orderPay")
@CrossOrigin(allowCredentials = "true")
public class HiGoOrderPayController {

    @Autowired
    private MallOrderClientService mallOrderClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private MSPrimeClient msPrimeClient;

    @Autowired
    private MallPayClientService mallPayClientService;

    @Autowired
    private MerchantsConfigClientService merchantsConfigClientService;

    private Logger logger = LoggerFactory.getLogger(HiGoOrderPayController.class);

    /**
     * 根据订单编码 获取订单明细集合
     *
     * @param orderCode
     * @return
     */
    @GetMapping("/queryPayOrderData")
    public List<ShowPayOrderMessage> queryPayOrderData(@RequestParam("orderCode") String orderCode) {
        OrderOrders orderOrders = mallOrderClientService.queryOrderByOrderCode(orderCode).getData();
        List<OrderOrderDetails> orderDetailsList = mallOrderClientService.queryDetailByOrderCode(orderCode).getData();

        //商家筛选
        Set<String> merchantCodeSet = new HashSet<>();
        for (OrderOrderDetails orderOrderDetails : orderDetailsList) {
            merchantCodeSet.add(orderOrderDetails.getMerchantCode());
        }

        List<ShowPayOrderMessage> showPayOrderMessageList = new ArrayList<>();
        for (String merchantCode : merchantCodeSet) {

            ShowPayOrderMessage showPayOrderMessage = new ShowPayOrderMessage();

            Merchants merchants = merchantsClientService.getMerchantByCode(merchantCode).getData();
            showPayOrderMessage.setMerchants(merchants);
            List<OrderOrderDetails> showPayOrderDetails = new ArrayList<>();
            for (OrderOrderDetails orderOrderDetails : orderDetailsList) {
                if (merchants.getMerchantCode().equals(orderOrderDetails.getMerchantCode())){
                    CardElectronicSell cardElectronicSell = msPrimeClient.queryBatchSellByCode(orderOrderDetails.getProductionCode()).getData();
                    orderOrderDetails.setProductionUrl(cardElectronicSell.getBackGround());
                    showPayOrderDetails.add(orderOrderDetails);
                }
            }
            showPayOrderMessage.setOrderOrderDetailsList(showPayOrderDetails);
            showPayOrderMessageList.add(showPayOrderMessage);
        }

        return showPayOrderMessageList;
    }

    /**
     * 获取通联调取 H5 支付数据 (商城购物)
     *
     * @param payOrderData
     * @return
     * @throws Exception
     */
    @PostMapping("/unionOrderPayData")
    public Map unionOrderPayData(@RequestBody PayOrderData payOrderData) throws Exception {

        OrderOrders orderOrders = mallOrderClientService.queryOrderByOrderCode(payOrderData.getOrderCode()).getData();
        List<OrderOrderDetails> orderOrderDetailsList = mallOrderClientService.queryDetailByOrderCode(payOrderData.getOrderCode()).getData();

        if (orderOrderDetailsList==null || orderOrderDetailsList.size()<=0){
            throw new CheckException(ResultTypeEnum.NOT_PRODUCTION);
        }
        VipUser vipUser = msPrimeClient.queryUserById(orderOrders.getUserId() + "").getData();

        // 校验库存 与 账户总额
        PrimeBuyCardData primeBuyCardData = new PrimeBuyCardData();
        primeBuyCardData.setOpenId(vipUser.getOpenid());
        primeBuyCardData.setUserId(vipUser.getId()+"");
        primeBuyCardData.setUserPhone(vipUser.getPhoneNum());

        List<CardElectronicSell> cardElectronicSellList = new ArrayList<>();
        for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
            CardElectronicSell cardElectronicSell = msPrimeClient.queryBatchSellByCode(orderOrderDetails.getProductionCode()).getData();
            cardElectronicSellList.add(cardElectronicSell);
        }
        primeBuyCardData.setCardElectronicSellList(cardElectronicSellList);
        primeBuyCardData.setRead(true);

        RetCheckQuantityData retCheckQuantityData = msPrimeClient.checkQuantity(primeBuyCardData).getData();
        if (!retCheckQuantityData.isUserAccountFlag()){
            throw new CheckException(ResultTypeEnum.LIMIT_USER_ACCOUNT);
        }

        if (!retCheckQuantityData.isQuantityFlag()){
            throw new CheckException(ResultTypeEnum.NOT_INVENTORY);
        }

        //获取配置的支付数据
        List<MerchantsConfigVO> merchantsConfigVOList = getMerchantsConfigListResult(payOrderData.getMerchantCode());
        String mchId;
        String appId;
        String md5Key;
        for (MerchantsConfigVO merchantsConfigVO : merchantsConfigVOList) {
            if ("MCHID".equals(merchantsConfigVO.getKey())) {
                mchId = merchantsConfigVO.getValue();
                payOrderData.setMchId(mchId);
            }
            if ("APPID".equals(merchantsConfigVO.getKey())) {
                appId = merchantsConfigVO.getValue();
                payOrderData.setAppId(appId);
            }
            if ("MD5KEY".equals(merchantsConfigVO.getKey())) {
                md5Key = merchantsConfigVO.getValue();
                payOrderData.setMD5Key(md5Key);
            }
        }
        Integer discount = 0;
        //创建预支付流水
        mallOrderClientService.saveOrderPayTrace(orderOrders.getOrderCode(),
                0L,
                "",
                OrderConstant.SHOP_TYPE,
                OrderConstant.UNPAID_STATE,
                "allinpay_H5",
                "",
                orderOrders.getAmount() - discount,
                "");

        payOrderData.setTrxamt(orderOrders.getAmount() - discount);
        payOrderData.setBody(orderOrders.getComments());
        payOrderData.setAcct(vipUser.getOpenid());
        payOrderData.setSub_appid(WxConstant.appid);
        logger.info("支付参数为" + payOrderData);
        //拉取支付数据
        Map unionOrderMapData = mallPayClientService.mallUnionOrderBuyApiWeb(payOrderData).getData();
        unionOrderMapData.put("toPay",true);
        unionOrderMapData.put("message","创建支付流水成功,可支付");
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
        List<MerchantsConfigVO> list;
        if (MerchantChargeTypeConstant.CHARGE_BY_ENTITY.equals(chargeType)) {
            if (StringUtils.isEmpty(merchants.getBusinessSubjects())) {
                list = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
            } else {
                list = merchantsConfigClientService.getPayData(merchants.getBusinessSubjects()).getData();
            }
        } else if (MerchantChargeTypeConstant.CHARGE_BY_STORE.equals(chargeType)) {
            list = merchantsConfigClientService.getPayData(merchants.getMerchantCode()).getData();
        } else {
            throw new CheckException(ResultTypeEnum.CHARGE_TYPE_ERROR);
        }
        return list;
    }

}
