package com.ht.feignapi.prime.cardenum;

/**
 * @author: suyy
 * @Date: 2021/2/25 11:49
 */
public enum CardElectronicEnum {
    ONLINE_SELL("online_sell","线上售卖卡"),
    OFFLINE_SELL("offline_sell","线下售卖卡"),
    PASSWORD_CARD("password_card","电子密码卡"),
    PHYSICAL("physical","实体卡"),
    OFFLINE("offline","体验卡"),
    OFFLINE_PREPAY_SELL("online_prepay_sell","预付费售卖卡"),
    REBATE_PASSWORD_CARD("rebate_password_card","电子密码返利卡");

    private String value;
    private String desc;

    CardElectronicEnum(String value, String desc){
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
