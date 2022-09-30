package com.kang.listener;


import love.forte.common.ioc.annotation.Beans;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Beans
public class MsgLog {

    private String IMGurl = null;

    @OnPrivate
    public void privateMsg(PrivateMsg msg, MsgSender sender) {
        try {
            if (msg.getMsg().equals("来份涩图")) {
                System.out.println(msg.getAccountInfo().getAccountNickname() + "从私聊请求了一份涩图");
                String url = "https://ybapi.cn/API/setu.php?type=text";
                getimgurl(url);
                System.out.println(IMGurl);
                sender.SENDER.sendPrivateMsg(msg, "[CAT:image,file=" + IMGurl + "]");
                sender.SENDER.sendPrivateMsg(msg, "原链接：" + IMGurl);
                System.out.println("发送完成");
            }
        } catch (Exception e) {
            sender.SENDER.sendPrivateMsg(msg, "啊哦~涩图加载失败了，重新试试吧！");
        }
    }

    @OnGroup
    public void GroupMsg(GroupMsg msg, MsgSender sender) {
        int GroupCode = Integer.parseInt(msg.getGroupInfo().getGroupCode());
        try {
            if (msg.getMsg().equals("来份涩图")) {
                System.out.println(msg.getAccountInfo().getAccountNickname() + "从群：" + GroupCode + "请求了一份涩图");
                String url = "https://ybapi.cn/API/setu.php?type=text";
                getimgurl(url);
                System.out.println(IMGurl);
                sender.SENDER.sendPrivateMsg(msg, "[CAT:image,file=" + IMGurl + "]");
                sender.SENDER.sendPrivateMsg(msg, "原链接：" + IMGurl);
                sender.SENDER.sendGroupMsg(GroupCode, "[CAT:at,code=" + msg.getAccountInfo().getAccountCode() + "]涩图已经私发给你啦！");
                System.out.println("发送完成");
            }
//            else if (msg.getMsg().equals("来份色图")){
//                String url = "https://ybapi.cn/API/setu_r18.php?type=text";
//                getimgurl(url);
//                System.out.println(IMGurl);
//                sender.SENDER.sendPrivateMsg(msg,"[CAT:image,file="+IMGurl+",flash=true]");
//                sender.SENDER.sendGroupMsg(GroupCode,"[CAT:at,code="+msg.getAccountInfo().getAccountCode()+"]色图已经私发给你啦！");
//            }
        } catch (Exception e) {
            sender.SENDER.sendGroupMsg(GroupCode, "[CAT:at,code=\"+msg.getAccountInfo().getAccountCode()+\"]啊哦~涩图加载失败了，重新试试吧！");
        }

    }

    public void getimgurl(String url) {
        System.out.println("打开连接");
        try {

            URL u = new URL(url);

            URLConnection connection = u.openConnection();

            HttpURLConnection htCon = (HttpURLConnection) connection;

            int code = htCon.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK) {

                System.out.println("获取图片");

                BufferedReader in = new BufferedReader(new InputStreamReader(htCon.getInputStream()));

                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    IMGurl = inputLine;
                in.close();

            } else {

                System.out.println("Can not access the website");

            }

        } catch (MalformedURLException e) {

            System.out.println("Wrong URL");

        } catch (IOException e) {

            System.out.println("Can not connect");

        }
    }
}

/*
* accept-ranges: bytes
alt-svc: h3=":443"; ma=86400, h3-29=":443"; ma=86400
cache-control: max-age=31536000
cf-cache-status: MISS
cf-ray: 752310040e04cfc8-SJC
content-length: 1135337
content-type: image/jpeg
date: Thu, 29 Sep 2022 07:39:02 GMT
expires: Wed, 27 Sep 2023 10:21:57 GMT
last-modified: Sun, 30 Jan 2022 13:41:17 GMT
nel: {"success_fraction":0,"report_to":"cf-nel","max_age":604800}
report-to: {"endpoints":[{"url":"https:\/\/a.nel.cloudflare.com\/report\/v3?s=dURWcSUVHqRFjxUKfWht5GlyGgVjDW3ng%2FlSvkV5V3de5U1TQrOGJC1XZIRe9PFEQmDaLh80%2F7xTe%2FpEG9OIDnO%2Ba64hQ3SPeIktpFtWYAquKLax2TRiKf9ZPV2j"}],"group":"cf-nel","max_age":604800}
server: cloudflare
vary: Accept-Encoding
via: HTTP/2.0 PixivCatFRA, http/1.1 f001 (second)
x-cache-status: MISS
x-content-type-options: nosniff
x-proxied-by: Pixiv.CatFRA
* */
