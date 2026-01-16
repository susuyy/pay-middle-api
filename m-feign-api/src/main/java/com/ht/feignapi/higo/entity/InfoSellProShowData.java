package com.ht.feignapi.higo.entity;

import com.ht.feignapi.prime.entity.CardElectronicSell;
import com.ht.feignapi.tonglian.merchant.entity.Merchants;
import lombok.Data;

import javax.smartcardio.Card;
import java.io.Serializable;
import java.util.List;

@Data
public class InfoSellProShowData implements Serializable {

    private Merchants merchants;

    private List<CardElectronicSell> list;

}
