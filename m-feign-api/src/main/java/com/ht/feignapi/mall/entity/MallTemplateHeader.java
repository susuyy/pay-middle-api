package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MallTemplateHeader implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value="模板头id",name="id",example="1")
    private Long id;

    /**
     * 商城编码
     */
    @ApiModelProperty(value="商场编码",name="mallCode",example="KT-MALL-01")
    private String mallCode;

    /**
     * 商户编码
     */
    @ApiModelProperty(value="商户编码",name="merchantCode",example="HLTA_001")
    private String merchantCode;

    /**
     * 商户名称
     */
    @ApiModelProperty(value="商户名",name="merchantName",example="华联天安")
    private String merchantName;

    /**
     * 模板编码
     */
    @ApiModelProperty(value="模板编码",name="templateCode",example="template001")
    private String templateCode;

    /**
     * 模板名称
     */
    @ApiModelProperty(value="模板名称",name="templateName",example="模板01")
    private String templateName;

    /**
     * 模板版本
     */
    @ApiModelProperty(value="模板版本",name="version",example="1.0")
    private String version;

    /**
     * 模板状态
     */
    @ApiModelProperty(value="模板状态",name="state",example="1")
    private String state;

    /**
     * 模板类型
     */
    @ApiModelProperty(value="模板类型",name="type",example="CATEGORY")
    private String type;

    /**
     * 商城展示名称
     */
    @ApiModelProperty(value="商城展示名称",name="mallDisplayName",example="华联天安商城")
    private String mallDisplayName;

    /**
     * 商城头像地址
     */
    @ApiModelProperty(value="商城头像地址",name="mallIconUrl",example="http://hlta/xxx.png")
    private String mallIconUrl;

    /**
     * 商城主图地址
     */
    @ApiModelProperty(value="商城主图地址",name="mallBackgroupUrl",example="http://hlta/xxx.png")
    private String mallBackgroupUrl;

    @ApiModelProperty(value="创建时间",name="createAt",example="2020-08-26 15:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date createAt;

    @ApiModelProperty(value="更新时间",name="updateAt",example="2020-08-26 15:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date updateAt;

    @ApiModelProperty(value="商城模板字段描述",name="typeStr",example="banner轮播图")
    @TableField(exist = false)
    private String typeStr;

    @ApiModelProperty(value="商城模板详情",name="mallTemplateDetailList",example="")
    @TableField(exist = false)
    private List<MallTemplateDetail> mallTemplateDetailList;

}
