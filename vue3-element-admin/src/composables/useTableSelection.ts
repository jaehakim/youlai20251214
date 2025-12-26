import { computed, 참조 } from "vue";

/**
 * 테이블 행 선택 Composable
 *
 * @description 통합된 테이블 행 선택 로직 제공, 선택된 ID 관리 및 선택 초기화 포함
 * @template T 데이터 항목 타입, id 속성 필수
 * @returns 선택된 ID 목록, 선택 변경 처리 함수, 선택 초기화 함수 반환
 *
 * @example
 * ```typescript
 * const { selectedIds, handleSelectionChange, clearSelection } = useTableSelection<UserVO>();
 * ```
 */
export function useTableSelection<T extends { id: string | number }>() {
  /**
   * 선택된 데이터 항목 ID 목록
   */
  const selectedIds = 참조<(string | number)[]>([]);

  /**
   * 테이블 선택 항목 변경 처리
   * @param selection 선택된 행 데이터 목록
   */
  function handleSelectionChange(selection: T[]): void {
    selectedIds.value = selection.map((item) => item.id);
  }

  /**
   * 선택 초기화
   */
  function clearSelection(): void {
    selectedIds.value = [];
  }

  /**
   * 지정 ID가 선택되었는지 확인
   * @param id 확인할 ID
   * @returns 선택 여부
   */
  function isSelected(id: string | number): boolean {
    return selectedIds.value.includes(id);
  }

  /**
   * 선택된 개수 조회
   */
  const selectedCount = computed(() => selectedIds.value.length);

  /**
   * 선택된 항목이 있는지 여부
   */
  const hasSelection = computed(() => selectedIds.value.length > 0);

  return {
    selectedIds,
    selectedCount,
    hasSelection,
    handleSelectionChange,
    clearSelection,
    isSelected,
  };
}
