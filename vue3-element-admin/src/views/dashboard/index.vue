<template>
  <div class="dashboard-container">
    <!-- GitHub 배지 -->
    <github-corner class="github-corner" />

    <el-card shadow="never" class="mt-2">
      <div class="flex flex-wrap">
        <!-- 왼쪽 인사말 영역 -->
        <div class="flex-1 flex items-start">
          <el-avatar
            :src="avatarUrl80"
            :size="80"
            @error="handleAvatarError"
          />
          <div class="ml-5">
            <p>{{ greetings }}</p>
            <p class="text-sm text-gray">오늘 날씨가 맑고, 기온은 15℃에서 25℃ 사이이며, 남동풍입니다.</p>
          </div>
        </div>

        <!-- 오른쪽 아이콘 영역 - PC -->
        <div class="hidden sm:block">
          <div class="flex items-end space-x-6">
            <!-- 저장소 -->
            <div>
              <div class="font-bold color-#ff9a2e text-sm flex items-center">
                <el-icon class="mr-2px"><Folder /></el-icon>
                저장소
              </div>
              <div class="mt-3 whitespace-nowrap">
                <el-link href="https://gitee.com/youlaiorg/vue3-element-admin" target="_blank">
                  <div class="i-svg:gitee text-lg color-#F76560" />
                </el-link>
                <el-divider direction="vertical" />
                <el-link href="https://github.com/youlaitech/vue3-element-admin" target="_blank">
                  <div class="i-svg:github text-lg color-#4080FF" />
                </el-link>
                <el-divider direction="vertical" />
                <el-link href="https://gitcode.com/youlai/vue3-element-admin" target="_blank">
                  <div class="i-svg:gitcode text-lg color-#FF9A2E" />
                </el-link>
              </div>
            </div>

            <!-- 문서 -->
            <div>
              <div class="font-bold color-#4080ff text-sm flex items-center">
                <el-icon class="mr-2px"><Document /></el-icon>
                문서
              </div>
              <div class="mt-3 whitespace-nowrap">
                <el-link href="https://juejin.cn/post/7228990409909108793" target="_blank">
                  <div class="i-svg:juejin text-lg" />
                </el-link>
                <el-divider direction="vertical" />
                <el-link
                  href="https://youlai.blog.csdn.net/article/details/130191394"
                  target="_blank"
                >
                  <div class="i-svg:csdn text-lg" />
                </el-link>
                <el-divider direction="vertical" />
                <el-link href="https://www.cnblogs.com/haoxianrui/p/17331952.html" target="_blank">
                  <div class="i-svg:cnblogs text-lg" />
                </el-link>
              </div>
            </div>

            <!-- 비디오 -->
            <div>
              <div class="font-bold color-#f76560 text-sm flex items-center">
                <el-icon class="mr-2px"><VideoCamera /></el-icon>
                비디오
              </div>
              <div class="mt-3 whitespace-nowrap">
                <el-link href="https://www.bilibili.com/video/BV1eFUuYyEFj" target="_blank">
                  <div class="i-svg:bilibili text-lg" />
                </el-link>
              </div>
            </div>
          </div>
        </div>

        <!-- 모바일 아이콘 영역 -->
        <div class="w-full sm:hidden mt-3">
          <div class="flex justify-end space-x-4 overflow-x-auto">
            <!-- 저장소 아이콘 -->
            <el-link href="https://gitee.com/youlaiorg/vue3-element-admin" target="_blank">
              <div class="i-svg:gitee text-lg color-#F76560" />
            </el-link>
            <el-link href="https://github.com/youlaitech/vue3-element-admin" target="_blank">
              <div class="i-svg:github text-lg color-#4080FF" />
            </el-link>
            <el-link href="https://gitcode.com/youlai/vue3-element-admin" target="_blank">
              <div class="i-svg:gitcode text-lg color-#FF9A2E" />
            </el-link>

            <!-- 문서 아이콘 -->
            <el-link href="https://juejin.cn/post/7228990409909108793" target="_blank">
              <div class="i-svg:juejin text-lg" />
            </el-link>
            <el-link href="https://youlai.blog.csdn.net/article/details/130191394" target="_blank">
              <div class="i-svg:csdn text-lg" />
            </el-link>
            <el-link href="https://www.cnblogs.com/haoxianrui/p/17331952.html" target="_blank">
              <div class="i-svg:cnblogs text-lg" />
            </el-link>

            <!-- 비디오 아이콘 -->
            <el-link href="https://www.bilibili.com/video/BV1eFUuYyEFj" target="_blank">
              <div class="i-svg:bilibili text-lg" />
            </el-link>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 데이터 통계 -->
    <el-row :gutter="10" class="mt-5">
      <!-- 온라인 사용자 수 -->
      <el-col :span="8" :xs="24" class="mb-xs-3">
        <el-card shadow="never" class="h-full flex flex-col">
          <template #header>
            <div class="flex-x-between">
              <span class="text-gray">온라인 사용자</span>
              <el-tag type="danger" size="small">실시간</el-tag>
            </div>
          </template>

          <div class="flex-x-between mt-2 flex-1">
            <div class="flex-y-center">
              <span class="text-lg transition-all duration-300 hover:scale-110">
                {{ onlineUserCount }}
              </span>
              <span v-if="isConnected" class="ml-2 text-xs text-[#67c23a]">
                <el-icon><Connection /></el-icon>
                연결됨
              </span>
              <span v-else class="ml-2 text-xs text-[#f56c6c]">
                <el-icon><Failed /></el-icon>
                연결 해제
              </span>
            </div>
            <div class="i-svg:people w-8 h-8 animate-[pulse_2s_infinite]" />
          </div>

          <div class="flex-x-between mt-2 text-sm text-gray">
            <span>업데이트 시간</span>
            <span>{{ formattedTime }}</span>
          </div>
        </el-card>
      </el-col>

      <!-- 방문자 수(UV) -->
      <el-col :span="8" :xs="24" class="mb-xs-3">
        <el-skeleton :loading="visitStatsLoading" :rows="5" animated>
          <template #template>
            <el-card>
              <template #header>
                <div>
                  <el-skeleton-item variant="h3" style="width: 40%" />
                  <el-skeleton-item variant="rect" style="float: right; width: 1em; height: 1em" />
                </div>
              </template>

              <div class="flex-x-between">
                <el-skeleton-item variant="text" style="width: 30%" />
                <el-skeleton-item variant="circle" style="width: 2em; height: 2em" />
              </div>
              <div class="mt-5 flex-x-between">
                <el-skeleton-item variant="text" style="width: 50%" />
                <el-skeleton-item variant="text" style="width: 1em" />
              </div>
            </el-card>
          </template>
          <template v-if="!visitStatsLoading">
            <el-card shadow="never" class="h-full flex flex-col">
              <template #header>
                <div class="flex-x-between">
                  <span class="text-gray">방문자 수(UV)</span>
                  <el-tag type="success" size="small">일</el-tag>
                </div>
              </template>

              <div class="flex-x-between mt-2 flex-1">
                <div class="flex-y-center">
                  <span class="text-lg">{{ Math.round(transitionUvCount) }}</span>
                  <span
                    :class="[
                      'text-xs',
                      'ml-2',
                      computeGrowthRateClass(visitStatsData.uvGrowthRate),
                    ]"
                  >
                    <el-icon>
                      <Top v-if="visitStatsData.uvGrowthRate > 0" />
                      <Bottom v-else-if="visitStatsData.uvGrowthRate < 0" />
                    </el-icon>
                    {{ formatGrowthRate(visitStatsData.uvGrowthRate) }}
                  </span>
                </div>
                <div class="i-svg:visitor w-8 h-8" />
              </div>

              <div class="flex-x-between mt-2 text-sm text-gray">
                <span>총 방문자 수</span>
                <span>{{ Math.round(transitionTotalUvCount) }}</span>
              </div>
            </el-card>
          </template>
        </el-skeleton>
      </el-col>

      <!-- 페이지뷰(PV) -->
      <el-col :span="8" :xs="24">
        <el-skeleton :loading="visitStatsLoading" :rows="5" animated>
          <template #template>
            <el-card>
              <template #header>
                <div>
                  <el-skeleton-item variant="h3" style="width: 40%" />
                  <el-skeleton-item variant="rect" style="float: right; width: 1em; height: 1em" />
                </div>
              </template>

              <div class="flex-x-between">
                <el-skeleton-item variant="text" style="width: 30%" />
                <el-skeleton-item variant="circle" style="width: 2em; height: 2em" />
              </div>
              <div class="mt-5 flex-x-between">
                <el-skeleton-item variant="text" style="width: 50%" />
                <el-skeleton-item variant="text" style="width: 1em" />
              </div>
            </el-card>
          </template>
          <template v-if="!visitStatsLoading">
            <el-card shadow="never" class="h-full flex flex-col">
              <template #header>
                <div class="flex-x-between">
                  <span class="text-gray">페이지뷰(PV)</span>
                  <el-tag type="primary" size="small">일</el-tag>
                </div>
              </template>

              <div class="flex-x-between mt-2 flex-1">
                <div class="flex-y-center">
                  <span class="text-lg">{{ Math.round(transitionPvCount) }}</span>
                  <span
                    :class="[
                      'text-xs',
                      'ml-2',
                      computeGrowthRateClass(visitStatsData.pvGrowthRate),
                    ]"
                  >
                    <el-icon>
                      <Top v-if="visitStatsData.pvGrowthRate > 0" />
                      <Bottom v-else-if="visitStatsData.pvGrowthRate < 0" />
                    </el-icon>
                    {{ formatGrowthRate(visitStatsData.pvGrowthRate) }}
                  </span>
                </div>
                <div class="i-svg:browser w-8 h-8" />
              </div>

              <div class="flex-x-between mt-2 text-sm text-gray">
                <span>총 페이지뷰</span>
                <span>{{ Math.round(transitionTotalPvCount) }}</span>
              </div>
            </el-card>
          </template>
        </el-skeleton>
      </el-col>
    </el-row>

    <el-row :gutter="10" class="mt-5">
      <!-- 방문 추세 통계 차트 -->
      <el-col :xs="24" :span="16">
        <el-card>
          <template #header>
            <div class="flex-x-between">
              <span>방문 추세</span>
              <el-radio-group v-model="visitTrendDateRange" size="small">
                <el-radio-button :value="7">최근 7일</el-radio-button>
                <el-radio-button :value="30">최근 30일</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <ECharts :options="visitTrendChartOptions" height="400px" />
        </el-card>
      </el-col>
      <!-- 최신 동향 -->
      <el-col :xs="24" :span="8">
        <el-card>
          <template #header>
            <div class="flex-x-between">
              <span class="header-title">최신 동향</span>
              <el-link
                type="primary"
                underline="never"
                href="https://gitee.com/youlaiorg/vue3-element-admin/releases"
                target="_blank"
              >
                전체 기록
                <el-icon class="link-icon"><TopRight /></el-icon>
              </el-link>
            </div>
          </template>

          <el-scrollbar height="400px">
            <el-timeline class="p-3">
              <el-timeline-item
                v-for="(item, index) in vesionList"
                :key="index"
                :timestamp="item.date"
                placement="top"
                :color="index === 0 ? '#67C23A' : '#909399'"
                :hollow="index !== 0"
                size="large"
              >
                <div class="version-item" :class="{ 'latest-item': index === 0 }">
                  <div>
                    <el-text tag="strong">{{ item.title }}</el-text>
                    <el-tag v-if="item.tag" :type="index === 0 ? 'success' : 'info'" size="small">
                      {{ item.tag }}
                    </el-tag>
                  </div>

                  <el-text class="version-content">{{ item.content }}</el-text>

                  <div v-if="item.link">
                    <el-link
                      :type="index === 0 ? 'primary' : 'info'"
                      :href="item.link"
                      target="_blank"
                      underline="never"
                    >
                      세부사항
                      <el-icon class="link-icon"><TopRight /></el-icon>
                    </el-link>
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </el-scrollbar>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "Dashboard",
  inheritAttrs: false,
});

