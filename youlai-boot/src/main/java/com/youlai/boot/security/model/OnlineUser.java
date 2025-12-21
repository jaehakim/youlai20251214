package com.youlai.boot.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 온라인 사용자 정보 객체
 *
 * @author wangtao
 * @since 2025/2/27 10:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineUser {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 부서 ID
     */
    private Long deptId;

    /**
     * 데이터 권한 범위
     * <p>사용자가 접근 가능한 데이터 범위 정의 (전체, 본 부서 또는 사용자 정의 범위 등)</p>
     */
    private Integer dataScope;

    /**
     * 역할 권한 집합
     */
    private Set<String> roles;

}
