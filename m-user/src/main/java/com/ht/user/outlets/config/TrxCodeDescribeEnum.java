package com.ht.user.outlets.config;


public enum TrxCodeDescribeEnum {

    WECHAT_PAY("VSP501","微信支付"),
    WECHAT_CANCEL("VSP502","微信支付撤销"),
    WECHAT_REFUND("VSP503","微信支付退款"),
    PHONE_QQ_PAY("VSP505","手机QQ支付"),
    PHONE_QQ_CANCEL("VSP506","手机QQ支付撤销"),
    PHONE_QQ_REFUND("VSP507","手机QQ支付退款"),
    ALIPAY_PAY("VSP511",	"支付宝支付"),
    ALIPAY_CANCEL("VSP512","支付宝支付撤销"),
    ALIPAY_REFUND("VSP513","支付宝支付退款"),
    SCAN_PAY("VSP541","扫码支付"),
    SCAN_CANCEL("VSP542","扫码撤销"),
    SCAN_REFUND("VSP543","扫码退货"),
    UNIONPAY_PAY("VSP551","银联扫码支付"),
    UNIONPAY_CANCEL("VSP552","银联扫码撤销"),
    UNIONPAY_REFUND("VSP553","银联扫码退货"),
    DEBIT("VSP907","差错借记调整"),
    CREDIT("VSP908","差错贷记调整"),
    DIGITAL_CURRENCY_PAY("VSP611",	"数字货币支付"),
    DIGITAL_CURRENCY_CANCEL("VSP612",	"数字货币撤销"),
    DIGITAL_CURRENCY_REFUND("VSP613",	"数字货币退货"),
    CARD_REFUND("VSP003","退货"),
    TOP_UP("300002",	"充值");


    private String value;
    private String desc;

    TrxCodeDescribeEnum(String value, String desc){
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
        for (TrxCodeDescribeEnum trxCodeDescribeEnum : TrxCodeDescribeEnum.values()) {
            if(trxCodeDescribeEnum.getValue().equals(valueKey)){
                return trxCodeDescribeEnum.getDesc();
            }
        }
        return null;
    }

    /**
     * 获取 枚举 key
     * @param desc
     * @return
     */
    public static String getValueKeyByDesc(String desc){
        for (TrxCodeDescribeEnum trxCodeDescribeEnum : TrxCodeDescribeEnum.values()) {
            if(trxCodeDescribeEnum.getDesc().equals(desc)){
                return trxCodeDescribeEnum.getValue();
            }
        }
        return null;
    }

}
