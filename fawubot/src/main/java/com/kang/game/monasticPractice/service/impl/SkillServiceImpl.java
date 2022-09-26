package com.kang.game.monasticPractice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.entity.monasticPractice.play2.RoleSkill;
import com.kang.entity.monasticPractice.play2.Skill;
import com.kang.game.monasticPractice.mapper.RoleSkillMapper;
import com.kang.game.monasticPractice.mapper.SkillMapper;
import com.kang.game.monasticPractice.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 技能
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 16:46
 **/
@Service
public class SkillServiceImpl extends ServiceImpl<SkillMapper, Skill> implements SkillService {
    @Autowired
    private SkillMapper skillMapper;
    @Autowired
    private RoleSkillMapper roleSkillMapper;

    @Override
    public List<Skill> getSkill(Long roleId) {
        QueryWrapper<RoleSkill> roleSkillQueryWrapper = new QueryWrapper<>();
        roleSkillQueryWrapper.eq("role_id", roleId);
        roleSkillQueryWrapper.eq("flag", 1);
        List<RoleSkill> roleSkills = roleSkillMapper.selectList(roleSkillQueryWrapper);

        List<Long> skillIds = new ArrayList<>();
        roleSkills.forEach(rs -> skillIds.add(rs.getSkillId()));

        QueryWrapper<Skill> skillQueryWrapper = new QueryWrapper<>();
        skillQueryWrapper.in("id", skillIds);
        return skillMapper.selectList(skillQueryWrapper);
    }
}
