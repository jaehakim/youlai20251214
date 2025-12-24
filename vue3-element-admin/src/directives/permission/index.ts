import type { Directive, DirectiveBinding } from "vue";

import { useUserStore } from "@/저장소";
import { ROLE_ROOT } from "@/constants";

/**
 * 버튼权限
 */
export const hasPerm: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const requiredPerms = binding.value;

    // 검사传입의权限값是否合法
    if (!requiredPerms || (typeof requiredPerms !== "string" && !Array.isArray(requiredPerms))) {
      throw new Error(
        "필요해야提供权限标识！例如：v-has-perm=\"'sys:user:add'\" 或 v-has-perm=\"['sys:user:add', 'sys:user:edit']\""
      );
    }

    const { roles, perms } = useUserStore().userInfo;

    // 超级管理员拥有모든权限，만약是"*:*:*"权限标识，그러면不필요해야进行权限검사
    if (roles.includes(ROLE_ROOT) || requiredPerms.includes("*:*:*")) {
      return;
    }

    // 检查权限
    const hasAuth = Array.isArray(requiredPerms)
      ? requiredPerms.some((perm) => perms.includes(perm))
      : perms.includes(requiredPerms);

    // 만약없음权限，移除该元素
    if (!hasAuth && el.parentNode) {
      el.parentNode.removeChild(el);
    }
  },
};

/**
 * 역할权限지시문
 */
export const hasRole: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const requiredRoles = binding.value;

    // 검사传입의역할값是否合法
    if (!requiredRoles || (typeof requiredRoles !== "string" && !Array.isArray(requiredRoles))) {
      throw new Error(
        "필요해야提供역할标识！例如：v-has-role=\"'ADMIN'\" 或 v-has-role=\"['ADMIN', 'TEST']\""
      );
    }

    const { roles } = useUserStore().userInfo;

    // 检查是否有对应역할权限
    const hasAuth = Array.isArray(requiredRoles)
      ? requiredRoles.some((role) => roles.includes(role))
      : roles.includes(requiredRoles);

    // 만약없음权限，移除元素
    if (!hasAuth && el.parentNode) {
      el.parentNode.removeChild(el);
    }
  },
};
