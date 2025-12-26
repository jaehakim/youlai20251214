import type { DialogProps, DrawerProps, FormItemRule, PaginationProps } from "element-plus";
import type { FormProps, TableProps, ColProps, ButtonProps, CardProps } from "element-plus";
import type PageContent from "./PageContent.vue";
import type PageModal from "./PageModal.vue";
import type PageSearch from "./PageSearch.vue";
import type { CSSProperties } from "vue";

export type PageSearchInstance = InstanceType<typeof PageSearch>;
export type PageContentInstance = InstanceType<typeof PageContent>;
export type PageModalInstance = InstanceType<typeof PageModal>;

export type IObject = Record<string, any>;

type DateComponent = "date-picker" | "time-picker" | "time-select" | "custom-tag" | "input-tag";
type InputComponent = "input" | "select" | "input-number" | "cascader" | "tree-select";
type OtherComponent = "text" | "radio" | "checkbox" | "switch" | "icon-select" | "custom";
export type ISearchComponent = DateComponent | InputComponent | "custom";
export type IComponentType = DateComponent | InputComponent | OtherComponent;

type ToolbarLeft = "add" | "delete" | "import" | "export";
type ToolbarRight = "refresh" | "filter" | "imports" | "exports" | "search";
type ToolbarTable = "edit" | "view" | "delete";
export type IToolsButton = {
  name: string; // 버튼 이름
  text?: string; // 버튼 텍스트
  perm?: Array<string> | string; // 권한 식별자(완전한 권한 문자열 예: 'sys:user:add' 또는 작업 권한 예: 'add')
  attrs?: Partial<ButtonProps> & { style?: CSSProperties }; // 버튼 속성
  render?: (row: IObject) => boolean; // 조건부 렌더링
};
export type IToolsDefault = ToolbarLeft | ToolbarRight | ToolbarTable | IToolsButton;

export interface IOperateData {
  name: string;
  row: IObject;
  column: IObject;
  $index: number;
}

export interface ISearchConfig {
  // 권한 접두사(예: sys:user, 권한 식별자 그룹화에 사용), 제공하지 않으면 권한 검사 수행 안 함
  permPrefix?: string;
  // 레이블 콜론(기본값: false)
  colon?: boolean;
  // 폼 항목(기본값: [])
  formItems?: IFormItems<ISearchComponent>;
  // 확장/축소 활성화 여부(기본값: true)
  isExpandable?: boolean;
  // 기본 표시 폼 항목 개수(기본값: 3)
  showNumber?: number;
  // 카드 속성
  cardAttrs?: Partial<CardProps> & { style?: CSSProperties };
  // form 컴포넌트 속성
  form?: IForm;
  // 자동 적응 그리드 레이아웃(사용 시 폼에 style: { width: "200px" } 추가 불필요)
  grid?: boolean | "left" | "right";
}

