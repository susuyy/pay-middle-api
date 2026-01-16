package com.ht.feignapi.prime.entity;

import lombok.Data;

import javax.naming.directory.SearchResult;
import java.io.Serializable;

@Data
public class QrCodeCheckResult implements Serializable {

    private String codeType;

    private String desc;

}
