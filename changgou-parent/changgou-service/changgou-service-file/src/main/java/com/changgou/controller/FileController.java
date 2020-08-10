package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/upload")
public class FileController {

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping
    public String upload(@RequestParam("file") MultipartFile file){
        try {
            //文件上传的实体对象
            FastDFSFile fastDFSFile = new FastDFSFile(
                    file.getOriginalFilename(), //文件的名字
                    file.getBytes(),//文件的内容
                    StringUtils.getFilenameExtension(file.getOriginalFilename()),//获取文件的拓展名
                    null,
                    null);
            //执行文件上传
            String[] upload = FastDFSClient.upload(fastDFSFile);
            String url = "http://images-changgou-java.itheima.net/" + upload[0] + "/" + upload[1];
            return url;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //在前端页面用ajax请求一下图片上传的接口,获取到图片的地址

    //将图片的地址保存到相册表中
}
