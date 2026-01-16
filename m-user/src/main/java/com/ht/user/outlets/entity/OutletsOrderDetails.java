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
 * 订单明细
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutletsOrderDetails implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 金额：默认单位：分
     */
    private Integer amount;

    /**
     * 商品编码
     */
    private String productionCode;

    /**
     * 商品名称
     */
    private String productionName;

    /**
     * 商品分类编码
     */
    private String productionCategoryCode;

    /**
     * 商品分类名称
     */
    private String productionCategoryName;

    /**
     * 批次号
     */
    private String batchCode;

    /**
     * 状态
     */
    private String state;

    /**
     * 订单明细分类
     */
    private String type;

    /**
     * 折扣：默认单位：分
     */
    private Integer disccount;

    /**
     * 手机号
     */
    private String userPhone;

    /**
     * 卡类型
     */
    private String cardType;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;


}
