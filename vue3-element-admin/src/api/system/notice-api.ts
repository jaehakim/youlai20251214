import request from "@/utils/request";

const NOTICE_BASE_URL = "/api/v1/notices";

const NoticeAPI = {
  /** 통지 공고 페이지 데이터 조회 */
  getPage(queryParams?: NoticePageQuery) {
    return request<any, PageResult<NoticePageVO[]>>({
      url: `${NOTICE_BASE_URL}/page`,
      method: "get",
      params: queryParams,
    });
  },
  /** 통지 공고 폼 데이터 조회 */
  getFormData(id: string) {
    return request<any, NoticeForm>({ url: `${NOTICE_BASE_URL}/${id}/form`, method: "get" });
  },
  /** 통지 공고 추가 */
  create(data: NoticeForm) {
    return request({ url: `${NOTICE_BASE_URL}`, method: "post", data });
  },
  /** 통지 공고 업데이트 */
  update(id: string, data: NoticeForm) {
    return request({ url: `${NOTICE_BASE_URL}/${id}`, method: "put", data });
  },
  /** 대량 통지 공고 삭제, 쉼표(,)로 구분 */
  deleteByIds(ids: string) {
    return request({ url: `${NOTICE_BASE_URL}/${ids}`, method: "delete" });
  },
  /** 통지 발행 */
  publish(id: string) {
    return request({ url: `${NOTICE_BASE_URL}/${id}/publish`, method: "put" });
  },
  /** 통지 회수 */
  revoke(id: string) {
    return request({ url: `${NOTICE_BASE_URL}/${id}/revoke`, method: "put" });
  },
  /** 통지 상세 조회 */
  getDetail(id: string) {
    return request<any, NoticeDetailVO>({ url: `${NOTICE_BASE_URL}/${id}/detail`, method: "get" });
  },
  /** 전체 읽음 */
  readAll() {
    return request({ url: `${NOTICE_BASE_URL}/read-all`, method: "put" });
  },
  /** 내 통지 페이지 목록 조회 */
  getMyNoticePage(queryParams?: NoticePageQuery) {
    return request<any, PageResult<NoticePageVO[]>>({
      url: `${NOTICE_BASE_URL}/my`,
      method: "get",
      params: queryParams,
    });
  },
};

export default NoticeAPI;

export interface NoticePageQuery extends PageQuery {
  /** 제목 */
  title?: string;
  /** 발행 상태 (0:초안;1:발행됨;2:회수됨) */
  publishStatus?: number;
  /** 읽음 여부 (1:예;0:아니오) */
  isRead?: number;
}
export interface NoticeForm {
  /** 통지 ID (신규 추가 시 입력 불필요) */
  id?: string;
  /** 제목 */
  title?: string;
  /** 내용 */
  content?: string;
  /** 유형 */
  noticeType?: number;
  /** 우선 순위/수준 */
  noticeLevel?: string;
  /** 대상 유형 */
  targetType?: number;
  /** 대상 사용자 ID (쉼표(,)로 구분) */
  targetUserIds?: string;
}
export interface NoticePageVO {
  /** 통지 ID */
  id: string;
  /** 제목 */
  title?: string;
  /** 내용 */
  content?: string;
  /** 유형 */
  noticeType?: number;
  /** 발행인 ID */
  publisherId?: bigint;
  /** 우선 순위 */
  priority?: number;
  /** 대상 유형 */
  targetType?: number;
  /** 발행 상태 */
  publishStatus?: number;
  /** 발행 시간 */
  publishTime?: Date;
  /** 회수 시간 */
  revokeTime?: Date;
}
export interface NoticeDetailVO {
  /** 통지 ID */
  id?: string;
  /** 제목 */
  title?: string;
  /** 내용 */
  content?: string;
  /** 유형 */
  noticeType?: number;
  /** 발행인 이름 */
  publisherName?: string;
  /** 우선 순위/수준 */
  noticeLevel?: string;
  /** 발행 시간 */
  publishTime?: Date;
  /** 발행 상태 */
  publishStatus?: number;
}
