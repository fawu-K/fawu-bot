package com.kang.commons.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;


/**
 * @program: fawukang
 * @description: 七牛云上传图片
 * @author: K.faWu
 * @create: 2021-10-25 10:58
 **/
@Component
@PropertySource(value = "classpath:application.yml")
@ConfigurationProperties(prefix = "qiniuyun")
@Slf4j
public class QiniuUtil {

    /**
     *  设置需要操作的账号的AK和SK
     */
    @Value("${accessKey}")
    private String accessKey;
    @Value("${secretKey}")
    private String secretKey;
    /**
     *  要上传的空间
     */
    @Value("${bucket}")
    private String bucket;
    /**
     *  外链地址
     */
    @Value("${domain}")
    private String domain;

    /**
     * 上传文件并且返回文件地址
     *
     * @param inputStream 文件
     */
    public String setUploadManager(InputStream inputStream) {
        //设置密钥、文件连接、文件名等等属性
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.huanan());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //设置连接地址
        Auth auth = Auth.create(accessKey, secretKey);
        String prefix = "";
        int guid = 100;
        try {
            String s = auth.uploadToken(bucket);
            //生成文件名
            String s1 = UUID.randomUUID().toString().replaceAll("-","");
            //实现文件上传
            Response response = uploadManager.put(inputStream, s1, s, null, null);
            //解析上传成功结果
            DefaultPutRet defaultPutRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info("文件外链地址：" + domain + defaultPutRet.key);
            log.info("defaultPutRet.hash = " + defaultPutRet.hash);
            return domain + defaultPutRet.key;
        } catch (QiniuException e) {
            log.error("e.getMessage() = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除图片
     */
    public void deleteFile(String url){
        //创建凭证
        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, new Configuration());
        String key = url.substring(url.lastIndexOf("/")+1);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }
}

