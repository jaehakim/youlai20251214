import "vue-router";

declare module "vue-router" {
  // https://router.vuejs.org/zh/guide/advanced/meta.html#typescript
  // RouteMeta 인터페이스를 확장하여 meta 필드 타입 정의
  interface RouteMeta {
    /**
     * 메뉴 항목 명칭
     * @example 'Dashboard'
     */
    title?: string;

    /**
     * 메뉴 항목 아이콘
     * @example 'el-icon-edit'
     */
    icon?: string;

    /**
     * 메뉴 항목 숨김 여부
     * true 숨김, false 표시
     * @default false
     */
    hidden?: boolean;

    /**
     * 하위 메뉴가 하나뿐이어도 상위 메뉴 항상 표시
     * true 상위 메뉴 표시, false 상위 메뉴 숨기고 유일한 하위 노드 표시
     * @default false
     */
    alwaysShow?: boolean;

    /**
     * 페이지 탭에 고정 여부
     * true 고정, false 고정 안함
     * @default false
     */
    affix?: boolean;

    /**
     * 페이지 캐시 여부
     * true 캐시, false 캐시 안함
     * @default false
     */
    keepAlive?: boolean;

    /**
     * 브레드크럼 네비게이션에서 숨김 여부
     * true 숨김, false 표시
     * @default false
     */
    breadcrumb?: boolean;
  }
}
