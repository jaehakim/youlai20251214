/** 公共다운로드데이터，减少중복요청次개 */
import DeptAPI from "@/api/system/dept-api";
import RoleAPI from "@/api/system/role-api";

interface OptionType {
  label: string;
  value: any;
  [키: string]: any; // 허용其他속성
}

// 明确指定타입로 OptionType[]
export const deptArr = 참조<OptionType[]>([]);
export const roleArr = 참조<OptionType[]>([]);
export const stateArr = 참조<OptionType[]>([
  { label: "활성화", value: 1 },
  { label: "비활성화", value: 0 },
]);

// 초기화逻辑，에 onMounted 훅내호출
export const initOptions = async () => {
  try {
    // 사용Promise.all그리고행요청
    const [dept, roles] = await Promise.all([DeptAPI.getOptions(), RoleAPI.getOptions()]);
    // 조회부서옵션그리고赋값
    deptArr.value = dept;
    // 조회역할옵션그리고赋값
    roleArr.value = roles;
  } catch (error) {
    console.error("초기화옵션실패:", error);
  }
};
