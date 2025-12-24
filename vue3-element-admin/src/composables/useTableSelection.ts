import { computed, 참조 } from "vue";

/**
 * 표행선택 Composable
 *
 * @description 提供통계하나의표행선택逻辑，패키지括선택내ID관리및정리비어있음선택
 * @template T 데이터항목타입，必须패키지含 id 속성
 * @returns 돌아가기선택의ID목록、선택변경처리함수、정리비어있음선택함수
 *
 * @example
 * ```typescript
 * const { selectedIds, handleSelectionChange, clearSelection } = useTableSelection<UserVO>();
 * ```
 */
export function useTableSelection<T extends { id: string | number }>() {
  /**
   * 선택의데이터항목ID목록
   */
  const selectedIds = 참조<(string | number)[]>([]);

  /**
   * 표선택내항목변경처리
   * @param selection 선택의행데이터목록
   */
  function handleSelectionChange(selection: T[]): void {
    selectedIds.value = selection.map((item) => item.id);
  }

  /**
   * 정리비어있음선택
   */
  function clearSelection(): void {
    selectedIds.value = [];
  }

  /**
   * 확인指定ID여부被선택내
   * @param id 해야확인의ID
   * @returns 여부被선택내
   */
  function isSelected(id: string | number): boolean {
    return selectedIds.value.includes(id);
  }

  /**
   * 조회선택의개양
   */
  const selectedCount = computed(() => selectedIds.value.length);

  /**
   * 여부있음선택내항목
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
