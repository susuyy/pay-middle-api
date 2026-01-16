package com.ht.feignapi.policydocs.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.file.client.FileClient;
import com.ht.feignapi.policydocs.client.PolicyDocsClient;
import com.ht.feignapi.policydocs.entity.SubItemCategoryFile;
import com.ht.feignapi.policydocs.service.PolicyDocsService;
import com.ht.feignapi.policydocs.utils.ReadPdf;
import com.ht.feignapi.policydocs.vo.SubItemCategoryFileVo;
import com.ht.feignapi.result.Result;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;


/**
 * <p>
 * </p>
 *
 * @author hy.wang
 * @since 20/9/17
 */
@Service
public class PolicyDocsServiceImpl implements PolicyDocsService {


    @Autowired
    private FileClient fileClient;

    @Autowired
    private PolicyDocsClient policyDocsClient;


    private static final Logger log = LoggerFactory.getLogger(PolicyDocsServiceImpl.class);


    @Override
    public Result upLoad(MultipartFile file, String fileDirectory) {
        return fileClient.upLoad(file,fileDirectory);
    }

    @Override
    public void saveFileContentToEsTask() {

        Result result = policyDocsClient.listByEsFlag();
        List list = (List) result.getData();
        log.info("saveFileContentToEsTask list.size={}", list.size());
        for (int i = 0; i < list.size(); i++) {
            SubItemCategoryFile subItemCategoryFile = JSONObject.parseObject(list.get(i).toString(), SubItemCategoryFile.class);
            Long id = subItemCategoryFile.getId();
            String fileDirectory = subItemCategoryFile.getFileDirectory();
            String fileName = subItemCategoryFile.getFileName();

            InputStream ins = null;
            File file = null;
            OutputStream os = null;
            try {
                Response serviceResponse = fileClient.download(fileName, fileDirectory);
                Response.Body body = serviceResponse.body();
                ins = body.asInputStream();

                String realPath = this.getClass().getResource("/").getFile().toString();
                file = new File(realPath + fileName);
                os = new FileOutputStream(file);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                String content = ReadPdf.readPdfContent(file);
                SubItemCategoryFileVo subItemCategoryFileVo = new SubItemCategoryFileVo();
                subItemCategoryFileVo.setId(id);
                subItemCategoryFileVo.setContent(content);
                policyDocsClient.taskWriteEsFileContent(subItemCategoryFileVo);

            } catch (IOException e) {
                log.error("saveFileContentToEsTask error={}",e);
            }finally {
                try {
                    os.close();
                    ins.close();
                    file.delete();
                } catch (IOException e1) {
                    log.error("saveFileContentToEsTask error1={}",e1);
                }

            }

        }
    }

}
