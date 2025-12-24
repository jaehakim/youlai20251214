package com.youlai.boot.platform.ai.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youlai.boot.platform.ai.model.dto.AiExecuteRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiFunctionCallDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseRequestDTO;
import com.youlai.boot.platform.ai.model.dto.AiParseResponseDTO;
import com.youlai.boot.platform.ai.model.entity.AiCommandRecord;
import com.youlai.boot.platform.ai.service.AiCommandRecordService;
import com.youlai.boot.platform.ai.service.AiCommandService;
import com.youlai.boot.platform.ai.tools.UserTools;
import com.youlai.boot.security.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AI 명령 오케스트레이션 서비스 구현
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiCommandServiceImpl implements AiCommandService {

  private static final String SYSTEM_PROMPT = """
    당신은 지능형 엔터프라이즈 운영 도우미입니다，사용자의 자연어 명령을 표준 함수 호출로 파싱해야 합니다을사용자의자연어명령파싱표준으로의함수수호출。
    엄격한 JSON 형식으로 반환해주세요. 포함할 필드:
    - success: boolean
    - explanation: string
    - confidence: number (0-1)
    - error: string
    - provider: string
    - model: string
    - functionCalls: 배열, 각 요소는 name, description, arguments(객체) 포함
    명령을 인식할 수 없을 때는 success=false로 설정하고 error를 제공하세요.
    """;

  private final AiCommandRecordService recordService;
  private final UserTools userTools;
  private final ChatClient chatClient;

  @Override
  public AiParseResponseDTO parseCommand(AiParseRequestDTO request, HttpServletRequest httpRequest) {
    long startTime = System.currentTimeMillis();
    String command = Optional.ofNullable(request.getCommand()).orElse("").trim();

    if (StrUtil.isBlank(command)) {
      return AiParseResponseDTO.builder()
        .success(false)
        .error("명령은 비어있을 수 없습니다")
        .functionCalls(Collections.emptyList())
        .build();
    }

    Long userId = SecurityUtils.getUserId();
    String username = SecurityUtils.getUsername();
    String ipAddress = JakartaServletUtil.getClientIP(httpRequest);

    AiCommandRecord record = new AiCommandRecord();
    record.setUserId(userId);
    record.setUsername(username);
    record.setOriginalCommand(command);
    record.setIpAddress(ipAddress);
    record.setCurrentRoute(request.getCurrentRoute());
    record.setProvider("spring-ai");
    record.setModel("auto");

    String systemPrompt = buildSystemPrompt();
    String userPrompt = buildUserPrompt(request);

    try {
      log.info("📤 AI 모델로 명령 전송: {}", command);
      ChatResponse chatResponse = chatClient.prompt()
        .system(systemPrompt)
        .user(userPrompt)
        .call().chatResponse();

      String rawContent = Optional.ofNullable(chatResponse.getResult())
        .map(result -> result.getOutput().getText())
        .orElse("");

      ParseResult parseResult = parseAiResponse(rawContent);

      record.setProvider(StrUtil.emptyToDefault(parseResult.provider(), "spring-ai"));
      record.setModel(StrUtil.emptyToDefault(parseResult.model(), "auto"));
      record.setParseSuccess(parseResult.success());
      record.setExplanation(parseResult.explanation());
      record.setFunctionCalls(JSONUtil.toJsonStr(parseResult.functionCalls()));
      record.setConfidence(parseResult.confidence() != null ? BigDecimal.valueOf(parseResult.confidence()) : null);
      record.setParseErrorMessage(parseResult.success() ? null : StrUtil.emptyToDefault(parseResult.error(), "파싱 실패"));
      record.setParseTime(System.currentTimeMillis() - startTime);

      recordService.save(record);

      AiParseResponseDTO response = AiParseResponseDTO.builder()
        .parseLogId(record.getId())
        .success(parseResult.success())
        .functionCalls(parseResult.functionCalls())
        .explanation(parseResult.explanation())
        .confidence(parseResult.confidence())
        .error(parseResult.error())
        .rawResponse(rawContent)
        .build();

      if (!parseResult.success()) {
        log.warn("❗️ AI가 명령을 파싱하지 못했습니다: {}", parseResult.error());
      } else {
        log.info("✅ 파싱 성공, 감사 기록 ID: {}", record.getId());
      }

      return response;
    } catch (Exception e) {
      long duration = System.currentTimeMillis() - startTime;
      record.setParseSuccess(false);
      record.setFunctionCalls(JSONUtil.toJsonStr(Collections.emptyList()));
      record.setParseErrorMessage(e.getMessage());
      record.setParseTime(duration);
      recordService.save(record);

      log.error("❌ 명령 파싱 실패: {}", e.getMessage(), e);
      throw new RuntimeException("명령 파싱 실패: " + e.getMessage(), e);
    }
  }

  private String buildSystemPrompt() {
    return SYSTEM_PROMPT;
  }

  private String buildUserPrompt(AiParseRequestDTO request) {
    JSONObject payload = JSONUtil.createObj()
      .set("command", request.getCommand())
      .set("currentRoute", request.getCurrentRoute())
      .set("currentComponent", request.getCurrentComponent())
      .set("context", Optional.ofNullable(request.getContext()).orElse(Collections.emptyMap()))
      .set("availableFunctions", availableFunctions());

    return StrUtil.format("""
      아래 컨텍스트를 바탕으로 사용자 의도를 식별하고, 시스템 프롬프트 요구사항에 맞는 JSON을 출력하세요:
      {}
      """, JSONUtil.toJsonPrettyStr(payload));
  }

  private List<Map<String, Object>> availableFunctions() {
    return List.of(
      Map.of(
        "name", "updateUserNickname",
        "description", "사용자명으로 사용자 닉네임 업데이트",
        "requiredParameters", List.of("username", "nickname")
      )
    );
  }

  private ParseResult parseAiResponse(String rawContent) {
    if (StrUtil.isBlank(rawContent)) {
      throw new IllegalStateException("AI 반환 내용이 비어있습니다");
    }

    try {
      JSONObject jsonObject = JSONUtil.parseObj(rawContent);
      boolean success = jsonObject.getBool("success", false);
      String explanation = jsonObject.getStr("explanation");
      Double confidence = jsonObject.containsKey("confidence") ? jsonObject.getDouble("confidence") : null;
      String error = jsonObject.getStr("error");
      String provider = jsonObject.getStr("provider");
      String model = jsonObject.getStr("model");

      List<AiFunctionCallDTO> functionCalls = toFunctionCallList(jsonObject.getJSONArray("functionCalls"));

      return new ParseResult(success, explanation, confidence, error, provider, model, functionCalls);
    } catch (Exception ex) {
      throw new IllegalStateException("AI 응답을 파싱할 수 없습니다: " + ex.getMessage(), ex);
    }
  }

  private List<AiFunctionCallDTO> toFunctionCallList(JSONArray array) {
    if (array == null || array.isEmpty()) {
      return Collections.emptyList();
    }

    List<AiFunctionCallDTO> result = new ArrayList<>();
    for (Object element : array) {
      JSONObject functionJson = JSONUtil.parseObj(element);
      Map<String, Object> arguments = Optional.ofNullable(functionJson.getJSONObject("arguments"))
        .map(obj -> obj.toBean(new TypeReference<Map<String, Object>>() {
        }))
        .orElse(Collections.emptyMap());

      result.add(AiFunctionCallDTO.builder()
        .name(functionJson.getStr("name"))
        .description(functionJson.getStr("description"))
        .arguments(arguments)
        .build());
    }
    return result;
  }

  private record ParseResult(
    boolean success,
    String explanation,
    Double confidence,
    String error,
    String provider,
    String model,
    List<AiFunctionCallDTO> functionCalls
  ) {
  }

  @Override
  public Object executeCommand(AiExecuteRequestDTO request, HttpServletRequest httpRequest) throws Exception {
    long startTime = System.currentTimeMillis();

    // 사용자 정보 조회
    Long userId = SecurityUtils.getUserId();
    String username = SecurityUtils.getUsername();
    String ipAddress = JakartaServletUtil.getClientIP(httpRequest);

    AiFunctionCallDTO functionCall = request.getFunctionCall();

    // 위험한 작업 여부 판단
    boolean isDangerous = isDangerousOperation(functionCall.getName());

    // 파싱 로그 ID로 감사 기록 조회, 존재하지 않으면 새 기록 생성
    AiCommandRecord record;
    if (StrUtil.isNotBlank(request.getParseLogId())) {
      // 이미 존재하는 감사 기록 업데이트 (파싱 단계에서 이미 생성됨)
      record = recordService.getById(request.getParseLogId());
      if (record == null) {
        throw new IllegalStateException("해당 파싱 기록을 찾을 수 없습니다, ID: " + request.getParseLogId());
      }
    } else {
      // 파싱 로그 ID가 없으면 새 기록 생성 (직접 실행 상황 호환)
      record = new AiCommandRecord();
      record.setUserId(userId);
      record.setUsername(username);
      record.setOriginalCommand(request.getOriginalCommand());
      record.setIpAddress(ipAddress);
      record.setCurrentRoute(request.getCurrentRoute());
      recordService.save(record);
    }

    // 실행 관련 필드 업데이트
    record.setFunctionName(functionCall.getName());
    record.setFunctionArguments(JSONUtil.toJsonStr(functionCall.getArguments()));
    record.setIsDangerous(isDangerous);
    record.setRequiresConfirmation(request.getConfirmMode() != null &&
      "manual".equals(request.getConfirmMode()));
    record.setUserConfirmed(request.getUserConfirmed());
    record.setIdempotencyKey(request.getIdempotencyKey());
    record.setUserAgent(httpRequest.getHeader("User-Agent"));
    record.setExecuteStatus("pending");

    try {
      // 멱등성 검사
      if (StrUtil.isNotBlank(request.getIdempotencyKey())) {
        AiCommandRecord existing = recordService.getOne(
          new LambdaQueryWrapper<AiCommandRecord>()
            .eq(AiCommandRecord::getIdempotencyKey, request.getIdempotencyKey())
            .ne(AiCommandRecord::getId, record.getId()) // 현재 기록 제외
        );
        if (existing != null) {
          log.warn("⚠️ 중복 실행 감지됨, 멱등성 토큰: {}", request.getIdempotencyKey());
          throw new IllegalStateException("해당 작업이 이미 실행되었습니다. 중복 제출하지 마세요");
        }
      }

      // 🎯 구체적인 함수 호출 실행
      Object result = executeFunctionCall(functionCall);

      // 실행 성공 업데이트
      record.setExecuteStatus("success");
      record.setExecuteResult(JSONUtil.toJsonStr(result));
      record.setExecutionTime(System.currentTimeMillis() - startTime);

      // 감사 기록 업데이트
      recordService.updateById(record);

      log.info("✅ 명령 실행 성공, 감사 기록 ID: {}", record.getId());

      return result;

    } catch (Exception e) {
      // 실행 실패 업데이트
      record.setExecuteStatus("failed");
      record.setExecuteErrorMessage(e.getMessage());
      record.setExecutionTime(System.currentTimeMillis() - startTime);

      // 감사 기록 업데이트
      recordService.updateById(record);

      log.error("❌ 명령 실행 실패, 감사 기록 ID: {}", record.getId(), e);

      // 오류 발생, Controller에서 통합 처리
      throw e;
    }
  }

  /**
   * 위험한 작업 여부 판단
   */
  private boolean isDangerousOperation(String functionName) {
    String[] dangerousKeywords = {"delete", "remove", "drop", "truncate", "clear"};
    String lowerName = functionName.toLowerCase();
    for (String keyword : dangerousKeywords) {
      if (lowerName.contains(keyword)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 구체적인 함수 호출 실행의함수수호출
   */
  private Object executeFunctionCall(AiFunctionCallDTO functionCall) {
    String functionName = functionCall.getName();
    Map<String, Object> arguments = functionCall.getArguments();

    log.info("🎯 실행함수수: {}, 파라미터수: {}", functionName, arguments);

    // 함수에 따라수이름라우팅다양한으로의핸들러
    switch (functionName) {
      case "updateUserNickname":
        return executeUpdateUserNickname(arguments);
      default:
        throw new UnsupportedOperationException("지원하지 않음의함수수: " + functionName);
    }
  }

  /**
   * 사용 Tool: 사용자명으로 사용자 닉네임 업데이트
   */
  private Object executeUpdateUserNickname(Map<String, Object> arguments) {
    String username = (String) arguments.get("username");
    String nickname = (String) arguments.get("nickname");

    log.info("🔧 [Tool] 사용자 닉네임 업데이트: username={}, nickname={}", username, nickname);
    String resultMsg = userTools.updateUserNickname(username, nickname);

    boolean success = resultMsg != null && resultMsg.contains("성공");
    if (!success) {
      throw new RuntimeException(resultMsg != null ? resultMsg : "사용자 닉네임 업데이트 실패");
    }

    return Map.of("username", username, "nickname", nickname, "message", resultMsg);
  }
}


