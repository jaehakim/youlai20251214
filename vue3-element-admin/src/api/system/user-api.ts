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

/** 로그인 사용자 정보 */
export interface UserInfo {
  /** 사용자 ID */
  userId?: string;

  /** 사용자 이름 */
  username?: string;

  /** 닉네임 */
  nickname?: string;

  /** 아바타 URL */
  avatar?: string;

  /** 역할 */
  roles: string[];

  /** 권한 */
  perms: string[];
}

/**
 * 사용자 페이지네이션 조회 객체
 */
export interface UserPageQuery extends PageQuery {
  /** 검색 키워드 */
  keywords?: string;

  /** 사용자 상태 */
  status?: number;

  /** 부서 ID */
  deptId?: string;

  /** 시작 시간 */
  createTime?: [string, string];
}

/** 사용자 페이지네이션 객체 */
export interface UserPageVO {
  /** 사용자 ID */
  id: string;
  /** 사용자 아바타 URL */
  avatar?: string;
  /** 생성 시간 */
  createTime?: Date;
  /** 부서 명칭 */
  deptName?: string;
  /** 사용자 이메일 */
  email?: string;
  /** 성별 */
  gender?: number;
  /** 휴대폰 */
  mobile?: string;
  /** 사용자 닉네임 */
  nickname?: string;
  /** 역할 명칭, 여러 개는 영문 쉼표(,)로 구분 */
  roleNames?: string;
  /** 사용자 상태(1:활성화;0:비활성화) */
  status?: number;
  /** 사용자 이름 */
  username?: string;
}

/** 사용자 양식 타입 */
export interface UserForm {
  /** 사용자 ID */
  id?: string;
  /** 사용자 아바타 */
  avatar?: string;
  /** 부서 ID */
  deptId?: string;
  /** 이메일 */
  email?: string;
  /** 성별 */
  gender?: number;
  /** 휴대폰 */
  mobile?: string;
  /** 닉네임 */
  nickname?: string;
  /** 역할 ID 컬렉션 */
  roleIds?: number[];
  /** 사용자 상태(1:정상;0:비활성화) */
  status?: number;
  /** 사용자 이름 */
  username?: string;
}

/** 개인 센터 사용자 정보 */
export interface UserProfileVO {
  /** 사용자 ID */
  id?: string;

  /** 사용자 이름 */
  username?: string;

  /** 닉네임 */
  nickname?: string;

  /** 아바타 URL */
  avatar?: string;

  /** 성별 */
  gender?: number;

  /** 휴대폰 */
  mobile?: string;

  /** 이메일 */
  email?: string;

  /** 부서 명칭 */
  deptName?: string;

  /** 역할 명칭, 여러 개는 영문 쉼표(,)로 구분 */
  roleNames?: string;

  /** 생성 시간 */
  createTime?: Date;
}

/** 개인 센터 사용자 정보 양식 */
export interface UserProfileForm {
  /** 사용자 ID */
  id?: string;

  /** 사용자 이름 */
  username?: string;

  /** 닉네임 */
  nickname?: string;

  /** 아바타 URL */
  avatar?: string;

  /** 성별 */
  gender?: number;

  /** 휴대폰 */
  mobile?: string;

  /** 이메일 */
  email?: string;
}

/** 비밀번호 수정 양식 */
export interface PasswordChangeForm {
  /** 기존 비밀번호 */
  oldPassword?: string;
  /** 새 비밀번호 */
  newPassword?: string;
  /** 새 비밀번호 확인 */
  confirmPassword?: string;
}

/** 휴대폰 수정 양식 */
export interface MobileUpdateForm {
  /** 휴대폰 */
  mobile?: string;
  /** 인증 코드 */
  code?: string;
}

/** 이메일 수정 양식 */
export interface EmailUpdateForm {
  /** 이메일 */
  email?: string;
  /** 인증 코드 */
  code?: string;
}
