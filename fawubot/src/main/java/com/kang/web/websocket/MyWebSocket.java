package com.kang.web.websocket;



import com.google.gson.Gson;
import com.kang.entity.Msg;
import com.kang.entity.ResultByQq;
import com.kang.manager.BotAutoManager;
import com.kang.web.service.ResultByQqService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: service
 * @description:
 * @author: K.faWu
 * @create: 2022-07-13 16:02
 **/

@ServerEndpoint(value = "/websocket/{code}/{groupCode}")
@Controller
public class MyWebSocket {

    private static BotAutoManager botAutoManager;
    private static ResultByQqService resultByQqService;
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     * 用来记录sessionId和该session进行绑定
     */
    private static final Map<String, MyWebSocket> map = new ConcurrentHashMap<String, MyWebSocket>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 用来解决webSocket中无法注入mapper
     */
    private static ApplicationContext applicationContext;


    public static void setApplicationContext(ApplicationContext applicationContext) {
        MyWebSocket.applicationContext = applicationContext;
        MyWebSocket.botAutoManager = MyWebSocket.applicationContext.getBean(BotAutoManager.class);
        MyWebSocket.resultByQqService = MyWebSocket.applicationContext.getBean(ResultByQqService.class);
    }
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("code") String code, @PathParam("groupCode") String groupCode) {
        //加入set中
        this.session = session;
        map.put(code + groupCode, this);
        //在线数加1
        addOnlineCount();
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("code") String code, @PathParam("groupCode") String groupCode) {
        //从set中删除
        map.remove(code + groupCode);
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        //把前端发送的消息整合后发送到QQ上
        ResultByQq<Msg> resultByQq = botAutoManager.sendMsg(message);
        //经过处理发送回去。表明发送成功
        sendInfo(resultByQq, resultByQq.getAccountCode(), resultByQq.getGroupCode());
    }


    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static <T> void sendInfo(T message, String code, String groupCode) {
        String s = new Gson().toJson(message);
        MyWebSocket myWebSocket = map.get(code + groupCode);
        if (myWebSocket != null) {
            try {
                myWebSocket.sendMessage(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }
}
