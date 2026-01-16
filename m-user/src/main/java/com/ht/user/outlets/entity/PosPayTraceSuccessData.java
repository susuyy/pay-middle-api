package com.ht.user.outlets.entity;

import com.ht.user.card.vo.PosSelectCardNo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PosPayTraceSuccessData implements Serializable {


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
     * 有效期
     */
    private String expDate;

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
     * 系统参考号
     */
    private String refNo;

    /**
     * 授权码
     */
    private String authNo;

    /**
     * 返回码
     */
    private String rejCode;

    /**
     * 发卡行名称
     */
    private String issName;

    /**
     * 卡组织或钱包机构
     */
    private String cups;


    /**
     * 微信支付宝ID OPENID/USER ID  卡号
     */
    private String cardNo;

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
     * 借贷记卡标识 0借记  1贷记
     */
    private String cardTypeIdenty;

    /**
     * 内外卡标识 0内卡  1外卡
     */
    private String wildCardSign;

    /**
     * 交易单号
     */
    private String transTicketNo;

    /**
     * 卡类型
     */
    private String cardtype;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 调用的支付渠道的api
     */
    private String channelApi;

    /**
     * 支付完成时间
     */
    private String payTime;

    /**
     * 手续费,单位分
     */
    private Integer fee;

    /**
     * 版本号
     */
    private String version;
}
