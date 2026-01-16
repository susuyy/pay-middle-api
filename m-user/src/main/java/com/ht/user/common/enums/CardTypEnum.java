package com.ht.user.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/23 15:45
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CardTypEnum {
    COUPON("coupon","优惠券"),
    MONEY("money","代金券"),
    NUMBER("number","计次券");

    @EnumValue
    @Getter
    @Setter
    private String value;

    @Getter
    @Setter
    private String name;

    /**
     * 卡类型枚举
     * @param value 键
     * @param name 值
     */
    CardTypEnum(String value,String name){
        this.value = value;
        this.name = name;
    }

}
