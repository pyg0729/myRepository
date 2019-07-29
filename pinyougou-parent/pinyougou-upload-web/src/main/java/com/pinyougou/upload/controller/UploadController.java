package com.pinyougou.upload.controller;
/*
 *
 *    @苑帅
 *    @时间为：2019-06-28-20-23
 *
 */

import com.pinyougou.common.util.FastDFSClient;
import entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    /**
     * 发送上传的路径的请求：/uploadFile
     * 参数：文件对象本身
     * 返回值：页面需要图片的路径回显 result:{boolean Strig message}
     */
    @RequestMapping("/uploadFile")
    @CrossOrigin(origins = {"http://localhost:9102","http://localhost:9101"},allowCredentials = "true")
    public Result uploadFile(MultipartFile file) {
        try {
            //获取原上传文件的扩展名
            String originalFilename = file.getOriginalFilename();
            //截取文件名后缀              lastindexof 从最后一个出现的位置开始截取    +1  表示不包括
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //获取原文件的字节数组
            byte[] bytes = file.getBytes();
            //调用fastdfsclient的api  上传图片

            FastDFSClient fastDFSClient = new FastDFSClient("classpath:/config/fastdfs_client.conf");
            //上传完成后的图片途径
            String path = fastDFSClient.uploadFile(bytes, extName);
            //拼接真实路径
            String realPath = "http://192.168.25.129/"+path;

            //返回result  带有图片的路径
            return new Result(true,realPath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"文件上传失败！");
        }
    }
}
