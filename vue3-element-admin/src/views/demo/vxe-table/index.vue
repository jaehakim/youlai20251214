<template>
  <div class="app-container">
    <!-- 테이블 -->
    <vxe-grid ref="xGrid" v-bind="gridOptions" v-on="gridEvents">
      <!-- 검색 -->
      <!-- <template #form-roles="{ data }">
        <el-select
          v-model="data.roles"
          multiple
          collapse-tags
          collapse-tags-tooltip
          placeholder="역할 선택"
          filterable
          clearable
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </template> -->
      <!-- 왼쪽 버튼 목록 -->
      <template #toolbar-btns>
        <vxe-button status="primary" icon="vxe-icon-add" @click="curd.onShowModal()">
          사용자 추가
        </vxe-button>
        <vxe-button status="danger" icon="vxe-icon-delete" @click="curd.onDelete()">
          일괄 삭제
        </vxe-button>
      </template>
      <!-- 확장 열 -->
      <template #column-expand="{ row }">
        <div style="padding: 20px">
          <ul>
            <li>
              <span>ID：</span>
              <span>{{ row.id }}</span>
            </li>
            <li>
              <span>UserName：</span>
              <span>{{ row.username }}</span>
            </li>
            <li>
              <span>CreateTime：</span>
              <span>{{ row.createTime }}</span>
            </li>
          </ul>
        </div>
      </template>
      <!-- 역할 열 -->
      <template #column-roles="{ row, column }">
        <el-tag
          v-for="(role, index) in row[column.field].split(',')"
          :key="index"
          :type="role === 'admin' ? 'primary' : 'warning'"
          effect="plain"
        >
          {{ role }}
        </el-tag>
      </template>
      <!-- 작업 열 -->
      <template #column-operate="{ row }">
        <el-button link type="primary" @click="curd.onShowModal(row)">수정</el-button>
        <el-button link type="danger" @click="curd.onDelete(row)">삭제</el-button>
      </template>
    </vxe-grid>
    <!-- 팝업 -->
    <vxe-modal ref="xModal" v-bind="modalOptions">
      <!-- 양식 -->
      <vxe-form ref="xForm" v-bind="formOptions" />
    </vxe-modal>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from "vue";
import type {
  VxeGridInstance,
  VxeGridProps,
  VxeGridListeners,
  VxeModalInstance,
  VxeModalProps,
  VxeFormInstance,
  VxeFormProps,
} from "vxe-table";
import { VXETable } from "vxe-table";

const options = [
  { label: "관리자", value: "admin" },
  { label: "사용자", value: "user" },
  { label: "게스트", value: "guest" },
];
onMounted(() => {
  setTimeout(() => {
    gridOptions!.formConfig!.items!.forEach((item) => {
      if (item.field === "roles") {
        item!.itemRender!.props!.options = options;
      }
    });
  }, 500);
});

