package com.ht.feignapi.tonglian.card.controller;

import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.RetPageData;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.InventoryClientService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.RequestQueryOrderData;
import com.ht.feignapi.tonglian.card.entity.UseCardData;
import com.ht.feignapi.tonglian.card.entity.UserPlaceOrderData;
import com.ht.feignapi.tonglian.card.entity.UserTopUpOrderData;
import com.ht.feignapi.tonglian.card.service.CardOrdersService;
import com.ht.feignapi.tonglian.card.service.CardUserService;
import com.ht.feignapi.tonglian.config.CardOrdersStateConfig;
import com.ht.feignapi.tonglian.config.PlaceOrderResult;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.order.client.OrderClientService;
import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import com.ht.feignapi.tonglian.order.entity.CardOrdersVO;
import com.ht.feignapi.tonglian.order.entity.PaySuccess;
import com.ht.feignapi.tonglian.user.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@RequestMapping("/tonglian/cardOrders")
@CrossOrigin(allowCredentials = "true")
public class CardOrdersController {

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private CardOrdersService cardOrdersService;

    @Autowired
    private OrderClientService orderClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private CardUserService cardUserService;

    @Autowired
    private InventoryClientService inventoryClientService;

    /**
     * 分页获取用户订单列表
     *
     * @param requestOrderData
     * @return
     */
    @PostMapping("/list")
    public RetPageData listByUserIdAndMerchantCodeAndType(@RequestBody RequestQueryOrderData requestOrderData) {
        String merchantCode = requestOrderData.getMerchantCode();
        List<Merchants> merchantAndSon = merchantsClientService.getSubMerchants(merchantCode).getData();
        RetPageData retPageData = cardOrdersService.orderListPage(requestOrderData.getOpenid(),
                merchantAndSon,
                requestOrderData.getType(),
                requestOrderData.getState(),
                requestOrderData.getPageNo(),
                requestOrderData.getPageSize());
        return retPageData;
    }

    /**
     * C端买券下单接口
     *
     * @param userPlaceOrderData
     * @return
     */
    @PostMapping("/placeOrder")
    public PlaceOrderResult placeOrder(@RequestBody UserPlaceOrderData userPlaceOrderData) {
        PlaceOrderResult placeOrderResult = cardOrdersService.placeOrder(userPlaceOrderData);
        return placeOrderResult;
    }


    /**
     * C端用户 购买卡券 支付成功 修改订单信息
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/paySuccess")
    public String paySuccess(@RequestBody PaySuccess paySuccess) {
        try {
            String orderCode = paySuccess.getOrderCode();
            CardOrdersVO ordersVO = orderClientService.queryByOrderCode(orderCode).getData();
            String state = ordersVO.getState();
            if (CardOrdersStateConfig.PAID.equals(state)) {
                return "已支付";
            }
            orderClientService.buyCardPaySuccess(paySuccess);

            Map<String,Integer> map = new HashMap<String,Integer>();
            map.put("amount",1);
            for (CardOrderDetails cardOrderDetails: ordersVO.getCardOrderDetailsList()) {
                inventoryClientService.subtractInventory(ordersVO.getMerchantCode(),cardOrderDetails.getProductionCode(),map);
            }
            return "支付成功";
        } catch (Exception e) {
            System.out.println(e);
            return "支付失败";
        }
    }


    /**
     * 用户扫商户码支付下单
     *
     * @param merchantQrCodePayData
     * @return
     */
    @PostMapping("/merchantQrCodePay")
    public PlaceOrderResult merchantQrCodePay(@RequestBody MerchantQrCodePayData merchantQrCodePayData) {
        PlaceOrderResult placeOrderResult = orderClientService.merchantQrCodePlaceOrder(merchantQrCodePayData.getAmount(),
                merchantQrCodePayData.getMerchantCode(),
                merchantQrCodePayData.getPaySource(),
                merchantQrCodePayData.getPayType()).getData();
        return placeOrderResult;
    }

