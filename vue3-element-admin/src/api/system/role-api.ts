import request from "@/utils/request";

const ROLE_BASE_URL = "/api/v1/roles";

const RoleAPI = {
  /** 역할 페이지 데이터 조회 */
  getPage(queryParams?: RolePageQuery) {
    return request<any, PageResult<RolePageVO[]>>({
      url: `${ROLE_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /** 역할 드롭다운 데이터 조회 */
  getOptions() {
    return request<any, OptionType[]>({ url: `${ROLE_BASE_URL}/options`, method: "get" });
  },
  /** 역할의 메뉴 ID 집합 조회 */
  getRoleMenuIds(roleId: string) {
    return request<any, string[]>({ url: `${ROLE_BASE_URL}/${roleId}/menuIds`, method: "get" });
  },
  /** 메뉴 권한 할당 */
  updateRoleMenus(roleId: string, data: number[]) {
    return request({ url: `${ROLE_BASE_URL}/${roleId}/menus`, method: "put", data });
  },
  /** 역할 폼 데이터 조회 */
  getFormData(id: string) {
    return request<any, RoleForm>({ url: `${ROLE_BASE_URL}/${id}/form`, method: "get" });
  },
  /** 역할 추가 */
  create(data: RoleForm) {
    return request({ url: `${ROLE_BASE_URL}`, method: "post", data });
  },
  /** 역할 업데이트 */
  update(id: string, data: RoleForm) {
    return request({ url: `${ROLE_BASE_URL}/${id}`, method: "put", data });
  },
  /** 대량 역할 삭제, 쉼표(,)로 구분 */
  deleteByIds(ids: string) {
    return request({ url: `${ROLE_BASE_URL}/${ids}`, method: "delete" });
  },
};

export default RoleAPI;

export interface RolePageQuery extends PageQuery {
  /** 검색 키워드 */
  keywords?: string;
}
export interface RolePageVO {
  /** 역할 ID */
  id?: string;
  /** 역할 코드 */
  code?: string;
  /** 역할명 */
  name?: string;
  /** 정렬 */
  sort?: number;
  /** 역할 상태 */
  status?: number;
  /** 생성 시간 */
  createTime?: Date;
  /** 수정 시간 */
  updateTime?: Date;
}
export interface RoleForm {
  /** 역할 ID */
  id?: string;
  /** 역할 코드 */
  code?: string;
  /** 데이터 권한 */
  dataScope?: number;
  /** 역할명 */
  name?: string;
  /** 정렬 */
  sort?: number;
  /** 역할 상태 (1-정상; 0-비활성) */
  status?: number;
}
