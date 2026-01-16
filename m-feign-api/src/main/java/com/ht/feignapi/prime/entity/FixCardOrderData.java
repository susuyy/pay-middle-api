package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FixCardOrderData implements Serializable {

    private List<String> acmuId;


}
