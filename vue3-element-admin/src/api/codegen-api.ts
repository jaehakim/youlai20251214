import request from "@/utils/request";

const GENERATOR_BASE_URL = "/api/v1/codegen";

const GeneratorAPI = {
  /** 데이터 테이블 페이지 목록 조회 */
  getTablePage(params: TablePageQuery) {
    return request<any, PageResult<TablePageVO[]>>({
      url: `${GENERATOR_BASE_URL}/table/page`,
      method: "get",
      params,
    });
  },

  /** 코드 생성 설정 조회 */
  getGenConfig(tableName: string) {
    return request<any, GenConfigForm>({
      url: `${GENERATOR_BASE_URL}/${tableName}/config`,
      method: "get",
    });
  },

  /** 코드 생성 설정 저장 */
  saveGenConfig(tableName: string, data: GenConfigForm) {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/config`,
      method: "post",
      data,
    });
  },

  /** 코드 생성 미리보기 데이터 조회 */
  getPreviewData(tableName: string, pageType?: "classic" | "curd") {
    return request<any, GeneratorPreviewVO[]>({
      url: `${GENERATOR_BASE_URL}/${tableName}/preview`,
      method: "get",
      params: pageType ? { pageType } : undefined,
    });
  },

  /** 코드 생성 설정 초기화 */
  resetGenConfig(tableName: string) {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/config`,
      method: "delete",
    });
  },

  /**
   * 下载 ZIP 文件
   * @param url
   * @param fileName
   */
  download(tableName: string, pageType?: "classic" | "curd") {
    return request({
      url: `${GENERATOR_BASE_URL}/${tableName}/download`,
      method: "get",
      params: pageType ? { pageType } : undefined,
      responseType: "blob",
    }).then((response) => {
      const fileName = decodeURI(
        response.headers["content-disposition"].split(";")[1].split("=")[1]
      );

      const blob = new Blob([response.data], { type: "application/zip" });
      const a = document.createElement("a");
      const url = window.URL.createObjectURL(blob);
      a.href = url;
      a.download = fileName;
      a.click();
      window.URL.revokeObjectURL(url);
    });
  },
};

export default GeneratorAPI;

/** 코드 생성 미리보기 객체 */
export interface GeneratorPreviewVO {
  /** 파일 생성 경로 */
  path: string;
  /** 파일명 */
  fileName: string;
  /** 파일 내용 */
  content: string;
}

/**  데이터 테이블 페이지 쿼리 파라미터 */
export interface TablePageQuery extends PageQuery {
  /** 키워드(테이블명) */
  keywords?: string;
}

/** 데이터 테이블 페이지 객체 */
export interface TablePageVO {
  /** 테이블명 */
  tableName: string;

  /** 테이블 설명 */
  tableComment: string;

  /** 스토리지 엔진 */
  engine: string;

  /** 문자집합 정렬 규칙 */
  tableCollation: string;

  /** 생성 시간 */
  createTime: string;
}

/** 코드 생성 설정 폼 */
export interface GenConfigForm {
  /** 기본 키 */
  id?: string;

  /** 테이블명 */
  tableName?: string;

  /** 비즈니스명 */
  businessName?: string;

  /** 모듈명 */
  moduleName?: string;

  /** 패키지명 */
  packageName?: string;

  /** 엔티티명 */
  entityName?: string;

  /** 작성자 */
  author?: string;

  /** 상위 메뉴 */
  parentMenuId?: string;

  /** 백엔드 애플리케이션명 */
  backendAppName?: string;
  /** 프론트엔드 애플리케이션명 */
  frontendAppName?: string;

  /** 필드 설정 목록 */
  fieldConfigs?: FieldConfig[];

  /** 페이지 타입 classic|curd */
  pageType?: "classic" | "curd";

  /** 제거할 테이블 접두사, 예: sys_ */
  removeTablePrefix?: string;
}

/** 필드 설정 */
export interface FieldConfig {
  /** 기본 키 */
  id?: string;

  /** 컬럼명 */
  columnName?: string;

  /** 컬럼 타입 */
  columnType?: string;

  /** 필드명 */
  fieldName?: string;

  /** 필드 타입 */
  fieldType?: string;

  /** 필드 설명 */
  fieldComment?: string;

  /** 목록에 표시 여부 */
  isShowInList?: number;

  /** 폼에 표시 여부 */
  isShowInForm?: number;

  /** 쿼리 조건에 표시 여부 */
  isShowInQuery?: number;

  /** 필수 입력 여부 */
  isRequired?: number;

  /** 폼 타입 */
  formType?: number;

  /** 쿼리 타입 */
  queryType?: number;

  /** 필드 길이 */
  maxLength?: number;

  /** 필드 정렬 */
  fieldSort?: number;

  /** 사전 타입 */
  dictType?: string;
}
