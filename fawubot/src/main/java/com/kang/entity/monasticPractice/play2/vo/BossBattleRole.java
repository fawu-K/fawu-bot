package com.kang.entity.monasticPractice.play2.vo;

import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.Skill;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.List;

public class BossBattleRole extends BattleRole {
        /**
         * 对boss造成的伤害总和
         */
        private BigDecimal killNum;

        public BigDecimal getKillNum(){
            if (killNum == null) {
                return new BigDecimal("0");
            }
            return killNum;
        }

        public void setKillNum(BigDecimal killNum) {
            this.killNum = killNum;
        }

        public BossBattleRole(){}

        public BossBattleRole(BattleRole battleRole){
            BeanUtils.copyProperties(battleRole, this);
        }

        public BossBattleRole(BattleRole battleRole, BigDecimal killNum) {
            this(battleRole);
            this.killNum = killNum;
        }
    }
