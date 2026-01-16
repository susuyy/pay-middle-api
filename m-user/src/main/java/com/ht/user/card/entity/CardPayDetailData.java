package com.ht.user.card.entity;


import com.ht.user.card.vo.PosSelectCardNo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class CardPayDetailData implements Serializable {

    private List<PosSelectCardNo> cardNoList;

    private Map<String, Integer> couponMoneyMap;
}
