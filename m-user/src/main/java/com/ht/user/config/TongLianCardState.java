package com.ht.user.config;

public enum TongLianCardState {

    CARD_NAME(1, "通联储值虚拟卡用户钱包"),

    CATEGORY(99999, "通联储值虚拟卡"),

    CARD_CODE(866, "通联虚拟卡系统默认卡card_code"),

    /**
     * 0-正常
     * 1-挂失
     * 2-冻结
     * 3-作废
     */
    STATE_NORMAL(3, "normal"),
    STATE_LOSS(4, "loss"),
    STATE_FREEZE(5, "freeze"),
    STATE_INVALID(6, "invalid"),


    TYPE(1, "account");


    private int code;
    private String desc;

    TongLianCardState(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
