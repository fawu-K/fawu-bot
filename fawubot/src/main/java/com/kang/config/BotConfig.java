package com.kang.config;

import com.kang.commons.util.CommonsUtils;
import com.kang.entity.GroupState;
import com.kang.web.service.GroupStateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author K.faWu
 * @program service
 * @description: bot启动加载类
 * @create 2022-08-04 16:02
 **/

@Component
@Slf4j
public class BotConfig {

    /**
     * 权限账号
     */
    private static String ROOT_CODE;

    /**
     * 天行数据登录key
     */
    private static String TIAN_KEY;

    private final GroupStateService groupStateService;

    /**
     * 默认状态
     */
    private static boolean defaultState = false;

    /**
     * 机器人在对应群中是否启动是否启动
     */
    private static final Map<String, Boolean> BOT_STATE = new HashMap<>();

    @Autowired
    public BotConfig(GroupStateService groupStateService){
        this.groupStateService = groupStateService;
        this.setBotStateMap();
    }

    /**
     * 利用非静态方法对静态属性赋值
     * @param rootCode yml文件中的系统账号
     */
    @Value("${bot.root-code}")
    private void setRootCode(String rootCode){
        ROOT_CODE = rootCode;
    }

    /**
     * 返回系统权限账号
     * @return 系统权限账号
     */
    public static String getRootCode(){
        return ROOT_CODE;
    }

    /**
     * 配置文件引入天行数据key
     * @param tianKey key
     */
    @Value("${bot.tianapi-key}")
    private void setTianKey(String tianKey) {
        TIAN_KEY = tianKey;
    }

    /**
     * 返回天行数据的key
     * @return TIAN_KEY
     */
    public static String getTianKey(){
        return TIAN_KEY;
    }

    /**
     * 获取所有群状态
     */
    public static Map<String, Boolean> getBotState() {
        return BOT_STATE;
    }

    /**
     * 根据传入的账号获取群状态
     */
    public boolean getBotState(String code) {
        Boolean state = getBotState().get(code);
        if (CommonsUtils.isEmpty(state)) {
            setBotState(code);
            return false;
        }
        return state;
    }

    /**
     * 初始化机器人在各个群的状态
     */
    public void setBotStateMap() {
        List<GroupState> groupStates = groupStateService.getAll();
        if (CommonsUtils.isNotEmpty(groupStates)) {
            groupStates.forEach(gs -> getBotState().put(gs.getCode(), gs.getState() == 1));
            log.debug(String.valueOf(BOT_STATE));
        }
    }

    /**
     * 修改机器人在指定群中的状态
     * @param code 群号
     * @param state 状态
     */
    public void setBotState(String code, boolean state) {
        Map<String, Boolean> botState = getBotState();
        botState.put(code, state);
        groupStateService.save(code, state);
    }

    /**
     * 将机器人在在指定群的状态修改为默认状态
     * @param code 群号
     */
    public void setBotState(String code) {
        this.setBotState(code, defaultState);
    }
}
