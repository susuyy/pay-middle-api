package com.ht.feignapi.result;

import com.ht.feignapi.mall.util.JudgeParamUtil;
import lombok.Getter;

@Getter
public enum ResultTypeEnum {

    SERVICE_SUCCESS(1200, "成功"),
    SERVICE_ERROR(1500, "服务异常"),


    AUTH_TOKEN_ERROR(40001, "token失效"),

    RESPONSE_PACK_ERROR(2100, "结果封装异常"),

    BIND_EXCEPTION(10101, "参数异常"),
    PARA_MISSING_EXCEPTION(10102, "参数不完整"),

    FEIGN_ERROR(10103, "feign调用失败"),

    ACCESS_DENIED(10104, "权限不足,禁止访问"),

    TOKEN_ERROR(10105, "无效token,禁止访问"),

    REGISTER_ERROR(10106, "请先根据手机号获取验证码"),

    AUTH_CODE_ERROR(10107, "验证码错误"),

    USER_NULL(10108, "用户不存在"),

    IC_CARD_ID_NULL(10109, "实体卡号为空"),
    CARD_NULL(16100,"异常卡,无法支付"),

    SEND_CODE_ERROR(10110, "发送验证码失败"),
    CARD_BIND(10111, "卡已存在绑定信息"),
    OPENID_ERROR(10112, "openid错误"),
    USER_NOT_NULL(10113, "用户已存在"),
    MERCHANT_ERROR(10114, "商户编码错误"),
    LOGIN_ERROR(10115, "账号或密码错误"),
    USER_GET_CARD_ERROR(10116, "领取卡券失败,不满足条件"),
    WX_AUTH_ERROR(10117, "授权错误,请获取授权或重试"),
    CHARGE_TYPE_ERROR(10118, "无法确认收款类型"),
    PRODUCTION_CODE_NOT_EXIST(10119, "产品不存在"),
    INVENTORY(10120, "库存不足,请调整库存"),
    TEL_NOT_NULL(10121, "手机号已绑定其他微信号,无法重复绑定"),
    WX_GET_SIGNATURE_ERROR(10122, "获取微信JSSDK签名出错"),
    USER_MONEY_NOT_ENOUGH(11120, "用户余额不足消费"),
    USER_CARD_NOT(11121, "用户未绑定卡"),
    CARD_USED_ERROR(11122, "卡券使用失败"),
    NOT_INVENTORY(11123, "库存不足"),
    QR_CODE_INV(11124, "二维码已失效"),
    NOT_ALLOWED_USE(11125, "卡券使用失败,不满足使用条件"),
    PRODUCTION_OVERDUE(11126, "存在过期商品"),
    CARD_PAY_ERROR(11127, "交易失败"),
    CODE_EXIST(11128, "code已存在"),
    INVENTORY_NULL(11129, "库存不存在"),
    QR_AUTH_CODE_ERROR(11123, "二维码失效"),
    AUTH_FORMAT_CODE_ERROR(11133, "用户标识码不匹配"),//v1.1.0
    AUTH_VALID_CODE_ERROR(11134, "用户标识码无效"),
    CARD_TYPE_PREPAY_NO(11135,"当前仅允许预付费售卖卡核销"),
    SAVE_OR_UPDATE_FAIL(11130, "保存失败"),
    MIS_ORDER_NULL(11140, "不存在待支付的mis订单"),
    CASH_ID_ERROR(11141,"收银机款台号错误"),
    CARD_USE_ERROR(11142,"优惠券不满足使用条件"),
    CARD_STATE_ERROR(11143,"卡券不属于待使用状态"),
    DATA_NULL(11144,"提交信息不能为空"),
    ORDER_AMOUNT_MEET(11145,"订单金额核销溢出,无需继续支付"),
    CARD_USE_VALID(11146,"该卡不在使用期限内"),
    CARD_MONEY_ERROR(11147,"卡余额不足,请重新做单"),
    CARD_ORDER_TIME_ERROR(11148,"订单失效,请重新推送"),
    CARD_MONEY_ZERO(11149,"卡余额为0元,请换卡支付"),
    LIMIT_PAY_TYPE_ERROR(11150,"不予支付,卡类型与支付通道不匹配,请选择正确的支付方式"),
    MIS_ORDER_NULL_ERROR(11151,"上送的订单数据为空"),
    REFUND_MONEY_ZERO_ERROR(11152,"该订单已无可退金额"),
    REFUND_PASSWORD_ERROR(11153,"退款密码错误"),

    REGISTER_BRH_ERROR(12100,"合作机构入驻注册失败"),
    DECRYPT_ERROR(15100,"数据解密失败"),
    MD5_SIGN_ERROR(15101,"签名加密识别失败"),
    NOT_PRODUCTION(16100, "购买商品不能为空"),
    LIMIT_USER_ACCOUNT(16101,"超出个人购买额度"),
    NOT_PAID(16102,"该订单未支付"),
    REFUND_ERROR(16103,"系统退款失败"),
    READ_ERROR(16104,"请先阅读勾选法律条文"),
    REFUND_MONEY_ERROR(16105,"退款金额大于实付金额"),
    REFUND_TRACE_ERROR(16106,"退款流水错误"),
    ORDER_CLOSE_ERROR(16107,"只允许关闭待支付的订单"),
    CANCEL_ERROR(16108,"仅可撤销已支付订单"),
    BATCH_PENDING(16109,"请等待操作完毕");


    private Integer code;
    private String message;

    ResultTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
