package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UpdateCardNoData implements Serializable {

    private List<CardElectronic> cardElectronicList;

    private List<CardOrderDetails> cardOrderDetailsList;
}
