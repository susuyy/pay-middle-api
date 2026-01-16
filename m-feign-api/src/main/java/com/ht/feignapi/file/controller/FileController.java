package com.ht.feignapi.file.controller;

import com.ht.feignapi.file.client.FileClient;
import com.ht.feignapi.result.Result;
import feign.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;


@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger log = LoggerFactory.getLogger(FileController.class);


    @Autowired
    private FileClient fileClient;




    /**
     * 上传文件
     * @param file
     * @param fileDirectory
     * @return
     */
    @RequestMapping(value = "/upload",method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result upLoad(@RequestPart(value = "file") MultipartFile file,@RequestParam("fileDirectory") String fileDirectory) {
        return fileClient.upLoad(file,fileDirectory);
    }

    /**
     * 下载文件
     * @param fileName 文件名称
     * @param fileDirectory 文件目录
     */
    @RequestMapping("/download")
    private void download(HttpServletResponse response, @RequestParam("fileName") String fileName,
                          @RequestParam("fileDirectory") String fileDirectory) {

        InputStream inputStream = null;
        try {
            Response serviceResponse = fileClient.download(fileName,fileDirectory);
            Response.Body body = serviceResponse.body();
            inputStream = body.asInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            response.setHeader("Content-Disposition", serviceResponse.headers().get("Content-Disposition").toString().replace("[","").replace("]",""));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            int length = 0;
            byte[] temp = new byte[1024 * 10];
            while ((length = bufferedInputStream.read(temp)) != -1) {
                bufferedOutputStream.write(temp, 0, length);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            bufferedInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            log.error("download error={}",e);
        }


    }




}
