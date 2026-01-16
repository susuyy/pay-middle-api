package com.ht.user.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;

public enum PayTypeEnum implements IEnum<String> {

    PAID("paid","已支付"),
    REFUND("refund","已退款"),
    CANCEL("cancel","撤销"),
    UNPAID("unpaid","未支付");


    @EnumValue
    private String flag;

    private String desc;

    PayTypeEnum(String flag, String desc){
        this.flag = flag;
        this.desc = desc;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.desc;
    }

    @Override
    public String getValue() {
        return flag;
    }

    /**
     * 获取 枚举 desc
     * @param valueKey
     * @return
     */
    public static String getDescByValueKey(String valueKey){
        for (PayTypeEnum payTypeEnum : PayTypeEnum.values()) {
            if(payTypeEnum.getValue().equals(valueKey)){
                return payTypeEnum.getDesc();
            }
        }
        return null;
    }
}
