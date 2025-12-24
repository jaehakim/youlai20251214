
#  Docker Compose로 미들웨어 MySQL, Redis, Minio, Xxl-Job 설치

## 설치

```bash
docker-compose -f ./docker-compose.yml -p youlai-boot up -d
```

- p youlai-boot 指定命名空间，避免与其他컨테이너충돌，这里方便관리，统一관리和언마운트

## 제거
```bash
docker-compose -f ./docker-compose.yml -p youlai-boot down
```

