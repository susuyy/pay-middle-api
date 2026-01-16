package com.ht.merchant;

import com.ht.merchant.result.Result;
import com.ht.merchant.service.MerchantsConfigService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MMerchantApplicationTests {

    @Autowired
    private MerchantsConfigService merchantsConfigService;

    @Test
    public void testGetKey() {
        System.out.println(merchantsConfigService.getListByKey("WZSYH", "WX_APPID"));

    }

    /**
     * 拉取商户数据测试
     */
    @Test
    public void testSendPost() {
        String authorizeUrl = "https://gateway.hualta.com/m-merchant/merchants/searchSubMerchants";
        RestTemplate client = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType type=MediaType.parseMediaType("application/json;charset=UTF-8");
        httpHeaders.setContentType(type);
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageSize","10");
        map.put("pageNo","1");
        map.put("objectMerchantCode","HLMSD");
        HttpEntity<Map<String, Object>> objectHttpEntity = new HttpEntity<>(map,httpHeaders);
        ResponseEntity<Result> responseResultResponseEntity = client.postForEntity(authorizeUrl, objectHttpEntity, Result.class);
        System.out.println(responseResultResponseEntity.getBody());
    }
}
