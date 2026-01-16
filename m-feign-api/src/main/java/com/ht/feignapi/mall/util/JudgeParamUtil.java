package com.ht.feignapi.mall.util;

import java.util.List;
import java.util.Map;

/**
 * @Author: Liwg
 * @Date: 2020/9/4 10:29
 * 参数判断
 */
public class JudgeParamUtil {

    private  boolean code;
    private  String message;


    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public boolean getCode() {
        return code;
    }

    public void setCode(boolean code) {
        this.code = code;
    }

    public JudgeParamUtil(boolean code, String message){
        this.code = code;
        this.message = message;
    }
    public JudgeParamUtil(boolean code){
        this.code = code;
    }

    /**
     * 判断参数是否不存在
     * @param paramList
     * @param paramMap
     * @return
     */
    public static  JudgeParamUtil missParams(List<String> paramList, Map<String,String> paramMap){
        for(String param:paramList){
            if(!paramMap.containsKey(param)){
                return new JudgeParamUtil(false,"缺少参数："+param);
            }
        }
        return new JudgeParamUtil(true);
    }
    /**
     * 判断参数值是否为空 空false
     * @param paramList
     * @param paramMap
     * @return
     */
    public static  JudgeParamUtil valueMissParams(List<String> paramList, Map<String,String> paramMap){
        for(String param:paramList){
            if(paramMap.get(param)== ""){
                return new JudgeParamUtil(false,"缺少参数："+param);
            }
        }
        return new JudgeParamUtil(true);
    }
}
