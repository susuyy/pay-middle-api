package com.ht.feignapi.mall.entity;

import com.ht.feignapi.tonglian.card.entity.CardMapMerchantCards;
import com.ht.feignapi.tonglian.card.entity.CardMapUserCards;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MoreMerchantCouponData implements Serializable {

    private Boolean flag;

    private List<CardMapUserCards> cardMapUserCardsList;
}
