package com.ht.user.outlets.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/22 10:14
 */
@Data
public class QueryOutletsOrdersVO implements Serializable {

    /**
     * 页号
     */
    private Integer pageNo;

    /**
     * 页大小
     */
    private  Integer pageSize;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 订单类型:后台充值订单admin_recharge,后台调账admin_adjust,购物订单shop,
     consume核销订单；
     prime_buy_card免税店购卡订单；
     */
    private String type;

    /**
     * 订单状态
     */
    private String state;

    /**
     * 购买人手机号
     */
    private String actualPhone;

    /**
     * 银行卡号
     */
    private String sourceId;

    /**
     * 收银台号
     */
    private String cashId;

    /**
     * 支付凭证号
     */
    private String payCode;

    /**
     * 开始日期
     */
    private String startCreateAt;

    /**
     * 结束日期
     */
    private String endCreateAt;


}
