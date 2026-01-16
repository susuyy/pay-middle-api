package com.ht.feignapi.shoppingmall.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Vip_User实体类
 * </p>
 *
 * @author suyangyu
 * @since 2020-06-09
 */
@Data
public class VipUser {


    /**
     * id
     */
    private Long id;

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 会员等级
     */
    private Integer vipLevel;

    /**
     * 积分
     */
    private Integer point;


    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 创建时间
     */
    private Date createAt;

    /**
     * 修改时间
     */
    private Date updateAt;

    /**
     * 密码
     */
    private String password;

    /**
     * 外部数据字段
     */
    private String refJdFlag;

    /**
     * 外部数据字段
     */
    private String refKey;

    /**
     * 外部数据字段
     */
    private String refSxlFlag;

    /**
     * 外部手机号
     */
    private String refPhone;


}
