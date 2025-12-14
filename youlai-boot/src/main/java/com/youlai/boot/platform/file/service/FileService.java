package com.youlai.boot.platform.file.service;

import com.youlai.boot.platform.file.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 객체存储서비스인터페이스层
 *
 * @author haoxr
 * @since 2022/11/19
 */
public interface FileService {

    /**
     * 업로드파일
     * @param file 폼 파일 객체
     * @return 파일信息
     */
    FileInfo uploadFile(MultipartFile file);

    /**
     * 삭제파일
     *
     * @param filePath 파일完整URL
     * @return 삭제결과
     */
    boolean deleteFile(String filePath);


}
