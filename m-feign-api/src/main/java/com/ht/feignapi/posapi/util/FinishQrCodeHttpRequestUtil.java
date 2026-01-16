package com.ht.feignapi.posapi.util;


import com.ht.feignapi.posapi.entity.FinishInterestSpecData;
import com.ht.feignapi.result.CheckException;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tonglian.admin.entity.LoginVo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FinishQrCodeHttpRequestUtil {


    public Object finishQrCodeHttpRequest(FinishInterestSpecData finishInterestSpecData){
        String finishSystemUrl = "";

        if (finishInterestSpecData.getInterestsSpecNo().contains("HIGO")){
            finishSystemUrl = "https://test-global.allinpayhk.com/m-prime/map-user-interests-spec/finish";
        }


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<FinishInterestSpecData> request = new HttpEntity<>(finishInterestSpecData, headers);
        ResponseEntity<Result> result = restTemplate.postForEntity(finishSystemUrl,
                request, Result.class);

        return result.getBody();
    }

}
