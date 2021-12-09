package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Controller
@RequestMapping("upload")
public class UploadController {

    @Resource
    private UploadService uploadService;

    @PostMapping("image")
    public ResponseEntity<String> uploadImage(@RequestParam(value = "file",required = false) MultipartFile file){
        String url = uploadService.uploadImage(file);
        if(StringUtils.isEmpty(url)){
            return ResponseEntity.badRequest().body("上传失败");
        }
        return ResponseEntity.ok(url);
    }
}
