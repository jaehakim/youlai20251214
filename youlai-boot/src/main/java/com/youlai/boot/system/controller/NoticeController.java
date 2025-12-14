package com.youlai.boot.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.system.model.form.NoticeForm;
import com.youlai.boot.system.model.query.NoticePageQuery;
import com.youlai.boot.system.model.vo.NoticeDetailVO;
import com.youlai.boot.system.model.vo.NoticePageVO;
import com.youlai.boot.system.model.vo.UserNoticePageVO;
import com.youlai.boot.system.service.NoticeService;
import com.youlai.boot.system.service.UserNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 공지사항프론트엔드 컨트롤러
 *
 * @author youlaitech
 * @since 2024-08-27 10:31
 */
@Tag(name = "09.공지사항")
@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    private final UserNoticeService userNoticeService;

    @Operation(summary = "공지사항페이지 목록")
    @GetMapping("/page")
    @PreAuthorize("@ss.hasPerm('sys:notice:query')")
    public PageResult<NoticePageVO> getNoticePage(NoticePageQuery queryParams) {
        IPage<NoticePageVO> result = noticeService.getNoticePage(queryParams);
        return PageResult.success(result);
    }

    @Operation(summary = "추가공지사항")
    @PostMapping
    @PreAuthorize("@ss.hasPerm('sys:notice:add')")
    public Result<?> saveNotice(@RequestBody @Valid NoticeForm formData) {
        boolean result = noticeService.saveNotice(formData);
        return Result.judge(result);
    }

    @Operation(summary = "공지사항 폼 데이터 조회")
    @GetMapping("/{id}/form")
    @PreAuthorize("@ss.hasPerm('sys:notice:edit')")
    public Result<NoticeForm> getNoticeForm(
            @Parameter(description = "공지사항ID") @PathVariable Long id
    ) {
        NoticeForm formData = noticeService.getNoticeFormData(id);
        return Result.success(formData);
    }

    @Operation(summary = "공지사항 상세 읽기 조회")
    @GetMapping("/{id}/detail")
    public Result<NoticeDetailVO> getNoticeDetail(
            @Parameter(description = "공지사항ID") @PathVariable Long id
    ) {
        NoticeDetailVO detailVO = noticeService.getNoticeDetail(id);
        return Result.success(detailVO);
    }

    @Operation(summary = "수정공지사항")
    @PutMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPerm('sys:notice:edit')")
    public Result<Void> updateNotice(
            @Parameter(description = "공지사항ID") @PathVariable Long id,
            @RequestBody @Validated NoticeForm formData
    ) {
        boolean result = noticeService.updateNotice(id, formData);
        return Result.judge(result);
    }

    @Operation(summary = "발행공지사항")
    @PutMapping("/{id}/publish")
    @PreAuthorize("@ss.hasPerm('sys:notice:publish')")
    public Result<Void> publishNotice(
            @Parameter(description = "공지사항ID") @PathVariable Long id
    ) {
        boolean result = noticeService.publishNotice(id);
        return Result.judge(result);
    }

    @Operation(summary = "회수공지사항")
    @PutMapping("/{id}/revoke")
    @PreAuthorize("@ss.hasPerm('sys:notice:revoke')")
    public Result<Void> revokeNotice(
            @Parameter(description = "공지사항ID") @PathVariable Long id
    ) {
        boolean result = noticeService.revokeNotice(id);
        return Result.judge(result);
    }

    @Operation(summary = "삭제공지사항")
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPerm('sys:notice:delete')")
    public Result<Void> deleteNotices(
            @Parameter(description = "공지사항ID，여러 개는영문쉼표(,)로 구분") @PathVariable String ids
    ) {
        boolean result = noticeService.deleteNotices(ids);
        return Result.judge(result);
    }

    @Operation(summary = "전체 읽음")
    @PutMapping("/read-all")
    public Result<Void> readAll() {
        userNoticeService.readAll();
        return Result.success();
    }

    @Operation(summary = "내 공지사항 페이지 목록 조회")
    @GetMapping("/my")
    public PageResult<UserNoticePageVO> getMyNoticePage(
            NoticePageQuery queryParams
    ) {
        IPage<UserNoticePageVO> result = noticeService.getMyNoticePage(queryParams);
        return PageResult.success(result);
    }
}
