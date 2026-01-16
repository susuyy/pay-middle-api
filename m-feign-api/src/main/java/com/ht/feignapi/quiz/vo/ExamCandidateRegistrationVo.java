package com.ht.feignapi.quiz.vo;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;


@Data
public class ExamCandidateRegistrationVo {


    /**
     * 考生考试报名主键ID
     */
    private Long id;

    /**
     * 考试批次编码
     */
    private String examBatchCode;

    /**
     * 考试批次名称
     */
    private String examBatchName;

    /**
     * 考试报名入口ID
     */
    private Long examEntryEntranceId;

    /**
     * 微信openid
     */
    private String openid;


    /**
     * 头像
     */
    private String avatar;


    /**
     * 账号
     */
    private String account;

    /**
     * 真实姓名
     */
    private String realName;


    /**
     * 电话
     */
    private String tel;

    /**
     * 区域ID
     */
    private Long regionId;



    /**
     * 证件号
     */
    private String idCardNum;



    /**
     * 证件类型 0=身份证
     */
    private Integer idCardType=0;


    /**
     * 性别 0=男 1=女
     */
    private Integer sex=0;


    /**
     * 出生日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    /**
     * 民族类型
     */
    private Integer famousFamilyType=0;

    /**
     * 文化程度类型
     */
    private Integer eduDegreeType=0;

    /**
     * 国籍类型
     */
    private Integer nationalityType=0;

    /**
     * 邮政编码
     */
    private String postalCode;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 技术职称类型
     */
    private Integer techType=0;


    /**
     * 住址
     */
    private String address;



    /**
     * 初次领证驾驶证日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date initialLicenseDate;


    /**
     * 驾驶编号
     */
    private String driverNumber;

    /**
     * 驾驶证有效开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date driverValidStartDate;


    /**
     * 驾驶证有效截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date driverValidEndDate;


    /**
     * 驾驶证发证机关
     */
    private String licenseIssueAuthority;


    /**
     * 准驾类型
     */
    private JSONArray vehicleType;


    /**
     * 申请种类 0=初领 1=增驾
     */
    private Integer applyType=0;


    /**
     * 原从业资格证号
     */
    private String originalCertificateNo;


    /**
     * 原从业资格证号
     */
    private String certificateNo;

    /**
     * 申请类别 0=出租汽车驾驶员
     */
    private Integer applyCategory=0;


    /**
     * 准考证号
     */
    private String admissionTicketNumber;

    private Long examRoomId;


    /**
     * 正面头像地址
     */
    private String headPositivePicUrl;


    /**
     * 反面头像地址
     */
    private String headReversePicUrl;

    /**
     * 驾驶证图片地址
     */
    private String driverCertificateUrl;

    /**
     * 驾驶证反面图片地址
     */
    private String driverReverseUrl;


    /**
     * 交通材料图片集地址
     */
    private JSONArray transportMaterialUrls;


    private String code;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateAt;

    /**
     * 审核状态 0=未通过 1=已通过
     */
    private Integer status=0;

    /**
     * 1=参加考试
     */
    private Integer flag=0;//1=参加考试

    /**
     * 审核备注
     */
    private String approveRemark;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否快速提交
     */
    private boolean enterFlag=false;//true=是 ，false=否


}
