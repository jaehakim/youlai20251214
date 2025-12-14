package com.youlai.boot.system.model.bo;

import lombok.Data;

import java.util.Set;

/**
 * 역할 권한 비즈니스 객체
 *
 * @author haoxr
 * @since 2023/11/29
 */
@Data
public class RolePermsBO {

    /**
     * 역할 코드
     */
    private String roleCode;

    /**
     * 권한 식별자 집합
     */
    private Set<String> perms;

}