import { dayjs } from "element-plus";
import LogAPI, { VisitStatsVO, VisitTrendVO } from "@/api/system/log-api";
import { useUserStore } from "@/store/modules/user-store";
import { formatGrowthRate } from "@/utils";
import { generateMSAvatarBase64 } from "@/utils/avatar";
import { useTransition, useDateFormat } from "@vueuse/core";
import { Connection, Failed } from "@element-plus/icons-vue";
import { useOnlineCount } from "@/composables";

// 온라인 사용자 수 컴포넌트 관련
const { onlineUserCount, lastUpdateTime, isConnected } = useOnlineCount();

// 이전 사용자 수 기록, 추세 계산용
const previousCount = ref(0);

// 사용자 수 변화 감시, 추세 계산
watch(onlineUserCount, (newCount, oldCount) => {
  if (oldCount > 0) {
    previousCount.value = oldCount;
  }
});

// 타임스탐프 형식화
const formattedTime = computed(() => {
  if (!lastUpdateTime.value) return "--";
  return useDateFormat(lastUpdateTime, "HH:mm:ss").value;
});

interface VersionItem {
  id: string;
  title: string; // 버전 제목 (예: v2.4.0)
  date: string; // 릴리스 시간
  content: string; // 버전 설명
  link: string; // 세부사항 링크
  tag?: string; // 버전 태그 (선택 사항)
}

