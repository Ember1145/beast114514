package com.lts.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;


        public List<String> upload(List<byte[]> files, List<String> objectNames) {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            List<String> urls = new ArrayList<>();

            try {
                for (int i = 0; i < files.size(); i++) {
                    byte[] bytes = files.get(i);
                    String objectName = objectNames.get(i);

                    // 创建PutObject请求。
                    ossClient.putObject(new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(bytes)));

                    // 构建访问的URL，假设Bucket是公共的。私有的需要生成签名URL。
                    String fileUrl = "https://" + bucketName + "." + endpoint + "/" + objectName;
                    urls.add(fileUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 处理异常，适当记录日志或抛出自定义异常，可根据业务需求定制
            } finally {
                // 关闭OSSClient。
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }

            return urls;
        }

    }
