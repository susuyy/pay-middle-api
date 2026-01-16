package com.ht.feignapi.tencent.controller;

import com.ht.feignapi.result.Result;
import com.ht.feignapi.tencent.service.TencentCosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/14 11:49
 */
@RestController
@RequestMapping("/cos")
public class TencentCosController {
    @Autowired
    private TencentCosService tencentCosService;

    @PostMapping("/privateBucket")
    public Result saveToPrivateBucket(@RequestParam("file") MultipartFile file) throws Exception {
        return tencentCosService.uploadFileToPrivateBucket(file);
    }

    @PostMapping("/publicBucket")
    public Result saveToPublicBucket(@RequestParam("file") MultipartFile file) throws Exception {
        return tencentCosService.uploadFileToPublicBucket(file);
    }
}
