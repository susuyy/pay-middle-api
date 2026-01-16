package com.ht.user.outlets.entity;

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
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-11-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutletsOrdersGoods implements Serializable {

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

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;


}