// #region vxe-grid
interface RowMeta {
  id: number;
  username: string;
  roles: string;
  phone: string;
  email: string;
  status: boolean;
  createTime: string;
}
const xGrid = ref<VxeGridInstance<RowMeta>>();
const gridOptions = reactive<VxeGridProps<RowMeta>>({
  // 부모 요소의 변화를 자동으로 감시하여 테이블을 다시 계산합니다
  autoResize: true,
  // 테이블 바닥글 표시 여부
  showFooter: true,
  // 테이블 푸터 데이터 (우선순위가 footerMethod보다 높음)
  // footerData: [
  //   {
  //     username: "-",
  //     roles: "-",
  //     phone: "-",
  //     email: "-",
  //     status: "활성화：7개",
  //     createTime: "-",
  //   },
  // ],
  // 테이블 푸터의 데이터 조회 메서드, 2차원 배열 반환
  footerMethod({ columns, data }) {
    return [
      columns.map((column, columnIndex) => {
        if (columnIndex === 0 || column.field === undefined) {
          return "";
        } else if (column.field === "status") {
          return `활성화：${data.reduce((sum, row) => sum + (row.status ? 1 : 0), 0)}개`;
        }
        return "-";
      }),
    ];
  },
  // 열 설정
  columns: [
    { type: "checkbox", width: 60 },
    {
      type: "expand",
      width: 60,
      slots: {
        // type=expand일 때만 유효, 펼치기 후의 내용 템플릿 사용자 정의
        content: "column-expand",
      },
    },
    { type: "seq", width: 60 },
    { field: "id", title: "ID", visible: false },
    { field: "username", title: "사용자명" },
    { field: "roles", title: "역할", slots: { default: "column-roles" } },
    { field: "phone", title: "휴대폰 번호" },
    { field: "email", title: "이메일" },
    {
      field: "status",
      title: "상태",
      sortable: true,
      filters: [
        { label: "활성화", value: true },
        { label: "비활성화", value: false },
      ],
      // 데이터 필터, filters일 때만 유효, 다중 선택 필터 허용 여부
      filterMultiple: false,
      formatter({ cellValue }) {
        return cellValue === true ? "활성화" : "비활성화";
      },
    },
    { field: "createTime", title: "생성 시간", sortable: true },
    {
      title: "작업",
      width: "150px",
      fixed: "right",
      showOverflow: false,
      slots: {
        default: "column-operate",
      },
    },
  ],
  // 열 설정 정보
  columnConfig: {
    // 각 열의 열 너비 조정 활성화 여부
    resizable: true,
  },
  // 사용자 정의 열 설정 항목
  customConfig: {
    // 열 선택 허용 여부
    checkMethod: ({ column }) => !["username"].includes(column.field),
  },
  // 체크박스 설정 항목
  checkboxConfig: {
    // 선택 상태 유지 여부 (row-config.keyField 필요)
    // reserve: true,
  },
  // 펼치기 행 설정 항목 (가상 스크롤 지원하지 않음)
  expandConfig: {
    // 펼치기 열에 표시할 필드명, 셀 내에 직접 표시 가능
    // labelField: "username",
    // 매번 한 행만 펼치기 가능
    accordion: true,
  },
  // 행 설정 정보
  rowConfig: {
    // 사용자 정의 행 데이터의 고유 주요 키 필드명
    keyField: "id",
    // 마우스로 행 클릭 시, 현재 행 하이라이트 여부
    isCurrent: true,
  },
  // 양식 설정 항목
  formConfig: {
    // 항목 설정
    items: [
      {
        span: 4,
        field: "username",
        title: "사용자명",
        // 접두사 설정 항목
        titlePrefix: {
          useHTML: true,
          content:
            '링크 클릭: <a class="link" href="https://vxetable.cn" target="_blank">vxe-table 공식 사이트</a>',
          icon: "vxe-icon-question-circle-fill",
        },
        // 항목 렌더러 설정 항목
        itemRender: {
          // 렌더러 이름
          name: "VxeInput",
          // 렌더링 파라미터
          props: {
            type: "text",
            clearable: true,
            placeholder: "사용자명을 입력하세요",
          },
        },
      },
      {
        span: 4,
        field: "roles",
        title: "역할",
        // 기본값 접기
        folding: true,
        itemRender: {
          name: "VxeSelect",
          props: {
            multiple: true,
            multiCharOverflow: -1,
            filterable: true,
            clearable: true,
            options: [],
            placeholder: "역할 선택",
          },
        },
      },
      // {
      //   span: 4,
      //   field: "roles",
      //   title: "역할",
      //   // 기본값 접기
      //   folding: true,
      //   // 슬롯
      //   slots: {
      //     // 사용자 정의 양식 항목
      //     default: "form-roles",
      //   },
      // },
      {
        collapseNode: true,
        itemRender: {
          name: "VxeButtonGroup",
          options: [
            {
              type: "submit",
              status: "primary",
              icon: "vxe-icon-search",
              content: "검색",
            },
            { type: "reset", icon: "vxe-icon-refresh", content: "재설정" },
          ],
        },
      },
    ],
  },
  // 도구 모음 설정
  toolbarConfig: {
    // 가져오기 버튼 설정 ("import-config" 설정 필요)
    import: true,
    // 내보내기 버튼 설정 ("export-config" 설정 필요)
    export: true,
    // 인쇄 버튼 설정 ("print-config" 설정 필요)
    print: true,
    // 새로고침 버튼 설정
    refresh: true,
    // 최대화 표시 허용 여부
    zoom: true,
    // 사용자 정의 열 설정
    custom: true,
    // 슬롯
    slots: {
      // 버튼 목록
      buttons: "toolbar-btns",
    },
  },
  // 가져오기 설정 항목
  importConfig: {},
  // 내보내기 설정 항목
  exportConfig: {
    // 지정된 열
    columns: [{ field: "phone" }, { field: "email" }, { field: "status" }, { field: "createTime" }],
  },
  // 인쇄 설정 항목
  printConfig: {},
  // 필터 설정 항목
  filterConfig: {
    // 모든 열에서 서버 측 필터 사용 여부
    remote: false,
  },
  // 정렬 설정 항목
  sortConfig: {
    // 모든 열에서 서버 측 정렬 사용 여부
    remote: false,
    // 다중 열 조합 정렬 활성화 여부
    multiple: false,
    // multiple일 때만 유효, 트리거 순서에 따라 정렬 여부
    chronological: true,
  },
  // 페이지네이션 설정 항목
  pagerConfig: {
    enabled: true,
    pageSize: 10,
  },
  // 데이터 프록시 설정 항목
  proxyConfig: {
    // 자동으로 조회 데이터 로딩 여부
    autoLoad: true,
    // 동적 순번 프록시 활성화 (페이지네이션 후 인덱스가 현재 페이지의 시작 순번으로 자동 계산)
    seq: true,
    // 양식 프록시
    form: true,
    // 필터 프록시 여부 (filter-config.remote=true일 때만 유효)
    filter: true,
    // 정렬 프록시 여부 (sort-config.remote=true일 때만 유효)
    sort: true,
    // 조회 응답 값 설정
    response: {
      // pager-config 설정 시 유효, 응답 결과 내 조회 데이터 목록의 속성 (페이지네이션 시나리오)
      result: "result",
      // pager-config 설정 시 유효, 응답 결과 내 조회 페이지네이션의 속성 (페이지네이션 시나리오)
      total: "total",
    },
    ajax: {
      // Promise 수신
      query: ({ page: { currentPage, pageSize }, form, filters, sort, sorts }) => {
        console.log({ currentPage, pageSize, form, filters, sort, sorts });
        return new Promise<{ total: number; result: RowMeta[] }>((resolve) => {
          setTimeout(() => {
            const list = [
              {
                username: "Richard Clark",
                roles: "editor",
                phone: "18185826431",
                email: "y.djf@xiswx.fk",
                status: true,
                createTime: "2010-04-17 12:39:20",
                id: 810000201008060500,
              },
              {
                username: "Robert Garcia",
                roles: "admin",
                phone: "18125716043",
                email: "z.japgndxosu@inoudjxc.ie",
                status: false,
                createTime: "2020-01-02 11:51:58",
                id: 130000201904129330,
              },
              {
                username: "Thomas Moore",
                roles: "admin",
                phone: "18106622048",
                email: "j.fvsgnjjutm@fmjw.se",
                status: true,
                createTime: "1983-10-12 10:06:41",
                id: 420000198203053100,
              },
              {
                username: "Dorothy Lewis",
                roles: "admin",
                phone: "13321357284",
                email: "o.htso@iwxvehrs.tj",
                status: true,
                createTime: "1970-03-03 00:26:45",
                id: 150000201803243100,
              },
              {
                username: "George Rodriguez",
                roles: "admin",
                phone: "18158641167",
                email: "x.sigizx@fwknokiqn.tr",
                status: true,
                createTime: "1988-03-16 14:46:26",
                id: 610000199308265900,
              },
              {
                username: "Angela Jackson",
                roles: "admin",
                phone: "19810721230",
                email: "j.gqrdqaqtu@ipthgm.fj",
                status: true,
                createTime: "2006-09-26 12:53:37",
                id: 350000197310101440,
              },
              {
                username: "James Walker",
                roles: "admin",
                phone: "18123903251",
                email: "k.axmdcsl@mcmeudog.cl",
                status: true,
                createTime: "1981-01-19 12:51:34",
                id: 130000199308208900,
              },
              {
                username: "Paul Garcia",
                roles: "admin",
                phone: "18617930381",
                email: "c.glufsn@vwqntlllj.es",
                status: false,
                createTime: "2009-12-04 20:40:57",
                id: 510000199212239200,
              },
              {
                username: "Jeffrey Miller",
                roles: "admin",
                phone: "18145245413",
                email: "u.poqrqw@arto.rw",
                status: false,
                createTime: "1991-04-01 05:16:52",
                id: 330000198604109760,
              },
              {
                username: "Donna Lewis",
                roles: "editor",
                phone: "19839835537",
                email: "l.lmpeoupu@rujdlzdbk.gf",
                status: true,
                createTime: "1987-11-29 21:47:37",
                id: 640000197005230500,
              },
              {
                username: "Jennifer Smith",
                roles: "editor",
                phone: "18145245413",
                email: "j.jqx@xjxqx.jp",
                status: true,
                createTime: "1991-04-01 05:16:52",
                id: 640000197005230000,
              },
            ];
            resolve({
              result: list.slice((currentPage - 1) * pageSize, currentPage * pageSize),
              total: list.length,
            });
          }, 500);
        });
      },
    },
  },
});
const gridEvents: VxeGridListeners<RowMeta> = {
  // form-config 설정 시 유효, 양식 초기화 시 해당 이벤트 트리거
  formReset() {
    console.log("Form Reset");
  },
};
// #endregion

