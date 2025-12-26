import { store } from "@/store";
import DictAPI, { type DictItemOption } from "@/api/system/dict-api";
import { STORAGE_KEYS } from "@/constants";

export const useDictStore = defineStore("dict", () => {
  // 사전 데이터 캐시
  const dictCache = useStorage<Record<string, DictItemOption[]>>(STORAGE_KEYS.DICT_CACHE, {});

  // 요청 큐 (중복 요청 방지)
  const requestQueue: Record<string, Promise<void>> = {};

  /**
   * 사전 데이터 캐시
   * @param dictCode 사전 코드
   * @param data 사전 항목 목록
   */
  const cacheDictItems = (dictCode: string, data: DictItemOption[]) => {
    dictCache.value[dictCode] = data;
  };

  /**
   * 사전 데이터 로드 (캐시에 없으면 요청)
   * @param dictCode 사전 코드
   */
  const loadDictItems = async (dictCode: string) => {
    if (dictCache.value[dictCode]) return;
    // 중복 요청 방지
    if (!requestQueue[dictCode]) {
      requestQueue[dictCode] = DictAPI.getDictItems(dictCode)
        .then((data) => {
          cacheDictItems(dictCode, data);
          Reflect.deleteProperty(requestQueue, dictCode);
        })
        .catch((error) => {
          // 요청 실패, 큐 정리, 재시도 허용
          Reflect.deleteProperty(requestQueue, dictCode);
          throw error;
        });
    }
    await requestQueue[dictCode];
  };

  /**
   * 사전 항목 목록 조회
   * @param dictCode 사전 코드
   * @returns 사전 항목 목록
   */
  const getDictItems = (dictCode: string): DictItemOption[] => {
    return dictCache.value[dictCode] || [];
  };

  /**
   * 지정된 사전 항목 제거
   * @param dictCode 사전 코드
   */
  const removeDictItem = (dictCode: string) => {
    if (dictCache.value[dictCode]) {
      Reflect.deleteProperty(dictCache.value, dictCode);
    }
  };

  /**
   * 사전 캐시 전체 정리
   */
  const clearDictCache = () => {
    dictCache.value = {};
  };

  return {
    loadDictItems,
    getDictItems,
    removeDictItem,
    clearDictCache,
  };
});

export function useDictStoreHook() {
  return useDictStore(store);
}
