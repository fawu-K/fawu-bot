package com.kang.entity.monasticPractice.play2;

import java.io.Serializable;
import lombok.Data;

/**
 * role_equipment
 * @author 
 */
@Data
public class RoleEquipment implements Serializable {
    /**
     * 角色拥有的装备
     */
    private Long id;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 装备id
     */
    private Long equipmentId;

    /**
     * 是否装备在身上
     */
    private Integer flag;

    private static final long serialVersionUID = 1L;
}