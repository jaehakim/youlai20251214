package com.youlai.boot.system.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

import java.util.Set;
import com.baomidou.mybatisplus.core.metadata.OrderItem;


/**
 * 로그 페이지 쿼리 객체
 *
 * @author Ray
 * @since 2.10.0
 */
@Schema(description = "로그 페이지 쿼리 객체")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class LogPageQuery extends BasePageQuery {

    @Schema(description="키워드(로그 내용/요청 경로/요청 메서드/지역/브라우저/운영체제)")
    private String keywords;

    @Schema(description="작업 시간 범위")
    List<String> createTime;

    /**
     * 로그 도메인에서 허용되는 정렬 필드
     * @return 허용된 정렬 필드 집합
     */
    @Override
    protected Set<String> getCustomAllowedSortFields() {
        return Set.of(
                "module",           // 로그 모듈
                "level",            // 로그 레벨
                "username",         // 사용자명
                "method",           // HTTP 메소드
                "ipAddress",        // IP 주소
                "requestUri",       // 요청 URI
                "time",             // 실행 시간
                "message"           // 로그 메시지
        );
    }

    /**
     * 로그의 기본 정렬: 생성시간 내림차순 (최신 로그가 먼저)
     * @return 기본 OrderItem 리스트
     */
    @Override
    protected List<OrderItem> getDefaultOrderItems() {
        return List.of(OrderItem.desc("create_time"));
    }
}
