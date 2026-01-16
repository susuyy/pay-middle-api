package com.ht.feignapi.bpmn.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 流程与bpmn文件关联表
 * </p>
 *
 * @author ${author}
 * @since 2020-10-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProcessLinkConditionConfigVo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * processId
     */
    @ApiModelProperty(value = "processId",name = "部署的processId",example = "testKey:1:8fff489a-09fd-11eb-9b74-acb57d55f94d")
    private String processId;

    /**
     * 环节id
     */
    @ApiModelProperty(value = "环节id",name = "linkId",example = "sp1")
    private String linkId;

    /**
     * 表单字段名
     */
    @ApiModelProperty(value = "formFieldName",name = "表单字段名",example = "money")
    private String formFieldName;

    /**
     * 判断条件的el表达式
     ${audit >= 1}

     audit:字段名
     */
    @ApiModelProperty(value = "conditionEl",name = "判断条件的el表达式",example = "${audit >= 1}")
    private String conditionEl;


}