    /**
     * 用户扫商户码支付成功 修改订单数据
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/qrCodePaySuccess")
    public String qrCodePaySuccess(@RequestBody PaySuccess paySuccess) {
        try {
            orderClientService.merchantQrCodePaySuccess(paySuccess);
            return "支付成功";
        } catch (Exception e) {
            return "支付失败";
        }
    }


    /**
     * C端公众号 卡券组合支付 下单
     *
     * @param userCashCardPayOrderData
     * @return
     */
    @PostMapping("/userCashCardPayOrder")
    public Object userCashCardPayOrder(@RequestBody UserCashCardPayOrderData userCashCardPayOrderData) {
        UserUsers usrUsers = authClientService.queryByOpenid(userCashCardPayOrderData.getOpenid()).getData();
        if (usrUsers == null) {
            //非会员用户
            PlaceOrderResult placeOrderResult = orderClientService.merchantQrCodePlaceOrder(userCashCardPayOrderData.getAmount(),
                    userCashCardPayOrderData.getMerchantCode(), "非会员扫商户码支付", "通联支付").getData();
            placeOrderResult.setIsToPay(true);
            placeOrderResult.setUseCardFlag(false);
            placeOrderResult.setUseCardMessage("非会员,无卡券");
            placeOrderResult.setUserAccount(0);
            return placeOrderResult;
        }

        if (StringUtils.isEmpty(userCashCardPayOrderData.getOpenid())) {
            //非会员用户 无openid 直接下单
            PlaceOrderResult placeOrderResult = orderClientService.merchantQrCodePlaceOrder(userCashCardPayOrderData.getAmount(),
                    userCashCardPayOrderData.getMerchantCode(), "非会员扫商户码支付", "通联支付").getData();
            placeOrderResult.setIsToPay(true);
            placeOrderResult.setUseCardFlag(false);
            placeOrderResult.setUseCardMessage("非会员,无卡券");
            placeOrderResult.setUserAccount(0);
            return placeOrderResult;
        }

        List<String> cardNoList = userCashCardPayOrderData.getCardNoList();
        if (cardNoList != null && cardNoList.size() > 0) {
            //判断卡券类型,校验使用规则
            List<PosSelectCardNo> posSelectCardNos = new ArrayList<>();
            for (String cardNo : cardNoList) {
                PosSelectCardNo posSelectCardNo = new PosSelectCardNo();
                posSelectCardNo.setCardNo(cardNo);
                posSelectCardNos.add(posSelectCardNo);
            }
            UseCardData useCardData = cardUserService.checkUseCard(posSelectCardNos, userCashCardPayOrderData.getAmount());
            if (!useCardData.getUseLimitFlag()) {
                throw new CheckException(ResultTypeEnum.CARD_USED_ERROR.getCode(),useCardData.getUseMessage());
            }
        }
        userCashCardPayOrderData.setUserId(usrUsers.getId());
        UserCashCardPayOrderReturn userCashCardPayOrderReturn = orderClientService.userCashCardPayPlaceOrder(userCashCardPayOrderData).getData();
        return userCashCardPayOrderReturn;
    }

    /**
     * C端公众号 组合支付成功 修改订单状态
     *
     * @param paySuccess
     * @return
     */
    @PostMapping("/updateCashCardOrder")
    public String updateCashCardOrder(@RequestBody PaySuccess paySuccess) {
        if (StringUtils.isEmpty(paySuccess.getUserId()) || "-1".equals(paySuccess.getUserId())) {
            //无user_id 非会员支付成功修改订单状态
            orderClientService.merchantQrCodePaySuccess(paySuccess);
            return "支付成功";
        }
        String orderCode = paySuccess.getOrderCode();
        CardOrdersVO ordersVO = orderClientService.queryByOrderCode(orderCode).getData();
        String state = ordersVO.getState();
        if (CardOrdersStateConfig.PAID.equals(state)) {
            return "已支付";
        }
        orderClientService.paySuccessCashCardOrder(paySuccess);
        return "支付成功";
    }

    /**
     * C端公众号 创建充值订单
     *
     * @param userTopUpOrderData
     * @return
     */
    @PostMapping("/userTopUpOrder")
    public String userTopUpOrder(@RequestBody UserTopUpOrderData userTopUpOrderData) {
        UserUsers userUsers = authClientService.queryByOpenid(userTopUpOrderData.getOpenId()).getData();
        userTopUpOrderData.setUserId(userUsers.getId());
        return orderClientService.createUserTopUpOrder(userTopUpOrderData).getData();
    }
}

