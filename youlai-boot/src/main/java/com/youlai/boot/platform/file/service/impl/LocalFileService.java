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
 * 로컬 저장서비스类
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
     * 업로드파일方法
     *
     * @param file 폼 파일 객체
     * @return 파일信息
     */
    @Override
    public FileInfo uploadFile(MultipartFile file) {
        // 조회파일名
        String originalFilename = file.getOriginalFilename();
        // 조회파일후缀
        String suffix = FileUtil.getSuffix(originalFilename);
        // 생성uuid
        String fileName = IdUtil.simpleUUID()+ "." + suffix;;
        // 생성파일名(日期파일夹)
        String folder = DateUtil.format(LocalDateTime.now(), DatePattern.PURE_DATE_PATTERN);
        String filePrefix = storagePath.endsWith(File.separator) ? storagePath : storagePath + File.separator;
        //  try-with-resource 语法糖自动释放流
        try (InputStream inputStream = file.getInputStream()) {
            // 업로드파일
            FileUtil.writeFromStream(inputStream, filePrefix + folder + File.separator + fileName);
        } catch (Exception e) {
            log.error("파일 업로드실패", e);
            throw new RuntimeException("파일 업로드실패");
        }
        // 조회파일접근경로，因값这里是로컬 저장，所는直接返回파일의相对경로，需要前端自行处理접근前缀
        String fileUrl = File.separator + folder + File.separator + fileName;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(originalFilename);
        fileInfo.setUrl(fileUrl);
        return fileInfo;
    }


    /**
     * 삭제파일
     * @param filePath 파일完整URL
     * @return 여부삭제성공
     */
    @Override
    public boolean deleteFile(String filePath) {
        //判断파일여부값空
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        // 判断filepath여부값파일夹
        if (FileUtil.isDirectory(storagePath + filePath)) {
            // 禁止삭제파일夹
            return false;
        }
        // 삭제파일
        return FileUtil.del(storagePath + filePath);
    }
}
