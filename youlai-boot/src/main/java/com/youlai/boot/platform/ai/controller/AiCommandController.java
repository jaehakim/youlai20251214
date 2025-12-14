package com.youlai.boot.platform.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.ai.model.dto.AiExecuteRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseResponseDTO;
import com.youlai.boot.platform.ai.model.query.AiCommandPageQuery;
import com.youlai.boot.platform.ai.model.vo.AiCommandRecordVO;
import com.youlai.boot.platform.ai.service.AiCommandRecordService;
import com.youlai.boot.platform.ai.service.AiCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI 명령컨트롤러（基于 Spring AI）
 *
 * @author Ray.Hao
 * @since 3.0.0
 */
@Tag(name = "AI 명령인터페이스")
@RestController
@RequestMapping("/api/v1/ai/command")
@RequiredArgsConstructor
@Slf4j
public class AiCommandController {

  private final AiCommandService aiCommandService;
  private final AiCommandRecordService recordService;

  @Operation(summary = "자연어 명령 파싱")
  @PostMapping("/parse")
  public Result<AiParseResponseDTO> parseCommand(
    @RequestBody AiParseRequestDTO request,
    HttpServletRequest httpRequest
  ) {
    log.info("收到AI 명령파싱 요청: {}", request.getCommand());

    try {
      AiParseResponseDTO response = aiCommandService.parseCommand(request, httpRequest);
      return Result.success(response);
    } catch (Exception e) {
      log.error("명령解析실패", e);
      return Result.success(AiParseResponseDTO.builder()
        .success(false)
        .error(e.getMessage())
        .build());
    }
  }

  @Operation(summary = "파싱된 명령 실행")
  @PostMapping("/execute")
  public Result<Object> executeCommand(
    @RequestBody AiExecuteRequestDTO request,
    HttpServletRequest httpRequest
  ) {
    log.info("收到AI 명령执行请求: {}", request.getFunctionCall().getName());
    try {
      Object result = aiCommandService.executeCommand(request, httpRequest);
      return Result.success(result);
    } catch (Exception e) {
      log.error("명령 실행실패", e);
      return Result.failed(e.getMessage());
    }
  }

  @Operation(summary = "AI 명령 기록 페이지 목록 조회")
  @GetMapping("/records")
  public PageResult<AiCommandRecordVO> getRecordPage(AiCommandPageQuery queryParams) {
    IPage<AiCommandRecordVO> page = recordService.getRecordPage(queryParams);
    return PageResult.success(page);
  }

  @Operation(summary = "명령 실행 취소")
  @PostMapping("/rollback/{recordId}")
  public Result<?> rollbackCommand(
    @Parameter(description = "기록 ID") @PathVariable String recordId
  ) {
    recordService.rollbackCommand(recordId);
    return Result.success("撤销성공");
  }

}




