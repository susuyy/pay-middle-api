package com.ht.feignapi.tonglian.card.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/7/8 15:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("card_map_user_cards_trace")
public class CardMapUserCardsTrace implements Serializable {
    private Long id;
    private Long userId;
    private String merchantCode;
    private String cardCode;
    private String cardNo;
    private String actionType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date actionDate;

    private String state;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date createAt;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm",timezone = "GMT+8")
    private Date updateAt;

    private String batchCode;

    @TableField(exist = false)
    private String cardName;

    @TableField(exist = false)
    private String tel;

    private String cardPicUrl;
}
