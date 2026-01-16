package com.ht.quiz.examroom.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class ExamMakeUpVo {

    private Long id;

    /**
     * 考生报名id
     */
    private Long examCandidateRegistrationId;

    /**
     * 考生身份证号
     */
    private String idCardNum;


    /**
     * 姓名
     */
    private String realName;


    /**
     * 电话
     */
    private String tel;


    /**
     * 科目类别 公共科目=1 , 区域科目=2
     */
    private Integer categoryType=0;

    /**
     * 补考审核状态  0=未审核 1=审核通过 2=审核未通过
     */
    private Integer status=0;

    /**
     * 审核备注
     */
    private String approveRemark;

    /**
     * 备注
     */
    private String remark;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

}
