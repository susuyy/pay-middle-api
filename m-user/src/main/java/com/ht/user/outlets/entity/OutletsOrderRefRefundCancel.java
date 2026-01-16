package com.ht.user.outlets.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2021-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutletsOrderRefRefundCancel implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 平台分配的商户号
     */
    private String cusid;

    /**
     * 平台分配的APPID
     */
    private String appid;

    /**
     * 收银宝平台的退款交易流水号
     */
    private String trxid;

    /**
     * 商户的退款交易订单号
     */
    private String reqsn;

    /**
     * 商户的退款交易订单号
     */
    private String refReqsn;

    /**
     * 交易的状态
     */
    private String trxstatus;

    /**
     * 交易完成时间yyyyMMddHHmmss
     */
    private String fintime;

    /**
     * 失败的原因说明
     */
    private String errmsg;

    /**
     * 手续费
     */
    private String fee;

    /**
     * 交易类型
     */
    private String trxcode;

    /**
     * 交易类型 中文描述
     */
    private String trxcodeDescribe;

    /**
     * 渠道流水号

如支付宝，微信平台订单号
     */
    private String chnltrxid;

    /**
     * 业务编码
     */
    private String businessType;

    /**
     * 服务类型 pos为pos机业务,qrCode为扫码业务
     */
    private String serviceType;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    /**
     * 交易单号 本次退款平台交易单号   pos业务返回
     */
    private String outtrxid;

    /**
     * 卡号   pos业务返回
     */
    private String acctno;

    /**
     * 发卡机构  pos业务返回
     */
    private String bankname;

    /**
     * 收单机构   pos业务返回
     */
    private String aptcode;

    /**
     * 授权码  pos业务返回
     */
    private String authcode;

    /**
     * 退款/撤销 金额 单位分 我方系统冗余字段
     */
    private Long amount;

}
