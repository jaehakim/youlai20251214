import type { Directive, DirectiveBinding } from "vue";

import { useUser스토어 } from "@/저장소";
import { ROLE_ROOT } from "@/constants";

/**
 * 버튼권한
 */
export const hasPerm: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const requiredPerms = binding.value;

    // 검사传입의권한값여부合法
    if (!requiredPerms || (typeof requiredPerms !== "string" && !Array.isArray(requiredPerms))) {
      throw new Error(
        "필요해야提供권한标识！例예：v-has-perm=\"'sys:user:add'\" 또는 v-has-perm=\"['sys:user:add', 'sys:user:edit']\""
      );
    }

    const { roles, perms } = useUser스토어().userInfo;

    // 슈퍼 관리자拥있음모든권한，만약예"*:*:*"권한标识，그러면아님필요해야进행권한검사
    if (roles.includes(ROLE_ROOT) || requiredPerms.includes("*:*:*")) {
      return;
    }

    // 확인권한
    const hasAuth = Array.isArray(requiredPerms)
      ? requiredPerms.some((perm) => perms.includes(perm))
      : perms.includes(requiredPerms);

    // 만약없음권한，移除该元素
    if (!hasAuth && el.parentNode) {
      el.parentNode.removeChild(el);
    }
  },
};

/**
 * 역할권한지시문
 */
export const hasRole: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    const requiredRoles = binding.value;

    // 검사传입의역할값여부合法
    if (!requiredRoles || (typeof requiredRoles !== "string" && !Array.isArray(requiredRoles))) {
      throw new Error(
        "필요해야提供역할标识！例예：v-has-role=\"'ADMIN'\" 또는 v-has-role=\"['ADMIN', 'TEST']\""
      );
    }

    const { roles } = useUser스토어().userInfo;

    // 확인여부있음对应역할권한
    const hasAuth = Array.isArray(requiredRoles)
      ? requiredRoles.some((role) => roles.includes(role))
      : roles.includes(requiredRoles);

    // 만약없음권한，移除元素
    if (!hasAuth && el.parentNode) {
      el.parentNode.removeChild(el);
    }
  },
};
