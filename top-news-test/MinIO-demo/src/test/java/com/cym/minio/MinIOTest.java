package com.cym.minio;

import com.google.errorprone.annotations.Var;
import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {

    @Autowired
    private FileStorageService fileStorageService;

    @Test
    public void testUpload() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("D:\\\\Code\\\\Java\\\\heima-leadnews\\\\list.html");
        String path = fileStorageService.uploadHtmlFile("", "list.html", fileInputStream);
        System.out.println(path);
    }

    // @Test
    // public void testUpload() throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
    //     // 1.获取客户端
    //     MinioClient minioClient = MinioClient.builder().credentials("admin", "admin123").endpoint("http://101.126.76.60:9001").build();
    //
    //     // 2.上传
    //     FileInputStream fileInputStream = new FileInputStream("D:\\Code\\Java\\heima-leadnews\\list.html");
    //     PutObjectArgs putObjectArgs = PutObjectArgs.builder()
    //             .object("list.html") // 文件名称
    //             .contentType("text/html") // 文件类型
    //             .bucket("test") // 桶名称
    //             .stream(fileInputStream, fileInputStream.available(), -1).build();
    //     minioClient.putObject(putObjectArgs);
    //     // 访问路径
    //     System.out.println("http://101.126.76.60:9000/test/list.html");
    //
    // }
}
