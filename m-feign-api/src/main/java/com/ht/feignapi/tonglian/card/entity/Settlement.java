package com.ht.feignapi.tonglian.card.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class Settlement implements Serializable {

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
     * 金额
     */
    private Integer amount;

    /**
     * 商户编码
     */
    @NotNull(message = "商户编码不能为空")
    private String merchantCode;

    /**
     * 用户标识码
     */
    private String userFlagCode;

    /**
     * 验证码
     */
    private String authCode;

}
