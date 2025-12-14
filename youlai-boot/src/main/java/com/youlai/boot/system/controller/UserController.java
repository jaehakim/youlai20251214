package com.youlai.boot.system.controller;

import cn.idev.excel.EasyExcel;
import cn.idev.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.common.annotation.Log;
import com.youlai.boot.common.annotation.RepeatSubmit;
import com.youlai.boot.common.enums.LogModuleEnum;
import com.youlai.boot.common.model.Option;
import com.youlai.boot.core.web.ExcelResult;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.common.util.ExcelUtils;
import com.youlai.boot.security.util.SecurityUtils;
import com.youlai.boot.system.listener.UserImportListener;
import com.youlai.boot.system.model.dto.UserExportDTO;
import com.youlai.boot.system.model.dto.UserImportDTO;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.model.form.*;
import com.youlai.boot.system.model.query.UserPageQuery;
import com.youlai.boot.system.model.dto.CurrentUserDTO;
import com.youlai.boot.system.model.vo.UserPageVO;
import com.youlai.boot.system.model.vo.UserProfileVO;
import com.youlai.boot.system.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 사용자 컨트롤러
 *
 * @author Ray.Hao
 * @since 2022/10/16
 */
@Tag(name = "02.사용자 인터페이스")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 페이지 목록")
    @GetMapping("/page")
    @Log(value = "사용자 페이지 목록", module = LogModuleEnum.USER)
    public PageResult<UserPageVO> getUserPage(
            @Valid UserPageQuery queryParams
    ) {
        IPage<UserPageVO> result = userService.getUserPage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "사용자 추가")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:user:add')")
    @RepeatSubmit
    @Log(value = "사용자 추가", module = LogModuleEnum.USER)
    public Result<?> saveUser(
            @RequestBody @Valid UserForm userForm
    ) {
        boolean result = userService.saveUser(userForm);
        return Result.judge(result);
    }

    @Operation(summary = "사용자 폼 데이터 조회")
    @GetMapping("/{userId}/form")
    @PreAuthorize("@ss.hasPerm('sys:user:edit')")
    @Log(value = "사용자폼데이터", module = LogModuleEnum.USER)
    public Result<UserForm> getUserForm(
            @Parameter(description = "사용자ID") @PathVariable Long userId
    ) {
        UserForm formData = userService.getUserFormData(userId);
        return Result.success(formData);
    }

    @Operation(summary = "사용자 수정")
    @PutMapping(value = "/{userId}")
    @PreAuthorize("@ss.hasPerm('sys:user:edit')")
    @Log(value = "사용자 수정", module = LogModuleEnum.USER)
    public Result<Void> updateUser(
            @Parameter(description = "사용자ID") @PathVariable Long userId,
            @RequestBody @Valid UserForm userForm
    ) {
        boolean result = userService.updateUser(userId, userForm);
        return Result.judge(result);
    }

    @Operation(summary = "사용자 삭제")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('sys:user:delete')")
    @Log(value = "사용자 삭제", module = LogModuleEnum.USER)
    public Result<Void> deleteUsers(
            @Parameter(description = "사용자ID，여러 개는영문쉼표(,)로 구분") @PathVariable String ids
    ) {
        boolean result = userService.deleteUsers(ids);
        return Result.judge(result);
    }

    @Operation(summary = "사용자 수정상태")
    @PatchMapping(value = "/{userId}/status")
    @PreAuthorize("@ss.hasPerm('sys:user:edit')")
    @Log(value = "사용자 수정상태", module = LogModuleEnum.USER)
    public Result<Void> updateUserStatus(
            @Parameter(description = "사용자ID") @PathVariable Long userId,
            @Parameter(description = "사용자상태(1:활성화;0:비활성화)") @RequestParam Integer status
    ) {
        boolean result = userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getStatus, status)
        );
        return Result.judge(result);
    }

    @Operation(summary = "현재 로그인 사용자 정보 조회")
    @GetMapping("/me")
    @Log(value = "현재 로그인 사용자 정보 조회", module = LogModuleEnum.USER)
    public Result<CurrentUserDTO> getCurrentUser() {
        CurrentUserDTO currentUserDTO = userService.getCurrentUserInfo();
        return Result.success(currentUserDTO);
    }

    @Operation(summary = "사용자 가져오기 템플릿 다운로드")
    @GetMapping("/template")
    @Log(value = "사용자 가져오기 템플릿 다운로드", module = LogModuleEnum.USER)
    public void downloadTemplate(HttpServletResponse response)  {
        String fileName = "사용자 가져오기템플릿.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        String fileClassPath = "templates" + File.separator + "excel" + File.separator + fileName;
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileClassPath);

        try (ServletOutputStream outputStream = response.getOutputStream();
             ExcelWriter excelWriter = EasyExcel.write(outputStream).withTemplate(inputStream).build()) {
            excelWriter.finish();
        } catch (IOException e) {
            throw new RuntimeException("사용자 가져오기 템플릿 다운로드실패", e);
        }
    }

    @Operation(summary = "사용자 가져오기")
    @PostMapping("/import")
    @PreAuthorize("@ss.hasPerm('sys:user:import')")
    @Log(value = "사용자 가져오기", module = LogModuleEnum.USER)
    public Result<ExcelResult> importUsers(MultipartFile file) throws IOException {
        UserImportListener listener = new UserImportListener();
        ExcelUtils.importExcel(file.getInputStream(), UserImportDTO.class, listener);
        return Result.success(listener.getExcelResult());
    }

    @Operation(summary = "사용자 내보내기")
    @GetMapping("/export")
    @PreAuthorize("@ss.hasPerm('sys:user:export')")
    @Log(value = "사용자 내보내기", module = LogModuleEnum.USER)
    public void exportUsers(UserPageQuery queryParams, HttpServletResponse response) throws IOException {
        String fileName = "사용자 목록.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        List<UserExportDTO> exportUserList = userService.listExportUsers(queryParams);
        EasyExcel.write(response.getOutputStream(), UserExportDTO.class).sheet("사용자 목록")
                .doWrite(exportUserList);
    }

    @Operation(summary = "개인센터 사용자 정보 조회")
    @GetMapping("/profile")
    @Log(value = "개인센터 사용자 정보 조회", module = LogModuleEnum.USER)
    public Result<UserProfileVO> getUserProfile() {
        Long userId = SecurityUtils.getUserId();
        UserProfileVO userProfile = userService.getUserProfile(userId);
        return Result.success(userProfile);
    }

    @Operation(summary = "개인센터 사용자 정보 수정")
    @PutMapping("/profile")
    @Log(value = "개인센터 사용자 정보 수정", module = LogModuleEnum.USER)
    public Result<?> updateUserProfile(@RequestBody UserProfileForm formData) {
        boolean result = userService.updateUserProfile(formData);
        return Result.judge(result);
    }

    @Operation(summary = "지정된 사용자 비밀번호 재설정")
    @PutMapping(value = "/{userId}/password/reset")
    @PreAuthorize("@ss.hasPerm('sys:user:reset-password')")
    public Result<?> resetUserPassword(
            @Parameter(description = "사용자ID") @PathVariable Long userId,
            @RequestParam String password
    ) {
        boolean result = userService.resetUserPassword(userId, password);
        return Result.judge(result);
    }

    @Operation(summary = "현재 사용자 비밀번호 변경")
    @PutMapping(value = "/password")
    public Result<?> changeCurrentUserPassword(
            @RequestBody PasswordUpdateForm data
    ) {
        Long currUserId = SecurityUtils.getUserId();
        boolean result = userService.changeUserPassword(currUserId, data);
        return Result.judge(result);
    }

    @Operation(summary = "발송SMS 인증코드(휴대폰 번호 바인딩 또는 변경)")
    @PostMapping(value = "/mobile/code")
    public Result<?> sendMobileCode(
            @Parameter(description = "휴대폰 번호", required = true) @RequestParam String mobile
    ) {
        boolean result = userService.sendMobileCode(mobile);
        return Result.judge(result);
    }

    @Operation(summary = "휴대폰 번호 바인딩 또는 변경")
    @PutMapping(value = "/mobile")
    public Result<?> bindOrChangeMobile(
            @RequestBody @Validated MobileUpdateForm data
    ) {
        boolean result = userService.bindOrChangeMobile(data);
        return Result.judge(result);
    }

    @Operation(summary = "발송이메일 인증코드(이메일 바인딩 또는 변경)")
    @PostMapping(value = "/email/code")
    public Result<Void> sendEmailCode(
            @Parameter(description = "이메일 주소", required = true) @RequestParam String email
    ) {
        userService.sendEmailCode(email);
        return Result.success();
    }

    @Operation(summary = "이메일 바인딩 또는 변경")
    @PutMapping(value = "/email")
    public Result<?> bindOrChangeEmail(
            @RequestBody @Validated EmailUpdateForm data
    ) {
        boolean result = userService.bindOrChangeEmail(data);
        return Result.judge(result);
    }

    @Operation(summary = "사용자 드롭다운 옵션 조회")
    @GetMapping("/options")
    public Result<List<Option<String>>> listUserOptions() {
        List<Option<String>> list = userService.listUserOptions();
        return Result.success(list);
    }
}
