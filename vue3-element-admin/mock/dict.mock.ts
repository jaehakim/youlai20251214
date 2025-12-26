import { defineMock } from "./base";

export default defineMock([
  {
    url: "dicts/page",
    method: ["GET"],
    body: {
      code: "00000",
      data: {
        list: [
          {
            id: 1,
            name: "성별",
            dictCode: "gender",
            status: 1,
          },
        ],
        total: 1,
      },
      msg: "모두 정상",
    },
  },

  /**
   * 사전 목록
   */
  {
    url: "dicts",
    method: ["GET"],
    body: {
      code: "00000",
      data: {
        list: [
          {
            value: "gender",
            label: "성별",
          },
        ],
        total: 1,
      },
      msg: "모두 정상",
    },
  },

  // 사전 추가
  {
    url: "dicts",
    method: ["POST"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "사전 추가 " + body.name + " 성공",
      };
    },
  },

  // 사전 폼 데이터 조회
  {
    url: "dicts/:id/form",
    method: ["GET"],
    body: ({ params }) => {
      return {
        code: "00000",
        data: dictMap[params.id],
        msg: "모두 정상",
      };
    },
  },

  // 사전 수정
  {
    url: "dicts/:id",
    method: ["PUT"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "사전 수정 " + body.name + " 성공",
      };
    },
  },

  // 사전 삭제
  {
    url: "dicts/:ids",
    method: ["DELETE"],
    body({ params }) {
      return {
        code: "00000",
        data: null,
        msg: "사전 삭제 " + params.ids + " 성공",
      };
    },
  },

  //---------------------------------------------------
  // 사전 항목 관련 인터페이스
  //---------------------------------------------------

  // 사전 항목 페이지 목록
  {
    url: "dicts/:dictCode/items/page",
    method: ["GET"],
    body: {
      code: "00000",
      data: {
        list: [
          {
            id: 1,
            dictCode: "gender",
            label: "남",
            value: "1",
            sort: 1,
            status: 1,
          },
          {
            id: 2,
            dictCode: "gender",
            label: "여",
            value: "2",
            sort: 2,
            status: 1,
          },
          {
            id: 3,
            dictCode: "gender",
            label: "비공개",
            value: "0",
            sort: 3,
            status: 1,
          },
        ],
        total: 3,
      },
      msg: "모두 정상",
    },
  },
  // 사전 항목 목록
  {
    url: "dicts/:dictCode/items",
    method: ["GET"],
    body: ({ params }) => {
      const dictCode = params.dictCode;

      let list = null;

      if (dictCode == "gender") {
        list = [
          {
            value: "1",
            label: "남",
          },
          {
            value: "2",
            label: "여",
          },
          {
            value: "0",
            label: "비공개",
          },
        ];
      } else if (dictCode == "notice_level") {
        list = [
          {
            value: "L",
            label: "낮음",
            tag: "info",
          },
          {
            value: "M",
            label: "중간",
            tag: "warning",
          },
          {
            value: "H",
            label: "높음",
            tag: "danger",
          },
        ];
      } else if (dictCode == "notice_type") {
        list = [
          {
            value: "1",
            label: "시스템 업그레이드",
            tag: "success",
          },
          {
            value: "2",
            label: "시스템 유지보수",
            tag: "primary",
          },
          {
            value: "3",
            label: "보안 경고",
            tag: "danger",
          },
          {
            value: "4",
            label: "휴가 공지",
            tag: "success",
          },
          {
            value: "5",
            label: "회사 뉴스",
            tag: "primary",
          },
          {
            value: "99",
            label: "기타",
            tag: "info",
          },
        ];
      }

      return {
        code: "00000",
        data: list,
        msg: "모두 정상",
      };
    },
  },
  // 사전 항목 추가
  {
    url: "dicts/:dictCode/items",
    method: ["POST"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "사전 항목 추가 " + body.name + " 성공",
      };
    },
  },

  // 사전 항목 폼 데이터
  {
    url: "dicts/:dictCode/items/:itemId/form",
    method: ["GET"],
    body: ({ params }) => {
      return {
        code: "00000",
        data: dictItemMap[params.itemId],
        msg: "모두 정상",
      };
    },
  },

  // 사전 항목 수정
  {
    url: "dicts/:dictCode/items/:itemId",
    method: ["PUT"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "사전 항목 수정 " + body.name + " 성공",
      };
    },
  },

  // 사전 항목 삭제
  {
    url: "dicts/:dictCode/items/:itemId",
    method: ["DELETE"],
    body({ params }) {
      return {
        code: "00000",
        data: null,
        msg: "사전 항목 삭제 " + params.itemId + " 성공",
      };
    },
  },
]);

// 사전 매핑 테이블 데이터
const dictMap: Record<string, any> = {
  1: {
    id: 1,
    name: "성별",
    dictCode: "gender",
    status: 1,
  },
};

// 사전 항목 매핑 테이블 데이터
const dictItemMap: Record<string, any> = {
  1: {
    id: 1,
    value: "1",
    label: "남",
    sort: 1,
    status: 1,
    tagType: "primary",
  },
  2: {
    id: 2,
    value: "2",
    label: "여",
    sort: 2,
    status: 1,
    tagType: "danger",
  },
  3: {
    id: 3,
    value: "0",
    label: "비공개",
    sort: 3,
    status: 1,
    tagType: "info",
  },
};
