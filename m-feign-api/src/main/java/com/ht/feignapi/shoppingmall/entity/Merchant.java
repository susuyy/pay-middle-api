package com.ht.feignapi.shoppingmall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 商家
 * </p>
 *
 * @author hy.wang
 * @since 2021-09-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sho_merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;
    /**
     * 商家名称
     */
    private String merName;
    /**
     * 商家编码
     */
    private String merCode;
    /**
     * 商家图片（封面）
     */
    private String merImage;
    /**
     * 商家类别(针对美食商家)
     */
    private String merCategory;

    /**
     * 商家店类型(购物=5、 出行=7、生活=8、娱乐=9、景点=4、酒店=6、美食=3)
     */
    private Integer merStoreType;

    /**
     * 商家注册电话
     */
    private String tel;

    /**
     * 商家客服电话
     */
    private String merTel;

    /**
     * 门店地址
     */
    private String address;
    /**
     * 详情地址
     */
    private String addressDetail;
    /**
     * 门店描述（简述）
     */
    private String merDesc;

    /**
     * 省份名称
     */
    private String provinceName;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 市编码
     */
    private String cityCode;

    /**
     * 区名称
     */
    private String countyName;

    /**
     * 区编码
     */
    private String countyCode;

    /**
     * 状态 0=未启用 1=启用
     */
    private Integer merStatus=0;

    /**
     * 商家推荐标识（默认0不推荐，1为推荐）
     */
    private Integer recommendFlag=0;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 纬度
     */
    private String latitude;


    /**
     * 权重
     */
    private Integer weight;

    /**
     * 商家轮播图
     */
    private String merPhotos;

    /**
     * 商家详情
     */
    private String merContent;

    /**
     * 商家标签
     */
    private String merTags;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;


}
