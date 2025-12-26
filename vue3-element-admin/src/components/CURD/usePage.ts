import { ref } from "vue";
import type { IObject, PageContentInstance, PageModalInstance, PageSearchInstance } from "./types";

function usePage() {
  const searchRef = ref<PageSearchInstance>();
  const contentRef = ref<PageContentInstance>();
  const addModalRef = ref<PageModalInstance>();
  const editModalRef = ref<PageModalInstance>();

  // 검색
  function handleQueryClick(queryParams: IObject) {
    const filterParams = contentRef.value?.getFilterParams();
    contentRef.value?.fetchPageData({ ...queryParams, ...filterParams }, true);
  }
  // 초기화
  function handleResetClick(queryParams: IObject) {
    const filterParams = contentRef.value?.getFilterParams();
    contentRef.value?.fetchPageData({ ...queryParams, ...filterParams }, true);
  }
  // 신규
  function handleAddClick(RefImpl?: Ref<PageModalInstance>) {
    if (RefImpl) {
      RefImpl?.value.setModalVisible();
      RefImpl?.value.handleDisabled(false);
    } else {
      addModalRef.value?.setModalVisible();
      addModalRef.value?.handleDisabled(false);
    }
  }
  // 편집
  async function handleEditClick(
    row: IObject,
    callback?: (result?: IObject) => IObject,
    RefImpl?: Ref<PageModalInstance>
  ) {
    if (RefImpl) {
      RefImpl.value?.setModalVisible();
      RefImpl.value?.handleDisabled(false);
      const from = await (callback?.(row) ?? Promise.resolve(row));
      RefImpl.value?.setFormData(from ? from : row);
    } else {
      editModalRef.value?.setModalVisible();
      editModalRef.value?.handleDisabled(false);
      const from = await (callback?.(row) ?? Promise.resolve(row));
      editModalRef.value?.setFormData(from ? from : row);
    }
  }
  // 보기
  async function handleViewClick(
    row: IObject,
    callback?: (result?: IObject) => IObject,
    RefImpl?: Ref<PageModalInstance>
  ) {
    if (RefImpl) {
      RefImpl.value?.setModalVisible();
      RefImpl.value?.handleDisabled(true);
      const from = await (callback?.(row) ?? Promise.resolve(row));
      RefImpl.value?.setFormData(from ? from : row);
    } else {
      editModalRef.value?.setModalVisible();
      editModalRef.value?.handleDisabled(true);
      const from = await (callback?.(row) ?? Promise.resolve(row));
      editModalRef.value?.setFormData(from ? from : row);
    }
  }
  // 양식 제출
  function handleSubmitClick() {
    // 검색 조건에 따라 목록 데이터 새로고침
    const queryParams = searchRef.value?.getQueryParams();
    contentRef.value?.fetchPageData(queryParams, true);
  }
  // 내보내기
  function handleExportClick() {
    // 검색 조건에 따라 데이터 내보내기
    const queryParams = searchRef.value?.getQueryParams();
    contentRef.value?.exportPageData(queryParams);
  }
  // 검색 표시/숨김
  function handleSearchClick() {
    searchRef.value?.toggleVisible();
  }
  // 필터 데이터
  function handleFilterChange(filterParams: IObject) {
    const queryParams = searchRef.value?.getQueryParams();
    contentRef.value?.fetchPageData({ ...queryParams, ...filterParams }, true);
  }

  return {
    searchRef,
    contentRef,
    addModalRef,
    editModalRef,
    handleQueryClick,
    handleResetClick,
    handleAddClick,
    handleEditClick,
    handleViewClick,
    handleSubmitClick,
    handleExportClick,
    handleSearchClick,
    handleFilterChange,
  };
}

export default usePage;