const userStore = useUserStore();

// 아바타 로딩 실패 상태
const avatarLoadError = ref(false);

// 80x80 크기 아바타 URL
const avatarUrl80 = computed(() => {
  // 아바타가 있고 로딩 실패하지 않았으면 해당 URL 반환
  if (userStore.userInfo.avatar && !avatarLoadError.value) {
    return userStore.userInfo.avatar;
  }
  // 아바타가 없거나 로딩 실패 → MS 메신저 스타일 기본 아바타 생성 (Base64 SVG)
  return generateMSAvatarBase64(userStore.userInfo.username || "User", 80);
});

/**
 * 아바타 이미지 로딩 실패 처리
 */
function handleAvatarError() {
  console.warn("사용자 아바타 이미지 로딩 실패");
  avatarLoadError.value = true;
  // el-avatar 컴포넌트가 자동으로 기본 아바타 표시
}

// 현재 공지 사항 목록
const vesionList = ref<VersionItem[]>([
  {
    id: "1",
    title: "v3.0.0",
    date: "2025-06-06 00:00:00",
    content: "레이아웃 재작성, 코드 규범 리팩토링.",
    link: "https://gitee.com/youlaiorg/vue3-element-admin/releases",
    tag: "마일스톤",
  },
  {
    id: "2",
    title: "v2.4.0",
    date: "2021-09-01 00:00:00",
    content: "기본 프레임워크 구축 구현, 권한 관리, 라우팅 시스템 등 핵심 기능 포함.",
    link: "https://gitee.com/youlaiorg/vue3-element-admin/releases",
    tag: "마일스톤",
  },
  {
    id: "3",
    title: "v2.4.0",
    date: "2021-09-01 00:00:00",
    content: "기본 프레임워크 구축 구현, 권한 관리, 라우팅 시스템 등 핵심 기능 포함.",
    link: "https://gitee.com/youlaiorg/vue3-element-admin/releases",
    tag: "마일스톤",
  },
]);

