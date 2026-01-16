package com.ht.user.mall.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单明细
 * </p>
 *
 * @author ${author}
 * @since 2020-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderShoppingCartDetails implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 订单编码
     */
    private String orderCode;

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
     * 活动编码   - 对应原批次号
     */
    private String activityCode;

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
    private Integer discount;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;


}
