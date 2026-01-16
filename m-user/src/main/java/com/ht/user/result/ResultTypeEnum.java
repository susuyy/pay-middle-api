package com.ht.user.result;

import com.ht.user.common.Result;
import lombok.Getter;

@Getter
public enum ResultTypeEnum {

    SERVICE_SUCCESS(1200, "成功"),
    SERVICE_ERROR(1500, "服务异常"),

    RESPONSE_PACK_ERROR(2100, "结果封装异常"),

    BIND_EXCEPTION(10101, "参数异常"),
    PARA_MISSING_EXCEPTION(10102, "参数不完整"),
    CODE_EXIST(11128,"code已存在"),

    DECRYPT_ERROR(15100,"数据解密失败"),

    MIS_ORDER_NULL_ERROR(11151,"上送的订单数据为空"),

    SEARCH_DATA_NULL_ERROR(11152,"上送的查询数据为空"),

    ORDER_CLOSE_ERROR(16107,"只允许关闭待支付的订单"),

    CASH_ID_ERROR(11141,"收银机款台号错误"),

    REFUND_MONEY_ZERO_ERROR(11152,"该订单已无可退金额"),
    MONEY_ERROR(11153,"请上送有效的金额"),

    REFUND_ERROR(16103,"系统退款失败"),

    CANCEL_ERROR(16108,"仅可撤销已支付订单"),

    MIS_ORDER_NULL(11140, "不存在待支付的订单"),

    ORDER_CODE_REPEAT(11145, "订单号已存在"),

    ORDER_CODE_NULL(11146, "订单号orderCode不能为空"),

    POS_PAY_ERROR(11147, "订单支付失败,rejCode不等于00"),

    PAY_QR_CODE_NULL_ERROR(11147, "付款码不能为空"),

    ORDER_ERROR(11148, "存在重复订单,不予支付,请重新做单"),

    DIGITAL_RMB_REFUND_ERROR(11149,"数字货币暂不支持线上退款/撤销,请操作终端退款/撤销"),

    ORDER_NULL(11150,"订单不存在"),

    CHECK_TRACE_NO_NULL(11152, "获取的查询编码为空,请重新扫码或手输"),

    NOT_PAY(11153, "支付未完成"),

    POS_NUM_ERROR(11154,"pos号不匹配,不予退款"),

    TRACE_POS_NUMBER_ERROR(11155,"该笔交易无法从pos操作,请联系后台");


    private Integer code;
    private String message;

    ResultTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