// #region vxe-modal
const xModal = ref<VxeModalInstance>();
const modalOptions = reactive<VxeModalProps>({
  // 창 제목
  title: "",
  // 마스크 레이어 클릭으로 창 닫기 허용 여부
  maskClosable: true,
  // Esc 키로 창 닫기 허용 여부
  escClosable: true,
  // 창 숨기기 전 실행
  beforeHideMethod: () => {
    xForm.value?.clearValidate();
    return Promise.resolve();
  },
});
// #endregion

// #region vxe-form
const xForm = ref<VxeFormInstance>();
const formOptions = reactive<VxeFormProps>({
  // 모든 항목의 그리드가 차지하는 열 수
  span: 24,
  // 모든 항목의 제목 너비
  titleWidth: 100,
  // 양식 데이터
  data: {
    username: "",
    password: "",
  },
  // 항목 목록
  items: [
    {
      field: "username",
      title: "사용자명",
      itemRender: {
        name: "VxeInput",
        props: {
          placeholder: "입력하세요",
        },
      },
    },
    {
      field: "password",
      title: "비밀번호",
      itemRender: {
        name: "VxeInput",
        props: {
          placeholder: "입력해주세요",
        },
      },
    },
    {
      align: "right",
      itemRender: {
        name: "$buttons",
        children: [
          {
            props: {
              content: "취소",
            },
            events: {
              click: () => xModal.value?.close(),
            },
          },
          {
            props: {
              type: "submit",
              content: "확인",
              status: "primary",
            },
            events: {
              click: () => curd.onSubmitForm(),
            },
          },
        ],
      },
    },
  ],
  /** 검증 규칙 */
  rules: {
    username: [
      {
        required: true,
        validator: ({ itemValue }) => {
          switch (true) {
            case !itemValue:
              return new Error("입력하세요");
            case !itemValue.trim():
              return new Error("공백은 유효하지 않습니다");
          }
        },
      },
    ],
    password: [
      {
        required: true,
        validator: ({ itemValue }) => {
          switch (true) {
            case !itemValue:
              return new Error("입력하세요");
            case !itemValue.trim():
              return new Error("공백은 유효하지 않습니다");
          }
        },
      },
    ],
  },
});
// #endregion

const curd = {
  commitQuery: () => xGrid.value?.commitProxy("query"),
  onShowModal: (row?: RowMeta) => {
    if (row) {
      modalOptions.title = "사용자 수정";
    } else {
      modalOptions.title = "사용자 추가";
    }
    xModal.value?.open();
  },
  /** 확인 및 저장 */
  onSubmitForm: () => {
    console.log("양식 제출");
  },
  onDelete: (row?: RowMeta) => {
    let ids = [];
    if (row === undefined) {
      // 현재 선택된 행 데이터 가져오기
      const selected = xGrid.value?.getCheckboxRecords();
      if (!selected || selected.length === 0) {
        VXETable.modal.message({
          content: "최소 1개 데이터를 선택하세요",
          status: "warning",
        });
        return;
      }
      ids = selected.map((item) => item.id);
    } else {
      ids = [row.id];
    }
    VXETable.modal.confirm("삭제하시겠습니까?").then((type) => {
      if (type === "confirm") {
        // 삭제 작업 실행
        console.log("삭제된 ID:", ids);
      }
    });
  },
};
</script>

<style scoped></style>
