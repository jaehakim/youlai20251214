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
 * MinIO 파일 업로드서비스类
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
     * 서비스Endpoint
     */
    private String endpoint;
    /**
     * 접근凭据
     */
    private String accessKey;
    /**
     * 凭据密钥
     */
    private String secretKey;
    /**
     * 存储桶이름
     */
    private String bucketName;
    /**
     * 自定义域名
     */
    private String customDomain;

    private MinioClient minioClient;

    // 依赖注入完成之후执行初始化
    @PostConstruct
    public void init() {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        // 생성存储桶(存储桶不存에)
        // createBucketIfAbsent(bucketName);
    }


    /**
     * 업로드파일
     *
     * @param file 폼 파일 객체
     * @return 파일信息
     */
    @Override
    public FileInfo uploadFile(MultipartFile file) {

        // 생성存储桶(存储桶不存에)，如果有搭建好의minio서비스，建议放에init方法중
        createBucketIfAbsent(bucketName);

        // 파일原生이름
        String originalFilename = file.getOriginalFilename();
        // 파일후缀
        String suffix = FileUtil.getSuffix(originalFilename);
        // 파일夹이름
        String dateFolder = DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
        // 파일이름
        String fileName = IdUtil.simpleUUID() + "." + suffix;

        //  try-with-resource 语法糖自动释放流
        try (InputStream inputStream = file.getInputStream()) {
            // 파일 업로드
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(dateFolder + "/"+ fileName)
                    .contentType(file.getContentType())
                    .stream(inputStream, inputStream.available(), -1)
                    .build();
            minioClient.putObject(putObjectArgs);

            // 返回파일경로
            String fileUrl;
            // 미설정自定义域名
            if (StrUtil.isBlank(customDomain)) {
                // 조회파일URL
                GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(dateFolder + "/"+ fileName)
                        .method(Method.GET)
                        .build();

                fileUrl = minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
                fileUrl = fileUrl.substring(0, fileUrl.indexOf("?"));
            } else {
                // 설정自定义파일경로域名
                fileUrl = customDomain + "/"+ bucketName + "/"+ dateFolder + "/"+ fileName;
            }

            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(originalFilename);
            fileInfo.setUrl(fileUrl);
            return fileInfo;
        } catch (Exception e) {
            log.error("업로드파일실패", e);
            throw new BusinessException(ResultCode.UPLOAD_FILE_EXCEPTION, e.getMessage());
        }
    }


    /**
     * 삭제파일
     *
     * @param filePath 파일完整경로
     * @return 여부삭제성공
     */
    @Override
    public boolean deleteFile(String filePath) {
        Assert.notBlank(filePath, "삭제파일경로不能값空");
        try {
            String fileName;
            if (StrUtil.isNotBlank(customDomain)) {
                // https://oss.youlai.tech/default/20221120/test.jpg → 20221120/websocket.jpg
                fileName = filePath.substring(customDomain.length() + 1 + bucketName.length() + 1); // 两个/占2个字符长度
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
            log.error("삭제파일실패", e);
            throw new BusinessException(ResultCode.DELETE_FILE_EXCEPTION, e.getMessage());
        }
    }


    /**
     * PUBLIC桶策略
     * 如果不설정，则새建의存储桶默认是PRIVATE，则存储桶파일会拒绝접근 Access Denied
     *
     * @param bucketName 存储桶이름
     * @return 存储桶策略
     */
    private static String publicBucketPolicy(String bucketName) {
        // AWS의S3存储桶策略 JSON 格式 https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/example-bucket-policies.html
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
     * 생성存储桶(存储桶不存에)
     *
     * @param bucketName 存储桶이름
     */
    @SneakyThrows
    private void createBucketIfAbsent(String bucketName) {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
        if (!minioClient.bucketExists(bucketExistsArgs)) {
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();

            minioClient.makeBucket(makeBucketArgs);

            // 设置存储桶접근권한값PUBLIC， 如果不설정，则새建의存储桶默认是PRIVATE，则存储桶파일会拒绝접근 Access Denied
            SetBucketPolicyArgs setBucketPolicyArgs = SetBucketPolicyArgs
                    .builder()
                    .bucket(bucketName)
                    .config(publicBucketPolicy(bucketName))
                    .build();
            minioClient.setBucketPolicy(setBucketPolicyArgs);
        }
    }
}
