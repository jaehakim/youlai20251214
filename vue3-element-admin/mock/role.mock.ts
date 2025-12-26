import { defineMock } from "./base";

export default defineMock([
  {
    url: "roles/options",
    method: ["GET"],
    body: {
      code: "00000",
      data: [
        {
          value: 2,
          label: "시스템 관리자",
        },
        {
          value: 4,
          label: "시스템 관리자1",
        },
        {
          value: 5,
          label: "시스템 관리자2",
        },
        {
          value: 6,
          label: "시스템 관리자3",
        },
        {
          value: 7,
          label: "시스템 관리자4",
        },
        {
          value: 8,
          label: "시스템 관리자5",
        },
        {
          value: 9,
          label: "시스템 관리자6",
        },
        {
          value: 10,
          label: "시스템 관리자7",
        },
        {
          value: 11,
          label: "시스템 관리자8",
        },
        {
          value: 12,
          label: "시스템 관리자9",
        },
        {
          value: 3,
          label: "방문 게스트",
        },
      ],
      msg: "모두 정상",
    },
  },

  {
    url: "roles/page",
    method: ["GET"],
    body: {
      code: "00000",
      data: {
        list: [
          {
            id: 2,
            name: "시스템 관리자",
            code: "ADMIN",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
          {
            id: 3,
            name: "방문 게스트",
            code: "GUEST",
            status: 1,
            sort: 3,
            createTime: "2021-05-26 15:49:05",
            updateTime: "2019-05-05 16:00:00",
          },
          {
            id: 4,
            name: "시스템 관리자1",
            code: "ADMIN1",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
          {
            id: 5,
            name: "시스템 관리자2",
            code: "ADMIN2",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
          {
            id: 6,
            name: "시스템 관리자3",
            code: "ADMIN3",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
          {
            id: 7,
            name: "시스템 관리자4",
            code: "ADMIN4",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
          {
            id: 8,
            name: "시스템 관리자5",
            code: "ADMIN5",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
          {
            id: 9,
            name: "시스템 관리자6",
            code: "ADMIN6",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: "2023-12-04 11:43:15",
          },
          {
            id: 10,
            name: "시스템 관리자7",
            code: "ADMIN7",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
          {
            id: 11,
            name: "시스템 관리자8",
            code: "ADMIN8",
            status: 1,
            sort: 2,
            createTime: "2021-03-25 12:39:54",
            updateTime: null,
          },
        ],
        total: 10,
      },
      msg: "모두 정상",
    },
  },

  // 역할 추가
  {
    url: "roles",
    method: ["POST"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "역할 추가 " + body.name + " 성공",
      };
    },
  },

  // 역할 폼 데이터 조회
  {
    url: "roles/:id/form",
    method: ["GET"],
    body: ({ params }) => {
      return {
        code: "00000",
        data: roleMap[params.id],
        msg: "모두 정상",
      };
    },
  },
  // 역할 수정
  {
    url: "roles/:id",
    method: ["PUT"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "역할 수정 " + body.name + " 성공",
      };
    },
  },

  // 역할 삭제
  {
    url: "roles/:id",
    method: ["DELETE"],
    body({ params }) {
      return {
        code: "00000",
        data: null,
        msg: "역할 삭제 " + params.id + " 성공",
      };
    },
  },
  // 역할의 메뉴 ID 조회
  {
    url: "roles/:id/menuIds",
    method: ["GET"],
    body: ({}) => {
      return {
        code: "00000",
        data: [
          1, 2, 31, 32, 33, 88, 3, 70, 71, 72, 4, 73, 75, 74, 5, 76, 77, 78, 6, 79, 81, 84, 85, 86,
          87, 40, 41, 26, 30, 20, 21, 22, 23, 24, 89, 90, 91, 36, 37, 38, 39, 93, 94, 95, 97, 102,
          89, 90, 91, 93, 94, 95, 97, 102, 103, 104,
        ],
        msg: "모두 정상",
      };
    },
  },
  // 역할 메뉴 저장
  {
    url: "roles/:id/menus",
    method: ["PUT"],
    body: {
      code: "00000",
      data: null,
      msg: "모두 정상",
    },
  },
]);

// 역할 매핑 테이블 데이터
const roleMap: Record<string, any> = {
  2: {
    id: 2,
    name: "시스템 관리자",
    code: "ADMIN",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },
  3: {
    id: 3,
    name: "방문 게스트",
    code: "GUEST",
    status: 1,
    sort: 3,
    createTime: "2021-05-26 15:49:05",
    updateTime: "2019-05-05 16:00:00",
  },
  4: {
    id: 4,
    name: "시스템 관리자1",
    code: "ADMIN1",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },
  5: {
    id: 5,
    name: "시스템 관리자2",
    code: "ADMIN2",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },

  6: {
    id: 6,
    name: "시스템 관리자3",
    code: "ADMIN3",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },
  7: {
    id: 7,
    name: "시스템 관리자4",
    code: "ADMIN4",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },
  8: {
    id: 8,
    name: "시스템 관리자5",
    code: "ADMIN5",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },
  9: {
    id: 9,
    name: "시스템 관리자6",
    code: "ADMIN6",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: "2023-12-04 11:43:15",
  },
  10: {
    id: 10,
    name: "시스템 관리자7",
    code: "ADMIN7",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },
  11: {
    id: 11,
    name: "시스템 관리자8",
    code: "ADMIN8",
    status: 1,
    sort: 2,
    createTime: "2021-03-25 12:39:54",
    updateTime: null,
  },
};
