package com.ht.user.outlets.entity;

import lombok.Data;

import javax.naming.directory.SearchResult;
import java.io.Serializable;

@Data
public class CheckPayTraceData implements Serializable {

    /**
     * 扫码 获取到的流水号
     */
    private String trxid;

}
