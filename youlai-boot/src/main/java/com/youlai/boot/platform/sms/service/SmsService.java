package com.youlai.boot.platform.sms.service;

import com.youlai.boot.platform.sms.enums.SmsTypeEnum;

import java.util.Map;

/**
 * SMS 서비스 인터페이스
 *
 * @author Ray.Hao
 * @since 2024/8/17
 */
public interface SmsService {

    /**
     * 발송SMS
     *
     * @param mobile         휴대폰 번호 13388886666
     * @param smsType        SMS템플릿 SMS_194640010，템플릿내용：귀사의인증코드값：${code}，요청에5분 내사용
     * @param templateParams 템플릿파라미터수 [{"code":"123456"}] ，용도치환SMS템플릿중의변수
     * @return boolean 여부발송성공
     */
    boolean sendSms(String mobile, SmsTypeEnum smsType, Map<String, String> templateParams);
}
