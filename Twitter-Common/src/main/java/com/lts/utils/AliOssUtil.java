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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            List<String> urls = new ArrayList<>();
            try {
                for (int i = 0; i < files.size(); i++) {
                    byte[] bytes = files.get(i);
                    String objectName = objectNames.get(i);
                    ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
                    // 创建PutObject请求。
                    StringBuilder stringBuilder = new StringBuilder("https://");
                    stringBuilder
                            .append(bucketName)
                            .append(".")
                            .append(endpoint)
                            .append("/")
                            .append(objectName);
                    String fileUrl = stringBuilder.toString(); // 将StringBuilder对象转化为String
                    log.info("文件上传到:{}", stringBuilder.toString());
                    urls.add(fileUrl);
                    log.info("目前数组{}",urls);
                }
            } catch (OSSException oe) {
                System.out.println("Caught an OSSException, which means your request made it to OSS, "
                        + "but was rejected with an error response for some reason.");
                System.out.println("Error Message:" + oe.getErrorMessage());
                System.out.println("Error Code:" + oe.getErrorCode());
                System.out.println("Request ID:" + oe.getRequestId());
                System.out.println("Host ID:" + oe.getHostId());
            } catch (ClientException ce) {
                System.out.println("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message:" + ce.getMessage());
                // 处理异常，适当记录日志或抛出自定义异常，可根据业务需求定制
            }
            finally {
                // 关闭OSSClient。
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
            return urls;
        }
    public void deleteFiles(List<String> fileUrls) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            for (String urlString : fileUrls) {
                URL url = new URL(urlString);
                String key = url.getPath().substring(1);
                log.info("position:{}",url.getPath());
                ossClient.deleteObject(bucketName, key);
                log.info("成功删除OSS对象: {}", key);
            }
        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message: {}", oe.getErrorMessage());
            log.error("Error Code: {}", oe.getErrorCode());
            log.error("Request ID: {}", oe.getRequestId());
            log.error("Host ID: {}", oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, indicating that the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: {}", ce.getMessage());
        } catch (Exception e) {
            log.error("Exception encountered: {}", e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    public String upload(byte[] bytes, String objectName) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        StringBuilder stringBuilder = new StringBuilder("https://");
        stringBuilder
                .append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);
        log.info("文件上传到:{}", stringBuilder.toString());
        return stringBuilder.toString();
    }
    public void deleteFile(String fileUrl) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
                URL url = new URL(fileUrl);
                String key = url.getPath().substring(1);
                log.info("position:{}",url.getPath());
                ossClient.deleteObject(bucketName, key);
                log.info("成功删除OSS对象: {}", key);

        } catch (OSSException oe) {
            log.error("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.error("Error Message: {}", oe.getErrorMessage());
            log.error("Error Code: {}", oe.getErrorCode());
            log.error("Request ID: {}", oe.getRequestId());
            log.error("Host ID: {}", oe.getHostId());
        } catch (ClientException ce) {
            log.error("Caught an ClientException, indicating that the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            log.error("Error Message: {}", ce.getMessage());
        } catch (Exception e) {
            log.error("Exception encountered: {}", e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
    }
