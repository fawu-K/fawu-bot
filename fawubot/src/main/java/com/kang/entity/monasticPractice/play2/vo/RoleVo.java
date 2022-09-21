package com.kang.entity.monasticPractice.play2.vo;

import com.kang.entity.monasticPractice.play2.Lv;
import com.kang.entity.monasticPractice.play2.Role;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-08-22 11:46
 **/
@Data
public class RoleVo extends Role {
    public RoleVo(){
        super();
    }

    public RoleVo(Role role, Lv lv) {
        this();
        BeanUtils.copyProperties(role, this);
        this.setExpMax(lv.getExpMax());
        this.setLvName(lv.getName());
    }
    public RoleVo(String accountCode, String name, String sex) {
        super(accountCode, name, sex);
    }

    /**
     *升到下一级所需要的经验
     */
    private BigDecimal expMax;

    /**
     * 等级名称
     */
    private String lvName;

    @Override
    public String toString() {
        return "【" + this.getName() + "】" +
                "\n所属人：" + this.getUserId() +
                "\n性别：" + (this.getSex()==1 ? "男" : "女") +
                "\n等级：" + lvName +
                "\nexp：" + getExp() +"/" + getExpMax() +
                "\nhp：" + getHp() +
                "\n攻击：" + getAttack() +
                "\n防御：" + getDefe() +
                (getType() == 1? "": "\n类型：NPC角色") +
                "\n先天之气：" + getGasNum() + "条" +
                "\n修炼类型：" + getLvType();
    }
}
