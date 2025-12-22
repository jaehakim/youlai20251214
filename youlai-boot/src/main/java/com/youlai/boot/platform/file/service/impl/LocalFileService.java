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
        //  try-with-resource 문법으로 스트림 자동 해제
        try (InputStream inputStream = file.getInputStream()) {
            // 파일 업로드
            FileUtil.writeFromStream(inputStream, filePrefix + folder + File.separator + fileName);
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 실패");
        }
        // 파일 접근 경로 조회, 여기는 로컬 저장이므로 파일의 상대 경로를 직접 반환하며 프론트엔드에서 접근 접두사를 직접 처리해야 함
        String fileUrl = File.separator + folder + File.separator + fileName;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(originalFilename);
        fileInfo.setUrl(fileUrl);
        return fileInfo;
    }


    /**
     * 파일 삭제
     * @param filePath 파일 전체 URL
     * @return 삭제 성공 여부
     */
    @Override
    public boolean deleteFile(String filePath) {
        // 파일이 비어있는지 판단
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        // filepath가 폴더인지 판단
        if (FileUtil.isDirectory(storagePath + filePath)) {
            // 폴더 삭제 금지
            return false;
        }
        // 파일 삭제
        return FileUtil.del(storagePath + filePath);
    }
}
