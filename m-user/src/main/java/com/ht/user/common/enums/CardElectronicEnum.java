package com.ht.user.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @author: suyy
 * @Date: 2021/2/25 11:49
 */

public enum CardElectronicEnum implements IEnum<String> {

    ONLINE_SELL("online_sell","线上售卖卡"),
    OFFLINE_SELL("offline_sell","线下售卖卡"),
    PASSWORD_CARD("password_card","电子密码卡"),
    PHYSICAL("physical","实体卡"),
    OFFLINE("offline","体验卡"),
    OFFLINE_PREPAY_SELL("online_prepay_sell","预付费售卖卡"),
    REBATE_PASSWORD_CARD("rebate_password_card","电子密码返利卡");

    @EnumValue
    private String flag;

    private String desc;

    CardElectronicEnum(String flag, String desc){
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
        for (CardElectronicEnum cardElectronicEnum : CardElectronicEnum.values()) {
            if(cardElectronicEnum.getValue().equals(valueKey)){
                return cardElectronicEnum.getDesc();
            }
        }
        return null;
    }

}
