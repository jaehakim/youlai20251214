import request from "@/utils/request";

const USER_BASE_URL = "/api/v1/users";

const UserAPI = {
  /**
   * 현재 로그인된 사용자 정보 조회
   *
   * @returns 로그인 사용자 닉네임, 프로필 사진 정보, 역할 및 권한 포함
   */
  getInfo() {
    return request<any, UserInfo>({
      url: `${USER_BASE_URL}/me`,
      method: "get",
    });
  },

  /**
   * 사용자 페이지 목록 조회
   *
   * @param queryParams 쿼리 파라미터
   */
  getPage(queryParams: UserPageQuery) {
    return request<any, PageResult<UserPageVO[]>>({
      url: `${USER_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },

  /**
   * 사용자 폼 상세 조회
   *
   * @param userId 사용자 ID
   * @returns 사용자 폼 상세
   */
  getFormData(userId: string) {
    return request<any, UserForm>({
      url: `${USER_BASE_URL}/${userId}/form`,
      method: "get",
    });
  },

  /**
   * 사용자 추가
   *
   * @param data 사용자 폼 데이터
   */
  create(data: UserForm) {
    return request({
      url: `${USER_BASE_URL}`,
      method: "post",
      data,
    });
  },

  /**
   * 사용자 수정
   *
   * @param id 사용자 ID
   * @param data 사용자 폼 데이터
   */
  update(id: string, data: UserForm) {
    return request({
      url: `${USER_BASE_URL}/${id}`,
      method: "put",
      data,
    });
  },

  /**
   * 사용자 암호 수정
   *
   * @param id 사용자 ID
   * @param password 새 암호
   */
  resetPassword(id: string, password: string) {
    return request({
      url: `${USER_BASE_URL}/${id}/password/reset`,
      method: "put",
      params: { password },
    });
  },

  /**
   * 대량 사용자 삭제, 쉼표(,)로 구분
   *
   * @param ids 사용자 ID 문자열, 쉼표(,)로 구분
   */
  deleteByIds(ids: string) {
    return request({
      url: `${USER_BASE_URL}/${ids}`,
      method: "delete",
    });
  },

  /** 사용자 가져오기 템플릿 다운로드 */
  downloadTemplate() {
    return request({
      url: `${USER_BASE_URL}/template`,
      method: "get",
      responseType: "blob",
    });
  },

  /**
   * 사용자 내보내기
   *
   * @param queryParams 쿼리 파라미터
   */
  export(queryParams: UserPageQuery) {
    return request({
      url: `${USER_BASE_URL}/export`,
      method: "get",
      params: queryParams,
      responseType: "blob",
    });
  },

  /**
   * 사용자 가져오기
   *
   * @param deptId 부서 ID
   * @param file 가져오기 파일
   */
  import(deptId: string, file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return request<any, ExcelResult>({
      url: `${USER_BASE_URL}/import`,
      method: "post",
      params: { deptId },
      data: formData,
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  /** 개인 정보 센터 사용자 정보 조회 */
  getProfile() {
    return request<any, UserProfileVO>({
      url: `${USER_BASE_URL}/profile`,
      method: "get",
    });
  },

  /** 개인 정보 센터 사용자 정보 수정 */
  updateProfile(data: UserProfileForm) {
    return request({
      url: `${USER_BASE_URL}/profile`,
      method: "put",
      data,
    });
  },

  /** 개인 정보 센터 사용자 암호 수정 */
  changePassword(data: PasswordChangeForm) {
    return request({
      url: `${USER_BASE_URL}/password`,
      method: "put",
      data,
    });
  },

  /** SMS 인증 코드 발송 (휴대폰 번호 바인딩 또는 변경) */
  sendMobileCode(mobile: string) {
    return request({
      url: `${USER_BASE_URL}/mobile/code`,
      method: "post",
      params: { mobile },
    });
  },

  /** 휴대폰 번호 바인딩 또는 변경 */
  bindOrChangeMobile(data: MobileUpdateForm) {
    return request({
      url: `${USER_BASE_URL}/mobile`,
      method: "put",
      data,
    });
  },

  /** 이메일 인증 코드 발송 (이메일 바인딩 또는 변경) */
  sendEmailCode(email: string) {
    return request({
      url: `${USER_BASE_URL}/email/code`,
      method: "post",
      params: { email },
    });
  },

  /** 이메일 바인딩 또는 변경 */
  bindOrChangeEmail(data: EmailUpdateForm) {
    return request({
      url: `${USER_BASE_URL}/email`,
      method: "put",
      data,
    });
  },

  /**
   *  사용자 드롭다운 목록 조회
   */
  getOptions() {
    return request<any, OptionType[]>({
      url: `${USER_BASE_URL}/options`,
      method: "get",
    });
  },
};

export default UserAPI;

/** 登录用户信息 */
export interface UserInfo {
  /** 用户ID */
  userId?: string;

  /** 用户名 */
  username?: string;

  /** 昵称 */
  nickname?: string;

  /** 头像URL */
  avatar?: string;

  /** 角色 */
  roles: string[];

  /** 权限 */
  perms: string[];
}

/**
 * 用户分页查询对象
 */
export interface UserPageQuery extends PageQuery {
  /** 搜索关键字 */
  keywords?: string;

  /** 用户状态 */
  status?: number;

  /** 部门ID */
  deptId?: string;

  /** 开始时间 */
  createTime?: [string, string];
}

/** 用户分页对象 */
export interface UserPageVO {
  /** 用户ID */
  id: string;
  /** 用户头像URL */
  avatar?: string;
  /** 创建时间 */
  createTime?: Date;
  /** 部门名称 */
  deptName?: string;
  /** 用户邮箱 */
  email?: string;
  /** 性别 */
  gender?: number;
  /** 手机号 */
  mobile?: string;
  /** 用户昵称 */
  nickname?: string;
  /** 角色名称，多个使用英文逗号(,)分割 */
  roleNames?: string;
  /** 用户状态(1:启用;0:禁用) */
  status?: number;
  /** 用户名 */
  username?: string;
}

/** 用户表单类型 */
export interface UserForm {
  /** 用户ID */
  id?: string;
  /** 用户头像 */
  avatar?: string;
  /** 部门ID */
  deptId?: string;
  /** 邮箱 */
  email?: string;
  /** 性别 */
  gender?: number;
  /** 手机号 */
  mobile?: string;
  /** 昵称 */
  nickname?: string;
  /** 角色ID集合 */
  roleIds?: number[];
  /** 用户状态(1:正常;0:禁用) */
  status?: number;
  /** 用户名 */
  username?: string;
}

/** 个人中心用户信息 */
export interface UserProfileVO {
  /** 用户ID */
  id?: string;

  /** 用户名 */
  username?: string;

  /** 昵称 */
  nickname?: string;

  /** 头像URL */
  avatar?: string;

  /** 性别 */
  gender?: number;

  /** 手机号 */
  mobile?: string;

  /** 邮箱 */
  email?: string;

  /** 部门名称 */
  deptName?: string;

  /** 角色名称，多个使用英文逗号(,)分割 */
  roleNames?: string;

  /** 创建时间 */
  createTime?: Date;
}

/** 个人中心用户信息表单 */
export interface UserProfileForm {
  /** 用户ID */
  id?: string;

  /** 用户名 */
  username?: string;

  /** 昵称 */
  nickname?: string;

  /** 头像URL */
  avatar?: string;

  /** 性别 */
  gender?: number;

  /** 手机号 */
  mobile?: string;

  /** 邮箱 */
  email?: string;
}

/** 修改密码表单 */
export interface PasswordChangeForm {
  /** 原密码 */
  oldPassword?: string;
  /** 新密码 */
  newPassword?: string;
  /** 确认新密码 */
  confirmPassword?: string;
}

/** 修改手机表单 */
export interface MobileUpdateForm {
  /** 手机号 */
  mobile?: string;
  /** 验证码 */
  code?: string;
}

/** 修改邮箱表单 */
export interface EmailUpdateForm {
  /** 邮箱 */
  email?: string;
  /** 验证码 */
  code?: string;
}
