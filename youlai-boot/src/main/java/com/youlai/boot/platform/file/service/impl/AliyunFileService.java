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
 * Aliyun 객체存储서비스类
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
     * 서비스Endpoint
     */
    private String endpoint;
    /**
     * 접근凭据
     */
    private String accessKeyId;
    /**
     * 凭据密钥
     */
    private String accessKeySecret;
    /**
     * 存储桶이름
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

        // 조회파일이름
        String originalFilename = file.getOriginalFilename();
        // 생성파일名(日期파일夹)
        String suffix = FileUtil.getSuffix(originalFilename);
        String uuid = IdUtil.simpleUUID();
        String fileName = DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + uuid + "." + suffix;
        //  try-with-resource 语法糖自动释放流
        try (InputStream inputStream = file.getInputStream()) {

            // 设置업로드파일의元信息，例如Content-Type
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            // 생성PutObjectRequest객체，지정된Bucket이름、객체이름和输入流
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, metadata);
            // 업로드파일
            aliyunOssClient.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드실패");
        }
        // 조회파일접근경로
        String fileUrl = "https://" + bucketName + "." + endpoint + "/" + fileName;
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(originalFilename);
        fileInfo.setUrl(fileUrl);
        return fileInfo;
    }

    @Override
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "삭제파일경로不能값空");
        String fileHost = "https://" + bucketName + "." + endpoint; // 파일主机域名
        String fileName = filePath.substring(fileHost.length() + 1); // +1 是/占원个字符，截断左闭右开
        aliyunOssClient.deleteObject(bucketName, fileName);
        return true;
    }
}
