package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderWayBills implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    private Long id;

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 运单编号
     */
    private String wayBillCode;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String county;

    /**
     * 地址
     */
    private String address;

    /**
     * 电话
     */
    private String tel;

    /**
     * 名称
     */
    private String name;

    /**
     * 运费：单位：分
     */
    private Integer billFee;

    /**
     * 类型
     */
    private String type;

    /**
     * 状态
     */
    private String state;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 商户名称
     */
    @TableField(exist = false)
    private String merchantName;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;

    /**
     * 商品集合
     */
    @TableField(exist = false)
    private List<OrderOrderDetails> orderOrderDetailsList;

    /**
     * 展示金额
     */
    @TableField(exist = false)
    private Integer totalShowMoney;

    /**
     * 商品总数
     */
    @TableField(exist = false)
    private Integer totalOrderDetailsCount;
}
