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
type ToolbarRight = "참조resh" | "filter" | "imports" | "exports" | "search";
type ToolbarTable = "edit" | "view" | "delete";
export type IToolsButton = {
  name: string; // 버튼이름칭
  text?: string; // 버튼文本
  perm?: Array<string> | string; // 权限标识(可以是完整权限문자열如'sys:user:add'或작업权限如'add')
  attrs?: Partial<ButtonProps> & { style?: CSSProperties }; // 버튼속성
  render?: (row: IObject) => boolean; // 조건렌더링
};
export type IToolsDefault = ToolbarLeft | ToolbarRight | ToolbarTable | IToolsButton;

export interface IOperateData {
  name: string;
  row: IObject;
  column: IObject;
  $index: number;
}

export interface ISearchConfig {
  // 权限前缀(如sys:user，용도그룹成权限标识)，不提供그러면不进行权限검사
  permP참조ix?: string;
  // 标签冒号(기본값：false)
  colon?: boolean;
  // 양식항목(기본값：[])
  formItems?: IFormItems<ISearchComponent>;
  // 是否开启펼치기및축소(기본값：true)
  isExpandable?: boolean;
  // 기본값展示의양식항목개量(기본값：3)
  showNumber?: number;
  // 卡片속성
  cardAttrs?: Partial<CardProps> & { style?: CSSProperties };
  // form컴포넌트속성
  form?: IForm;
  // 自适应网格레이아웃(사용시양식不해야추가 style: { width: "200px" })
  grid?: boolean | "left" | "right";
}

export interface IContentConfig<T = any> {
  // 权限前缀(如sys:user，용도그룹成权限标识)，不提供그러면不进行权限검사
  permP참조ix?: string;
  // table컴포넌트속성
  table?: Omit<TableProps<any>, "data">;
  // 페이지네이션컴포넌트位置(기본값：left)
  pagePosition?: "left" | "right";
  // pagination컴포넌트속성
  pagination?:
    | boolean
    | Partial<
        Omit<
          PaginationProps,
          "v-model:page-size" | "v-model:current-page" | "total" | "currentPage"
        >
      >;
  // 목록의网络요청함수(필요돌아가기promise)
  indexAction: (queryParams: T) => Promise<any>;
  // 기본값의페이지네이션관련의요청 파라미터
  request?: {
    pageName: string;
    limitName: string;
  };
  // 데이터格式파싱의콜백함수
  parseData?: (res: any) => {
    total: number;
    list: IObject[];
    [키: string]: any;
  };
  // 수정속성의网络요청함수(필요돌아가기promise)
  modifyAction?: (data: {
    [키: string]: any;
    field: string;
    value: boolean | string | number;
  }) => Promise<any>;
  // 삭제의网络요청함수(필요돌아가기promise)
  deleteAction?: (ids: string) => Promise<any>;
  // 백엔드내보내기의网络요청함수(필요돌아가기promise)
  exportAction?: (queryParams: T) => Promise<any>;
  // 프론트엔드全量내보내기의网络요청함수(필요돌아가기promise)
  exportsAction?: (queryParams: T) => Promise<IObject[]>;
  // 가져오기템플릿
  importTemplate?: string | (() => Promise<any>);
  // 백엔드가져오기의网络요청함수(필요돌아가기promise)
  importAction?: (file: File) => Promise<any>;
  // 프론트엔드가져오기의网络요청함수(필요돌아가기promise)
  importsAction?: (data: IObject[]) => Promise<any>;
  // 주요키이름(기본값로id)
  pk?: string;
  // 表格工具열(기본값:add,delete,export,也可사용자 정의)
  toolbar?: Array<ToolbarLeft | IToolsButton>;
  // 表格工具열右측아이콘(기본값:참조resh,filter,imports,exports,search)
  defaultToolbar?: Array<ToolbarRight | IToolsButton>;
  // table컴포넌트列속성(额外의속성templet,operat,slotName)
  cols: Array<{
    type?: "default" | "selection" | "index" | "expand";
    label?: string;
    prop?: string;
    width?: string | number;
    align?: "left" | "center" | "right";
    columnKey?: string;
    reserveSelection?: boolean;
    // 列是否显示
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
    // image템플릿관련파라미터
    imageWidth?: number;
    imageHeight?: number;
    // list템플릿관련파라미터
    selectList?: IObject;
    // switch템플릿관련파라미터
    activeValue?: boolean | string | number;
    inactiveValue?: boolean | string | number;
    activeText?: string;
    inactiveText?: string;
    // input템플릿관련파라미터
    inputType?: string;
    // price템플릿관련파라미터
    priceFormat?: string;
    // date템플릿관련파라미터
    dateFormat?: string;
    // tool템플릿관련파라미터
    operat?: Array<ToolbarTable | IToolsButton>;
    // filter값연결符
    filterJoin?: string;
    [키: string]: any;
    // 초기화데이터함수
    initFn?: (item: IObject) => void;
  }>;
}

export interface IModalConfig<T = any> {
  // 权限前缀(如sys:user，용도그룹成权限标识)，不提供그러면不进行权限검사
  permP참조ix?: string;
  // 标签冒号(기본값：false)
  colon?: boolean;
  // 주요키이름(주요해야용도편집데이터,기본값로id)
  pk?: string;
  // 컴포넌트타입(기본값：dialog)
  component?: "dialog" | "drawer";
  // dialog컴포넌트속성
  dialog?: Partial<Omit<DialogProps, "modelValue">>;
  // drawer컴포넌트속성
  drawer?: Partial<Omit<DrawerProps, "modelValue">>;
  // form컴포넌트속성
  form?: IForm;
  // 양식항목
  formItems: IFormItems<IComponentType>;
  // 제출之前처리
  beforeSubmit?: (data: T) => void;
  // 제출의网络요청함수(필요돌아가기promise)
  formAction?: (data: T) => Promise<any>;
}

export type IForm = Partial<Omit<FormProps, "model" | "rules">>;

// 양식항목
export type IFormItems<T = IComponentType> = Array<{
  // 컴포넌트타입(如input,select,radio,custom等)
  type: T;
  // 标签提示
  tips?: string | IObject;
  // 标签文本
  label: string;
  // 키이름
  prop: string;
  // 컴포넌트속성
  attrs?: IObject;
  // 컴포넌트可옵션(오직适용도select,radio,checkbox컴포넌트)
  options?: Array<{ label: string; value: any; [키: string]: any }> | Ref<any[]>;
  // 검증规그러면
  rules?: FormItemRule[];
  // 初始값
  initialValue?: any;
  // 슬롯이름(适용도사용자 정의컴포넌트，설정타입로custom)
  slotName?: string;
  // 是否隐藏
  hidden?: boolean;
  // layout컴포넌트Col속성
  col?: Partial<ColProps>;
  // 컴포넌트이벤트
  events?: Record<string, (...args: any) => void>;
  // 초기화데이터함수扩展
  initFn?: (item: IObject) => void;
}>;

export interface IPageForm {
  // 주요키이름(주요해야용도편집데이터,기본값로id)
  pk?: string;
  // form컴포넌트속성
  form?: IForm;
  // 양식항목
  formItems: IFormItems<IComponentType>;
}
