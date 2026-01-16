package com.ht.feignapi.prime.entity;

import lombok.Data;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class ExcelRequestData implements Serializable {


    MultipartFile file;
    MultipartFile certFile;
    String operatorId;
    String operatorAccount;
    String payType;
    String payAmount;
}
