package com.ht.feignapi.bpmn.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Liwg
 * @Date: 2020/9/23 18:39
 */
@Data
public class BpmnMessageVo implements Serializable {

    @ApiModelProperty(value="bpmnFileUrl",name="bpmn文件路径",example="processes/travelTest.bpmn20.xml")
    private String bpmnFileUrl;

    @ApiModelProperty(value="bpmnName",name="bpmn流程名称",example="差旅报销测试流程")
    private String bpmnName;
}
