package com.ht.feignapi.tonglian.card.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 14:21
 */
@Data
public class MerchantCardListVo implements Serializable {
    private String cardCode;
    private String merchantCode;
    private String merchantName;
    private String cardType;
    private String categoryName;
    private String merchantCardName;
    private String batchCode;
    private String cardName;
    private Double price;
    private Integer leftAmount;
    private Integer inventory;
    private Boolean onSaleState;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date onSaleDate;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date haltSaleDate;
    private String cardPicUrl;
}
