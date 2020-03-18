package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {
    //定义一个符合图片格式用来校验的集合
    private static final List<String> CONTENT_TYPES= Arrays.asList("image/jpeg", "image/gif");
    //输出日志
    private static final Logger LOGGER= LoggerFactory.getLogger(UploadService.class);

    @Autowired
    private FastFileStorageClient storageClient;
    public String uploadImage(MultipartFile file)  {
        //获取用户上传的文件的content_type类型
        String originalFilename = file.getOriginalFilename();

        String contentType = file.getContentType();
        //判断是否符合标准
        if(!CONTENT_TYPES.contains(contentType)){
            LOGGER.info("文件类型不合法"+originalFilename);
            return null;
        }
        //校验文件的内容
        try {
            BufferedImage bufferedImage  = ImageIO.read(file.getInputStream());
            if (bufferedImage ==null){
                LOGGER.info("文件内容不合法"+originalFilename);
                return null;
            }
            //保存到服务器
            //1. file.transferTo(new File("D:\\leyou\\images\\" + originalFilename));

            //获取图片的后缀名
            String last = StringUtils.substringAfterLast(originalFilename, ".");
            //保存到服务器
            StorePath storePath = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), last, null);

            //返回路径
            //1. return  "http://image.leyou.com/" + originalFilename;
            //返回路径
            return  "http://image.leyou.com/"+ storePath.getFullPath();
    }

         catch (IOException e) {
             LOGGER.info("服务器内部错误：{}", originalFilename);
             e.printStackTrace();
        }
        return null;
    }
}
