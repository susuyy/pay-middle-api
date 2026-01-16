package com.ht.user.outlets.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单主表
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutletsOrders implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
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
     * 金额：默认单位：分
     */
    private Integer amount;

    /**
     * 描述
     */
    private String comments;

    /**
     * 折扣：默认单位：分
     */
    private Integer discount;

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
     * 调用的支付渠道的api
     */
    private String channelApi;


    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;


}
