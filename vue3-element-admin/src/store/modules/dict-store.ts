import { 저장소 } from "@/저장소";
import DictAPI, { type DictItemOption } from "@/api/system/dict-api";
import { STORAGE_KEYS } from "@/constants";

export const useDict스토어 = define스토어("dict", () => {
  // 사전데이터캐시
  const dictCache = useStorage<Record<string, DictItemOption[]>>(STORAGE_KEYS.DICT_CACHE, {});

  // 요청큐（방지중복요청）
  const requestQueue: Record<string, Promise<void>> = {};

  /**
   * 캐시사전데이터
   * @param dictCode 사전인코딩
   * @param data 사전항목목록
   */
  const cacheDictItems = (dictCode: string, data: DictItemOption[]) => {
    dictCache.value[dictCode] = data;
  };

  /**
   * 로드사전데이터（만약캐시내없음그러면요청）
   * @param dictCode 사전인코딩
   */
  const loadDictItems = async (dictCode: string) => {
    if (dictCache.value[dictCode]) return;
    // 방지중복요청
    if (!requestQueue[dictCode]) {
      requestQueue[dictCode] = DictAPI.getDictItems(dictCode)
        .then((data) => {
          cacheDictItems(dictCode, data);
          Reflect.deleteProperty(requestQueue, dictCode);
        })
        .catch((error) => {
          // 요청실패，정리큐，허용재시도
          Reflect.deleteProperty(requestQueue, dictCode);
          throw error;
        });
    }
    await requestQueue[dictCode];
  };

  /**
   * 조회사전항목목록
   * @param dictCode 사전인코딩
   * @returns 사전항목목록
   */
  const getDictItems = (dictCode: string): DictItemOption[] => {
    return dictCache.value[dictCode] || [];
  };

  /**
   * 移除指定사전항목
   * @param dictCode 사전인코딩
   */
  const removeDictItem = (dictCode: string) => {
    if (dictCache.value[dictCode]) {
      Reflect.deleteProperty(dictCache.value, dictCode);
    }
  };

  /**
   * 정리비어있음사전캐시
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

export function useDict스토어Hook() {
  return useDict스토어(저장소);
}
