package com.youlai.boot.common.constant;

/**
 * JWT Claims 선언 상수
 * <p>
 * JWT Claims는 Payload의 일부로, 엔티티(일반적으로 사용자)의 상태와 추가 메타데이터를 포함합니다.
 *
 * @author haoxr
 * @since 2023/11/24
 */
public interface JwtClaimConstants {

    /**
     * 토큰 유형
     */
    String TOKEN_TYPE = "tokenType";

    /**
     * 사용자 ID
     */
    String USER_ID = "userId";

    /**
     * 부서 ID
     */
    String DEPT_ID = "deptId";

    /**
     * 데이터 권한
     */
    String DATA_SCOPE = "dataScope";

    /**
     * 권한(역할 Code) 집합
     */
    String AUTHORITIES = "authorities";

    /**
     * 사용자별 이전 토큰을 무효화하기 위한 보안 버전 번호
     */
    String SECURITY_VERSION = "securityVersion";

}
