package com.ht.feignapi.bpmn.entity;

import lombok.Data;
import org.bouncycastle.operator.MacCalculatorProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class CreateActivityDataVo implements Serializable {

    //提交人
    private String userId;

    //电话
    private String userTel;

    //实例Id
    private String processId;

   //审批意见
    private String opinionKeyValue;

    //变更表单的编号
    private String relationListCode;

    //附件
    private List<Map<String,Object>> enclosureKeyValue;

    //部门
    private String departmentName;

    //场景名称
    private String typeName;
    //场景类型
    private String typeCode;


    //表单模板Code及数据
    private Map<String,Object> dataMap;



}
