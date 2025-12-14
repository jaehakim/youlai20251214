package com.youlai.boot.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 사용자 내보내기 뷰 객체
 *
 * @author haoxr
 * @since 2022/4/11 8:46
 */

@Data
@ColumnWidth(20)
public class UserExportDTO {

    @ExcelProperty(value = "사용자명")
    private String username;

    @ExcelProperty(value = "사용자 닉네임")
    private String nickname;

    @ExcelProperty(value = "부서")
    private String deptName;

    @ExcelProperty(value = "성별")
    private String gender;

    @ExcelProperty(value = "휴대폰 번호")
    private String mobile;

    @ExcelProperty(value = "이메일")
    private String email;

    @ExcelProperty(value = "생성 시간")
    @DateTimeFormat("yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createTime;


}
