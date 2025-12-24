import request from "@/utils/request";
const MENU_BASE_URL = "/api/v1/menus";

const MenuAPI = {
  /** 현재 사용자의 라우트 목록 조회 */
  getRoutes() {
    return request<any, RouteVO[]>({ url: `${MENU_BASE_URL}/routes`, method: "get" });
  },
  /** 메뉴 트리 목록 조회 */
  getList(queryParams: MenuQuery) {
    return request<any, MenuVO[]>({ url: `${MENU_BASE_URL}`, method: "get", params: queryParams });
  },
  /** 메뉴 드롭다운 데이터 조회 */
  getOptions(onlyParent?: boolean) {
    return request<any, OptionType[]>({
      url: `${MENU_BASE_URL}/options`,
      method: "get",
      params: { onlyParent },
    });
  },
  /** 메뉴 폼 데이터 조회 */
  getFormData(id: string) {
    return request<any, MenuForm>({ url: `${MENU_BASE_URL}/${id}/form`, method: "get" });
  },
  /** 메뉴 추가 */
  create(data: MenuForm) {
    return request({ url: `${MENU_BASE_URL}`, method: "post", data });
  },
  /** 메뉴 수정 */
  update(id: string, data: MenuForm) {
    return request({ url: `${MENU_BASE_URL}/${id}`, method: "put", data });
  },
  /** 메뉴 삭제 */
  deleteById(id: string) {
    return request({ url: `${MENU_BASE_URL}/${id}`, method: "delete" });
  },
};

export default MenuAPI;

export interface MenuQuery {
  /** 검색 키워드 */
  keywords?: string;
}
import type { MenuTypeEnum } from "@/enums/system/menu-enum";
export interface MenuVO {
  /** 하위 메뉴 */
  children?: MenuVO[];
  /** 컴포넌트 경로 */
  component?: string;
  /** 아이콘 */
  icon?: string;
  /** 메뉴 ID */
  id?: string;
  /** 메뉴명 */
  name?: string;
  /** 상위 메뉴 ID */
  parentId?: string;
  /** 버튼 권한 식별자 */
  perm?: string;
  /** 리다이렉트 경로 */
  redirect?: string;
  /** 라우트명 */
  routeName?: string;
  /** 라우트 상대 경로 */
  routePath?: string;
  /** 메뉴 정렬(숫자가 작을수록 앞에 표시) */
  sort?: number;
  /** 메뉴 타입 */
  type?: MenuTypeEnum;
  /** 표시 여부(1:표시;0:숨김) */
  visible?: number;
}
export interface MenuForm {
  /** 메뉴 ID */
  id?: string;
  /** 상위 메뉴 ID */
  parentId?: string;
  /** 메뉴명 */
  name?: string;
  /** 표시 여부(1-예 0-아니오) */
  visible: number;
  /** 아이콘 */
  icon?: string;
  /** 정렬 */
  sort?: number;
  /** 라우트명 */
  routeName?: string;
  /** 라우트 경로 */
  routePath?: string;
  /** 컴포넌트 경로 */
  component?: string;
  /** 리다이렉트 라우트 경로 */
  redirect?: string;
  /** 메뉴 타입 */
  type?: MenuTypeEnum;
  /** 권한 식별자 */
  perm?: string;
  /** 【메뉴】페이지 캐시 활성화 여부 */
  keepAlive?: number;
  /** 【디렉토리】하위 라우트가 하나만 있어도 항상 표시 */
  alwaysShow?: number;
  /** 기타 파라미터 */
  params?: KeyValue[];
}
interface KeyValue {
  key: string;
  value: string;
}
export interface RouteVO {
  /** 하위 라우트 목록 */
  children: RouteVO[];
  /** 컴포넌트 경로 */
  component?: string;
  /** 라우트 속성 */
  meta?: Meta;
  /** 라우트명 */
  name?: string;
  /** 라우트 경로 */
  path?: string;
  /** 리다이렉트 링크 */
  redirect?: string;
}
export interface Meta {
  /** 【디렉토리】하위 라우트가 하나만 있어도 항상 표시 */
  alwaysShow?: boolean;
  /** 숨김 여부(true-예 false-아니오) */
  hidden?: boolean;
  /** 아이콘 */
  icon?: string;
  /** 【메뉴】페이지 캐시 활성화 여부 */
  keepAlive?: boolean;
  /** 라우트 제목 */
  title?: string;
}
