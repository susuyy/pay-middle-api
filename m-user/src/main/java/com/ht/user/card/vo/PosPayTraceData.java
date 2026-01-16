package com.ht.user.card.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PosPayTraceData implements Serializable {

    /**
     * 用户手机号
     */
    private String tel;

    /**
     * 实体卡卡号
     */
    private String icCardId;

    /**
     * 用户id
     */
    private Long userId;


    /**
     * 用户标识码
     */
    private String userFlagCode;

    /**
     * 商户编码
     */
    private String merchantCode;

    /**
     * 业务类型
     */
    private String businessId;

    /**
     * 金额
     */
    private Integer amount;

    /**
     * 流水号
     */
    private String traceNo;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 商户号
     */
    private String merchId;

    /**
     * 商户名
     */
    private String merchName;

    /**
     * 终端号
     */
    private String terId;

    /**
     * 微信支付宝ID OPENID/USER ID
     */
    private String cardNo;

    /**
     * 返回码
     */
    private String rejCode;

    /**
     * 卡组织或钱包机构
     */
    private String cups;

    /**
     * 交易日期
     */
    private String date;

    /**
     * 交易时间
     */
    private String time;

    /**
     * 返回码解释
     */
    private String rejCodeCn;

    /**
     * 订单编码
     */
    private String orderCode;


    /**
     * 组合支付 优惠券No
     */
    private List<PosSelectCardNo> cardNoList;


}
