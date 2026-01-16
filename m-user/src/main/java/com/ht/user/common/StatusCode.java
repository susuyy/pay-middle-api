package com.ht.user.common;

/**
 * 定义业务响应状态码和信息描述
 *
 * @author suyangyu
 * @since 2020-06-09
 */
public enum StatusCode {

    SUCCESS(20000, "操作成功"),

    ERROR(20001, "操作失败"),

    CODE_ERROR(20002, "验证码错误"),

    CARD_BIND(20003, "卡已存在绑定信息"),

    SEND_CODE_ERROR(20004, "短信发送失败"),

    OPENID_ERROR(20005, "openid错误"),

    PHONE_NULL(20006, "手机号为空"),

    USER_NULL(20007, "用户不存在,请先注册"),

    USER_EXIST(20008, "用户已存在"),

    IC_CARD_ID_NULL(20009, "提交的实体卡号为空"),

    GET_CARD_ERROR(20010, "卡券领取失败,不满足条件"),

    ORDER_PAID(20011, "订单已完成支付,无需重复修改"),

    CARD_USE_ERROR(20012, "下单失败,会员卡使用存在限制");


    private int code;
    private String desc;

    StatusCode(int code, String desc) {
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

