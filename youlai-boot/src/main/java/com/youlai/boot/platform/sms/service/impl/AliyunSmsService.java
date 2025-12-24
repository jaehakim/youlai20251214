package com.youlai.boot.platform.sms.service.impl;

import cn.hutool.json.JSONUtil;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.youlai.boot.config.property.AliyunSmsProperties;
import com.youlai.boot.platform.sms.enums.SmsTypeEnum;
import com.youlai.boot.platform.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 알리바바 클라우드 SMS 서비스 클래스
 *
 * @author Ray
 * @since 2024/8/17
 */
@Service
@RequiredArgsConstructor
public class AliyunSmsService implements SmsService {

    private final AliyunSmsProperties aliyunSmsProperties;

    /**
     * 발송SMS인증코드
     *
     * @param mobile         휴대폰 번호 13388886666
     * @param smsType        SMS템플릿 SMS_194640010
     * @param templateParams 템플릿파라미터수 [{"code":"123456"}]
     * @return boolean 여부발송성공
     */
    @Override
    public boolean sendSms(String mobile, SmsTypeEnum smsType, Map<String, String> templateParams) {

        String templateCode = aliyunSmsProperties.getTemplates().get(smsType.getValue());

        DefaultProfile profile = DefaultProfile.getProfile(aliyunSmsProperties.getRegionId(),
                aliyunSmsProperties.getAccessKeyId(), aliyunSmsProperties.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        // 공통 요청 객체 생성
        CommonRequest request = new CommonRequest();
        // 요청 방식 지정
        request.setSysMethod(MethodType.POST);
        // SMS API의 요청 주소 (고정)
        request.setSysDomain(aliyunSmsProperties.getDomain());
        // 서명 알고리즘 버전 (고정)
        request.setSysVersion("2017-05-25");
        // 요청 API의 이름 (고정)
        request.setSysAction("SendSms");
        // 지역 이름 지정
        request.putQueryParameter("RegionId", aliyunSmsProperties.getRegionId());
        // SMS를 전송할 휴대폰 번호 지정
        request.putQueryParameter("PhoneNumbers", mobile);
        // 신청한 서명
        request.putQueryParameter("SignName", aliyunSmsProperties.getSignName());
        // 신청한 템플릿 코드
        request.putQueryParameter("TemplateCode", templateCode);

        request.putQueryParameter("TemplateParam", JSONUtil.toJsonStr(templateParams));

        try {
            CommonResponse response = client.getCommonResponse(request);
            return response.getHttpResponse().isSuccess();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }


}
