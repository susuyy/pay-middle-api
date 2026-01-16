package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 卡定义
 * </p>
 *
 * @author ${author}
 * @since 2020-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OrderProductions implements Serializable {

    private static final long serialVersionUID=1L;

    private Long id;

    /**
     * 上下架状态：默认：N 下架，Y 上架
     */
    private String onSaleState;

    /**
     * 主体编码
     */
    private String merchantCode;

    /**
     * 商品编码
     */
    private String productionCode;

    /**
     * 商品名称
     */
    private String productionName;

    /**
     * 分类编码
     */
    private String categoryCode;

    /**
     * 分类编码
     */
    private String categoryLevel02Code;

    /**
     * 分类编码
     */
    private String categoryLevel03Code;

    /**
     * 分类名称
     */
    private String categoryLevel02Name;

    /**
     * 分类名称
     */
    private String categoryLevel03Name;

    /**
     * 产品库存
     */
    @TableField(exist = false)
    private Integer inventory;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 商品图片
     */
    private String productionPicUrl;

    /**
     * 卡状态
     */
    private String state;

    /**
     * 卡类型
     */
    private String type;

    /**
     * 卡面值：单位：分
     */
    private Integer faceValue;

    /**
     * 卡价格：单位： 分
     */
    private Integer price;

    /**
     * 有效时长：单位：小时
     */
    private Integer periodOfValidity;

    /**
     * 有效时间分类
     */
    private String validityType;

    /**
     * 有效开始时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone="GMT+8")
    private Date validFrom;

    /**
     * 有效结束时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone="GMT+8")
    private Date validTo;

    /**
     * 领取后几天生效: 单位：小时
     */
    private Integer validGapAfterApplied;

    /**
     * 是否可转移: 默认：N 不可转移， Y 可转移
     */
    private String flagTransfer;

    /**
     * 次数卡：卡次数
     */
    private Integer batchTimes;

    /**
     * 单位
     */
    private String unit;

    /**
     * 使用须知
     */
    private String notice;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateAt;

    private Integer amount;

    private Integer limitAmountPerOrder;

    private Integer limitAmountTotal;

    private Integer points;

    /**
     * 商品富文本信息
     */
    private String detail;

    private List<String> instruments;
}
