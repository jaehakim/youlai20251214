package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.youlai.boot.common.enums.LogModuleEnum;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 시스템 로그 엔티티 클래스
 *
 * @author Ray.Hao
 * @since 2.10.0
 */
@Data
@TableName("sys_log")
public class Log implements Serializable {

    /**
     * 기본 키
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 로그 모듈
     */
    private LogModuleEnum module;

    /**
     * 요청 방식
     */
    @TableField(value = "request_method")
    private String requestMethod;

    /**
     * 요청 파라미터
     */
    @TableField(value = "request_params")
    private String requestParams;

    /**
     * 응답 내용
     */
    @TableField(value = "response_content")
    private String responseContent;

    /**
     * 로그 내용
     */
    private String content;

    /**
     * 요청 경로
     */
    private String requestUri;

    /**
     * 요청 메서드
     */
    private String method;

    /**
     * IP 주소
     */
    private String ip;

    /**
     * 지역
     */
    private String province;

    /**
     * 도시
     */
    private String city;

    /**
     * 브라우저
     */
    private String browser;

    /**
     * 브라우저 버전
     */
    private String browserVersion;

    /**
     * 운영 체제
     */
    private String os;

    /**
     * 실행 시간(밀리초)
     */
    private Long executionTime;

    /**
     * 생성자 ID
     */
    private Long createBy;

    /**
     * 생성 시간
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}