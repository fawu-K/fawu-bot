package com.kang.game.monasticPractice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kang.entity.monasticPractice.play2.Skill;

import java.util.List;

/**
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 16:45
 **/


public interface SkillService extends IService<Skill> {
    /**
     * 根据角色id获取其携带的技能
     * @param roleId 角色id
     * @return 携带的技能
     */
    List<Skill> getSkill(Long roleId);
}
