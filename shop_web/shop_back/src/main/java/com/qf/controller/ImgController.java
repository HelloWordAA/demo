package com.qf.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@Controller
@RequestMapping("/imgs")
public class ImgController {
    //相当于fastDFS的文件存储客户端
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    //本地路径,本地上传
//    private static final String UPLOADER_PATH = "C:\\worker\\imgs\\";
    @RequestMapping("/uploader")
    @ResponseBody
    public String uploaderImg(MultipartFile file){
        //获取最后一个.的下标
        int index = file.getOriginalFilename().lastIndexOf(".");
        //获得文件后缀
        String suffix = file.getOriginalFilename().substring(index+1);

        try {

            //上传图片并创建缩略图    （ ，文件大小，后缀，元数据）
            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), suffix, null);
            //获取上传到FastDFS中的图片访问路径
            String storeUrl = storePath.getFullPath();

            return "{\"uploadPath\":\""+storeUrl+"\"}";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        /*传到本地*/
//        try {
//            InputStream in = file.getInputStream();
//            OutputStream out  = new FileOutputStream(UPLOADER_PATH+ UUID.randomUUID().toString());
//            IOUtils.copy(in,out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "success";
    }
}
