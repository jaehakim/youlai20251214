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
 * AI 명령编排서비스구현
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiCommandServiceImpl implements AiCommandService {

  private static final String SYSTEM_PROMPT = """
    你是원个智能의企业操作助手，需要을사용자의自然语言명령解析成标准의函수调用。
    请返回严格의 JSON 格式，包含字段：
    - success: boolean
    - explanation: string
    - confidence: number (0-1)
    - error: string
    - provider: string
    - model: string
    - functionCalls: 수组，每个元素包含 name、description、arguments(객체)
    当无法识别명령时，success=false，并给出 error。
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
        .error("명령不能값空")
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
      log.info("📤 발송명령에 AI 模型: {}", command);
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
      record.setParseErrorMessage(parseResult.success() ? null : StrUtil.emptyToDefault(parseResult.error(), "解析실패"));
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
        log.warn("❗️ AI 미能解析명령: {}", parseResult.error());
      } else {
        log.info("✅ 解析성공，审计기록 ID: {}", record.getId());
      }

      return response;
    } catch (Exception e) {
      long duration = System.currentTimeMillis() - startTime;
      record.setParseSuccess(false);
      record.setFunctionCalls(JSONUtil.toJsonStr(Collections.emptyList()));
      record.setParseErrorMessage(e.getMessage());
      record.setParseTime(duration);
      recordService.save(record);

      log.error("❌ 解析명령실패: {}", e.getMessage(), e);
      throw new RuntimeException("解析명령실패: " + e.getMessage(), e);
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
      请根据는下컨텍스트识别사용자意图，并输出符合시스템提示要求의 JSON：
      {}
      """, JSONUtil.toJsonPrettyStr(payload));
  }

  private List<Map<String, Object>> availableFunctions() {
    return List.of(
      Map.of(
        "name", "updateUserNickname",
        "description", "根据사용자명업데이트사용자닉네임",
        "requiredParameters", List.of("username", "nickname")
      )
    );
  }

  private ParseResult parseAiResponse(String rawContent) {
    if (StrUtil.isBlank(rawContent)) {
      throw new IllegalStateException("AI 返回내용값空");
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
      throw new IllegalStateException("无法解析 AI 响应: " + ex.getMessage(), ex);
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

    // 조회사용자 정보
    Long userId = SecurityUtils.getUserId();
    String username = SecurityUtils.getUsername();
    String ipAddress = JakartaServletUtil.getClientIP(httpRequest);

    AiFunctionCallDTO functionCall = request.getFunctionCall();

    // 判断여부값危险操作
    boolean isDangerous = isDangerousOperation(functionCall.getName());

    // 根据解析로그ID조회审计기록，如果不存에则생성새기록
    AiCommandRecord record;
    if (StrUtil.isNotBlank(request.getParseLogId())) {
      // 업데이트이미存에의审计기록（解析阶段이미생성）
      record = recordService.getById(request.getParseLogId());
      if (record == null) {
        throw new IllegalStateException("미找到对应의解析기록，ID: " + request.getParseLogId());
      }
    } else {
      // 如果没有解析로그ID，생성새기록（兼容直接执行의情况）
      record = new AiCommandRecord();
      record.setUserId(userId);
      record.setUsername(username);
      record.setOriginalCommand(request.getOriginalCommand());
      record.setIpAddress(ipAddress);
      record.setCurrentRoute(request.getCurrentRoute());
      recordService.save(record);
    }

    // 업데이트执行관련字段
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
      // 幂等性检查
      if (StrUtil.isNotBlank(request.getIdempotencyKey())) {
        AiCommandRecord existing = recordService.getOne(
          new LambdaQueryWrapper<AiCommandRecord>()
            .eq(AiCommandRecord::getIdempotencyKey, request.getIdempotencyKey())
            .ne(AiCommandRecord::getId, record.getId()) // 排除현재기록
        );
        if (existing != null) {
          log.warn("⚠️ 检测到重复执行，幂等性토큰: {}", request.getIdempotencyKey());
          throw new IllegalStateException("该操作이미执行，请勿重复제출");
        }
      }

      // 🎯 执行具体의函수调用
      Object result = executeFunctionCall(functionCall);

      // 업데이트执行성공
      record.setExecuteStatus("success");
      record.setExecuteResult(JSONUtil.toJsonStr(result));
      record.setExecutionTime(System.currentTimeMillis() - startTime);

      // 업데이트审计기록
      recordService.updateById(record);

      log.info("✅ 명령 실행성공，审计기록 ID: {}", record.getId());

      return result;

    } catch (Exception e) {
      // 업데이트执行실패
      record.setExecuteStatus("failed");
      record.setExecuteErrorMessage(e.getMessage());
      record.setExecutionTime(System.currentTimeMillis() - startTime);

      // 업데이트审计기록
      recordService.updateById(record);

      log.error("❌ 명령 실행실패，审计기록 ID: {}", record.getId(), e);

      // 抛出오류，由 Controller 统원处理
      throw e;
    }
  }

  /**
   * 判断여부값危险操作
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
   * 执行具体의函수调用
   */
  private Object executeFunctionCall(AiFunctionCallDTO functionCall) {
    String functionName = functionCall.getName();
    Map<String, Object> arguments = functionCall.getArguments();

    log.info("🎯 执行函수: {}, 参수: {}", functionName, arguments);

    // 根据函수이름路由到不同의处理器
    switch (functionName) {
      case "updateUserNickname":
        return executeUpdateUserNickname(arguments);
      default:
        throw new UnsupportedOperationException("不支持의函수: " + functionName);
    }
  }

  /**
   * 사용 Tool: 根据사용자명업데이트사용자닉네임
   */
  private Object executeUpdateUserNickname(Map<String, Object> arguments) {
    String username = (String) arguments.get("username");
    String nickname = (String) arguments.get("nickname");

    log.info("🔧 [Tool] 업데이트사용자닉네임: username={}, nickname={}", username, nickname);
    String resultMsg = userTools.updateUserNickname(username, nickname);

    boolean success = resultMsg != null && resultMsg.contains("성공");
    if (!success) {
      throw new RuntimeException(resultMsg != null ? resultMsg : "업데이트사용자닉네임실패");
    }

    return Map.of("username", username, "nickname", nickname, "message", resultMsg);
  }
}


