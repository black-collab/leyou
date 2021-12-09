package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg", "image/gif","image/png");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    @Resource
    private FastFileStorageClient storageClient;

    public String uploadImage(MultipartFile file) {
        String name = file.getOriginalFilename();
        //验证文件类型
        try {
            String contentType = file.getContentType();
            if(!CONTENT_TYPES.contains(contentType)){
                LOGGER.info("文件类型不合法: {}",name);
                return null;
            }
            //验证文件是否是图片
            BufferedImage read = ImageIO.read(file.getInputStream());
            if(read == null){
                LOGGER.info("文件内容不合法: {}",name);
                return null;
            }
            //保存到服务器
            String suffix = StringUtils.substringAfterLast(name, ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, null);
            //返回url
            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            LOGGER.info("服务器内部错误: {}",name);
            e.printStackTrace();
        }
        return null;
    }
}
