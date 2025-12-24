import { watchEffect, computed } from "vue";
import { useWindowSize } from "@vueuse/core";
import { useApp스토어 } from "@/저장소";
import { DeviceEnum } from "@/enums/settings/device-enum";

/**
 * 设备检测및응답式처리
 * 리스닝屏幕尺寸변경，자동调整设备타입및측엣지열상태
 */
export function useDeviceDetection() {
  const app스토어 = useApp스토어();
  const { width } = useWindowSize();

  // 桌面设备断点
  const DESKTOP_BREAKPOINT = 992;

  // 계算设备타입
  const isDesktop = computed(() => width.value >= DESKTOP_BREAKPOINT);
  const isMobile = computed(() => app스토어.device === DeviceEnum.MOBILE);

  // 리스닝屏幕尺寸변경，자동调整设备타입및측엣지열상태
  watchEffect(() => {
    const deviceType = isDesktop.value ? DeviceEnum.DESKTOP : DeviceEnum.MOBILE;

    // 업데이트设备타입
    app스토어.toggleDevice(deviceType);

    // 에 따라设备타입调整측엣지열상태
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
