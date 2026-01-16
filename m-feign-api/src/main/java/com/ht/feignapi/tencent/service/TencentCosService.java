package com.ht.feignapi.tencent.service;


import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/14 10:09
 */
@Service
@FeignClient(url = "${custom.client.tencent.url}", name = "${custom.client.tencent.name}")
public interface TencentCosService {

    /**
     * 调用腾讯云私有读bucket保存
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cos/privateBucket", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result uploadFileToPrivateBucket(MultipartFile file);

    /**
     * 调用腾讯云公有读bucket保存
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cos/publicBucket", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result uploadFileToPublicBucket(MultipartFile file);
}
