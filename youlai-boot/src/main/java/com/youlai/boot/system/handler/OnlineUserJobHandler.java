package com.youlai.boot.system.handler;


import com.youlai.boot.system.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 온라인 사용자 정기 작업
 *
 * @since 2024/10/7
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OnlineUserJobHandler {

    private final UserOnlineService userOnlineService;
    private final SimpMessagingTemplate messagingTemplate;

    // 매 3분마다 온라인 사용자 수 통계, 서버 부담 감소
    @Scheduled(cron = "0 */3 * * * ?")
    public void execute() {
        log.info("정기 작업: 온라인 사용자 수 통계");
        // 온라인 사용자 수를 새 토픽으로 푸시
        int count = userOnlineService.getOnlineUserCount();
        messagingTemplate.convertAndSend("/topic/online-count", count);
    }

}
