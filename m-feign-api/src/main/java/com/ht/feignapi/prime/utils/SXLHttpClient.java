package com.ht.feignapi.prime.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SXLHttpClient {

    public static Map sxlPost(String url, Object params) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Object> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> result = restTemplate.postForEntity("http://139.198.26.226/v0.1" + url,
                request, Map.class);
        return result.getBody();
    }

}
