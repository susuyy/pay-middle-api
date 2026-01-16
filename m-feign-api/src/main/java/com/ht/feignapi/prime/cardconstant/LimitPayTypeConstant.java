package com.ht.feignapi.prime.cardconstant;

import lombok.Data;

import java.io.Serializable;

@Data
public class LimitPayTypeConstant implements Serializable {

    /**
     * 正式实体卡支付
     */
    public static final String CARD_PHYSICAL_NORMAL = "card_physical_normal";

    /**
     * 赠送实体卡支付
     */
    public static final String CARD_PHYSICAL_FREE = "card_physical_free";

    /**
     * 临时支付方式,不校验支付类型
     */
    public static final String CARD_PHYSICAL_TEMP = "card_physical_temp";
}
