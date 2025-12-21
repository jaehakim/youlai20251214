package com.youlai.boot.security.model;

import lombok.Data;
import java.util.Set;

/**
 * 사용자 인증 자격 증명 정보
 *
 * @author Ray.Hao
 * @since 2022/10/22
 */
@Data
public class UserAuthCredentials {

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 닉네임
     */
    private String nickname;

    /**
     * 부서 ID
     */
    private Long deptId;

    /**
     * 사용자 비밀번호
     */
    private String password;

    /**
     * 상태 (1:활성화; 0:비활성화)
     */
    private Integer status;

    /**
     * 사용자가 속한 역할 집합
     */
    private Set<String> roles;

    /**
     * 데이터 권한 범위, 사용자가 접근 가능한 데이터 수준 제어에 사용
     *
     * @see com.youlai.boot.common.enums.DataScopeEnum
     */
    private Integer dataScope;

}
