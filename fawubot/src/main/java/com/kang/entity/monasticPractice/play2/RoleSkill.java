package com.kang.entity.monasticPractice.play2;

import lombok.Data;

/**
 * 角色技能对应类
 *
 * @author K.faWu
 * @program fawu-bot
 * @date 2022-09-26 16:51
 **/
@Data
public class RoleSkill {
    private Long id;
    private Long roleId;
    private Long skillId;
    private int flag;
}
