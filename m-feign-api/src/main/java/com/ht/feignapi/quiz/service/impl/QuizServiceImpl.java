package com.ht.feignapi.quiz.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ht.feignapi.aliyun.client.AliyunClientService;
import com.ht.feignapi.aliyun.entity.AliMsgResponseEntity;
import com.ht.feignapi.auth.client.AuthClientService;
import com.ht.feignapi.auth.entity.UserUsers;
import com.ht.feignapi.quiz.client.QuizClientService;
import com.ht.feignapi.quiz.service.QuizService;
import com.ht.feignapi.quiz.vo.*;
import com.ht.feignapi.result.Result;
import com.ht.quiz.examroom.vo.ExamMakeUpVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>
 * </p>
 *
 * @author hy.wang
 * @since 20/9/17
 */
@Service
public class QuizServiceImpl implements QuizService {


    @Autowired
    private AuthClientService authClientService;

    @Autowired
    private QuizClientService quizClientService;

    @Autowired
    private AliyunClientService aliyunClientService;

    private static final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);



    @Override
    public Result register(UserUsers userUsers) {
        return authClientService.register(userUsers);
    }

    @Override
    public Result user(Principal user) {
        return authClientService.user(user);
    }

    @Override
    public Result modifyStatus(ExamCandidateRegistrationVo examRegistrationVo) {

        Result result = quizClientService.modifyStatus(examRegistrationVo);
        JSONObject json = (JSONObject)result.getData();
        ExamCandidateRegistrationVo model =  JSONObject.toJavaObject(json,ExamCandidateRegistrationVo.class);
        log.info("modifyStatus one model={}",model);
        Long candidateId = model.getId();
        String examBatchName = (null != model ? model.getExamBatchName():"出租汽车驾驶员从业资格考试");
        String realName = model.getRealName();
        String tel = model.getTel();
        String idCardNum = model.getIdCardNum();
        Integer status = model.getStatus();
        String approveStatus = (0==status?"待审核":(1==status?"通过":"不通过"));
        String approveRemark = model.getApproveRemark();
        Date createAt = model.getCreateAt();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String examAt = sdf.format(createAt);

        if(StringUtils.isBlank(approveRemark) && "通过".equals(approveStatus)){
            approveRemark = "您的材料已审核通过，请耐心等待考试通知";
        }

        JSONObject smgJson = new JSONObject();
        smgJson.put("examBatchName",examBatchName);
        smgJson.put("realName",realName);
        smgJson.put("idCardNum",idCardNum);
        smgJson.put("tel",tel);
        smgJson.put("examAt",examAt);
        smgJson.put("approveStatus",approveStatus);
        smgJson.put("approveRemark",approveRemark);
        //报名审核状态短信通知


        String code = "1200";
        boolean isTryFlag = true;
        int count = 0;
        while(isTryFlag){
            AliMsgResponseEntity aliMsgResponseEntity = aliyunClientService.sendRegExamMsg(tel, smgJson);
            log.info("modifyStatus count={},tel={},smgJson={},aliMsgResponseEntity={}",count,tel,smgJson,aliMsgResponseEntity);
            code = aliMsgResponseEntity.getCode();
            if("1200".equals(code)){
                isTryFlag = false;
            }else{
                count ++;
            }
            if(count>2){
                code = "10110";
                isTryFlag = false;
            }
        }

        ExamCandidateNoticeRecordResultVo candidateNoticeRecordResultVo = new ExamCandidateNoticeRecordResultVo();
        String smsResultRemark = "";
        Integer noticeStatus = 0;
        Integer recordStatus = 0;

        if("1200".equals(code)){
            smsResultRemark = "发送成功";
            noticeStatus = 1;//已发送信息成功
            recordStatus = status;
        }else{
            smsResultRemark = "发送失败，手机空号或者停机等，请联系阿里云短信商";
            noticeStatus = 0;//未发送信息
            recordStatus = 0;//待审核状态
        }

        candidateNoticeRecordResultVo.setId(candidateId);
        candidateNoticeRecordResultVo.setNoticeStatus(noticeStatus);
        candidateNoticeRecordResultVo.setSmsResultRemark(smsResultRemark);
        candidateNoticeRecordResultVo.setStatus(recordStatus);
        quizClientService.statusRecordResultNew(candidateNoticeRecordResultVo);
        log.info("modifyStatus statusRecordResultNew candidateNoticeRecordResultVo={}",candidateNoticeRecordResultVo);

        return Result.success("操作成功");

    }



    @Override
    public Result noticeExamMsg(ExamNoticeVo examNoticeVo) {

        Result result = quizClientService.noticeExamCandidateRegistration(examNoticeVo);
        JSONArray list = (JSONArray)result.getData();

        for (int i = 0; i < list.size() ; i++) {
            JSONObject json = (JSONObject)list.get(i);
            ExamRoomMapCandidateNewVo model =  JSONObject.toJavaObject(json,ExamRoomMapCandidateNewVo.class);
            Long examRoomMapCandidateId = model.getId();
            String examBatchName = model.getExamBatchName();
            String realName = model.getCandidateName();
            String tel = model.getTel();
            String idCardNum = model.getIdCardNum();
            String admissionTicketNumber = model.getAdmissionTicketNumber();
            String examRoomName = model.getExamRoomName();
            String seatNumber = model.getSeatNumber();
            String address = model.getAddress();
            String addressDetail = model.getAddressDetail();
            Date examAt = model.getExamAt();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String examAtStr = sdf.format(examAt);

            if(StringUtils.isBlank(seatNumber)){
                seatNumber = "随机号座位";
            }else{
                seatNumber = seatNumber+"号座位";
            }

            JSONObject smgJson = new JSONObject();
            smgJson.put("realName",realName);
            smgJson.put("idCardNum",idCardNum);
            smgJson.put("examBatchName",examBatchName);
            smgJson.put("examAt",examAtStr);
            smgJson.put("admissionTicketNumber",admissionTicketNumber);
            smgJson.put("examRoomName",examRoomName+seatNumber);
            smgJson.put("address",address);
            smgJson.put("addressDetail",addressDetail);
            smgJson.put("reminder","请凭本人有效身份证，准考证入场");
            smgJson.put("notice","请保持本人手机电量充足上网及微信功能正常");
            smgJson.put("remark","考试之前，多练习模拟题，提高考试通过率");
            smgJson.put("remind","认真阅读考试须知，提前30分钟入场");

            //考试通知
            AliMsgResponseEntity aliMsgResponseEntity = aliyunClientService.sendExamMsg(tel, smgJson);
            log.info("noticeExamMsg tel={},smgJson={},aliMsgResponseEntity={}",tel,smgJson,aliMsgResponseEntity);

            ExamNoticeRecordResultVo examNoticeRecordResultVo = new ExamNoticeRecordResultVo();
            String smsResultRemark = "";
            Integer noticeStatus = 0;
            String code = aliMsgResponseEntity.getCode();
            if("1200".equals(code)){
                smsResultRemark = "发送成功";
                noticeStatus = 1;//已发送信息成功
            }else{
                smsResultRemark = "发送失败，手机空号或者停机等，请联系阿里云短信商";
                noticeStatus = 0;//未发送信息
            }

            examNoticeRecordResultVo.setId(examRoomMapCandidateId);
            examNoticeRecordResultVo.setNoticeStatus(noticeStatus);
            examNoticeRecordResultVo.setSmsResultRemark(smsResultRemark);
            quizClientService.examNoticeRecordResultNew(examNoticeRecordResultVo);
            log.info("noticeExamMsg examNoticeRecordResultNew examNoticeRecordResultVo={}",examNoticeRecordResultVo);

        }

        return Result.success("操作成功");

    }

    @Override
    public Result setExamMakeUpStatus(ExamMakeUpVo examMakeUpVo) {
        Result result = quizClientService.setExamMakeUpStatus(examMakeUpVo);
        log.info("setExamMakeUpStatus result={}",result);
        JSONObject json = (JSONObject)result.getData();
        ExamMakeUpVo model =  JSONObject.toJavaObject(json,ExamMakeUpVo.class);
        log.info("setExamMakeUpStatus model={}",model);
        return Result.success("操作成功");
    }


    @Override
    public Result noticeExamCertificateMsg(ExamNoticeVo examNoticeVo) {

//        Result result = quizClientService.noticeExamCertificateNew(examNoticeVo);
//        JSONArray list = (JSONArray)result.getData();
//
//        for (int i = 0; i < list.size() ; i++) {
//            JSONObject json = (JSONObject)list.get(i);
//            ExamCandidateCertificateNewVo model =  JSONObject.toJavaObject(json,ExamCandidateCertificateNewVo.class);
//            Long candidateCertificateId = model.getId();
//            String examBatchName = model.getExamBatchName();
//            String realName = model.getRealName();
//            String tel = model.getTel();
//            String idCardNum = model.getIdCardNum();
//            String address = model.getAddress();
//
//
//            JSONObject smgJson = new JSONObject();
//            smgJson.put("realName",realName);
//            smgJson.put("idCardNum",idCardNum);
//            smgJson.put("examBatchName",examBatchName);
//            smgJson.put("address",address);
//
//            //考试通知
////            AliMsgResponseEntity aliMsgResponseEntity = aliyunClientService.sendExamCertificateMsg(tel, smgJson);
//            log.info("noticeExamCertificateMsg tel={},smgJson={},aliMsgResponseEntity={}",tel,smgJson,aliMsgResponseEntity);
//
//            ExamNoticeRecordResultVo examNoticeRecordResultVo = new ExamNoticeRecordResultVo();
//            String smsResultRemark = "";
//            Integer noticeStatus = 0;
//            String code = aliMsgResponseEntity.getCode();
//            if("1200".equals(code)){
//                smsResultRemark = "发送成功";
//                noticeStatus = 1;//已发送信息成功
//            }else{
//                smsResultRemark = "发送失败，手机空号或者停机等，请联系阿里云短信商";
//                noticeStatus = 0;//未发送信息
//            }
//
//            examNoticeRecordResultVo.setId(candidateCertificateId);
//            examNoticeRecordResultVo.setNoticeStatus(noticeStatus);
//            examNoticeRecordResultVo.setSmsResultRemark(smsResultRemark);
//            quizClientService.examNoticeCertificateRecordResultNew(examNoticeRecordResultVo);
//            log.info("noticeExamCertificateMsg examNoticeCertificateRecordResultNew examNoticeRecordResultVo={}",examNoticeRecordResultVo);
//
//        }

        return Result.success("操作成功");
    }

}
