package com.youlai.boot.system.model.dto;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 사용자 가져오기 객체
 *
 * @author Ray.Hao
 * @since 2022/4/10
 */
@Data
public class UserImportDTO {

    @ExcelProperty(value = "사용자명")
    private String username;

    @ExcelProperty(value = "닉네임")
    private String nickname;

    @ExcelProperty(value = "성별")
    private String genderLabel;

    @ExcelProperty(value = "휴대폰 번호")
    private String mobile;

    @ExcelProperty(value = "이메일")
    private String email;

    @ExcelProperty("역할")
    private String roleCodes;

    @ExcelProperty("부서")
    private String deptCode;

}
