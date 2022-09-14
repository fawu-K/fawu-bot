package com.kang.web.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kang.commons.util.BotUtil;
import com.kang.config.BotConfig;
import com.kang.root.RootListener;
import love.forte.simbot.api.message.events.GroupMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.baomidou.mybatisplus.core.toolkit.StringPool.OFF;
import static com.baomidou.mybatisplus.core.toolkit.StringPool.ON;
import static com.kang.root.RootListener.BLACKLISTS;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-09-13 09:46
 **/
@Service
public class RootService {

    @Autowired
    private BotConfig botConfig;

    /**
     * 进行开机关机操作
     */
    public void openOrOff(GroupMsg groupMsg, boolean flag) {
        String groupCode = BotUtil.getGroupCode(groupMsg);
        String code = BotUtil.getCode(groupMsg);

        if (code.equals(BotConfig.getRootCode())) {
            botConfig.setBotState(groupCode, flag);
        } else {
            //其他人操作记录在黑名单里
            String key = groupCode + code;
            RootListener.Blacklist blacklist = new RootListener.Blacklist(groupCode, code, flag? ON : OFF);
            List<RootListener.Blacklist> bs;
            if (BLACKLISTS.containsKey(key)) {
                bs = BLACKLISTS.get(key);
            } else {
                bs = new ArrayList<>();
            }
            bs.add(blacklist);
            BLACKLISTS.put(key, bs);
        }
        testSystemUsage();
    }

    public void testSystemUsage() {
        final long GB = 1024 * 1024 * 1024;
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        String osJson = JSON.toJSONString(operatingSystemMXBean);
//            System.out.println("osJson is " + osJson);
        JSONObject jsonObject = JSON.parseObject(osJson);
        double processCpuLoad = jsonObject.getDouble("processCpuLoad") * 100;
        double systemCpuLoad = jsonObject.getDouble("systemCpuLoad") * 100;
        Long totalPhysicalMemorySize = jsonObject.getLong("totalPhysicalMemorySize");
        Long freePhysicalMemorySize = jsonObject.getLong("freePhysicalMemorySize");
        double totalMemory = 1.0 * totalPhysicalMemorySize / GB;
        double freeMemory = 1.0 * freePhysicalMemorySize / GB;
        double memoryUseRatio = 1.0 * (totalPhysicalMemorySize - freePhysicalMemorySize) / totalPhysicalMemorySize * 100;

        String result = "系统CPU占用率: " +
                twoDecimal(systemCpuLoad) +
                "%，\n内存占用率：" +
                twoDecimal(memoryUseRatio) +
                "%，\n系统总内存：" +
                twoDecimal(totalMemory) +
                "GB，\n系统剩余内存：" +
                twoDecimal(freeMemory) +
                "GB，\n该进程占用CPU：" +
                twoDecimal(processCpuLoad) +
                "%";
        System.out.println(result);

    }

    public double twoDecimal(double doubleValue) {
        BigDecimal bigDecimal = new BigDecimal(doubleValue).setScale(2, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}
