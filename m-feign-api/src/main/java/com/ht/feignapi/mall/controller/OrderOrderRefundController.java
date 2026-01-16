package com.ht.feignapi.mall.controller;


import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.MallAppShowClientService;
import com.ht.feignapi.mall.clientservice.MallOrderClientService;
import com.ht.feignapi.mall.clientservice.MallOrderRefundClient;
import com.ht.feignapi.mall.clientservice.OrderCategoriesClientService;
import com.ht.feignapi.mall.constant.CardUserMallConstant;
import com.ht.feignapi.mall.constant.OrderConstant;
import com.ht.feignapi.mall.constant.ProductionsCategoryConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.mall.service.OrderRefundService;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.card.clientservice.CardMapUserClientService;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import com.ht.feignapi.tonglian.config.UserCardsStateConfig;
import com.ht.feignapi.tonglian.config.UserCardsTypeConfig;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import com.ht.feignapi.tonglian.user.service.UserUsersService;
import com.ht.feignapi.tonglian.utils.AliMsgSendUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2021-01-13
 */
@RestController
@RequestMapping(value = "/mall/orderRefund",produces={"application/json; charset=UTF-8"})
@CrossOrigin(allowCredentials = "true")
public class OrderOrderRefundController {

    private final static Logger logger = LoggerFactory.getLogger(OrderOrderRefundController.class);

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MallOrderRefundClient mallOrderRefundClient;

    @Autowired
    private CardMapUserClientService cardMapUserClientService;

    @Autowired
    private OrderRefundService orderRefundService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    @Autowired
    private UserUsersService userUsersService;

    /**
     * 提交退款单
     * @param refundListData
     * @return
     */
    @PostMapping("/addOrderRefund")
    public void addOrderRefund(@RequestBody RefundListData refundListData){
        List<OrderOrderDetails> orderOrderDetailsList = refundListData.getOrderOrderDetailsList();
        if (orderOrderDetailsList==null || orderOrderDetailsList.size()<=0){
            throw new CheckException(ResultTypeEnum.DATA_NULL);
        }
        mallOrderRefundClient.addOrderRefund(refundListData);
        cardMapUserClientService.updateRefundState(refundListData.getOrderOrderDetailsList());
        try {
            //暂留阿甘电话
            logger.info("********************发送退款短信******************");
            AliMsgSendUtil.sendNotifyMsg("15108951532","SMS_212275766");
        }catch (ClientException e){
            logger.error("发送短信失败: " + e.toString());
        }
    }

    /**
     * 查询一笔订单可退款的details
     * @param orderCode
     * @return
     */
    @GetMapping("/queryRefundOrderDetails")
    public List<OrderOrderDetails> queryRefundOrderDetails(@RequestParam("orderCode")String orderCode){
        return orderRefundService.queryRefundOrderDetailsList(orderCode);
    }

    /**
     * check 一笔订单是否还有可退明细
     * @param orderCode
     * @return
     */
    @GetMapping("/checkRefundOrderDetails")
    public boolean checkRefundOrderDetails(@RequestParam("orderCode")String orderCode){
        return orderRefundService.checkRefundOrderDetails(orderCode);
    }

    /**
     * 获取主体的退款订单分页信息
     * @param objMerchantCode 主体id
     * @return
     */
    @GetMapping("/{objMerchantCode}")
    public Result<Page<OrderRefund>> getOrderRefundList(
            @PathVariable("objMerchantCode") String objMerchantCode,
            @RequestParam(value = "pageNo",defaultValue = "0",required = false) Long pageNo,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) Long pageSize){
        List<Merchants> merchantsList = merchantsClientService.getSubMerchants(objMerchantCode).getData();
        Result<Page<OrderRefund>> refundResult = mallOrderRefundClient.getRefundList(merchantsList.stream().map(Merchants::getMerchantCode).collect(Collectors.toList()),pageNo ,pageSize );
        for (OrderRefund orderRefund : refundResult.getData().getRecords()) {
            Assert.notNull(orderRefund.getUserId(),"退款单数据有误！UserId不存在");
            UserUsers userUsers = authClientService.getUserByIdTL(orderRefund.getUserId().toString()).getData();
            orderRefund.setUserName(userUsers.getNickName());

            Merchants merchants = merchantsClientService.getMerchantByCode(orderRefund.getMerchantCode()).getData();
            orderRefund.setMerchantName(merchants.getMerchantName());
        }
        return refundResult;
    }


    /**
     * 退款某个详情
     * @param refundDetailId 退款详情id
     * @return
     */
    @PutMapping("/{refundDetailId}")
    public Result doRefund(@PathVariable("refundDetailId") Long refundDetailId){
        Result<OrderRefundDetails> detailsResult = mallOrderRefundClient.getRefundDetailById(refundDetailId);
        Assert.isTrue(detailsResult.getCode().equals(ResultTypeEnum.SERVICE_SUCCESS.getCode()),"请求refundDetail信息失败");
        Assert.notNull(detailsResult.getData(),"非法refundDetailId");
        if (orderRefundService.doRefund(detailsResult.getData())){
            return Result.success("退款成功");
        }
        return Result.error("退款失败");
    }
}

