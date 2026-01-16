package com.ht.user.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/21 16:54
 */
@Data
public class VipLevelVo implements Serializable {
    private String levelName;
    private String vipLevelImgUrl;
    private String levelType;
}
