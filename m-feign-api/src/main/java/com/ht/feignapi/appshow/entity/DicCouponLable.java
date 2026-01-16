package com.ht.feignapi.appshow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 标签表
 * </p>
 *
 * @author ${author}
 * @since 2020-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="券标签表",description="券标签对象DicCouponLable")
public class DicCouponLable implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value="id",name="id",example="1")
    @TableId(type = IdType.AUTO)
    private  Long id;

    /**
     * 商场code
     */
    @ApiModelProperty(value="商场code",name="mallCode",example="KT-MALL-01")
    private String mallCode;

    /**
     * 券类型
PRODUCTION：商品
SHOP：商家
     */
    @ApiModelProperty(value="券类型",name="PRODUCTION",example="KT-MALL-01")
    private String couponType;

    /**
     * 券属于
例如：券属于商品id
券属于商家id
     */
    @ApiModelProperty(value="券属于",name="couponBelong",example="1")
    private Integer couponBelong;

    /**
     * 券详情的key值
     */
    @ApiModelProperty(value="券详情的key值",name="couponKey",example="限商家")
    private String couponKey;

    /**
     * 详情的值
     */
    @ApiModelProperty(value="详情的值",name="couponValue",example="仅限在Timmy的餐厅使用")
    private String couponValue;

    /**
     * 排序
     */
    @ApiModelProperty(value="排序",name="sortNum",example="1")
    private String sortNum;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间",name="createAt",example="2020-09-10 14:28:18")
    private LocalDateTime createAt;

    /**
     * 更新时间
     */
    @ApiModelProperty(value="更新时间",name="updateAt",example="2020-09-10 14:28:18")
    private LocalDateTime updateAt;


}
