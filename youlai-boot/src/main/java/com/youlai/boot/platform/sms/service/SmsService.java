package com.youlai.boot.platform.sms.service;

import com.youlai.boot.platform.sms.enums.SmsTypeEnum;

import java.util.Map;

/**
 * SMS서비스인터페이스层
 *
 * @author Ray.Hao
 * @since 2024/8/17
 */
public interface SmsService {

    /**
     * 발송SMS
     *
     * @param mobile         휴대폰 번호 13388886666
     * @param smsType        SMS템플릿 SMS_194640010，템플릿내용：您의인증코드값：${code}，请에5分钟内사용
     * @param templateParams 템플릿参수 [{"code":"123456"}] ，용도替换SMS템플릿중의变量
     * @return boolean 여부발송성공
     */
    boolean sendSms(String mobile, SmsTypeEnum smsType, Map<String, String> templateParams);
}
