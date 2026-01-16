package com.ht.user.outlets.util;

import com.ht.user.outlets.paystrategy.PayCompanyTypeEnum;

public class CompanyPayWeight {

    public static String qrPayCompany = "JlPay";

    public static String posPayCompany = "AllinPay";

    public static String ifOpenPos = "yes";

    /**
     * 获取 扫码类 支付公司标识
     * @return
     */
    public static String getPayCompanyFlag(String companyFlag){
        if ("JlPay".equals(companyFlag)) {
            return PayCompanyTypeEnum.JLPAY.getPayCompany();
        }else {
            return PayCompanyTypeEnum.ALLINPAY.getPayCompany();
        }
    }

    /**
     * 获取 pos类 支付公司标识
     * @return
     */
    public static String getPosPayCompanyFlag(String companyFlag){
        if ("JlPay".equals(companyFlag)) {
            return PayCompanyTypeEnum.JLPAY.getPayCompany();
        }else {
            return PayCompanyTypeEnum.ALLINPAY.getPayCompany();
        }
    }

}
