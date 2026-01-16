package com.ht.feignapi.policydocs.service;


import com.ht.feignapi.result.Result;
import org.springframework.web.multipart.MultipartFile;


public interface PolicyDocsService {


   Result upLoad(MultipartFile file, String fileDirectory);


   void saveFileContentToEsTask();

}
