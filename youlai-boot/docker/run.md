
#  Docker Compose로 미들웨어 MySQL, Redis, Minio, Xxl-Job 설치

## 설치

```bash
docker-compose -f ./docker-compose.yml -p youlai-boot up -d
```

- p youlai-boot는 네임스페이스를 지정하여 다른 컨테이너와의 충돌을 방지하며, 관리의 편의를 위해 통합 관리 및 언마운트합니다.

## 제거
```bash
docker-compose -f ./docker-compose.yml -p youlai-boot down
```

