import { defineMock } from "./base";

export default defineMock([
  {
    url: "dept/options",
    method: ["GET"],
    body: {
      code: "00000",
      data: [
        {
          value: 1,
          label: "유라이 기술",
          children: [
            {
              value: 2,
              label: "연구개발 부서",
            },
            {
              value: 3,
              label: "테스트 부서",
            },
          ],
        },
      ],
      msg: "모두 정상",
    },
  },

  {
    url: "dept",
    method: ["GET"],
    body: {
      code: "00000",
      data: [
        {
          id: 1,
          parentId: 0,
          name: "유라이 기술",
          code: "YOULAI",
          sort: 1,
          status: 1,
          children: [
            {
              id: 2,
              parentId: 1,
              name: "연구개발 부서",
              code: "RD001",
              sort: 1,
              status: 1,
              children: [],
              createTime: null,
              updateTime: "2022-04-19 12:46",
            },
            {
              id: 3,
              parentId: 1,
              name: "테스트 부서",
              code: "QA001",
              sort: 1,
              status: 1,
              children: [],
              createTime: null,
              updateTime: "2022-04-19 12:46",
            },
          ],
          createTime: null,
          updateTime: null,
        },
      ],
      msg: "모두 정상",
    },
  },

  // 부서 추가
  {
    url: "dept",
    method: ["POST"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "부서 추가 " + body.name + " 성공",
      };
    },
  },

  // 부서 폼 데이터 조회
  {
    url: "dept/:id/form",
    method: ["GET"],
    body: ({ params }) => {
      return {
        code: "00000",
        data: deptMap[params.id],
        msg: "모두 정상",
      };
    },
  },

  // 부서 수정
  {
    url: "dept/:id",
    method: ["PUT"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "부서 수정 " + body.name + " 성공",
      };
    },
  },

  // 부서 삭제
  {
    url: "dept/:id",
    method: ["DELETE"],
    body({ params }) {
      return {
        code: "00000",
        data: null,
        msg: "부서 삭제 " + params.id + " 성공",
      };
    },
  },
]);

// 부서 매핑 테이블 데이터
const deptMap: Record<string, any> = {
  1: {
    id: 1,
    name: "유래기술",
    code: "YOULAI",
    parentId: 0,
    status: 1,
    sort: 1,
  },
  2: {
    id: 2,
    name: "研发부서",
    code: "RD001",
    parentId: 1,
    status: 1,
    sort: 1,
  },
  3: {
    id: 3,
    name: "테스트부서",
    code: "QA001",
    parentId: 1,
    status: 1,
    sort: 1,
  },
};
