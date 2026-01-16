package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 调取通联开卡接口的响应数据
 *
 * @author suyangyu
 * @since 2020-06-09
 */
@Data
public class PpcsCloudCardOpenResponse implements Serializable {
    /**
     * 0-成功	结果
     */
    private Integer result;

    /**
     * 订单号,接口发起方需保证订单号唯一
     */
    private String order_id;

    /**
     * 流水号
     */
    private String trans_no;

    /**
     * 发卡机构号
     */
    private String brh_id;

    /**
     * 手机号
     */
    private String phone_num;

    /**
     * 卡号
     */
    private String card_id;
}
