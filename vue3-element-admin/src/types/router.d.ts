import "vue-router";

declare module "vue-router" {
  // https://router.vuejs.org/zh/guide/advanced/meta.html#typescript
  // 可以通거치扩展 RouteMeta 인터페이스来출력입 meta 필드
  interface RouteMeta {
    /**
     * 메뉴단일이름칭
     * @example 'Dashboard'
     */
    title?: string;

    /**
     * 메뉴단일아이콘
     * @example 'el-icon-edit'
     */
    icon?: string;

    /**
     * 여부隐藏메뉴단일
     * true 隐藏, false 표시
     * @default false
     */
    hidden?: boolean;

    /**
     * 항상 표시父级메뉴단일，即使오직있음하나개子메뉴단일
     * true 표시父级메뉴단일, false 隐藏父级메뉴단일，표시唯하나子노드
     * @default false
     */
    alwaysShow?: boolean;

    /**
     * 여부固定에页签上
     * true 固定, false 아님固定
     * @default false
     */
    affix?: boolean;

    /**
     * 여부캐시페이지
     * true 캐시, false 아님캐시
     * @default false
     */
    keepAlive?: boolean;

    /**
     * 여부에面패키지屑导航내隐藏
     * true 隐藏, false 표시
     * @default false
     */
    breadcrumb?: boolean;
  }
}
