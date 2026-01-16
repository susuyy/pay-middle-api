package com.ht.feignapi.tencent.controller;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import com.ht.feignapi.tencent.service.TonglianWxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author: zheng weiguang
 * @Date: 2020/11/4 10:37
 */
@RestController
@RequestMapping("/wxOfficial")
public class WxOfficialPlatformController {

    private static final Logger logger = LoggerFactory.getLogger(WxOfficialPlatformController.class);

    @Autowired
    private TonglianWxService tonglianWxService;

    @PostMapping("/{merchantCode}")
    public Result getOfficialJSJDKConfig(@PathVariable("merchantCode") String merchantCode,@RequestBody Map<String,String> url) {
        try{
            Assert.isTrue(url.containsKey("url"),"参数有误");
            return Result.success(tonglianWxService.getJsJDKConfig(merchantCode, url.get("url")));
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            return Result.error(ResultTypeEnum.WX_GET_SIGNATURE_ERROR);
        }
    }

}
