package com.kang.task.thread;

import com.kang.service.BotService;
import com.kang.task.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 下载涩图线程
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-29 16:20
 **/
@Slf4j
public class SeTuR18Thread implements Runnable {
    private final BotService botService;

    public SeTuR18Thread(){
        this.botService = (BotService) SpringContextUtil.getBean(BotService.class);
    }

    @Override
    public void run() {
        log.info("定时下载图片~");
        botService.downloadsSeTu(100);
    }
}
