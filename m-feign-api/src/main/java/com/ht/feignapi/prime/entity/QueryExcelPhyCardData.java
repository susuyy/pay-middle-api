package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryExcelPhyCardData implements Serializable {

    private String cardNo;

    private CardPhysical cardPhysical;
}
