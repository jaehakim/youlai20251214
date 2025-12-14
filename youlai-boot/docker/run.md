
#  Docker Compose로 미들웨어 MySQL, Redis, Minio, Xxl-Job 설치

## 설치

```bash
docker-compose -f ./docker-compose.yml -p youlai-boot up -d
```

- p youlai-boot 指定命名空间，避免与其他容器冲突，这里方便管理，统一管理和卸载

## 제거
```bash
docker-compose -f ./docker-compose.yml -p youlai-boot down
```

