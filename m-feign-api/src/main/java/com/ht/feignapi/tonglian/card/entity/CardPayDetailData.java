package com.ht.feignapi.tonglian.card.entity;

import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class CardPayDetailData implements Serializable {

    private List<PosSelectCardNo> cardNoList;

    private Map<String, Integer> couponMoneyMap;
}
