package com.ht.feignapi.quiz.vo;

import lombok.Data;


@Data
public class ExamCandidateCertificateNewVo {


    /**
     * 主键id
     */
    private Long id;

    /**
     * 考试批次名称
     */
    private String examBatchName;


    /**
     * 真实姓名
     */
    private String realName;


    /**
     * 证件号
     */
    private String idCardNum;


    /**
     * 电话
     */
    private String tel;



    /**
     * 领证地址
     */
    private String address;



}
