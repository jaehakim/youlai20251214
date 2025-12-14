package com.youlai.boot.common.constant;

/**
 * 시스템 상수
 *
 * @author Ray.Hao
 * @since 1.0.0
 */
public interface SystemConstants {

    /**
     * 루트 노드 ID
     */
    Long ROOT_NODE_ID = 0L;

    /**
     * 시스템 기본 비밀번호
     */
    String DEFAULT_PASSWORD = "123456";

    /**
     * 슈퍼 관리자 역할 코드
     */
    String ROOT_ROLE_CODE = "ROOT";


    /**
     * 시스템 설정 IP QPS 속도 제한 KEY
     */
    String SYSTEM_CONFIG_IP_QPS_LIMIT_KEY = "IP_QPS_THRESHOLD_LIMIT";

}
