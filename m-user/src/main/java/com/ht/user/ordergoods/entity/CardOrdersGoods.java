package com.ht.user.ordergoods.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-09-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CardOrdersGoods implements Serializable {

    private static final long serialVersionUID=1L;


    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 柜组编码
     */
    private String goodsGroupCode;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 品牌编码
     */
    private String brandCode;

    /**
     * 商品编码
     */
    private String goodsCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 商品价格(单位分,单价)
     */
    private Integer goodsPrice;

    /**
     * 商品优惠金额(单位分)
     */
    private Integer goodsDiscount;

    /**
     * 商品实付金额(单位分)
     */
    private Integer goodsPayPrice;

    /**
     * 商品促销活动类型
     */
    private String goodsActivityType;

    private Date createAt;

    private Date updateAt;


}
