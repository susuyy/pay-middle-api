package com.ht.feignapi.tonglian.card.entity;

import com.ht.feignapi.tonglian.user.entity.PosSelectCardNo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SettlementCard implements Serializable {

    /**
     * 用户手机号
     */
    private String tel;

    /**
     * 会员卡卡号
     */
    private String icCardId;

    /**
     * 磁条卡
     */
    private String magCard;

    /**
     * 用户标识码
     */
    private String userFlagCode;

    /**
     * 原始的支付金额
     */
    private Integer amount;

    /**
     * 用户选择的使用卡券
     */
    private List<PosSelectCardNo> cardNoList;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 终端号
     */
    private String terId;


}
