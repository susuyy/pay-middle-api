package com.ht.feignapi.prime.entity;

import com.ht.feignapi.tonglian.card.entity.CardListVo;
import lombok.Data;

import javax.smartcardio.Card;
import java.io.Serializable;
import java.util.List;

@Data
public class RetPosCombinationPayEleCard implements Serializable {

    private boolean useFlag;

    private Integer needPaidAmount;

    private Integer paidAmount;

    private Long userId;

    private boolean checkUserFlag;

    private boolean singleCardValidFlag;

    private List<CardConsumeDetails> cardConsumeDetailsList;


}
