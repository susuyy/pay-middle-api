package com.ht.feignapi.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ht.feignapi.tonglian.merchant.entity.MerchantsConfigVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
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
public class MallShops implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private  Long id;

    /**
     * 商城编码
     */
    private String mallCode;

    /**
     * 门店编码
     */
    private String merchantCode;

    /**
     * 门店名称
     */
    private String merchantName;

    /**
     * 位置
     */
    private String location;

    /**
     * 人均消费：单位 分
     */
    private Integer avgConsum;

    /**
     * 月均销售量
     */
    private Integer avgSales;

    /**
     * 分类编码
     */
    private String showCategoryCode;

    /**
     * 标签1
     */
    private String lable01;

    /**
     * 标签2
     */
    private String lable02;

    /**
     * 标签3
     */
    private String lable03;

    /**
     * 标签的值
     */
    private List<DicLable> lableValue;


    /**
     * 主图
     */
    private String mainPicUrl;

    /**
     * 分类
     */
    private String type;

    /**
     * 状态
     */
    private String state;

    /**
     * 排序序号，倒叙排列，数字越大，排到前面
     */
    private Integer sortNum;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private String merchantPhone;

    private String address;

    private String businessTime;

    private MerchantsConfigVO config;
}
