import { computed, 참조 } from "vue";

/**
 * 表格行선택 Composable
 *
 * @description 提供统하나의表格行선택逻辑，패키지括选내ID管理및清비어있음선택
 * @template T 데이터항목타입，必须패키지含 id 속성
 * @returns 돌아가기选의ID목록、선택变化처리함수、清비어있음선택함수
 *
 * @example
 * ```typescript
 * const { selectedIds, handleSelectionChange, clearSelection } = useTableSelection<UserVO>();
 * ```
 */
export function useTableSelection<T extends { id: string | number }>() {
  /**
   * 选의데이터항목ID목록
   */
  const selectedIds = 참조<(string | number)[]>([]);

  /**
   * 表格选내항목变化처리
   * @param selection 选의行데이터목록
   */
  function handleSelectionChange(selection: T[]): void {
    selectedIds.value = selection.map((item) => item.id);
  }

  /**
   * 清비어있음선택
   */
  function clearSelection(): void {
    selectedIds.value = [];
  }

  /**
   * 检查指定ID是否被选내
   * @param id 해야检查의ID
   * @returns 是否被选내
   */
  function isSelected(id: string | number): boolean {
    return selectedIds.value.includes(id);
  }

  /**
   * 조회选의개量
   */
  const selectedCount = computed(() => selectedIds.value.length);

  /**
   * 是否有选내항목
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
