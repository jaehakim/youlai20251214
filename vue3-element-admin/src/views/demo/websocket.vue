<template>
  <div class="app-container">
    <el-link
      href="https://gitee.com/youlaiorg/vue3-element-admin/blob/master/src/views/demo/websocket.vue"
      type="primary"
      target="_blank"
      class="mb-[20px]"
    >
      예제 소스 코드 클릭>>>>
    </el-link>
    <el-row :gutter="10">
      <el-col :span="12">
        <el-card>
          <el-row>
            <el-col :span="18">
              <el-input v-model="socketEndpoint" style="width: 200px" />
              <el-button
                type="primary"
                class="ml-5"
                :disabled="isConnected"
                @click="connectWebSocket"
              >
                연결
              </el-button>
              <el-button type="danger" :disabled="!isConnected" @click="disconnectWebSocket">
                연결 해제
              </el-button>
            </el-col>
            <el-col :span="6" class="text-right">
              연결 상태：
              <el-tag v-if="isConnected" type="success">연결됨</el-tag>
              <el-tag v-else type="info">연결 해제됨</el-tag>
            </el-col>
          </el-row>
        </el-card>
        <!-- 브로드캐스트 메시지 발송 부분 -->
        <el-card class="mt-5">
          <el-form label-width="90px">
            <el-form-item label="메시지 내용">
              <el-input v-model="topicMessage" type="textarea" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="sendToAll">브로드캐스트 발송</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <!-- 1:1 메시지 발송 부분 -->
        <el-card class="mt-5">
          <el-form label-width="90px">
            <el-form-item label="메시지 내용">
              <el-input v-model="queneMessage" type="textarea" />
            </el-form-item>
            <el-form-item label="메시지 수신자">
              <el-input v-model="receiver" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="sendToUser">1:1 메시지 발송</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <!-- 메시지 수신 표시 부분 -->
      <el-col :span="12">
        <el-card>
          <div class="chat-messages-wrapper">
            <div
              v-for="(message, index) in messages"
              :key="index"
              :class="[
                message.type === 'tip' ? 'system-notice' : 'chat-message',
                {
                  'chat-message--sent': message.sender === user스토어.userInfo.username,
                  'chat-message--received': message.sender !== user스토어.userInfo.username,
                },
              ]"
            >
              <template v-if="message.type != 'tip'">
                <div class="chat-message__content">
                  <div
                    :class="{
                      'chat-message__sender': message.sender === user스토어.userInfo.username,
                      'chat-message__receiver': message.sender !== user스토어.userInfo.username,
                    }"
                  >
                    {{ message.sender }}
                  </div>
                  <div class="text-gray-600">{{ message.content }}</div>
                </div>
              </template>
              <div v-else>{{ message.content }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { useStomp } from "@/composables/websocket/useStomp";
import { useUser스토어Hook } from "@/store/modules/user-store";

const user스토어 = useUser스토어Hook();
// WebSocket 주소를 수동으로 조정하기 위함
const socketEndpoint = ref(import.meta.env.VITE_APP_WS_ENDPOINT);
// 연결 상태 동기화
interface MessageType {
  type?: string;
  sender?: string;
  content: string;
}
const messages = ref<MessageType[]>([]);
// 브로드캐스트 메시지 내용
const topicMessage = ref("친구들, 시스템이 최신 상태로 복구되었습니다.");
// 1:1 메시지 내용(기본 예제)
const queneMessage = ref("Hi, " + user스토어.userInfo.username + " 이것은 1:1 메시지 예제입니다!");
const receiver = ref("root");

// useStomp hook 호출, 기본적으로 socketEndpoint 및 토큰 사용(여기서는 getAccessToken() 사용)
const { isConnected, connect, subscribe, disconnect } = useStomp({
  debug: true,
});

watch(
  () => isConnected.value,
  (connected) => {
    if (connected) {
      // 연결 성공 후 브로드캐스트 및 1:1 메시지 주제 구독
      subscribe("/topic/notice", (res) => {
        messages.value.push({
          sender: "Server",
          content: res.body,
        });
      });
      subscribe("/user/queue/greeting", (res) => {
        const messageData = JSON.parse(res.body) as MessageType;
        messages.value.push({
          sender: messageData.sender,
          content: messageData.content,
        });
      });
      messages.value.push({
        sender: "Server",
        content: "WebSocket 연결됨",
        type: "tip",
      });
    } else {
      messages.value.push({
        sender: "Server",
        content: "WebSocket 연결 해제됨",
        type: "tip",
      });
    }
  }
);

// WebSocket 연결
function connectWebSocket() {
  connect();
}

// WebSocket 연결 해제
function disconnectWebSocket() {
  disconnect();
}

// 브로드캐스트 메시지 발송
function sendToAll() {
  if (isConnected.value) {
    // 구독 모드를 직접 사용하여 브로드캐스트 메시지 처리
    subscribe("/app/broadcast", () => {});
    messages.value.push({
      sender: user스토어.userInfo.username,
      content: topicMessage.value,
    });
  }
}

// 1:1 메시지 발송
function sendToUser() {
  if (isConnected.value) {
    // 구독 모드를 사용하여 1:1 메시지 처리
    subscribe(`/app/sendToUser/${receiver.value}`, () => {});
    messages.value.push({
      sender: user스토어.userInfo.username,
      content: queneMessage.value,
    });
  }
}

onMounted(() => {
  connectWebSocket();
});

onBeforeUnmount(() => {
  disconnectWebSocket();
});
</script>

<style scoped lang="scss">
.chat-messages-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.chat-message {
  max-width: 80%;
  padding: 10px;
  border-radius: 5px;
  &--sent {
    align-self: flex-end;
    background-color: #dcf8c6;
  }
  &--received {
    align-self: flex-start;
    background-color: #e8e8e8;
  }
  &__content {
    display: flex;
    flex-direction: column;
    color: var(--el-text-color-primary); // 사용테마텍스트색상
  }
  &__sender {
    margin-bottom: 5px;
    font-weight: bold;
    text-align: right;
  }
  &__receiver {
    margin-bottom: 5px;
    font-weight: bold;
    text-align: left;
  }
}
.system-notice {
  align-self: center;
  padding: 5px 10px;
  font-size: 0.9em;
  color: var(--el-text-color-secondary);
  background-color: var(--el-fill-color-lighter);
  border-radius: 15px;
}
</style>
