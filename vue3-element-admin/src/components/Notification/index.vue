<template>
  <el-dropdown trigger="click">
    <el-badge v-if="noticeList.length > 0" :value="noticeList.length" :max="99">
      <div class="i-svg:bell" />
    </el-badge>

    <div v-else class="i-svg:bell" />

    <template #dropdown>
      <div class="p-5">
        <template v-if="noticeList.length > 0">
          <div v-for="(item, index) in noticeList" :key="index" class="w-500px py-3">
            <div class="flex-y-center">
              <DictLabel v-model="item.type" code="notice_type" size="small" />
              <el-text
                size="small"
                class="w-200px cursor-pointer !ml-2 !flex-1"
                truncated
                @click="handleReadNotice(item.id)"
              >
                {{ item.title }}
              </el-text>

              <div class="text-xs text-gray">
                {{ item.publishTime }}
              </div>
            </div>
          </div>
          <el-divider />
          <div class="flex-x-between">
            <el-link type="primary" underline="never" @click="handleViewMoreNotice">
              <span class="text-xs">더보기</span>
              <el-icon class="text-xs">
                <ArrowRight />
              </el-icon>
            </el-link>
            <el-link
              v-if="noticeList.length > 0"
              type="primary"
              underline="never"
              @click="handleMarkAllAsRead"
            >
              <span class="text-xs">모두 읽음</span>
            </el-link>
          </div>
        </template>
        <template v-else>
          <div class="flex-center h-150px w-350px">
            <el-empty :image-size="50" description="메시지 없음" />
          </div>
        </template>
      </div>
    </template>
  </el-dropdown>

  <el-dialog
    v-model="noticeDialogVisible"
    :title="noticeDetail?.title ?? '알림 상세'"
    width="800px"
    custom-class="notification-detail"
  >
    <div v-if="noticeDetail" class="p-x-20px">
      <div class="flex-y-center mb-16px text-13px text-color-secondary">
        <span class="flex-y-center">
          <el-icon><User /></el-icon>
          {{ noticeDetail.publisherName }}
        </span>
        <span class="ml-2 flex-y-center">
          <el-icon><Timer /></el-icon>
          {{ noticeDetail.publishTime }}
        </span>
      </div>

      <div class="max-h-60vh pt-16px mb-24px overflow-y-auto border-t border-solid border-color">
        <div v-html="noticeDetail.content"></div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import NoticeAPI, { NoticePageVO, NoticeDetailVO } from "@/api/system/notice-api";
import router from "@/router";

const noticeList = ref<NoticePageVO[]>([]);
const noticeDialogVisible = ref(false);
const noticeDetail = ref<NoticeDetailVO | null>(null);

import { useStomp } from "@/composables/websocket/useStomp";
const { subscribe, unsubscribe, isConnected } = useStomp();

watch(
  () => isConnected.value,
  (connected) => {
    if (connected) {
      subscribe("/user/queue/message", (message: any) => {
        console.log("알림 메시지 수신:", message);
        const data = JSON.parse(message.body);
        const id = data.id;
        if (!noticeList.value.some((notice) => notice.id == id)) {
          noticeList.value.unshift({
            id,
            title: data.title,
            type: data.type,
            publishTime: data.publishTime,
          });

          ElNotification({
            title: "새 알림 메시지를 받았습니다!",
            message: data.title,
            type: "success",
            position: "bottom-right",
          });
        }
      });
    }
  }
);

/**
 * 나의 알림 공지 조회
 */
function featchMyNotice() {
  NoticeAPI.getMyNoticePage({ pageNum: 1, pageSize: 5, isRead: 0 }).then((data) => {
    noticeList.value = data.list;
  });
}

// 알림 공지 읽기
function handleReadNotice(id: string) {
  NoticeAPI.getDetail(id).then((data) => {
    noticeDialogVisible.value = true;
    noticeDetail.value = data;
    // 읽음으로 표시
    const index = noticeList.value.findIndex((notice) => notice.id === id);
    if (index >= 0) {
      noticeList.value.splice(index, 1);
    }
  });
}

// 더보기
function handleViewMoreNotice() {
  router.push({ name: "MyNotice" });
}

// 모두 읽음
function handleMarkAllAsRead() {
  NoticeAPI.readAll().then(() => {
    noticeList.value = [];
  });
}

onMounted(() => {
  featchMyNotice();
});

onBeforeUnmount(() => {
  unsubscribe("/user/queue/message");
});
</script>

<style lang="scss" scoped></style>
