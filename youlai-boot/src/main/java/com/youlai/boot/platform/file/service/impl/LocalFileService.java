package com.youlai.boot.platform.file.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.youlai.boot.platform.file.model.FileInfo;
import com.youlai.boot.platform.file.service.FileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * 로컬 저장 서비스 클래스
 *
 * @author Theo
 * @since 2024-12-09 17:11
 */
@Data
@Slf4j
@Component
@ConditionalOnProperty(value = "oss.type", havingValue = "local")
@ConfigurationProperties(prefix = "oss.local")
@RequiredArgsConstructor
public class LocalFileService implements FileService {

    @Value("${oss.local.storage-path}")
    private String storagePath;

    /**
     * 파일 업로드 메서드
     *
     * @param file 폼 파일 객체
     * @return 파일 정보
     */
    @Override
    public FileInfo uploadFile(MultipartFile file) {
        // 파일 이름 조회
        String originalFilename = file.getOriginalFilename();
        // 파일 확장자 조회
        String suffix = FileUtil.getSuffix(originalFilename);
        // uuid 생성
        String fileName = IdUtil.simpleUUID()+ "." + suffix;;
        // 파일 이름 생성(날짜 폴더)
        String folder = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN);
        String filePrefix = storagePath.endsWith(File.separator) ? storagePath : storagePath + File.separator;
        String filePath = filePrefix + folder + File.separator + fileName;

        //  try-with-resource 문법으로 스트림 자동 해제
        try (InputStream inputStream = file.getInputStream()) {
            // 디렉토리 없으면 자동 생성
            FileUtil.mkParentDirs(new File(filePath));
            // 파일 업로드
            FileUtil.writeFromStream(inputStream, filePath);
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 실패");
        }
        // 파일 접근 경로 구성: /upload/{folder}/{fileName}
        // 예: /upload/20250105/abc123def456.jpg
        String fileUrl = "/upload/" + folder + "/" + fileName;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(originalFilename);
        fileInfo.setUrl(fileUrl);
        return fileInfo;
    }


    /**
     * 파일 삭제
     * @param filePath 파일 전체 URL (예: /upload/20250105/abc123.jpg 또는 /20250105/abc123.jpg)
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteFile(String filePath) {
        // 파일이 비어있는지 판단
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        // /upload/ 접두사 제거
        String relativePath = filePath;
        if (relativePath.startsWith("/upload/")) {
            relativePath = relativePath.substring("/upload".length());
        }

        // filepath가 폴더인지 판단
        if (FileUtil.isDirectory(storagePath + relativePath)) {
            // 폴더 삭제 금지
            return false;
        }
        // 파일 삭제
        return FileUtil.del(storagePath + relativePath);
    }
}
