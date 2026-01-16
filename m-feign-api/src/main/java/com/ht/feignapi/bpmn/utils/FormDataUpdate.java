package com.ht.feignapi.bpmn.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Liwg
 * @Date: 2020/11/20 11:08
 */
public class FormDataUpdate {

    public static List<Map<String,Object>> formDataUpdate(List<Map<String,String>> formConfigList
            ,Map<String,Object> dataMap
            ,String taskId
            ,String userKey
            ,String processInstanceId){
        List<Map<String,Object>> resList = new ArrayList<>();
        for(int i=0;i<formConfigList.size();i++){
            Map<String,Object> updateFormField = new HashMap<>();
            updateFormField.put("formTemplateCode",formConfigList.get(i).get("formTemplateCode"));
            updateFormField.put("formCode",processInstanceId);
            updateFormField.put("formType",formConfigList.get(i).get("formType"));
            updateFormField.put("formFields",dataMap.get(formConfigList.get(i).get("formTemplateCode")));
            if(formConfigList.get(i).get("ifOnly").equals("1")){
                updateFormField.put("taskId","");
            }else{
                updateFormField.put("taskId",taskId);
            }
            updateFormField.put("userId",userKey);
            resList.add(updateFormField);
        }
        return resList;
    }

}
