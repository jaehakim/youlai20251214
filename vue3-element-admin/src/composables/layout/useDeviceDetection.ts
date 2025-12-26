import { watchEffect, computed } from "vue";
import { useWindowSize } from "@vueuse/core";
import { useApp스토어 } from "@/저장소";
import { DeviceEnum } from "@/enums/settings/device-enum";

/**
 * 디바이스 감지 및 반응형 처리
 * 화면 크기 변경 감지, 자동으로 디바이스 타입 및 사이드바 상태 조정
 */
export function useDeviceDetection() {
  const app스토어 = useApp스토어();
  const { width } = useWindowSize();

  // 데스크톱 디바이스 브레이크포인트
  const DESKTOP_BREAKPOINT = 992;

  // 디바이스 타입 계산
  const isDesktop = computed(() => width.value >= DESKTOP_BREAKPOINT);
  const isMobile = computed(() => app스토어.device === DeviceEnum.MOBILE);

  // 화면 크기 변경 감지, 자동으로 디바이스 타입 및 사이드바 상태 조정
  watchEffect(() => {
    const deviceType = isDesktop.value ? DeviceEnum.DESKTOP : DeviceEnum.MOBILE;

    // 디바이스 타입 업데이트
    app스토어.toggleDevice(deviceType);

    // 디바이스 타입에 따라 사이드바 상태 조정
    if (isDesktop.value) {
      app스토어.openSideBar();
    } else {
      app스토어.closeSideBar();
    }
  });

  return {
    isDesktop,
    isMobile,
  };
}
