package com.youlai.boot.system.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 역할과 메뉴 연관 테이블
 */
@TableName("sys_role_menu")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleMenu {
    /**
     * 역할 ID
     */
    private Long roleId;

    /**
     * 메뉴 ID
     */
    private Long menuId;

}