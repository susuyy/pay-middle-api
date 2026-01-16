package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetServiceData implements Serializable {

    private Boolean flag;

    private String message;

    private UserUsers data;

}
