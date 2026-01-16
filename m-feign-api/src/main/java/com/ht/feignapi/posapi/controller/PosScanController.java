package com.ht.feignapi.posapi.controller;

import com.ht.feignapi.posapi.entity.FinishInterestSpecData;
import com.ht.feignapi.posapi.util.FinishQrCodeHttpRequestUtil;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pos/scan")
public class PosScanController {


    @Autowired
    private FinishQrCodeHttpRequestUtil finishQrCodeHttpRequestUtil;

    /**
     * pos核销码
     *
     * @param finishInterestSpecData
     * @return
     */
    @PostMapping("/finishQrCode")
    public Object finishQrCode(@RequestBody FinishInterestSpecData finishInterestSpecData) {
        Object data = finishQrCodeHttpRequestUtil.finishQrCodeHttpRequest(finishInterestSpecData);
        return data;
    }

}
