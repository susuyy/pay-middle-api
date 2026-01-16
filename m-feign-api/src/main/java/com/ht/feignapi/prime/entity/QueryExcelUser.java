package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryExcelUser implements Serializable {

    private Long userId;

    private String phoneNum;
}
