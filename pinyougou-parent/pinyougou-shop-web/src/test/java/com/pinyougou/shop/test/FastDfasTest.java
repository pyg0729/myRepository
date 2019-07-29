package com.pinyougou.shop.test;
/*
 *
 *    @苑帅
 *    @时间为：2019-06-28-10-46
 *
 */

import com.pinyougou.common.util.FastDFSClient;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

public class FastDfasTest {

    /**
     * 使用fastdfs完成图片上传
     * @throws Exception
     */
    @Test
    public void uploadFile() throws Exception {
        //创建一个配置文件     配置服务器的地址和端口  fastDfs client，conf

        //加载配置文件
        ClientGlobal.init("E:\\Java60\\MyPinyougou\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        //创建trackerclient对象
        TrackerClient trackerClient = new TrackerClient();
        //创建trackerServer对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //创建stroageServer  设置 null
        StorageServer storageServer = null;

        //创建 stroageClient 使用该client的api上传文件即可
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //参数一  表示文件的路径
        //参数二  表示文件的扩展名
        //参数三  表示文件的元数据
        String[] jpgs = storageClient.upload_file("C:\\Users\\yuans\\Pictures\\Camera Roll\\图片\\802d6066b33f9bfa0e9abe7067c79280.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }

    @Test
    public void uploadClientTest()throws Exception {
        FastDFSClient fastDFSClient = new FastDFSClient("E:\\Java60\\MyPinyougou\\pinyougou-parent\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        String jpg = fastDFSClient.uploadFile("C:\\Users\\yuans\\Pictures\\Camera Roll\\图片\\802d6066b33f9bfa0e9abe7067c79280.jpg", "jpg");

        System.out.println(jpg);
    }


}
