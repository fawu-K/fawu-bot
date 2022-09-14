package com.kang.web.service;

import com.kang.entity.Msg;
import com.kang.entity.ResultByQq;
import love.forte.simbot.api.message.MessageContent;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: service
 * @description: 处理接收到的消息
 * @author: K.faWu
 * @create: 2022-07-19 15:28
 **/

public interface ResultByQqService {

    /**
     * 将QQ发送过来的消息转换成前端展示需要的消息体
     *
     * @param resultByQq 前端需要的消息体
     * @param msgContent QQ发送过来的消息体
     */
    void msgToResultByQq(ResultByQq<Msg> resultByQq, MessageContent msgContent);

    /**
     * 生成可以发送的信息体
     *
     * @param resultByQq 发送过来的消息体
     * @return 构造成能够发送的消息体
     */
    MessageContent resultByQqToMsg(ResultByQq<?> resultByQq);

    String uploadPhoto(MultipartFile file);
}
