package com.ht.user.card.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

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
