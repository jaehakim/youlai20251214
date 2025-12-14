package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 사용자와 역할 연관 테이블
 *
 * @author Rya.Hao
 * @since 2022/12/17
 */
@TableName("sys_user_role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 역할 ID
     */
    private Long roleId;
}