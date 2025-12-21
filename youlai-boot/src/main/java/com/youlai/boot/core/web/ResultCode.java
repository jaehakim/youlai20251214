package com.youlai.boot.core.web;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 응답 코드 열거형
 * <p>
 * 알리바바 개발 매뉴얼 응답 코드 규범 참조
 * 00000 정상
 * A**** 클라이언트 오류
 * B**** 시스템 실행 오류
 * C**** 서드파티 서비스 호출 오류
 *
 * @author Ray.Hao
 * @since 2020/6/23
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum ResultCode implements IResultCode, Serializable {

    SUCCESS("00000", "성공"),

    /** 1급 거시적 오류 코드  */
    USER_ERROR("A0001", "클라이언트 오류"),

    /** 2급 거시적 오류 코드  */
    USER_REGISTRATION_ERROR("A0100", "사용자 등록 오류"),
    USER_NOT_AGREE_PRIVACY_AGREEMENT("A0101", "사용자가 개인정보 처리방침에 동의하지 않음"),
    REGISTRATION_COUNTRY_OR_REGION_RESTRICTED("A0102", "등록 국가 또는 지역 제한"),

    USERNAME_VERIFICATION_FAILED("A0110", "사용자명 검증 실패"),
    USERNAME_ALREADY_EXISTS("A0111", "사용자명이 이미 존재함"),
    USERNAME_CONTAINS_SENSITIVE_WORDS("A0112", "사용자명에 민감한 단어 포함"),
    USERNAME_CONTAINS_SPECIAL_CHARACTERS("A0113", "사용자명에 특수문자 포함"),

    PASSWORD_VERIFICATION_FAILED("A0120", "비밀번호 검증 실패"),
    PASSWORD_LENGTH_NOT_ENOUGH("A0121", "비밀번호 길이 부족"),
    PASSWORD_STRENGTH_NOT_ENOUGH("A0122", "비밀번호 강도 부족"),

    VERIFICATION_CODE_INPUT_ERROR("A0130", "인증코드 입력 오류"),
    SMS_VERIFICATION_CODE_INPUT_ERROR("A0131", "SMS 인증코드 입력 오류"),
    EMAIL_VERIFICATION_CODE_INPUT_ERROR("A0132", "이메일 인증코드 입력 오류"),
    VOICE_VERIFICATION_CODE_INPUT_ERROR("A0133", "음성 인증코드 입력 오류"),

    USER_CERTIFICATE_EXCEPTION("A0140", "사용자 증명서 오류"),
    USER_CERTIFICATE_TYPE_NOT_SELECTED("A0141", "사용자 증명서 유형 미선택"),
    MAINLAND_ID_NUMBER_VERIFICATION_ILLEGAL("A0142", "신분증 번호 검증 오류"),

    USER_BASIC_INFORMATION_VERIFICATION_FAILED("A0150", "사용자 기본 정보 검증 실패"),
    PHONE_FORMAT_VERIFICATION_FAILED("A0151", "휴대폰 형식 검증 실패"),
    ADDRESS_FORMAT_VERIFICATION_FAILED("A0152", "주소 형식 검증 실패"),
    EMAIL_FORMAT_VERIFICATION_FAILED("A0153", "이메일 형식 검증 실패"),

    /** 2급 거시적 오류 코드  */
    USER_LOGIN_EXCEPTION("A0200", "사용자 로그인 오류"),
    USER_ACCOUNT_FROZEN("A0201", "사용자 계정 동결됨"),
    USER_ACCOUNT_ABOLISHED("A0202", "사용자 계정 폐기됨"),

    USER_PASSWORD_ERROR("A0210", "사용자명 또는 비밀번호 오류"),
    USER_INPUT_PASSWORD_ERROR_LIMIT_EXCEEDED("A0211", "비밀번호 오류 횟수 초과"),
    USER_NOT_EXIST("A0212", "사용자가 존재하지 않음"),

    USER_IDENTITY_VERIFICATION_FAILED("A0220", "사용자 신원 검증 실패"),
    USER_FINGERPRINT_RECOGNITION_FAILED("A0221", "사용자 지문 인식 실패"),
    USER_FACE_RECOGNITION_FAILED("A0222", "사용자 얼굴 인식 실패"),
    USER_NOT_AUTHORIZED_THIRD_PARTY_LOGIN("A0223", "사용자가 제3자 로그인 권한을 획득하지 못함"),

    ACCESS_TOKEN_INVALID("A0230", "액세스 토큰이 유효하지 않거나 만료됨"),
    REFRESH_TOKEN_INVALID("A0231", "리프레시 토큰이 유효하지 않거나 만료됨"),

    // 인증코드 오류
    USER_VERIFICATION_CODE_ERROR("A0240", "인증코드 오류"),
    USER_VERIFICATION_CODE_ATTEMPT_LIMIT_EXCEEDED("A0241", "인증코드 시도 횟수 초과"),
    USER_VERIFICATION_CODE_EXPIRED("A0242", "인증코드 만료"),

    /** 2급 거시적 오류 코드  */
    ACCESS_PERMISSION_EXCEPTION("A0300", "접근 권한 오류"),
    ACCESS_UNAUTHORIZED("A0301", "접근 미승인"),
    AUTHORIZATION_IN_PROGRESS("A0302", "권한 부여 진행 중"),
    USER_AUTHORIZATION_APPLICATION_REJECTED("A0303", "사용자 권한 신청 거부됨"),

    ACCESS_OBJECT_PRIVACY_SETTINGS_BLOCKED("A0310", "접근 대상 개인정보 설정으로 차단됨"),
    AUTHORIZATION_EXPIRED("A0311", "권한 만료됨"),
    NO_PERMISSION_TO_USE_API("A0312", "API 사용 권한 없음"),

    USER_ACCESS_BLOCKED("A0320", "사용자 접근 차단됨"),
    BLACKLISTED_USER("A0321", "블랙리스트 사용자"),
    ACCOUNT_FROZEN("A0322", "계정 동결됨"),
    ILLEGAL_IP_ADDRESS("A0323", "잘못된 IP 주소"),
    GATEWAY_ACCESS_RESTRICTED("A0324", "게이트웨이 접근 제한"),
    REGION_BLACKLIST("A0325", "지역 블랙리스트"),

    SERVICE_ARREARS("A0330", "서비스 미납"),

    USER_SIGNATURE_EXCEPTION("A0340", "사용자 서명 오류"),
    RSA_SIGNATURE_ERROR("A0341", "RSA 서명 오류"),

    /** 2급 거시적 오류 코드  */
    USER_REQUEST_PARAMETER_ERROR("A0400", "사용자 요청 파라미터 오류"),
    CONTAINS_ILLEGAL_MALICIOUS_REDIRECT_LINK("A0401", "불법 악성 리다이렉트 링크 포함"),
    INVALID_USER_INPUT("A0402", "잘못된 사용자 입력"),

    REQUEST_REQUIRED_PARAMETER_IS_EMPTY("A0410", "필수 요청 파라미터 누락"),

    REQUEST_PARAMETER_VALUE_EXCEEDS_ALLOWED_RANGE("A0420", "요청 파라미터 값이 허용 범위 초과"),
    PARAMETER_FORMAT_MISMATCH("A0421", "파라미터 형식 불일치"),

    USER_INPUT_CONTENT_ILLEGAL("A0430", "사용자 입력 내용 불법"),
    CONTAINS_PROHIBITED_SENSITIVE_WORDS("A0431", "금지된 민감한 단어 포함"),

    USER_OPERATION_EXCEPTION("A0440", "사용자 작업 오류"),

    /** 2급 거시적 오류 코드  */
    USER_REQUEST_SERVICE_EXCEPTION("A0500", "사용자 서비스 요청 오류"),
    REQUEST_LIMIT_EXCEEDED("A0501", "요청 횟수 제한 초과"),
    REQUEST_CONCURRENCY_LIMIT_EXCEEDED("A0502", "요청 동시성 제한 초과"),
    USER_OPERATION_PLEASE_WAIT("A0503", "사용자 작업 대기 필요"),
    WEBSOCKET_CONNECTION_EXCEPTION("A0504", "WebSocket 연결 오류"),
    WEBSOCKET_CONNECTION_DISCONNECTED("A0505", "WebSocket 연결 끊김"),
    USER_DUPLICATE_REQUEST("A0506", "요청이 너무 빈번합니다. 잠시 후 다시 시도해주세요."),

    /** 2급 거시적 오류 코드  */
    USER_RESOURCE_EXCEPTION("A0600", "사용자 리소스 오류"),
    ACCOUNT_BALANCE_INSUFFICIENT("A0601", "계정 잔액 부족"),
    USER_DISK_SPACE_INSUFFICIENT("A0602", "사용자 디스크 공간 부족"),
    USER_MEMORY_SPACE_INSUFFICIENT("A0603", "사용자 메모리 공간 부족"),
    USER_OSS_CAPACITY_INSUFFICIENT("A0604", "사용자 OSS 용량 부족"),
    USER_QUOTA_EXHAUSTED("A0605", "사용자 할당량 소진"),
    USER_RESOURCE_NOT_FOUND("A0606", "사용자 리소스가 존재하지 않음"),

    /** 2급 거시적 오류 코드  */
    UPLOAD_FILE_EXCEPTION("A0700", "파일 업로드 오류"),
    UPLOAD_FILE_TYPE_MISMATCH("A0701", "업로드 파일 유형 불일치"),
    UPLOAD_FILE_TOO_LARGE("A0702", "업로드 파일이 너무 큼"),
    UPLOAD_IMAGE_TOO_LARGE("A0703", "업로드 이미지가 너무 큼"),
    UPLOAD_VIDEO_TOO_LARGE("A0704", "업로드 동영상이 너무 큼"),
    UPLOAD_COMPRESSED_FILE_TOO_LARGE("A0705", "업로드 압축 파일이 너무 큼"),

    DELETE_FILE_EXCEPTION("A0710", "파일 삭제 오류"),

    /** 2급 거시적 오류 코드  */
    USER_CURRENT_VERSION_EXCEPTION("A0800", "사용자 현재 버전 오류"),
    USER_INSTALLED_VERSION_NOT_MATCH_SYSTEM("A0801", "사용자 설치 버전과 시스템 불일치"),
    USER_INSTALLED_VERSION_TOO_LOW("A0802", "사용자 설치 버전이 너무 낮음"),
    USER_INSTALLED_VERSION_TOO_HIGH("A0803", "사용자 설치 버전이 너무 높음"),
    USER_INSTALLED_VERSION_EXPIRED("A0804", "사용자 설치 버전 만료됨"),
    USER_API_REQUEST_VERSION_NOT_MATCH("A0805", "사용자 API 요청 버전 불일치"),
    USER_API_REQUEST_VERSION_TOO_HIGH("A0806", "사용자 API 요청 버전이 너무 높음"),
    USER_API_REQUEST_VERSION_TOO_LOW("A0807", "사용자 API 요청 버전이 너무 낮음"),

    /** 2급 거시적 오류 코드  */
    USER_PRIVACY_NOT_AUTHORIZED("A0900", "사용자 개인정보 미승인"),
    USER_PRIVACY_NOT_SIGNED("A0901", "사용자 개인정보 미서명"),
    USER_CAMERA_NOT_AUTHORIZED("A0903", "사용자 카메라 미승인"),
    USER_PHOTO_LIBRARY_NOT_AUTHORIZED("A0904", "사용자 사진 라이브러리 미승인"),
    USER_FILE_NOT_AUTHORIZED("A0905", "사용자 파일 미승인"),
    USER_LOCATION_INFORMATION_NOT_AUTHORIZED("A0906", "사용자 위치 정보 미승인"),
    USER_CONTACTS_NOT_AUTHORIZED("A0907", "사용자 연락처 미승인"),

    /** 2급 거시적 오류 코드  */
    USER_DEVICE_EXCEPTION("A1000", "사용자 기기 오류"),
    USER_CAMERA_EXCEPTION("A1001", "사용자 카메라 오류"),
    USER_MICROPHONE_EXCEPTION("A1002", "사용자 마이크 오류"),
    USER_EARPIECE_EXCEPTION("A1003", "사용자 이어피스 오류"),
    USER_SPEAKER_EXCEPTION("A1004", "사용자 스피커 오류"),
    USER_GPS_POSITIONING_EXCEPTION("A1005", "사용자 GPS 위치 오류"),

    /** 1급 거시적 오류 코드  */
    SYSTEM_ERROR("B0001", "시스템 실행 오류"),

    /** 2급 거시적 오류 코드  */
    SYSTEM_EXECUTION_TIMEOUT("B0100", "시스템 실행 시간 초과"),

    /** 2급 거시적 오류 코드  */
    SYSTEM_DISASTER_RECOVERY_FUNCTION_TRIGGERED("B0200", "시스템 재해 복구 기능 발동"),

    SYSTEM_RATE_LIMITING("B0210", "시스템 속도 제한"),

    SYSTEM_FUNCTION_DEGRADATION("B0220", "시스템 기능 저하"),

    /** 2급 거시적 오류 코드  */
    SYSTEM_RESOURCE_EXCEPTION("B0300", "시스템 리소스 오류"),
    SYSTEM_RESOURCE_EXHAUSTED("B0310", "시스템 리소스 소진"),
    SYSTEM_DISK_SPACE_EXHAUSTED("B0311", "시스템 디스크 공간 소진"),
    SYSTEM_MEMORY_EXHAUSTED("B0312", "시스템 메모리 소진"),
    FILE_HANDLE_EXHAUSTED("B0313", "파일 핸들 소진"),
    SYSTEM_CONNECTION_POOL_EXHAUSTED("B0314", "시스템 연결 풀 소진"),
    SYSTEM_THREAD_POOL_EXHAUSTED("B0315", "시스템 스레드 풀 소진"),

    SYSTEM_RESOURCE_ACCESS_EXCEPTION("B0320", "시스템 리소스 접근 오류"),
    SYSTEM_READ_DISK_FILE_FAILED("B0321", "시스템 디스크 파일 읽기 실패"),


    /** 1급 거시적 오류 코드  */
    THIRD_PARTY_SERVICE_ERROR("C0001", "제3자 서비스 호출 오류"),

    /** 2급 거시적 오류 코드  */
    MIDDLEWARE_SERVICE_ERROR("C0100", "미들웨어 서비스 오류"),

    RPC_SERVICE_ERROR("C0110", "RPC 서비스 오류"),
    RPC_SERVICE_NOT_FOUND("C0111", "RPC 서비스를 찾을 수 없음"),
    RPC_SERVICE_NOT_REGISTERED("C0112", "RPC 서비스 미등록"),
    INTERFACE_NOT_EXIST("C0113", "인터페이스가 존재하지 않음"),

    MESSAGE_SERVICE_ERROR("C0120", "메시지 서비스 오류"),
    MESSAGE_DELIVERY_ERROR("C0121", "메시지 전달 오류"),
    MESSAGE_CONSUMPTION_ERROR("C0122", "메시지 소비 오류"),
    MESSAGE_SUBSCRIPTION_ERROR("C0123", "메시지 구독 오류"),
    MESSAGE_GROUP_NOT_FOUND("C0124", "메시지 그룹을 찾을 수 없음"),

    CACHE_SERVICE_ERROR("C0130", "캐시 서비스 오류"),
    KEY_LENGTH_EXCEEDS_LIMIT("C0131", "key 길이 제한 초과"),
    VALUE_LENGTH_EXCEEDS_LIMIT("C0132", "value 길이 제한 초과"),
    STORAGE_CAPACITY_FULL("C0133", "저장 용량 초과"),
    UNSUPPORTED_DATA_FORMAT("C0134", "지원하지 않는 데이터 형식"),

    CONFIGURATION_SERVICE_ERROR("C0140", "구성 서비스 오류"),

    NETWORK_RESOURCE_SERVICE_ERROR("C0150", "네트워크 리소스 서비스 오류"),
    VPN_SERVICE_ERROR("C0151", "VPN 서비스 오류"),
    CDN_SERVICE_ERROR("C0152", "CDN 서비스 오류"),
    DOMAIN_NAME_RESOLUTION_SERVICE_ERROR("C0153", "도메인 이름 해석 서비스 오류"),
    GATEWAY_SERVICE_ERROR("C0154", "게이트웨이 서비스 오류"),

    /** 2급 거시적 오류 코드  */
    THIRD_PARTY_SYSTEM_EXECUTION_TIMEOUT("C0200", "제3자 시스템 실행 시간 초과"),

    RPC_EXECUTION_TIMEOUT("C0210", "RPC 실행 시간 초과"),

    MESSAGE_DELIVERY_TIMEOUT("C0220", "메시지 전달 시간 초과"),

    CACHE_SERVICE_TIMEOUT("C0230", "캐시 서비스 시간 초과"),

    CONFIGURATION_SERVICE_TIMEOUT("C0240", "구성 서비스 시간 초과"),

    DATABASE_SERVICE_TIMEOUT("C0250", "데이터베이스 서비스 시간 초과"),

    /** 2급 거시적 오류 코드  */
    DATABASE_SERVICE_ERROR("C0300", "데이터베이스 서비스 오류"),

    TABLE_NOT_EXIST("C0311", "테이블이 존재하지 않음"),
    COLUMN_NOT_EXIST("C0312", "열이 존재하지 않음"),
    DATABASE_EXECUTION_SYNTAX_ERROR("C0313", "데이터베이스 실행 구문 오류"),

    MULTIPLE_SAME_NAME_COLUMNS_IN_MULTI_TABLE_ASSOCIATION("C0321", "다중 테이블 조인에 동일한 이름의 열이 여러 개 존재"),

    DATABASE_DEADLOCK("C0331", "데이터베이스 교착 상태"),

    PRIMARY_KEY_CONFLICT("C0341", "기본키 충돌"),
    INTEGRITY_CONSTRAINT_VIOLATION("C0342", "무결성 제약 조건 위반"),

    DATABASE_ACCESS_DENIED("C0351", "데모 환경에서는 데이터베이스 쓰기 기능이 비활성화되어 있습니다. 로컬에서 데이터베이스 연결을 수정하거나 Mock 모드를 활성화하여 체험해주세요"),

    /** 2급 거시적 오류 코드  */
    THIRD_PARTY_DISASTER_RECOVERY_SYSTEM_TRIGGERED("C0400", "제3자 재해 복구 시스템 발동"),
    THIRD_PARTY_SYSTEM_RATE_LIMITING("C0401", "제3자 시스템 속도 제한"),
    THIRD_PARTY_FUNCTION_DEGRADATION("C0402", "제3자 기능 저하"),

    /** 2급 거시적 오류 코드  */
    NOTIFICATION_SERVICE_ERROR("C0500", "알림 서비스 오류"),
    SMS_REMINDER_SERVICE_FAILED("C0501", "SMS 알림 서비스 실패"),
    VOICE_REMINDER_SERVICE_FAILED("C0502", "음성 알림 서비스 실패"),
    EMAIL_REMINDER_SERVICE_FAILED("C0503", "이메일 알림 서비스 실패");


    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

    private String code;

    private String msg;

    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + code + '\"' +
                ", \"msg\":\"" + msg + '\"' +
                '}';
    }


    public static ResultCode getValue(String code) {
        for (ResultCode value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return SYSTEM_ERROR; // 기본값: 시스템 실행 오류
    }
}
