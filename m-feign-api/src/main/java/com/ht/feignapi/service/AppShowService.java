package com.ht.feignapi.service;

import com.ht.feignapi.entity.appshow.User;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by yucsun on 3/9/17.
 */

@FeignClient(name = "${custom.client.appshow}")
@RestController
public interface AppShowService {
    @RequestMapping(value = "/test", method = GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    Object test(@RequestParam(name = "testVar") Integer testVar);
//    public CommonResult test(@RequestParam("testVar") Integer testVar);

//    @RequestMapping(value = "/test", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, method = {RequestMethod.GET})
//    public CommonResult test(
//            @RequestParam(FeignConfig.tokenKey) String boxFishAccessToken,
//            @RequestBody StudentInfoQueryLaterPageBySearchParam studentInfoQueryLaterPageBySearchParam);
}
