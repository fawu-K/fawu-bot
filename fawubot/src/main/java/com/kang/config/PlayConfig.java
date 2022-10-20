package com.kang.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kang.entity.monasticPractice.play2.Lv;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Speed;
import com.kang.entity.monasticPractice.play2.vo.BattleRole;
import com.kang.game.monasticPractice.service.LvService;
import com.kang.game.monasticPractice.service.RoleService;
import com.kang.game.monasticPractice.service.SpeedService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 问大天荒配置
 * @create 2022-09-20 14:11
 **/
@Slf4j
@Component
public class PlayConfig {


    /**
     * 先天之气数量，以及对应的修炼速度
     */
    static Map<Long, Speed> speedMap = new HashMap<>();

    /**
     * 等级列表，当角色升级时使用，等级+类型当作主键
     */
    static Map<String, Lv> lvMap = new HashMap<>();

    /**
     * 已经存在的账号
     */
    private static final Map<String, Role> ROLE_MAP = new HashMap<>();

    /**
     * 通过构造方法注入所有账号信息
     * 注入先天之气信息
     * 注入等级列表
     */
    public PlayConfig(RoleService roleService, SpeedService speedService, LvService lvService) {

        log.info("加载《问大天慌》用户角色...");
        List<BattleRole> list = roleService.getList();
        list.forEach(role -> ROLE_MAP.put(role.getUserId(), role));

        log.info("加载先天之气信息...");
        List<Speed> speedList = speedService.getAll();
        for (Speed speed : speedList) {
            speedMap.put(speed.getId(), speed);
        }

        log.info("加载等级列表...");
        List<Lv> lvList = lvService.getAll();
        for (Lv lv: lvList) {
            lvMap.put(lv.getLv() + lv.getType(), lv);
        }
    }

    /**
     * 获取当前已经存在的账号
     * @return 当前已经存在的账号
     */
    public static Map<String, Role> getRoleMap(){
        return ROLE_MAP;
    }

    /**
     * 获取指定人的账号
     * @param code 指定人qq
     * @return 账号id
     */
    public static Role getRoleMap(String code) {
        return ROLE_MAP.get(code);
    }

    /**
     * 向角色信息表里添加一个角色信息
     * @param code 角色账号
     * @param role 角色信息
     */
    public static void setRoleMap(String code, Role role) {
        ROLE_MAP.put(code, role);
    }

    /**
     * 获取已知先天之气对应修炼关系
     * @return 对应关系
     */
    public static Map<Long, Speed> getSpeedMap(){
        return speedMap;
    }

    /**
     * 获取指定先天之气对应的修炼关系
     * @return 修炼关系
     */
    public static Speed getSpeedMap(Long id) {
        return speedMap.get(id);
    }

    public static Speed getSpeedMap(Integer id) {
        return speedMap.get(id.longValue());
    }

    /**
     * 获取所有的等级关系
     * @return 等级关系
     */
    public static Map<String, Lv> getLvMap(){
        return lvMap;
    }

    /**
     * 获取指定的等级信息
     * @param type 等级类型
     * @param lv 等级
     * @return 等级信息
     */
    public static Lv getLvMap(String type, int lv) {
        return lvMap.get(lv + type);
    }
}
