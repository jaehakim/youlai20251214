/** 공통 다운로드 데이터, 중복 요청 횟수 감소 */
import DeptAPI from "@/api/system/dept-api";
import RoleAPI from "@/api/system/role-api";

interface OptionType {
  label: string;
  value: any;
  [key: string]: any; // 다른 속성 허용
}

// 타입을 OptionType[]으로 명시적 지정
export const deptArr = ref<OptionType[]>([]);
export const roleArr = ref<OptionType[]>([]);
export const stateArr = ref<OptionType[]>([
  { label: "활성화", value: 1 },
  { label: "비활성화", value: 0 },
]);

// 초기화 로직, onMounted 훅에서 호출
export const initOptions = async () => {
  try {
    // Promise.all을 사용하여 병렬 요청
    const [dept, roles] = await Promise.all([DeptAPI.getOptions(), RoleAPI.getOptions()]);
    // 부서 옵션 조회 및 할당
    deptArr.value = dept;
    // 역할 옵션 조회 및 할당
    roleArr.value = roles;
  } catch (error) {
    console.error("옵션 초기화 실패:", error);
  }
};
