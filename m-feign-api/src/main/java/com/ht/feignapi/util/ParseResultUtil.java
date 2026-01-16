package com.ht.feignapi.util;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.result.Result;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ParseResultUtil<T> {

    public T parseResult(Result<T> result,Class clazz){
        String string = JSONObject.toJSONString(result.getData());
        return (T) JSONObject.parseObject(string, clazz);
    }

    public T parse(Result<T> result,Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return result.getData();
//        T res = clazz.newInstance();
//        BeanUtils.copyProperties(result.getData(),res);
//        return res;
    }
}
