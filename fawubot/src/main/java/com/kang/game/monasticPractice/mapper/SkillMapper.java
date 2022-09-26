package com.kang.game.monasticPractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kang.entity.monasticPractice.play2.Skill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 技能
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 16:43
 **/

@Mapper
public interface SkillMapper extends BaseMapper<Skill> {
}
