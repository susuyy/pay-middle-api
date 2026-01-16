package com.ht.user.card.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@Data
public class UserCashCardPayOrderData implements Serializable {

    /**
     * 用户openid
     */
    private String openid;

    /**
     *  用户userId
     */
    private Long userId;

    /**
     * 支付金额 单位分
     */
    private Integer amount;

    /**
     * 是否需要余额支付
     */
    private Boolean isAccountPay;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 选择使用的卡券编号
     */
    private List<String> cardNoList;


}
