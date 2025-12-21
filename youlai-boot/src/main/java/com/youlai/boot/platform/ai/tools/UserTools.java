package com.youlai.boot.platform.ai.tools;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youlai.boot.system.model.entity.User;
import com.youlai.boot.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * Spring AI Tool 기반 사용자 관리 도구
 *
 * LLM이 Tool Calling을 통해 호출할 수 있는 제어된 CRUD 기능 제공
 */
@Component
@RequiredArgsConstructor
public class UserTools {

  private final UserService userService;

  @Tool(description = "키워드로 사용자 목록에서 사용자 필터링")
  public String queryUser(
    @ToolParam(description = "검색 키워드, 목록에서 검색 필터링에 사용") String keywords
  ) {
    // 검색 키워드 반환, 프론트엔드에서 사용자 목록 페이지에서 필터링
    return "사용자 목록에서 검색할 내용: " + keywords;
  }

  @Tool(description = "사용자명으로 사용자 닉네임 업데이트")
  public String updateUserNickname(
    @ToolParam(description = "사용자명") String username,
    @ToolParam(description = "새 닉네임") String nickname
  ) {

    boolean ok = userService.update(new LambdaUpdateWrapper<User>()
      .eq(User::getUsername, username)
      .set(User::getNickname, nickname)
    );
    return ok ? "사용자 닉네임 업데이트 성공" : "사용자 닉네임 업데이트 실패";
  }
}







