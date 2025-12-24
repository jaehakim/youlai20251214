import request from "@/utils/request";

const DICT_BASE_URL = "/api/v1/dicts";

const DictAPI = {
  /** 사전 페이지 목록 */
  getPage(queryParams: DictPageQuery) {
    return request<any, PageResult<DictPageVO[]>>({
      url: `${DICT_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /** 사전 목록 */
  getList() {
    return request<any, OptionType[]>({ url: `${DICT_BASE_URL}`, method: "get" });
  },
  /** 사전 폼 데이터 */
  getFormData(id: string) {
    return request<any, DictForm>({ url: `${DICT_BASE_URL}/${id}/form`, method: "get" });
  },
  /** 사전 추가 */
  create(data: DictForm) {
    return request({ url: `${DICT_BASE_URL}`, method: "post", data });
  },
  /** 사전 수정 */
  update(id: string, data: DictForm) {
    return request({ url: `${DICT_BASE_URL}/${id}`, method: "put", data });
  },
  /** 사전 삭제 */
  deleteByIds(ids: string) {
    return request({ url: `${DICT_BASE_URL}/${ids}`, method: "delete" });
  },

  /** 사전 항목 페이지 목록 조회 */
  getDictItemPage(dictCode: string, queryParams: DictItemPageQuery) {
    return request<any, PageResult<DictItemPageVO[]>>({
      url: `${DICT_BASE_URL}/${dictCode}/items/page`,
      method: "get",
      params: queryParams,
    });
  },
  /** 사전 항목 목록 조회 */
  getDictItems(dictCode: string) {
    return request<any, DictItemOption[]>({
      url: `${DICT_BASE_URL}/${dictCode}/items`,
      method: "get",
    });
  },
  /** 사전 항목 추가 */
  createDictItem(dictCode: string, data: DictItemForm) {
    return request({ url: `${DICT_BASE_URL}/${dictCode}/items`, method: "post", data });
  },
  /** 사전 항목 폼 데이터 조회 */
  getDictItemFormData(dictCode: string, id: string) {
    return request<any, DictItemForm>({
      url: `${DICT_BASE_URL}/${dictCode}/items/${id}/form`,
      method: "get",
    });
  },
  /** 사전 항목 수정 */
  updateDictItem(dictCode: string, id: string, data: DictItemForm) {
    return request({ url: `${DICT_BASE_URL}/${dictCode}/items/${id}`, method: "put", data });
  },
  /** 사전 항목 삭제 */
  deleteDictItems(dictCode: string, ids: string) {
    return request({ url: `${DICT_BASE_URL}/${dictCode}/items/${ids}`, method: "delete" });
  },
};

export default DictAPI;

export interface DictPageQuery extends PageQuery {
  /** 검색 키워드 */
  keywords?: string;
  /** 상태(1:활성;0:비활성) */
  status?: number;
}
export interface DictPageVO {
  /** 사전 ID */
  id: string;
  /** 사전명 */
  name: string;
  /** 사전 코드 */
  dictCode: string;
  /** 상태(1:활성;0:비활성) */
  status: number;
}
export interface DictForm {
  /** 사전 ID(추가 시 미입력) */
  id?: string;
  /** 사전명 */
  name?: string;
  /** 사전 코드 */
  dictCode?: string;
  /** 상태(1:활성;0:비활성) */
  status?: number;
  /** 비고 */
  remark?: string;
}
export interface DictItemPageQuery extends PageQuery {
  /** 검색 키워드 */
  keywords?: string;
  /** 사전 코드 */
  dictCode?: string;
}
export interface DictItemPageVO {
  /** 사전 항목 ID */
  id: string;
  /** 사전 코드 */
  dictCode: string;
  /** 사전 항목 값 */
  value: string;
  /** 사전 항목 레이블 */
  label: string;
  /** 상태(1:활성;0:비활성) */
  status: number;
  /** 정렬 */
  sort?: number;
}
export interface DictItemForm {
  /** 사전 항목 ID(추가 시 미입력) */
  id?: string;
  /** 사전 코드 */
  dictCode?: string;
  /** 사전 항목 값 */
  value?: string;
  /** 사전 항목 레이블 */
  label?: string;
  /** 상태(1:활성;0:비활성) */
  status?: number;
  /** 정렬 */
  sort?: number;
  /** 태그 타입 */
  tagType?: "success" | "warning" | "info" | "primary" | "danger" | "";
}
export interface DictItemOption {
  /** 값 */
  value: number | string;
  /** 레이블 */
  label: string;
  /** 태그 타입 */
  tagType?: "" | "success" | "info" | "warning" | "danger";
  [key: string]: any;
}