// 현재 시간 (인사말 계산용)
const currentDate = new Date();

// 인사말: 현재 시간에 따라 다른 인사말 반환
const greetings = computed(() => {
  const hours = currentDate.getHours();
  const nickname = userStore.userInfo.nickname;
  if (hours >= 6 && hours < 8) {
    return "아침이 밝아오고 있습니다. 좋은 아침입니다🌅!";
  } else if (hours >= 8 && hours < 12) {
    return `좋은 아침입니다, ${nickname}!`;
  } else if (hours >= 12 && hours < 18) {
    return `좋은 오후입니다, ${nickname}!`;
  } else if (hours >= 18 && hours < 24) {
    return `좋은 저녁입니다, ${nickname}!`;
  } else {
    return "하늘의 별에서 몰래 작은 별 조각을 빌렸습니다. 당신이 눈을 감으면 당신의 꿈에 뿌려질 것입니다. 좋은 밤🌛!";
  }
});

// 방문자 통계 데이터 로드 상태
const visitStatsLoading = ref(true);
// 방문자 통계 데이터
const visitStatsData = ref<VisitStatsVO>({
  todayUvCount: 0,
  uvGrowthRate: 0,
  totalUvCount: 0,
  todayPvCount: 0,
  pvGrowthRate: 0,
  totalPvCount: 0,
});

// 숫자 전환 애니메이션
const transitionUvCount = useTransition(
  computed(() => visitStatsData.value.todayUvCount),
  {
    duration: 1000,
    transition: [0.25, 0.1, 0.25, 1.0], // CSS cubic-bezier
  }
);

const transitionTotalUvCount = useTransition(
  computed(() => visitStatsData.value.totalUvCount),
  {
    duration: 1200,
    transition: [0.25, 0.1, 0.25, 1.0],
  }
);

