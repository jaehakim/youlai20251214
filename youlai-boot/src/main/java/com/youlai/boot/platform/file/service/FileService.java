package com.youlai.boot.platform.file.service;

import com.youlai.boot.platform.file.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 객체 스토리지 서비스 인터페이스
 *
 * @author haoxr
 * @since 2022/11/19
 */
public interface FileService {

    /**
     * 파일 업로드
     * @param file 폼 파일 객체
     * @return 파일 정보
     */
    FileInfo uploadFile(MultipartFile file);

    /**
     * 파일 삭제
     *
     * @param filePath 파일 전체 URL
     * @return 삭제 결과
     */
    boolean deleteFile(String filePath);


}
