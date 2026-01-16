package com.ht.feignapi.tonglian.card.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.prime.client.MSPrimeClient;
import com.ht.feignapi.prime.controller.PrimeUserConsumerController;
import com.ht.feignapi.prime.entity.CardElectronicVo;
import com.ht.feignapi.prime.entity.CardQueryData;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardCardsClientService;
import com.ht.feignapi.tonglian.card.entity.DESDataStr;
import com.ht.feignapi.tonglian.sysconstant.entity.DicConstant;
import com.ht.feignapi.util.DESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 卡定义 前端控制器
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-15
 */
@RestController
@RequestMapping("/tonglian/cards")
@CrossOrigin(allowCredentials = "true")
public class CardCardsController {

    @Autowired
    private CardCardsClientService cardCardsClientService;
    @Autowired
    private DESUtil desUtil;
    @Autowired
    private MSPrimeClient msPrimeClient;

    private Logger logger = LoggerFactory.getLogger(CardCardsController.class);


    /**
     * 获取卡券类别列表
     * @return
     */
    @GetMapping("/cardTypeList")
    public List<DicConstant> getCardTypeList() {
        return cardCardsClientService.getListByGroupCode().getData();
    }


    /**
     * hailv_v1.1.0
     * 海旅(线上商城 预付费卡) 用户卡列表
     * @param desDataStr
     * @return
     */
    @PostMapping("/page")
    public Result getCardsPage(@RequestBody DESDataStr desDataStr){
        String decryptDataStr = null;
        try {
            decryptDataStr = desUtil.decrypt(desDataStr.getDesDataStr());
        } catch (Exception e) {
            throw new CheckException(ResultTypeEnum.DECRYPT_ERROR);
        }

        CardQueryData cardQueryData = JSONObject.parseObject(decryptDataStr, CardQueryData.class);
        Result<Page<CardElectronicVo>> cardsPage = msPrimeClient.getCardsPage(cardQueryData);
        Page<CardElectronicVo> data = cardsPage.getData();
        String dataStr = JSONObject.toJSONString(data);
        String encrypt = desUtil.encrypt(dataStr);
        String userPhone = cardQueryData.getUserPhone();
        logger.info("getCardsPage userPhone={} 响应数据为:{}",userPhone,dataStr);
        logger.info("getCardsPage userPhone={} 加密响应数据为:{}:",userPhone,encrypt);
        return Result.success(encrypt);

//        //service.invoke()
//        JSONObject resultJson = new JSONObject();
//        JSONArray array = new JSONArray();
//        JSONObject subResultJson = new JSONObject();
//        subResultJson.put("id",1);
//        subResultJson.put("userPhone",1);
//        subResultJson.put("cardNo",1);
//        subResultJson.put("cardSta",1);
//        subResultJson.put("cardType",1);
//        subResultJson.put("validFrom",1);
//        subResultJson.put("validityDate",1);
//        subResultJson.put("refBrhId",1);
//        subResultJson.put("batchCode",1);
//        subResultJson.put("accountBalance",1);
//        subResultJson.put("cardProductName",1);
//        subResultJson.put("backGround",1);
//        array.add(subResultJson);
//        resultJson.put("records",array);
//        resultJson.put("total",1120);
//        resultJson.put("size",10);
//        resultJson.put("current",2);
//        resultJson.put("pages",111);
//        return Result.success(resultJson);

    }




}

