package com.ht.feignapi.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.mall.clientservice.OrderWayBillsClientService;
import com.ht.feignapi.mall.constant.OrderWayBillsTypeConstant;
import com.ht.feignapi.mall.entity.*;
import com.ht.feignapi.tonglian.merchant.clientservice.MerchantsClientService;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderWayBillsService {

    @Autowired
    private OrderWayBillsClientService orderWayBillsClientService;

    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private MerchantsClientService merchantsClientService;

    /**
     * 计算 某个商户的 派送费
     * @param merchantCode
     * @param cartTotalMoney
     * @param wayBillsType
     * @return
     */
    public double statementWayBillsMoney(String merchantCode, Integer cartTotalMoney, String wayBillsType) {
        List<OrderWayBillFeeRules> orderWayBillFeeRulesList = orderWayBillsClientService.queryWayBillFeeRules(merchantCode).getData();

        if (orderWayBillFeeRulesList==null || orderWayBillFeeRulesList.size()<1){
            return 0;
        }

        OrderWayBillFeeRules orderWayBillFeeRules;
        //todo 根据规则计算运费 后续完善
        for (OrderWayBillFeeRules orderWayBillFeeRulesSon : orderWayBillFeeRulesList) {
            if (wayBillsType.equals(orderWayBillFeeRulesSon.getType())){
                orderWayBillFeeRules = orderWayBillFeeRulesSon;
                Integer byPercent = orderWayBillFeeRules.getByPercent();
                if (byPercent!=null){
                    double v = byPercent * 0.01;
                    return cartTotalMoney * v;
                }
            }
        }
        return 0;
    }

    /**
     * 获取单个商家 某个类型的配送费
     * @param merchantCode
     * @param wayBillType
     * @return
     */
    public Integer getOneMerchantWayBillFee(String merchantCode,String wayBillType) {
        List<OrderWayBillFeeRules> orderWayBillFeeRulesList = orderWayBillsClientService.queryWayBillFeeRules(merchantCode).getData();
        for (OrderWayBillFeeRules orderWayBillFeeRules : orderWayBillFeeRulesList) {
            if (wayBillType.equals(orderWayBillFeeRules.getType())){
                return orderWayBillFeeRules.getDefaultFee();
            }
        }
        return 0;
    }

    /**
     * 分页展示用户 派送单
     * @param showMyWayBillsData
     * @return
     */
    public Page<OrderWayBills> showMyOrderWayBills(ShowMyWayBillsData showMyWayBillsData) {
        UserUsers userUsers = authClientService.queryByOpenid(showMyWayBillsData.getOpenId()).getData();
        showMyWayBillsData.setUserId(userUsers.getId());
        Page<OrderWayBills> orderWayBillsPage = orderWayBillsClientService.showMyOrderWayBills(showMyWayBillsData).getData();
        List<OrderWayBills> records = orderWayBillsPage.getRecords();
        for (OrderWayBills orderWayBills : records) {
            Integer totalOrderDetailsCount = 0;
            Merchants merchants = merchantsClientService.getMerchantByCode(orderWayBills.getMerchantCode()).getData();
            orderWayBills.setMerchantName(merchants.getMerchantName());
            WayBillPageData wayBillPageData = queryWayBillsProduction(orderWayBills.getId().toString(), orderWayBills.getWayBillCode());
            List<OrderOrderDetails> orderOrderDetailsList = wayBillPageData.getOrderOrderDetailsList();
            orderWayBills.setOrderOrderDetailsList(orderOrderDetailsList);
            Integer totalShowMoney = 0;
            for (OrderOrderDetails orderOrderDetails : orderOrderDetailsList) {
                totalShowMoney = totalShowMoney + (orderOrderDetails.getAmount() - orderOrderDetails.getDiscount());
                totalOrderDetailsCount = totalOrderDetailsCount + orderOrderDetails.getQuantity().intValue();
            }
            orderWayBills.setTotalShowMoney(totalShowMoney + orderWayBills.getBillFee());
            orderWayBills.setState("un_delivered".equals(orderWayBills.getState())? "未发货":"已发货" );
            orderWayBills.setTotalOrderDetailsCount(totalOrderDetailsCount);
        }
        return orderWayBillsPage;
    }

    /**
     * 查询派送单下的商品
     * @param id
     * @param wayBillCode
     * @return
     */
    public WayBillPageData queryWayBillsProduction(String id, String wayBillCode) {
        WayBillPageData wayBillPageData = orderWayBillsClientService.queryWayBillsProduction(id, wayBillCode).getData();
        OrderWayBills orderWayBills = wayBillPageData.getOrderWayBills();
        orderWayBills.setState("un_delivered".equals(orderWayBills.getState())? "未发货":"已发货" );
        return wayBillPageData;
    }
}
