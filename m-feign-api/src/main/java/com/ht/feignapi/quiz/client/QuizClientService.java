package com.ht.feignapi.quiz.client;


import com.ht.feignapi.quiz.vo.ExamCandidateNoticeRecordResultVo;
import com.ht.feignapi.quiz.vo.ExamCandidateRegistrationVo;
import com.ht.feignapi.quiz.vo.ExamNoticeRecordResultVo;
import com.ht.feignapi.quiz.vo.ExamNoticeVo;
import com.ht.feignapi.result.Result;
import com.ht.quiz.examroom.vo.ExamMakeUpVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient("${custom.client.quiz.name}")
public interface QuizClientService {

    /**
     * 根据微信openid 个人认证信息
     * @param openid 微信openid
     * @return Result
     */
    @GetMapping("/userRealNameAuth/openid/{openid}")
    Result getUserRealNameAuthByOpenid(@PathVariable(value = "openid", required = true) String openid);


    @PostMapping("/examCandidateRegistration/status/new")
    Result modifyStatus(@RequestBody ExamCandidateRegistrationVo examRegistrationVo);

    @PostMapping("/examCandidateRegistration/status/record/result/new")
    Result statusRecordResultNew(@RequestBody ExamCandidateNoticeRecordResultVo candidateNoticeRecordResultVo);





    @PostMapping("/examCandidateNoticeMsg/exam/notice/new")
    Result noticeExamCandidateRegistration(@RequestBody ExamNoticeVo examNoticeVo);

    @PostMapping("/examCandidateNoticeMsg/exam/notice/record/result/new")
    Result examNoticeRecordResultNew(@RequestBody ExamNoticeRecordResultVo examNoticeRecordResultVo);



    @PostMapping("/examCandidateNoticeMsg/exam/certificate/notice/new")
    Result noticeExamCertificateNew(@RequestBody ExamNoticeVo examNoticeVo);

    @PostMapping("/examCandidateNoticeMsg/exam/certificate/notice/record/result/new")
    Result examNoticeCertificateRecordResultNew(@RequestBody ExamNoticeRecordResultVo examNoticeRecordResultVo);


    @PostMapping("/examMakeUp/setExamMakeUpStatus/new")
    Result setExamMakeUpStatus(@RequestBody ExamMakeUpVo examMakeUpVo);


}
