package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
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
@ApiModel(value="模板对象",description="模板对象MallProductions")
public class MallTemplateDetail implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value="id",name="id",example="1")
    @TableId(type = IdType.AUTO)
    private  Long id;

    /**
     * 模板编码
     */
    @ApiModelProperty(value="模板编码",name="templateCode",example="template_main01")
    private String templateCode;

    /**
     * 模块编码
     */
    @ApiModelProperty(value="模块编码",name="modelCode",example="banner")
    private String modelCode;

    /**
     * 模块名称
     */
    @ApiModelProperty(value="模块名称",name="modelName",example="广告位")
    private String modelName;

    /**
     * 模块状态
     */
    @ApiModelProperty(value="模块状态",name="modelState",example="1")
    private String modelState;

    /**
     * 模块类型
     */
    @ApiModelProperty(value="模块类型",name="modelType",example="BANNER")
    private String modelType;

    /**
     * 模块子项key
     */
    @ApiModelProperty(value="模块子项key",name="key",example="banner01")
    @TableField("`key`")
    private String key;

    /**
     * 模块子项value
     */
    @ApiModelProperty(value="模块子项value",name="value",example="banner01")
    @TableField("`value`")
    private String value;

    /**
     * 模块子项显示内容
     */
    @ApiModelProperty(value="模块子项显示内容",name="displayName",example="")
    private String displayName;

    /**
     * 模块子项显示列表类型
     */
    @ApiModelProperty(value="模块子项显示列表类型",name="refListType",example="")
    private String refListType;

    /**
     * 模块图片url
     */
    @ApiModelProperty(value="模块图片url",name="url",example="https://hlta-mall.oss-cn-shenzhen.aliyuncs.com/KTYKT/101.png")
    private String url;

    /**
     * 模块子项排序
     */
    @ApiModelProperty(value="模块子项排序",name="sortNum",example="1")
    private Integer sortNum;

    /**
     * 模块子项状态
     */
    @ApiModelProperty(value="模块子项状态",name="state",example="1")
    private String state;

    private Date createAt;

    private Date updateAt;

    @TableField(exist = false)
    private String modelTypeStr;

    @TableField(exist = false)
    private List<MallShops> mallShopsList;

    @TableField(exist = false)
    private List<MallProductions> mallProductionsList;

}
