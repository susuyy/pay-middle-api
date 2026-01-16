package com.ht.user.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 14:31
 */
@Data
public class MerchantCardEditVo implements Serializable {
    /**
     * 券名称
     */
    private String cardName;

    /**
     * 上架时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date onSaleDate;

    /**
     * 下架时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date haltSaleDate;

    /**
     * 销售价格
     */
    private BigDecimal price;

    private BigDecimal referencePrice;

    /**
     * 初始库存
     */
    private Integer inventory;

    /**
     * 面向顾客类型
     * 如果是面向会员，则传入会员名字。
     * 如果是面向所有人，则传入“所有顾客”
     */
    private String memberType;

    /**
     * 每人每日限购
     */
    private Integer everydayLimit;

    /**
     * 每人总限购
     */
    private Integer totalLimit;

    /**
     * 类型
     */
    private String type;

    private String batchCode;

    private String faceValue;
}
