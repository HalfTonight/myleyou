package com.leyou.upload.test;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FastDFSTest {

    @Autowired
    //上传图片用到
    private FastFileStorageClient storageClient;

    @Autowired
    //缩略图用到
    private ThumbImageConfig thumbImageConfig;
    @Test
    public void testUpload()throws FileNotFoundException{

        File file=new File("G:\\毕业照\\2.JPG");
        StorePath storePath = this.storageClient.uploadFile(
                new FileInputStream(file), file.length(), "JPG", null
        );
        System.out.println(storePath.getFullPath());
        System.out.println(storePath.getPath());
    }

    @Test
    public void testUploadAndCreateThumb()throws FileNotFoundException{
        File file=new File("G:\\毕业照\\2.JPG");
        StorePath jpg = this.storageClient.uploadImageAndCrtThumbImage(new FileInputStream(file), file.length(), "JPG", null);
        System.out.println(jpg.getPath());
        System.out.println(jpg.getFullPath());
        String path = thumbImageConfig.getThumbImagePath(jpg.getPath());
        System.out.println(path);

    }

}
