package com.youlai.boot.platform.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.platform.file.model.FileInfo;
import com.youlai.boot.platform.file.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * MinIO 파일 업로드 서비스 클래스
 *
 * @author Ray.Hao
 * @since 2023/6/2
 */
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "minio")
@ConfigurationProperties(prefix = "oss.minio")
@RequiredArgsConstructor
@Data
@Slf4j
public class MinioFileService implements FileService {

    /**
     * 서비스 엔드포인트
     */
    private String endpoint;
    /**
     * 접근 자격증명
     */
    private String accessKey;
    /**
     * 자격증명 비밀키
     */
    private String secretKey;
    /**
     * 스토리지 버킷 이름
     */
    private String bucketName;
    /**
     * 사용자 정의 도메인
     */
    private String customDomain;

    private MinioClient minioClient;

    // 의존성 주입 완료 후 초기화 실행
    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        // 스토리지 버킷 생성(버킷이 존재하지 않을 경우)
        // createBucketIfAbsent(bucketName);
    }


    /**
     * 파일 업로드
     *
     * @param file 폼 파일 객체
     * @return 파일 정보
     */
    @Override
    public FileInfo uploadFile(MultipartFile file) {

        // 스토리지 버킷 생성(버킷이 존재하지 않을 경우), 구축된 minio 서비스가 있다면 init 메서드에 두는 것을 권장
        createBucketIfAbsent(bucketName);

        // 파일 원본 이름
        String originalFilename = file.getOriginalFilename();
        // 파일 확장자
        String suffix = FileUtil.getSuffix(originalFilename);
        // 폴더 이름
        String dateFolder = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        // 파일 이름
        String fileName = IdUtil.simpleUUID() + "." + suffix;

        //  try-with-resource 문법으로 스트림 자동 해제
        try (InputStream inputStream = file.getInputStream()) {
            // 파일 업로드
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(dateFolder + "/"+ fileName)
                    .contentType(file.getContentType())
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);

            // 파일 경로 반환
            String fileUrl;
            // 사용자 정의 도메인 미설정
            if (StrUtil.isBlank(customDomain)) {
                // 파일 URL 조회
                GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(dateFolder + "/"+ fileName)
                        .method(Method.GET)
                        .build();

                fileUrl = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
                fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
            } else {
                // 사용자 정의 파일 경로 도메인 설정
                fileUrl = customDomain + "/"+ bucketName + "/"+ dateFolder + "/"+ fileName;
            }

            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(originalFilename);
            fileInfo.setUrl(fileUrl);
            return fileInfo;
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new BusinessException(ResultCode.UPLOAD_FILE_EXCEPTION, e.getMessage());
        }
    }


    /**
     * 파일 삭제
     *
     * @param filePath 파일 전체 경로
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "삭제할 파일 경로는 비어있을 수 없습니다");
        try {
            String fileName;
            if (StrUtil.isNotBlank(customDomain)) {
                // https://oss.youlai.tech/default/20221120/test.jpg → 20221120/websocket.jpg
                fileName = filePath.substring(customDomain.length() + 1 + bucketName.length() + 1); // 두 개의 /가 2개 문자 길이를 차지
            } else {
                // http://localhost:9000/default/20221120/test.jpg → 20221120/websocket.jpg
                fileName = filePath.substring(endpoint.length() + 1 + bucketName.length() + 1);
            }
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build();

            minioClient.removeObject(removeObjectArgs);
            return true;
        } catch (Exception e) {
            log.error("파일 삭제 실패", e);
            throw new BusinessException(ResultCode.DELETE_FILE_EXCEPTION, e.getMessage());
        }
    }


    /**
     * PUBLIC 버킷 정책
     * 설정하지 않으면 새로 생성된 스토리지 버킷은 기본적으로 PRIVATE이며, 버킷 파일 접근이 거부됩니다 (Access Denied)
     *
     * @param bucketName 스토리지 버킷 이름
     * @return 스토리지 버킷 정책
     */
    private static String publicBucketPolicy(String bucketName) {
        // AWS의 S3 스토리지 버킷 정책 JSON 형식 https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/example-bucket-policies.html
        return "{\"Version\":\"2012-10-17\","
                + "\"Statement\":[{\"Effect\":\"Allow\","
                + "\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:ListBucketMultipartUploads\",\"s3:GetBucketLocation\",\"s3:ListBucket\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "\"]},"
                + "{\"Effect\":\"Allow\"," + "\"Principal\":{\"AWS\":[\"*\"]},"
                + "\"Action\":[\"s3:ListMultipartUploadParts\",\"s3:PutObject\",\"s3:AbortMultipartUpload\",\"s3:DeleteObject\",\"s3:GetObject\"],"
                + "\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";
    }

    /**
     * 스토리지 버킷 생성(버킷이 존재하지 않을 경우)
     *
     * @param bucketName 스토리지 버킷 이름
     */
    @SneakyThrows
    private void createBucketIfAbsent(String bucketName) {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        if (!minioClient.bucketExists(bucketExistsArgs)) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();

            minioClient.makeBucket(makeBucketArgs);

            // 스토리지 버킷 접근 권한을 PUBLIC으로 설정, 설정하지 않으면 새로 생성된 스토리지 버킷은 기본적으로 PRIVATE이며 버킷 파일 접근이 거부됩니다 (Access Denied)
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs
                    .builder()
                    .bucket(bucketName)
                    .config(publicBucketPolicy(bucketName))
                    .build();
            minioClient.setBucketPolicy(setBucketPolicyArgs);
        }
    }
}
