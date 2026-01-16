package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 订单明细
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderOrderDetails implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type= IdType.AUTO)
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
     * 商户名称 (数据库排除字段)
     */
    private String merchantName;

    /**
     * 用户 id
     */
    private Long userId;

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
    private String activityCode;

    /**
     * 关联的购物车 编码
     */
    private String shoppingCartOrderCode;

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

    /**
     * 商品顶级分类
     */
    @TableField(exist = false)
    private String oneLevelCategoryCode;

    /**
     * 商品图片
     */
    @TableField(exist = false)
    private String productionUrl;

    /**
     * 商品图片
     */
    @TableField(exist = false)
    private String comments;

    /**
     * 商品图片集合
     */
    private List<String> productionPicUrlList;

    /**
     *  mall_productions 表的id字段
     */
    private String mallProductionsId;

    private OrderWayBills orderWayBill;

    @TableField(exist = false)
    private Long mapUserCardId;

    @TableField(exist = false)
    private String mapUserCardNo;

    @TableField(exist = false)
    private String refundState;

    private String processState;
}
