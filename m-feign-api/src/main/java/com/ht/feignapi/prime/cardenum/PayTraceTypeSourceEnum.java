package com.ht.feignapi.prime.cardenum;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/25 11:49
 */
public enum PayTraceTypeSourceEnum {

    TL_POS("tl_pos","通联pos"),
    ACTUAL_CASH("actual_cash","现金"),
    COMPANY_PAY("company_pay","单位支付"),
    REMITTANCE_PAY("remittance","汇款"),
    FREE("free","免费"),
    OTHER_PAY("other","其他");

    private String value;
    private String desc;

    PayTraceTypeSourceEnum(String value, String desc){
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
        for (PayTraceTypeSourceEnum cardElectronicEnum : PayTraceTypeSourceEnum.values()) {
            if(cardElectronicEnum.getValue().equals(valueKey)){
                return cardElectronicEnum.getDesc();
            }
        }
        return null;
    }

}