const transitionPvCount = useTransition(
  computed(() => visitStatsData.value.todayPvCount),
  {
    duration: 1000,
    transition: [0.25, 0.1, 0.25, 1.0],
  }
);

const transitionTotalPvCount = useTransition(
  computed(() => visitStatsData.value.totalPvCount),
  {
    duration: 1200,
    transition: [0.25, 0.1, 0.25, 1.0],
  }
);

// 방문 추세 날짜 범위 (단위: 일)
const visitTrendDateRange = ref(7);
// 방문 추세 차트 구성
const visitTrendChartOptions = ref();

/**
 * 방문자 통계 데이터 가져오기
 */
const fetchVisitStatsData = () => {
  LogAPI.getVisitStats()
    .then((data) => {
      visitStatsData.value = data;
    })
    .finally(() => {
      visitStatsLoading.value = false;
    });
};

/**
 * 방문 추세 데이터를 가져오고 차트 구성을 업데이트합니다
 */
const fetchVisitTrendData = () => {
  const startDate = dayjs()
    .subtract(visitTrendDateRange.value - 1, "day")
    .toDate();
  const endDate = new Date();

  LogAPI.getVisitTrend({
    startDate: dayjs(startDate).format("YYYY-MM-DD"),
    endDate: dayjs(endDate).format("YYYY-MM-DD"),
  }).then((data) => {
    updateVisitTrendChartOptions(data);
  });
};

/**
 * 방문 추세 차트의 구성 항목 업데이트
 *
 * @param data - 방문 추세 데이터
 */
const updateVisitTrendChartOptions = (data: VisitTrendVO) => {
  visitTrendChartOptions.value = {
    tooltip: {
      trigger: "axis",
    },
    legend: {
      data: ["페이지뷰(PV)", "방문자 수(UV)"],
      bottom: 0,
    },
    grid: {
      left: "1%",
      right: "5%",
      bottom: "10%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: data.dates,
    },
    yAxis: {
      type: "value",
      splitLine: {
        show: true,
        lineStyle: {
          type: "dashed",
        },
      },
    },
    series: [
      {
        name: "페이지뷰(PV)",
        type: "line",
        data: data.pvList,
        areaStyle: {
          color: "rgba(64, 158, 255, 0.1)",
        },
        smooth: true,
        itemStyle: {
          color: "#4080FF",
        },
        lineStyle: {
          color: "#4080FF",
        },
      },
      {
        name: "방문자 수(UV)",
        type: "line",
        data: data.ipList,
        areaStyle: {
          color: "rgba(103, 194, 58, 0.1)",
        },
        smooth: true,
        itemStyle: {
          color: "#67C23A",
        },
        lineStyle: {
          color: "#67C23A",
        },
      },
    ],
  };
};

/**
 * 증장률에 따라 해당하는 CSS 클래스명 계산
 *
 * @param growthRate - 증장률 값
 */
const computeGrowthRateClass = (growthRate?: number): string => {
  if (!growthRate) {
    return "text-[--el-color-info]";
  }
  if (growthRate > 0) {
    return "text-[--el-color-danger]";
  } else if (growthRate < 0) {
    return "text-[--el-color-success]";
  } else {
    return "text-[--el-color-info]";
  }
};

// 방문 추세 날짜 범위 변경 감시, 추세 데이터 다시 가져오기
watch(
  () => visitTrendDateRange.value,
  () => {
    fetchVisitTrendData();
  },
  { immediate: true }
);

// 컴포넌트 마운트 후 방문자 통계 데이터와 공지 데이터 로드
onMounted(() => {
  fetchVisitStatsData();
});
</script>

<style lang="scss" scoped>
.dashboard-container {
  position: relative;
  padding: 24px;

  .github-corner {
    position: absolute;
    top: 0;
    right: 0;
    z-index: 1;
    border: 0;
  }

  .version-item {
    padding: 16px;
    margin-bottom: 12px;
    background: var(--el-fill-color-lighter);
    border-radius: 8px;
    transition: all 0.2s;

    &.latest-item {
      background: var(--el-color-primary-light-9);
      border: 1px solid var(--el-color-primary-light-5);
    }
    &:hover {
      transform: translateX(5px);
    }
    .version-content {
      margin-bottom: 12px;
      font-size: 13px;
      line-height: 1.5;
      color: var(--el-text-color-secondary);
    }
  }
}
</style>
