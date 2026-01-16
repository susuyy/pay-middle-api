package com.ht.user.outlets.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2021/11/22 10:19
 */
@Data
public class OutletsOrdersVO implements Serializable {

    private Long id;

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
     * 商户编号
     */
    private String merchantCode;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 售货员
     */
    private String saleId;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 金额 元
     */
    private BigDecimal amount;

    /**
     * 描述
     */
    private String comments;

    /**
     * 折扣 元
     */
    private BigDecimal discount;

    /**
     * 限制支付类型
     */
    private String limitPayType;

    /**
     * 门店编码
     */
    private String storeCode;

    /**
     * 购买人手机号
     */
    private String actualPhone;

    /**
     * 身份证编号
     */
    private String idCardNo;

    /**
     * 实付金额
     */
    private BigDecimal actualAmount;

    /**
     * 收银台号
     */
    private String cashId;

    /**
     * 商户名
     */
    private String merchName;


    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

}
