package com.ht.feignapi.bpmn.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 流程环节用户权限表
 * </p>
 *
 * @author ${author}
 * @since 2020-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class FormUserRoot implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value="id",name="主键",example="1")
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 流程id
     */
    @ApiModelProperty(value="processId",name="流程id",example="1")
    private String processId;

    /**
     * 流程deploymentId
     */
    @ApiModelProperty(value="deploymentId",name="流程deploymentId",example="1")
    private String deploymentId;

    /**
     * 流程名称
     */
    @ApiModelProperty(value="processName",name="流程名称",example="1")
    private String processName;

    /**
     * 流程版本
     */
    @ApiModelProperty(value="processVersion",name="流程版本",example="1")
    private String processVersion;

    /**
     * 流程key
     */
    @ApiModelProperty(value="processKey",name="流程key",example="1")
    private String processKey;

    /**
     * 环节id
     */
    @ApiModelProperty(value="linkId",name="环节id",example="1")
    private String linkId;

    /**
     * 环节名称
     */
    @ApiModelProperty(value="linkName",name="环节名称",example="1")
    private String linkName;

    /**
     * 用户类型
1:组
2:个人
     */
    @ApiModelProperty(value="userType",name="用户类型",example="1")
    private String userType;

    /**
     * 用户id
     */
    @ApiModelProperty(value="userId",name="用户id",example="1")
    private String userId;


}
