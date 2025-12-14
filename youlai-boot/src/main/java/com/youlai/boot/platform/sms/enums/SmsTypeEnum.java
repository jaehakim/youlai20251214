package com.youlai.boot.platform.sms.enums;

import com.youlai.boot.common.base.IBaseEnum;
import lombok.Getter;

/**
 * SMS 유형 열거형
 * <p>
 * value 값은 application-*.yml의 sms.templates.* 설정과 대응
 *
 * @author Ray.Hao
 * @since 2.21.0
 */
@Getter
public enum SmsTypeEnum implements IBaseEnum<String> {

    /**
     * 회원가입 SMS 인증 코드
     */
    REGISTER("register", "회원가입 SMS 인증 코드"),

    /**
     * 로그인 SMS 인증 코드
     */
    LOGIN("login", "로그인 SMS 인증 코드"),

    /**
     * 휴대폰 번호 변경 SMS 인증 코드
     */
    CHANGE_MOBILE("change-mobile", "휴대폰 번호 변경 SMS 인증 코드");

    private final String value;
    private final String label;

    SmsTypeEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
