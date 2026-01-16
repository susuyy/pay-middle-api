package com.ht.feignapi.appshow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ht.feignapi.tonglian.card.entity.CardLimits;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

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
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="券表",description="券对象MallCoupon")
public class MallCoupon implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value="id",name="id",example="1")
    @TableId(type = IdType.AUTO)
    private  Long id;

    /**
     * 商场编码
     */
    private String mallCode;

    /**
     * 券code
     */
    private String couponCode;

    /**
     * 券名称
     */
    private String couponName;

    /**
     * 券类型
     */
    private String couponType;

    /**
     * 券图片
     */
    private String couponUrl;

    /**
     * 商家code
     */
    private String merchantCode;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 热门与热门排序
0:非热门
其它数字热门：排序
数字越大排序越靠前
     */
    private String hotNum;

    private String category;

    private Integer inventory;

    /**
     * 券排序：数字越大越靠前
     */
    private String orderNum;

    /**
     * 券的详情，关联dic_coupon_lable表
     */
    private String detailsCode;

    /**
     * 有效开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GTM+8")
    private Date beginTime;

    /**
     * 有效截止时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GTM+8")
    private Date endTime;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date createAt;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date updateAt;

    private String state;

    private String limitForShow;

    private Integer faceValue;

    private String validTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date validFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date validTo;

    private List<CardLimits> limitsList;


}
