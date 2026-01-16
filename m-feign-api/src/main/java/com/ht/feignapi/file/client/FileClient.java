package com.ht.feignapi.file.client;

import feign.Response;
import com.ht.feignapi.config.FeignSupportConfig;
import com.ht.feignapi.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@FeignClient(value = "${custom.client.file.name}",configuration= FeignSupportConfig.class)
public interface FileClient {


    @RequestMapping(value = "/file/upload",method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result upLoad(@RequestPart(value = "file") MultipartFile file, @RequestParam("fileDirectory") String fileDirectory);



    @RequestMapping("/file/download")
    Response download(@RequestParam("fileName") String fileName,
                          @RequestParam("fileDirectory") String fileDirectory);


}
