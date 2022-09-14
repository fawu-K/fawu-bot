package com.kang.web.service.impl;

import catcode.Neko;
import com.kang.Constants;
import com.kang.commons.Result;
import com.kang.commons.util.HtmlUtil;
import com.kang.commons.util.QiniuUtil;
import com.kang.entity.Msg;
import com.kang.entity.ResultByQq;
import com.kang.web.service.ResultByQqService;
import lombok.extern.slf4j.Slf4j;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import love.forte.simbot.bot.BotManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: service
 * @description:
 * @author: K.faWu
 * @create: 2022-07-19 15:28
 **/
@Service
@Slf4j
public class ResultByQqServiceImpl implements ResultByQqService {


    @Autowired
    private MessageContentBuilderFactory messageContentBuilderFactory;
    @Autowired
    private QiniuUtil qiniuUtil;
    @Depend
    @Autowired
    private BotManager botManager;


    @Override
    public void msgToResultByQq(ResultByQq<Msg> resultByQq, MessageContent msgContent) {
        List<Msg> msgs = new ArrayList<>();

        List<Neko> cats = msgContent.getCats();
        for (Neko cat : cats) {
            Msg msg1 = new Msg();
            msg1.setType(cat.getType());
            msg1.setId(cat.get("id"));
            msg1.setText(cat.get("text"));
            msg1.setWidth(cat.get("width"));
            msg1.setHeight(cat.get("height"));
            msg1.setImageType(cat.get("imageType"));
            msg1.setIsEmoji(cat.get("isEmoji"));
            if ("network".equals(cat.get("type"))) {
                msg1.setUrl(cat.get(Constants.FILE));
            } else {
                msg1.setUrl(cat.get(Constants.URL));
            }
            if (Constants.AT.equals(cat.getType())){
                cat.getValues().forEach(msg1::setAt);
                String at = msg1.getAt();
                GroupMemberInfo memberInfo = botManager.getDefaultBot().getSender().GETTER.getMemberInfo(resultByQq.getGroupCode(), at);
                msg1.setText("@" + memberInfo.getAccountRemarkOrNickname());
            }
            msgs.add(msg1);
        }
        resultByQq.setMsg(msgs);
    }

    /**
     * 生成可以发送的信息体
     *
     * @param resultByQq 发送过来的消息体
     * @return 构造成能够发送的消息体
     */
    @Override
    public MessageContent resultByQqToMsg(ResultByQq<?> resultByQq) {

        MessageContentBuilder messageContentBuilder = messageContentBuilderFactory.getMessageContentBuilder();

        String text = resultByQq.getText();
        HtmlUtil.Html2Java html2Java = new HtmlUtil().toPlainText(text);
        String msg = html2Java.getText();
        List<String> imgs = html2Java.getImgs();
        List<String> ats = html2Java.getAts();

        //统计当前已经处理到的at次序
        int atCount = 0;
        //使用【/img】进行占位，
        String[] split = msg.split(Constants.MSG_IMG);
        if (split.length == 0){
            msgToImageOrFace(messageContentBuilder, imgs.get(0));
        }else {
            for (int i = 0; i < split.length; i++) {
                String splitStr = split[i].trim();
                if (ats != null && ats.size() > 0) {
                    //处理是否有at
                    atCount = msgToAt(messageContentBuilder, resultByQq, ats, atCount, splitStr);
                } else {
                    messageContentBuilder.text(splitStr);
                }
                //处理信息中的图片
                if (imgs.size() > 0) {
                    msgToImageOrFace(messageContentBuilder, imgs.get(i));
                }
            }
        }
        return messageContentBuilder.build();
    }

    /**
     * 处理信息中发送的图片
     *
     * @param messageContentBuilder 消息构造器
     * @param url                   图片的地址
     */
    private void msgToImageOrFace(MessageContentBuilder messageContentBuilder, String url) {
        if (url.startsWith(Constants.EMOJI_URL)) {
            //表示这是表情
            String id = url.replace(Constants.EMOJI_URL, "")
                    .replace(Constants.PNG_SUFFIX, "")
                    .replace(Constants.GIF_SUFFIX, "");
            messageContentBuilder.face(id);
        } else {
            //非表情即图片
            messageContentBuilder.image(url);
        }
    }

    /**
     * 对发送的信息中是否有at群成员的操作进行处理
     *
     * @param messageContentBuilder 消息构造器
     * @param resultByQq            消息体
     * @param ats                   被at的群成员列表
     * @param atCount               当前已经处理到的次序
     * @param splitStr              要处理的消息段
     * @return 此次处理后的达到的@次序
     */
    private int msgToAt(MessageContentBuilder messageContentBuilder, ResultByQq<?> resultByQq, List<String> ats, int atCount, String splitStr) {
        String[] atsplit = splitStr.split(Constants.MSG_AT);
        for (String at : atsplit) {
            String atCode = ats.get(atCount++);
            GroupMemberInfo memberInfo = botManager.getBot(resultByQq.getAccountCode()).getSender().GETTER.getMemberInfo(resultByQq.getGroupCode(), atCode);
            at = at.replace("@" + memberInfo.getAccountNickname(), "");
            messageContentBuilder.text(at).at(atCode);
        }
        return atCount;
    }

    @Override
    public String uploadPhoto(MultipartFile file) {
        //文件名
        //获取文件名
        String originalFilename = file.getOriginalFilename();
        //获取后缀名
        int i = originalFilename.lastIndexOf(".");
        String endName = originalFilename.substring(i);
        endName = endName.toLowerCase();
        //判断是否是指定类
        List<String> fileType = Arrays.asList(".gif", ".jpg", ".jfif", ".ico", ".png", ".bmp", ".svg");
        if (!fileType.contains(endName)) {
            Result.error("文件类型错误");
        }
        try {
            String path = qiniuUtil.setUploadManager(file.getInputStream());
            if (path == null) {
                Result.error("输入的字符不能为空！");
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            Result.error("文件上传失败");
        }
        return null;
    }
}
