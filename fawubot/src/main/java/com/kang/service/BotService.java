package com.kang.service;

import catcode.CatCodeUtil;
import catcode.Neko;
import com.alibaba.fastjson.JSONObject;
import com.kang.Constants;
import com.kang.commons.util.GifUtil;
import com.kang.commons.util.HttpClientUtil;
import com.kang.config.BotConfig;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 服务层
 * @create 2022-09-14 15:54
 **/
@Beans
@Service
public class BotService {

    @Depend
    @Autowired
    private MessageContentBuilderFactory messageContentBuilderFactory;

    /**
     * 群聊中被at后进行的操作
     * @param groupMsg
     * @param sender
     */
    public void atBot(GroupMsg groupMsg, Sender sender){

        String accountCode = groupMsg.getAccountInfo().getAccountCode();
        String text = groupMsg.getText();

        //调用api
        String reply = getTianRobot(text);

        //@ta,并回复
        MessageContentBuilder contentBuilder = messageContentBuilderFactory.getMessageContentBuilder();
        MessageContent content = contentBuilder.at(accountCode).text(reply).build();
        sender.sendGroupMsg(groupMsg, content);
    }

    /**
     * 调用天行数据智障机器人接口
     * @param key
     * @return
     */
    public String getTianRobot(String key) {
        //调用api
        String url = Constants.TIAN_ROBOT;
        url += "?key=" + BotConfig.getTianKey();
        url += "&question=" + key;
        String result = HttpClientUtil.doGet(url);
        JSONObject jsonObject = JSONObject.parseObject(result);
        return (String) ((List<JSONObject>)jsonObject.get("newslist")).get(0).get("reply");
    }

    /**
     * 生成可达鸭动图
     * @param groupMsg
     * @param sender
     * @throws IOException
     */
    public void duck(GroupMsg groupMsg, Sender sender) throws IOException {
        String text = groupMsg.getMsg();
        String[] split = text.split(" ");
        if (split.length >= 3) {
            String path = setDuck(split[1], split[2]);
            //msgBuilder.image()
            MessageContentBuilder contentBuilder = messageContentBuilderFactory.getMessageContentBuilder();
            MessageContent messageContent = contentBuilder.image(path).build();
            sender.sendGroupMsg(groupMsg, messageContent);
        }

    }

    private String setDuck(String leftStr, String rightStr) throws IOException {
        String dirPath = "D:/image/";
        String path = "D:\\image\\res.gif";
        //修改展示的类型
        String left = catToUrl(leftStr);
        boolean leftFlag = left.equals(leftStr);
        String right = catToUrl(rightStr);
        boolean rightFlag = right.equals(rightStr);

        List<BufferedImage> images = new ArrayList<>();
        for (int i = 0 ; i < 17;i++) {
            File outFile = new File(dirPath + i + ".png");
            BufferedImage image = ImageIO.read(outFile);
            switch (i) {
                case 0: draw(image, left,45, 107, leftFlag);break;
                case 1: draw(image, left,41, 95, leftFlag);break;
                case 2: draw(image, left,36, 76, leftFlag);break;
                case 3:
                case 4: draw(image, right,322, 66, rightFlag);break;
                case 5: draw(image, right,326, 79, rightFlag);break;
                case 6: draw(image, right,326, 72, rightFlag);break;
                case 7: draw(image, right,326, 75, rightFlag);break;
                case 8: draw(image, right,331, 75, rightFlag);break;
                case 9: draw(image, right,328, 76, rightFlag);break;
                case 10: draw(image, right,325, 67, rightFlag);break;
                case 11: draw(image, right,328, 70, rightFlag);break;
                case 12:
                case 13: draw(image, left,57, 93, leftFlag);break;
                case 14: draw(image, left,53, 85, leftFlag);break;
                case 15: draw(image, left,69, 98, leftFlag);break;
                case 16: draw(image, left,62, 90, leftFlag);break;
                case 17: draw(image, left,56, 85, leftFlag);break;
                default: break;
            }
            images.add(image);
        }
        GifUtil.imagesToGif(images, path);
        return path;
    }

    private void draw(BufferedImage image, String str, int x, int y, boolean flag) throws IOException {
        Graphics2D graphics = image.createGraphics();
        if (flag) {
            //文字
            graphics.setColor(Color.black);
            // 旋转
//            if (x>300){
//                graphics.rotate(122, x, y);
//
//            }
            graphics.setFont(new Font("黑体", Font.BOLD, 30));
            graphics.drawString(str,x, y);
        } else {
            //图片
            URL url = new URL(str);
            BufferedImage b = ImageIO.read(url);
            int width = b.getWidth();
            int height = b.getHeight();
//            int[] ints = new int[width * height];
//            ints = b.getRGB(0, 0, width, height, ints, 0, width);
//            image.setRGB(x, y, width, height, ints, 0, width);

            // 缩放画板
            //graphics.scale(0.5, 0.5);
            // 此时的宽和高如果大于画板则无效，自动缩放到充满画板
            graphics.drawImage(b, x, y-20, 100, 100, null);
        }
    }

    private String catToUrl(String str){
        if (str.startsWith("[CAT:")){
            CatCodeUtil codeUtil = CatCodeUtil.getInstance();
            Neko neko = codeUtil.getNeko(str);
            String type = neko.getType();

            if ("face".equals(type)) {
                String id = neko.get("id");
                return "http://emoji.fawukang.top/s"+id+".png ";
            } else if ("image".equals(type)) {
                return neko.get("url");
            }
        }
        return str;
    }
}
