import "vue-router";

declare module "vue-router" {
  // https://router.vuejs.org/zh/guide/advanced/meta.html#typescript
  // 可以通거치扩展 RouteMeta 인터페이스来输입 meta 字段
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
     * 是否隐藏메뉴단일
     * true 隐藏, false 显示
     * @default false
     */
    hidden?: boolean;

    /**
     * 始终显示父级메뉴단일，即使오직有하나개子메뉴단일
     * true 显示父级메뉴단일, false 隐藏父级메뉴단일，显示唯하나子노드
     * @default false
     */
    alwaysShow?: boolean;

    /**
     * 是否固定에页签上
     * true 固定, false 不固定
     * @default false
     */
    affix?: boolean;

    /**
     * 是否캐시페이지
     * true 캐시, false 不캐시
     * @default false
     */
    keepAlive?: boolean;

    /**
     * 是否에面패키지屑导航내隐藏
     * true 隐藏, false 显示
     * @default false
     */
    breadcrumb?: boolean;
  }
}
