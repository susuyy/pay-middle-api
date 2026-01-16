package com.ht.merchant.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author: zheng weiguang
 * @Date: 2020/6/23 11:52
 */
@Data
public class VipSearch {
    private String tel ;
    private String nickName;
    private String vipLevel;
    private String cardCode;
    private String state;
    private String registerOrigin;
    private String timeStart;
    private String timeEnd;
    private Long pageNo;
    private Long pageSize;
    private String merchantCode;
}
