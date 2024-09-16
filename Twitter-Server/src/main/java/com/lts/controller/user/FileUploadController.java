package com.lts.controller.user;

import com.lts.result.Result;
import com.lts.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Slf4j
public class FileUploadController {

    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/photo")
    public Result<List<String>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<byte[]> filesContent = new ArrayList<>();
        List<String> objectNames = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String objectName = UUID.randomUUID().toString() + extension;
                objectNames.add(objectName);
                filesContent.add(file.getBytes());
            }
            List<String> urls = aliOssUtil.upload(filesContent, objectNames);


            // 调用服务处理文件上传
            return Result.success(urls);

        } catch (IOException e) {
            // 异常处理逻辑
            throw new RuntimeException("读取文件内容失败", e);
        }
    }

   @PostMapping("/single")
    public Result<String> uploadFiles(@RequestParam("file") MultipartFile file) {
        try {
                String originalFilename = file.getOriginalFilename();
                String objectName = UUID.randomUUID().toString()+originalFilename;
                String url = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(url);
        } catch (IOException e) {
            // 异常处理逻辑
            throw new RuntimeException("读取文件内容失败", e);
        }
    }
    }