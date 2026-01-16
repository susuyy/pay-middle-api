package com.ht.feignapi.mall.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author ${author}
 * @since 2020-09-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class MallProductions implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private  Long id;

    private String templateCode;

    private String mallCode;

    private String productionCode;

    private String weight;

    private Integer inventory;

    private String productionName;

    private String productionUrl;

    private String merchantCode;

    private String merchantName;

    /**
     * 单价：单位：分
     */
    private Integer price;

    /**
     * 单位
     */
    private String unit;

    /**
     * 平均月销售额
     */
    private Integer avgSaleAmountPerMonth;

    /**
     * 总销售量
     */
    private Integer totalSalesAmount;

    /**
     * 外卖标识
     */
    private String flagTakeOut;

    /**
     * 折扣价：单位：分
     */
    private Integer discountPrice;

    /**
     * 状态
     */
    private String state;

    /**
     * 排序序号，倒叙排列，数字越大，排到前面
     */
    private Integer sortNum;

    /**
     * 品类编码（末级分类）
     */
    private String categoryCode;

    /**
     * 展示分类编码
     */
    private String showCategoryCode;

    /**
     * 标签的值
     */
    @TableField(exist = false)
    private List<DicLable> lableValue;

    /**
     * 属性
     */
    private String lable01;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateAt;

    /**
     * production的类型：cards为卡券类型，production为非卡券实体商品
     */
    private String productionType;

    private Long productionId;

    private String detail;

    private String validTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date onSaleDate;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    private Date haltSaleDate;

    private String merchantPhone;

    private List<MallProductionsImages> images;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
//    @JSONField(format = "yyyy/MM/dd HH:mm:ss")
    private Date endDate;

    private String merchantAddress;

    private List<String> instruments;

    private Integer discountPoints;

//    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
//    public Date getEndDate() {
//        return endDate;
//    }
//
//    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss",timezone = "GMT+8")
//    public void setEndDate(Date endDate) {
//        this.endDate = endDate;
//    }
}
