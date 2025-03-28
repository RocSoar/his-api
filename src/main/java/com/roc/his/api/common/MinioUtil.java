package com.roc.his.api.common;

import com.roc.his.api.exception.HisException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class MinioUtil {
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = new MinioClient.Builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadImage(String path, MultipartFile file) {
        try {
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(file.getInputStream(), -1, 5 * 1024 * 1024)
                            .contentType("image/jpeg")
                            .build());
            String fullPath = this.endpoint + "/" + this.bucket + "/" + path;
            log.debug("图片保存成功, 地址: {}", fullPath);
            return path;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("保存图片失败: {}", e.getMessage());
            throw new HisException("保存图片失败");
        }
    }

    public String uploadExcel(String path, MultipartFile file) {
        try {
            //Excel文件的MIME类型
            String mime = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            //Excel文件不能超过20M
            this.minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket).object(path)
                    .stream(file.getInputStream(), -1, 20 * 1024 * 1024)
                    .contentType(mime).build());
            log.debug("向" + path + "保存了excel文件");
            return path;
        } catch (Exception e) {
            log.error("保存excel文件失败", e);
            throw new HisException("保存excel文件失败");
        }
    }

    public InputStream downloadFile(String path) {
        try {
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build();
            return minioClient.getObject(args);
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new HisException("文件下载失败");
        }
    }

    public void deleteFile(String path) {
        try {
            this.minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(path)
                    .build());
            log.info("删除了" + path + "路径下的文件");
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new HisException("文件删除失败");
        }
    }
}
