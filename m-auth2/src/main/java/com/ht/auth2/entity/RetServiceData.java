package com.ht.auth2.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RetServiceData implements Serializable {

    private Boolean flag;

    private String message;

    private Object data;

}
