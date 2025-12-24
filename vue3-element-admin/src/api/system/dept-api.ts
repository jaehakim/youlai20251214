import request from "@/utils/request";

const DEPT_BASE_URL = "/api/v1/dept";

const DeptAPI = {
  /** 부서 트리 목록 조회 */
  getList(queryParams?: DeptQuery) {
    return request<any, DeptVO[]>({ url: `${DEPT_BASE_URL}`, method: "get", params: queryParams });
  },
  /** 부서 드롭다운 데이터 조회 */
  getOptions() {
    return request<any, OptionType[]>({ url: `${DEPT_BASE_URL}/options`, method: "get" });
  },
  /** 부서 폼 데이터 조회 */
  getFormData(id: string) {
    return request<any, DeptForm>({ url: `${DEPT_BASE_URL}/${id}/form`, method: "get" });
  },
  /** 부서 추가 */
  create(data: DeptForm) {
    return request({ url: `${DEPT_BASE_URL}`, method: "post", data });
  },
  /** 부서 수정 */
  update(id: string, data: DeptForm) {
    return request({ url: `${DEPT_BASE_URL}/${id}`, method: "put", data });
  },
  /** 부서 대량 삭제, 여러 개는 쉼표(,)로 구분 */
  deleteByIds(ids: string) {
    return request({ url: `${DEPT_BASE_URL}/${ids}`, method: "delete" });
  },
};

export default DeptAPI;

export interface DeptQuery {
  /** 검색 키워드 */
  keywords?: string;
  /** 상태 */
  status?: number;
}

export interface DeptVO {
  /** 하위 부서 */
  children?: DeptVO[];
  /** 생성 시간 */
  createTime?: Date;
  /** 부서 ID */
  id?: string;
  /** 부서명 */
  name?: string;
  /** 부서 코드 */
  code?: string;
  /** 상위 부서 ID */
  parentid?: string;
  /** 정렬 */
  sort?: number;
  /** 상태(1:활성;0:비활성) */
  status?: number;
  /** 수정 시간 */
  updateTime?: Date;
}

export interface DeptForm {
  /** 부서 ID(추가 시 미입력) */
  id?: string;
  /** 부서명 */
  name?: string;
  /** 부서 코드 */
  code?: string;
  /** 상위 부서 ID */
  parentId: string;
  /** 정렬 */
  sort?: number;
  /** 상태(1:활성;0:비활성) */
  status?: number;
}
