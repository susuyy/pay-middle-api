package com.ht.feignapi.policydocs.controller;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.file.client.FileClient;
import com.ht.feignapi.policydocs.client.PolicyDocsClient;
import com.ht.feignapi.policydocs.entity.SubItemCategoryFile;
import com.ht.feignapi.policydocs.service.PolicyDocsService;
import com.ht.feignapi.policydocs.utils.ReadPdf;
import com.ht.feignapi.policydocs.vo.SubItemCategoryFileVo;
import com.ht.feignapi.result.Result;
import com.ht.feignapi.result.ResultTypeEnum;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/docs")
public class PolicyDocsController {

    private static final Logger log = LoggerFactory.getLogger(PolicyDocsController.class);


    @Autowired
    private PolicyDocsClient policyDocsClient;

    @Autowired
    private FileClient fileClient;

    @Autowired
    private PolicyDocsService policyDocsService;


    @GetMapping("/test")
    public List test(){
        return new ArrayList();
    }


    /**
     * 文件上传
     * @param file
     * @param fileDirectory
     * @param categoryCode
     * @param subCategoryCode
     * @return
     */
    @RequestMapping(value = "/upload",method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result upLoad(@RequestPart(value = "file") MultipartFile file,
                         @RequestParam("fileDirectory") String fileDirectory,
                         @RequestParam("categoryCode") String categoryCode,
                         @RequestParam("subCategoryCode") String subCategoryCode,
                         @RequestParam("subItemCategoryCode") String subItemCategoryCode) {
        log.info("docs upLoad file ...");

        //判断文件是否为空
        if(file.isEmpty()){
            log.info("upLoad file is empty");
            return Result.error(ResultTypeEnum.SERVICE_ERROR,"文件为空");
        }
        try {
            String fileName = file.getOriginalFilename();
            Result result = policyDocsService.upLoad(file, fileDirectory);
            Integer code = result.getCode();
            if(code != 1200){
                log.info("upLoad 上传失败 fileName={}",fileName);
                return Result.error(ResultTypeEnum.SERVICE_ERROR,"文件上传失败");
            }

            String downloadUrl = result.getData()+"";
            log.info("upLoad 上传文件成功 result={}",result);
            SubItemCategoryFileVo subItemCategoryFileVo = new SubItemCategoryFileVo();
            subItemCategoryFileVo.setCategoryCode(categoryCode);
            subItemCategoryFileVo.setSubCategoryCode(subCategoryCode);
            subItemCategoryFileVo.setSubItemCategoryCode(subItemCategoryCode);
            subItemCategoryFileVo.setFileName(fileName);
            subItemCategoryFileVo.setFileDirectory(fileDirectory);
            subItemCategoryFileVo.setFileValue(downloadUrl);
            policyDocsClient.saveSubItemCategoryFile(subItemCategoryFileVo);
            log.info("upLoad 保存类目对象文件 subItemCategoryFileVo={}",subItemCategoryFileVo);
            return Result.success("上传成功");
        } catch (Exception e) {
            log.error("upLoad error={}",e);
            e.printStackTrace();
            //出现异常，则告诉页面失败
            return Result.error(ResultTypeEnum.SERVICE_ERROR,"上传失败");

        }
    }





}
