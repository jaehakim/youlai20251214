import type { Directive, DirectiveBinding } from "vue";

import { useUserStore } from "@/store";
import { ROLE_ROOT } from "@/constants";

/**
 * 버튼 권한
 */
export const hasPerm: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const requiredPerms = binding.value;

    // 전달된 권한 값이 유효한지 검사
    if (!requiredPerms || (typeof requiredPerms !== "string" && !Array.isArray(requiredPerms))) {
      throw new Error(
        "권한 식별자를 제공해야 합니다! 예: v-has-perm=\"'sys:user:add'\" 또는 v-has-perm=\"['sys:user:add', 'sys:user:edit']\""
      );
    }

    const { roles, perms } = useUserStore().userInfo;

    // 슈퍼 관리자는 모든 권한을 가집니다. "*:*:*" 권한 식별자인 경우 권한 검사를 수행하지 않음
    if (roles.includes(ROLE_ROOT) || requiredPerms.includes("*:*:*")) {
      return;
    }

    // 권한 확인
    const hasAuth = Array.isArray(requiredPerms)
      ? requiredPerms.some((perm) => perms.includes(perm))
      : perms.includes(requiredPerms);

    // 권한이 없으면 해당 요소 제거
    if (!hasAuth && el.parentNode) {
      el.parentNode.removeChild(el);
    }
  },
};

/**
 * 역할 권한 지시문
 */
export const hasRole: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const requiredRoles = binding.value;

    // 전달된 역할 값이 유효한지 검사
    if (!requiredRoles || (typeof requiredRoles !== "string" && !Array.isArray(requiredRoles))) {
      throw new Error(
        "역할 식별자를 제공해야 합니다! 예: v-has-role=\"'ADMIN'\" 또는 v-has-role=\"['ADMIN', 'TEST']\""
      );
    }

    const { roles } = useUserStore().userInfo;

    // 해당 역할 권한이 있는지 확인
    const hasAuth = Array.isArray(requiredRoles)
      ? requiredRoles.some((role) => roles.includes(role))
      : roles.includes(requiredRoles);

    // 권한이 없으면 요소 제거
    if (!hasAuth && el.parentNode) {
      el.parentNode.removeChild(el);
    }
  },
};
