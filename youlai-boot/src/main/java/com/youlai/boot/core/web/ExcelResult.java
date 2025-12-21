package com.youlai.boot.core.web;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel 내보내기 응답 구조체
 *
 * @author Theo
 * @since 2025/1/14 11:46:08
 */
@Data
public class ExcelResult {

    /**
     * 응답 코드, 가져오기 성공 여부를 확인
     */
    private String code;

    /**
     * 유효한 항목 수
     */
    private Integer validCount;

    /**
     * 무효한 항목 수
     */
    private Integer invalidCount;

    /**
     * 오류 메시지 정보
     */
    private List<String> messageList;

    public ExcelResult() {
        this.code = ResultCode.SUCCESS.getCode();
        this.validCount = 0;
        this.invalidCount = 0;
        this.messageList = new ArrayList<>();
    }
}
