import { defineMock } from "./base";

export default defineMock([
  {
    url: "users/me",
    method: ["GET"],
    body: {
      code: "00000",
      data: {
        userId: 2,
        username: "admin",
        nickname: "시스템 관리자",
        avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
        roles: ["ADMIN"],
        perms: [
          "sys:user:query",
          "sys:user:add",
          "sys:user:edit",
          "sys:user:delete",
          "sys:user:import",
          "sys:user:export",
          "sys:user:reset-password",

          "sys:role:query",
          "sys:role:add",
          "sys:role:edit",
          "sys:role:delete",

          "sys:dept:query",
          "sys:dept:add",
          "sys:dept:edit",
          "sys:dept:delete",

          "sys:menu:query",
          "sys:menu:add",
          "sys:menu:edit",
          "sys:menu:delete",

          "sys:dict:query",
          "sys:dict:add",
          "sys:dict:edit",
          "sys:dict:delete",
          "sys:dict:delete",

          "sys:dict-item:query",
          "sys:dict-item:add",
          "sys:dict-item:edit",
          "sys:dict-item:delete",

          "sys:notice:query",
          "sys:notice:add",
          "sys:notice:edit",
          "sys:notice:delete",
          "sys:notice:revoke",
          "sys:notice:publish",

          "sys:config:query",
          "sys:config:add",
          "sys:config:update",
          "sys:config:delete",
          "sys:config:refresh",
        ],
      },
      msg: "모두 정상",
    },
  },

  {
    url: "users/page",
    method: ["GET"],
    body: {
      code: "00000",
      data: {
        list: [
          {
            id: 2,
            username: "admin",
            nickname: "시스템 관리자",
            mobile: "17621210366",
            gender: 1,
            avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
            email: "",
            status: 1,
            deptId: 1,
            roleIds: [2],
          },
          {
            id: 3,
            username: "test",
            nickname: "테스트 사용자",
            mobile: "17621210366",
            gender: 1,
            avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
            email: "youlaitech@163.com",
            status: 1,
            deptId: 3,
            roleIds: [3],
          },
        ],
        total: 2,
      },
      msg: "모두 정상",
    },
  },

  // 사용자 추가
  {
    url: "users",
    method: ["POST"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "사용자 추가 " + body.nickname + " 성공",
      };
    },
  },

  // 사용자 폼 데이터 조회
  {
    url: "users/:userId/form",
    method: ["GET"],
    body: ({ params }) => {
      return {
        code: "00000",
        data: userMap[params.userId],
        msg: "모두 정상",
      };
    },
  },
  // 사용자 수정
  {
    url: "users/:userId",
    method: ["PUT"],
    body({ body }) {
      return {
        code: "00000",
        data: null,
        msg: "사용자 수정 " + body.nickname + " 성공",
      };
    },
  },

  // 사용자 삭제
  {
    url: "users/:userId",
    method: ["DELETE"],
    body({ params }) {
      return {
        code: "00000",
        data: null,
        msg: "사용자 삭제 " + params.id + " 성공",
      };
    },
  },

  // 비밀번호 재설정
  {
    url: "users/:userId/password/reset",
    method: ["PUT"],
    body({ query }) {
      return {
        code: "00000",
        data: null,
        msg: "비밀번호 재설정 성공, 새 비밀번호: " + query.password,
      };
    },
  },

  // Excel 내보내기
  {
    url: "users/_export",
    method: ["GET"],
    headers: {
      "Content-Disposition": "attachment; filename=%E7%94%A8%E6%88%B7%E5%88%97%E8%A1%A8.xlsx",
      "Content-Type": "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    },
  },

  {
    url: "users/profile",
    method: ["GET"],
    body: {
      code: "00000",
      data: {
        id: 2,
        username: "admin",
        nickname: "시스템 관리자",
        avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
        gender: 1,
        mobile: "17621210366",
        email: null,
        deptName: "유라이 기술",
        roleNames: "시스템 관리자",
        createTime: "2019-10-10",
      },
    },
  },

  {
    url: "users/profile",
    method: ["PUT"],
    body() {
      return {
        code: "00000",
        data: null,
        msg: "개인 정보 수정 성공",
      };
    },
  },

  {
    url: "users/password",
    method: ["PUT"],
    body() {
      return {
        code: "00000",
        data: null,
        msg: "비밀번호 수정 성공",
      };
    },
  },
]);

// 사용자 매핑 테이블 데이터
const userMap: Record<string, any> = {
  2: {
    id: 2,
    username: "admin",
    nickname: "系统管理员",
    mobile: "17621210366",
    gender: 1,
    avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
    email: "",
    status: 1,
    deptId: 1,
    roleIds: [2],
  },
  3: {
    id: 3,
    username: "test",
    nickname: "测试小用户",
    mobile: "17621210366",
    gender: 1,
    avatar: "https://foruda.gitee.com/images/1723603502796844527/03cdca2a_716974.gif",
    email: "youlaitech@163.com",
    status: 1,
    deptId: 3,
    roleIds: [3],
  },
};
