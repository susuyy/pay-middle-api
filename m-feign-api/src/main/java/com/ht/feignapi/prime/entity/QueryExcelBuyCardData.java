package com.ht.feignapi.prime.entity;

import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import lombok.Data;

import java.io.Serializable;

@Data
public class QueryExcelBuyCardData implements Serializable {

    private String cardNo;

    private CardElectronic cardElectronic;

//    private CardOrderDetails cardOrderDetails;
}
