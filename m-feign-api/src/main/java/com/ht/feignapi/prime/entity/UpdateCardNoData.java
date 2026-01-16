package com.ht.feignapi.prime.entity;

import com.ht.feignapi.tonglian.order.entity.CardOrderDetails;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateCardNoData implements Serializable {

    private List<CardElectronic> cardElectronicList;

    private List<CardOrderDetails> cardOrderDetailsList;
}
