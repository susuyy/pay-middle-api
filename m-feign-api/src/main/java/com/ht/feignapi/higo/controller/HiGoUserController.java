package com.ht.feignapi.higo.controller;

import com.ht.feignapi.higo.entity.HiGoReqUserData;
import com.ht.feignapi.higo.entity.ShowPayOrderMessage;
import com.ht.feignapi.higo.service.HiGoUserService;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.clientservice.MallPayClientService;
import com.ht.feignapi.mall.constant.MerchantChargeTypeConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.entity.OrderOrderDetails;
import com.ht.feignapi.mall.entity.OrderOrders;
import com.ht.feignapi.mall.entity.PayOrderData;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.prime.entity.VipUser;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsConfigClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/higo/user")
@CrossOrigin(allowCredentials = "true")
public class HiGoUserController {


    private Logger logger = LoggerFactory.getLogger(HiGoUserController.class);

    @Autowired
    private HiGoUserService hiGoUserService;

    /**
     * 根据订单编码 获取订单明细集合
     *
     * @param hiGoReqUserData
     * @return
     */
    @GetMapping("/userInfo")
    public VipUser userInfo(@RequestBody HiGoReqUserData hiGoReqUserData) {
       return hiGoUserService.userInfo(hiGoReqUserData);
    }



}
