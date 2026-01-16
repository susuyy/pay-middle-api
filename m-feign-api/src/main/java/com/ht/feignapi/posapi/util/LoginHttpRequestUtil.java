package com.ht.feignapi.posapi.util;


import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.admin.entity.LoginVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LoginHttpRequestUtil {


    public Object requestLoginHttp(LoginVo loginVo){
        String loginSystemUrl = "";

        if ("HIGO".equals(loginVo.getSystemCode())){
            loginSystemUrl = "https://test-global.allinpayhk.com/m-feign-api/tonglian/adminLogin/login";
        }else if ("HLMSD".equals(loginVo.getSystemCode())){
            loginSystemUrl = "https://gateway.hualta.com/m-feign-api/tonglian/adminLogin/login";
        }else {
            throw new CheckException(ResultTypeEnum.SERVICE_ERROR);
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<LoginVo> request = new HttpEntity<>(loginVo, headers);
        ResponseEntity<Result> result = restTemplate.postForEntity(loginSystemUrl,
                request, Result.class);

        return result.getBody();
    }

}