export interface IContentConfig<T = any> {
  // 권한 접두사(예: sys:user, 권한 식별자 그룹화에 사용), 제공하지 않으면 권한 검사 수행 안 함
  permPrefix?: string;
  // table 컴포넌트 속성
  table?: Omit<TableProps<any>, "data">;
  // 페이지네이션 컴포넌트 위치(기본값: left)
  pagePosition?: "left" | "right";
  // pagination 컴포넌트 속성
  pagination?:
    | boolean
    | Partial<
        Omit<
          PaginationProps,
          "v-model:page-size" | "v-model:current-page" | "total" | "currentPage"
        >
      >;
  // 목록 네트워크 요청 함수(Promise 반환 필요)
  indexAction: (queryParams: T) => Promise<any>;
  // 기본 페이지네이션 관련 요청 파라미터
  request?: {
    pageName: string;
    limitName: string;
  };
  // 데이터 형식 파싱 콜백 함수
  parseData?: (res: any) => {
    total: number;
    list: IObject[];
    [key: string]: any;
  };
  // 속성 수정 네트워크 요청 함수(Promise 반환 필요)
  modifyAction?: (data: {
    [key: string]: any;
    field: string;
    value: boolean | string | number;
  }) => Promise<any>;
  // 삭제 네트워크 요청 함수(Promise 반환 필요)
  deleteAction?: (ids: string) => Promise<any>;
  // 백엔드 내보내기 네트워크 요청 함수(Promise 반환 필요)
  exportAction?: (queryParams: T) => Promise<any>;
  // 프론트엔드 전체 내보내기 네트워크 요청 함수(Promise 반환 필요)
  exportsAction?: (queryParams: T) => Promise<IObject[]>;
  // 가져오기 템플릿
  importTemplate?: string | (() => Promise<any>);
  // 백엔드 가져오기 네트워크 요청 함수(Promise 반환 필요)
  importAction?: (file: File) => Promise<any>;
  // 프론트엔드 가져오기 네트워크 요청 함수(Promise 반환 필요)
  importsAction?: (data: IObject[]) => Promise<any>;
  // 기본 키 이름(기본값: id)
  pk?: string;
  // 테이블 툴바(기본값: add,delete,export, 사용자 정의 가능)
  toolbar?: Array<ToolbarLeft | IToolsButton>;
  // 테이블 툴바 우측 아이콘(기본값: refresh,filter,imports,exports,search)
  defaultToolbar?: Array<ToolbarRight | IToolsButton>;
  // table 컴포넌트 컬럼 속성(추가 속성: templet, operat, slotName)
  cols: Array<{
    type?: "default" | "selection" | "index" | "expand";
    label?: string;
    prop?: string;
    width?: string | number;
    align?: "left" | "center" | "right";
    columnKey?: string;
    reserveSelection?: boolean;
    // 컬럼 표시 여부
    show?: boolean;
    // 템플릿
    templet?:
      | "image"
      | "list"
      | "url"
      | "switch"
      | "input"
      | "price"
      | "percent"
      | "icon"
      | "date"
      | "tool"
      | "custom";
    // image 템플릿 관련 파라미터
    imageWidth?: number;
    imageHeight?: number;
    // list 템플릿 관련 파라미터
    selectList?: IObject;
    // switch 템플릿 관련 파라미터
    activeValue?: boolean | string | number;
    inactiveValue?: boolean | string | number;
    activeText?: string;
    inactiveText?: string;
    // input 템플릿 관련 파라미터
    inputType?: string;
    // price 템플릿 관련 파라미터
    priceFormat?: string;
    // date 템플릿 관련 파라미터
    dateFormat?: string;
    // tool 템플릿 관련 파라미터
    operat?: Array<ToolbarTable | IToolsButton>;
    // filter 값 연결자
    filterJoin?: string;
    [key: string]: any;
    // 초기화 데이터 함수
    initFn?: (item: IObject) => void;
  }>;
}

export interface IModalConfig<T = any> {
  // 권한 접두사(예: sys:user, 권한 식별자 그룹화에 사용), 제공하지 않으면 권한 검사 수행 안 함
  permPrefix?: string;
  // 레이블 콜론(기본값: false)
  colon?: boolean;
  // 기본 키 이름(주로 데이터 편집용, 기본값: id)
  pk?: string;
  // 컴포넌트 타입(기본값: dialog)
  component?: "dialog" | "drawer";
  // dialog 컴포넌트 속성
  dialog?: Partial<Omit<DialogProps, "modelValue">>;
  // drawer 컴포넌트 속성
  drawer?: Partial<Omit<DrawerProps, "modelValue">>;
  // form 컴포넌트 속성
  form?: IForm;
  // 폼 항목
  formItems: IFormItems<IComponentType>;
  // 제출 전 처리
  beforeSubmit?: (data: T) => void;
  // 제출 네트워크 요청 함수(Promise 반환 필요)
  formAction?: (data: T) => Promise<any>;
}

export type IForm = Partial<Omit<FormProps, "model" | "rules">>;

// 폼 항목
export type IFormItems<T = IComponentType> = Array<{
  // 컴포넌트 타입(예: input, select, radio, custom 등)
  type: T;
  // 레이블 힌트
  tips?: string | IObject;
  // 레이블 텍스트
  label: string;
  // 키 이름
  prop: string;
  // 컴포넌트 속성
  attrs?: IObject;
  // 컴포넌트 옵션(select, radio, checkbox 컴포넌트에만 적용)
  options?: Array<{ label: string; value: any; [key: string]: any }> | Ref<any[]>;
  // 검증 규칙
  rules?: FormItemRule[];
  // 초기 값
  initialValue?: any;
  // 슬롯 이름(사용자 정의 컴포넌트에 적용, 타입을 custom으로 설정)
  slotName?: string;
  // 숨김 여부
  hidden?: boolean;
  // layout 컴포넌트 Col 속성
  col?: Partial<ColProps>;
  // 컴포넌트 이벤트
  events?: Record<string, (...args: any) => void>;
  // 초기화 데이터 함수 확장
  initFn?: (item: IObject) => void;
}>;

export interface IPageForm {
  // 기본 키 이름(주로 데이터 편집용, 기본값: id)
  pk?: string;
  // form 컴포넌트 속성
  form?: IForm;
  // 폼 항목
  formItems: IFormItems<IComponentType>;
}
