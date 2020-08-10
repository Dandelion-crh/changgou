package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;


/**
 * 实现文件的操作的所有方法
 */
public class FastDFSClient {


    /**
     * 加载tracker配置文件
     * 目的:文件操作的时候,没必要每次都去加载一次tracker配置文件
     */
    static {
        try {
            ClassPathResource classPathResource = new ClassPathResource("fsdf.conf");
            ClientGlobal.init(classPathResource.getPath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 文件的上传
     */
    public static String[] upload(FastDFSFile fastDFSFile){
        //附加参数
        NameValuePair[] nameValuePairs = new NameValuePair[2];

        nameValuePairs[0] = new NameValuePair("location", "深圳宝安黑马训练营");
        nameValuePairs[1] = new NameValuePair("author", fastDFSFile.getAuthor());

        try {
            //获取trackerClient信息:初始化DataSource
            TrackerClient trackerClient = new TrackerClient();

            //获取trackerServer信息:getConnection
            TrackerServer trackerServer = trackerClient.getConnection();

            //获取strorageClient的信息:tracker获取storage信息
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //通过storage执行文件上传
            /**
             * 1.文件的字节码
             * 2.文件的拓展名
             * 3.附加参数
             */
            String[] result = storageClient.upload_file(fastDFSFile.getContent(), fastDFSFile.getExt(), nameValuePairs);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件的信息查询
     */
    public static FileInfo getFileInfo(String groupName, String fileRemoteName){
        try {
            //获取trackerClient信息:初始化DataSource
            TrackerClient trackerClient = new TrackerClient();

            //获取trackerServer信息:getConnection
            TrackerServer trackerServer = trackerClient.getConnection();

            //获取strorageClient的信息:tracker获取storage信息
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //的获取文件信息
            FileInfo file_info = storageClient.get_file_info(groupName, fileRemoteName);
            return file_info;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 文件的下载
     */
    public static InputStream downLoad(String groupName, String fileRemoteName){
        try {
            //获取trackerClient信息:初始化DataSource
            TrackerClient trackerClient = new TrackerClient();

            //获取trackerServer信息:getConnection
            TrackerServer trackerServer = trackerClient.getConnection();

            //获取strorageClient的信息:tracker获取storage信息
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //文件下载
            byte[] bytes = storageClient.download_file(groupName, fileRemoteName);

            return new ByteArrayInputStream(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件删除
     */
    public static Integer delete(String groupName, String fileRemoteName){
        try {
            //获取trackerClient信息:初始化DataSource
            TrackerClient trackerClient = new TrackerClient();

            //获取trackerServer信息:getConnection
            TrackerServer trackerServer = trackerClient.getConnection();

            //获取strorageClient的信息:tracker获取storage信息
            StorageClient storageClient = new StorageClient(trackerServer, null);

            //文件删:输出会是0删除成功 <0删除失败
            int i = storageClient.delete_file(groupName, fileRemoteName);
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        //测试获取文件的信息
//        FileInfo fileInfo = getFileInfo("group1", "M00/00/00/wKjThF7PNxaAeRvSAAQEGIy6oJ4894.jpg");
////        System.out.println(fileInfo);

        //文件下载测试
        //定义一个输出流
//        FileOutputStream outputStream = new FileOutputStream("D:/test/11231312.jpg");
//        InputStream inputStream = downLoad("group1", "M00/00/00/wKjThF7PNxaAeRvSAAQEGIy6oJ4894.jpg");
//
//        //定义一个缓冲区
//        byte[] buffer = new byte[1024];
//        //读取文件写文件
//        while (inputStream.read(buffer) != -1){
//            outputStream.write(buffer);
//        }
//        //释放资源
//        inputStream.close();
//        outputStream.close();
        //        //输出会是0删除成功 <0删除失败
        delete("group1","M00/00/00/wKjThF7PNxaAeRvSAAQEGIy6oJ4894.jpg");


    }
}
