package com.ht.user.card.entity;

import com.ht.user.card.vo.CardMapUserCardsVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class CardCouponData implements Serializable {

    private Integer couponMoney;

    private String type;

    private CardMapUserCardsVO cardMapUserCardsVO;
}
