import request from "@/utils/request";

const CONFIG_BASE_URL = "/api/v1/config";

const ConfigAPI = {
  /** 설정 페이지 데이터 조회 */
  getPage(queryParams?: ConfigPageQuery) {
    return request<any, PageResult<ConfigPageVO[]>>({
      url: `${CONFIG_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /** 설정 폼 데이터 조회 */
  getFormData(id: string) {
    return request<any, ConfigForm>({
      url: `${CONFIG_BASE_URL}/${id}/form`,
      method: "get",
    });
  },
  /** 설정 추가 */
  create(data: ConfigForm) {
    return request({ url: `${CONFIG_BASE_URL}`, method: "post", data });
  },
  /** 설정 수정 */
  update(id: string, data: ConfigForm) {
    return request({ url: `${CONFIG_BASE_URL}/${id}`, method: "put", data });
  },
  /** 설정 삭제 */
  deleteById(id: string) {
    return request({ url: `${CONFIG_BASE_URL}/${id}`, method: "delete" });
  },
  /** 설정 캐시 새로 고침 */
  refreshCache() {
    return request({ url: `${CONFIG_BASE_URL}/refresh`, method: "PUT" });
  },
};

export default ConfigAPI;

export interface ConfigPageQuery extends PageQuery {
  /** 검색 키워드 */
  keywords?: string;
}

export interface ConfigForm {
  /** 기본 키 */
  id?: string;
  /** 설정명 */
  configName?: string;
  /** 설정 키 */
  configKey?: string;
  /** 설정 값 */
  configValue?: string;
  /** 설명, 비고 */
  remark?: string;
}

export interface ConfigPageVO {
  /** 기본 키 */
  id?: string;
  /** 설정명 */
  configName?: string;
  /** 설정 키 */
  configKey?: string;
  /** 설정 값 */
  configValue?: string;
  /** 설명, 비고 */
  remark?: string;
}
