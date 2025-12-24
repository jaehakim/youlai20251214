import NProgress from "nprogress";
import "nprogress/nprogress.css";

// 진행률 표시줄
NProgress.configure({
  // 애니메이션 방식
  easing: "ease",
  // 진행률 표시줄 증가 속도
  speed: 500,
  // 로딩 아이콘 표시 여부
  showSpinner: false,
  // 자동 증가 간격
  trickleSpeed: 200,
  // 초기화 시 최소 백분율
  minimum: 0.3,
});

export default NProgress;
