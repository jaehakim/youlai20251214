package com.youlai.boot.platform.file.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.youlai.boot.platform.file.service.FileService;
import com.youlai.boot.platform.file.model.FileInfo;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * Aliyun 객체 스토리지 서비스 클래스
 *
 * @author haoxr
 * @since 2.3.0
 */
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "aliyun")
@ConfigurationProperties(prefix = "oss.aliyun")
@RequiredArgsConstructor
@Data
public class AliyunFileService implements FileService {
    /**
     * 서비스 엔드포인트
     */
    private String endpoint;
    /**
     * 접근 자격증명
     */
    private String accessKeyId;
    /**
     * 자격증명 비밀키
     */
    private String accessKeySecret;
    /**
     * 스토리지 버킷 이름
     */
    private String bucketName;

    private OSS aliyunOssClient;

    @PostConstruct
    public void init() {
        aliyunOssClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    @SneakyThrows
    public FileInfo uploadFile(MultipartFile file) {

        // 파일 이름 조회
        String originalFilename = file.getOriginalFilename();
        // 파일 이름 생성(날짜 폴더)
        String suffix = FileUtil.getSuffix(originalFilename);
        String uuid = IdUtil.simpleUUID();
        String fileName = DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + uuid + "." + suffix;
        //  try-with-resource 문법으로 스트림 자동 해제
        try (InputStream inputStream = file.getInputStream()) {

            // 업로드 파일의 메타 정보 설정, 예: Content-Type
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            // PutObjectRequest 객체 생성, Bucket 이름, 객체 이름 및 입력 스트림 지정
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            // 파일 업로드
            aliyunOssClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 실패");
        }
        // 파일 접근 경로 조회
        String fileUrl = "https://" + bucketName + "." + endpoint + "/" + fileName;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(originalFilename);
        fileInfo.setUrl(fileUrl);
        return fileInfo;
    }

    @Override
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "삭제할 파일 경로는 비어있을 수 없습니다");
        String fileHost = "https://" + bucketName + "." + endpoint; // 파일 호스트 도메인
        String fileName = filePath.substring(fileHost.length() + 1); // +1은 /가 한 문자를 차지하며, 왼쪽 닫힘 오른쪽 열림으로 절단
        aliyunOssClient.deleteObject(bucketName, fileName);
        return true;
    }
}
