package com.lts.controller.user;

import com.lts.result.Result;
import com.lts.utils.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class FileUploadController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/uploadMultipleFiles")
    public List<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        List<byte[]> filesContent = new ArrayList<>();
        List<String> objectNames = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                String objectName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
                objectNames.add(objectName);
                filesContent.add(file.getBytes());
            }

            // 调用服务处理文件上传
            return aliOssUtil.upload(filesContent, objectNames);

        } catch (IOException e) {
            // 异常处理逻辑
            throw new RuntimeException("读取文件内容失败", e);
        }
    }
//
//
    }