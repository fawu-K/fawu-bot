package com.kang.task.thread;

import com.kang.service.BotService;
import com.kang.task.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 线程，每日新闻线程
 * @create 2022-09-15 10:31
 **/
@Slf4j
public class TopnewsThread implements Runnable {
    private final BotService botService;

    public TopnewsThread(){
        this.botService = (BotService) SpringContextUtil.getBean(BotService.class);
    }

    @Override
    public void run() {
        log.info("定时发送新闻");
        botService.topnews();
    }
}
