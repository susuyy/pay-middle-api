package com.ht.feignapi.bpmn.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class SubmitActivityData implements Serializable {

    @ApiModelProperty(value="userId",name="当前审批人",example="test_001")
    private String userId;

    @ApiModelProperty(value="userTel",name="当前审批人电话",example="18888888888")
    private String userTel;

    @ApiModelProperty(value="userGroup",name="当前审批人所属组",example="WBJ-01-BZY")
    private String userGroup;

    /**
     * 审核  1通过    0驳回   -1结束
     */
    @ApiModelProperty(value="audit",name="是否通过",example="1")
    private String audit;

    @ApiModelProperty(value="typeCode",name="流程类型Code",example="CL-001")
    private String typeCode;

    @ApiModelProperty(value="departmentName",name="部门名称",example="技术部")
    private String departmentName;

    @ApiModelProperty(value="typeName",name="流程类型名称",example="差旅报销")
    private String typeName;


    @ApiModelProperty(value="opinionKeyValue",name="审批意见Key",example="opinionKey001")
    private String opinionKeyValue;


    //附件

    @ApiModelProperty(value="enclosureKeyValue",name="附件",example="opinionKey001")
    private List<Map<String,Object>> enclosureKeyValue;


    @ApiModelProperty(value="processInstanceId",name="流程实例id",example="processInstanceId001")
    private String processInstanceId;


    @ApiModelProperty(value="dataMap",name="表单模板Code及数据",example="")
    private Map<String,Object> dataMap;


}
