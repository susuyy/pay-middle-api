package com.ht.user.outlets.paystrategy;

public enum PayCompanyTypeEnum {

    JLPAY(1,"JlPay"),

    ALLINPAY(2,"AllinPay");


    /**
     * 状态值
     */
    private int code;

    /**
     * 类型描述
     */
    private String payCompany;

    private PayCompanyTypeEnum(int code, String payCompany) {
        this.code = code;
        this.payCompany = payCompany;
    }

    public int getCode() {
        return code;
    }

    public String getPayCompany() {
        return payCompany;
    }

    public static PayCompanyTypeEnum valueOf(int code) {
        for (PayCompanyTypeEnum payCompanyTypeEnum : PayCompanyTypeEnum.values()) {
            if (payCompanyTypeEnum.getCode()==code) {
                return payCompanyTypeEnum;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        System.out.println(PayCompanyTypeEnum.JLPAY.getPayCompany());
    }

}
