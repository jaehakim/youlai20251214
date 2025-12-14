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
 * 알리바바 클라우드SMS비즈니스类
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
     * @param templateParams 템플릿参수 [{"code":"123456"}]
     * @return boolean 여부발송성공
     */
    @Override
    public boolean sendSms(String mobile, SmsTypeEnum smsType, Map<String, String> templateParams) {

        String templateCode = aliyunSmsProperties.getTemplates().get(smsType.getValue());

        DefaultProfile profile = DefaultProfile.getProfile(aliyunSmsProperties.getRegionId(),
                aliyunSmsProperties.getAccessKeyId(), aliyunSmsProperties.getAccessKeySecret());
        IAcsClient client = new DefaultAcsClient(profile);

        // 생성通用의请求객체
        CommonRequest request = new CommonRequest();
        // 지정된请求方式
        request.setSysMethod(MethodType.POST);
        // SMSapi의请求주소(固定)
        request.setSysDomain(aliyunSmsProperties.getDomain());
        // 签名算法版(固定)
        request.setSysVersion("2017-05-25");
        // 请求 API 의이름(固定)
        request.setSysAction("SendSms");
        // 지정된地域이름
        request.putQueryParameter("RegionId", aliyunSmsProperties.getRegionId());
        // 要给哪个휴대폰 번호발송SMS  지정된휴대폰 번호
        request.putQueryParameter("PhoneNumbers", mobile);
        // 您의申请签名
        request.putQueryParameter("SignName", aliyunSmsProperties.getSignName());
        // 您申请의템플릿 code
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
