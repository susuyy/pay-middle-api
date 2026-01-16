package com.ht.user.card.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: zheng weiguang
 * @Date: 2021/2/22 15:15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CardOrderDetailsVo implements Serializable {
    /**
     * 商品名称
     */
    @TableField(value = "detail_production_name")
    private String productionName;

    /**
     * 商品编码
     */
    @TableField(value = "detail_production_code")
    private String productionCode;

    /**
     * 数量
     */
    @TableField(value = "detail_quantity")
    private BigDecimal quantity;

    /**
     * 金额：默认单位：分
     */
    @TableField(value = "detail_amount")
    private Integer amount;
}
