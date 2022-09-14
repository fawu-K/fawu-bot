package com.kang.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kang.commons.util.CommonsUtils;
import com.kang.entity.GroupState;
import com.kang.web.mapper.GroupStateMapper;
import com.kang.web.service.GroupStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author K.faWu
 * @program service
 * @description:
 * @create 2022-09-09 15:09
 **/
@Service
public class GroupStateServiceImpl extends ServiceImpl<GroupStateMapper, GroupState> implements GroupStateService {
    @Autowired
    private GroupStateMapper groupStateMapper;


    @Override
    public List<GroupState> getAll() {
        return groupStateMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void save(String code, boolean state) {
        int stateNum;
        if (state) {
            stateNum = 1;
        } else {
            stateNum = 0;
        }
        QueryWrapper<GroupState> wrapper = new QueryWrapper<>();
        wrapper.eq("code", code);
        GroupState groupState = groupStateMapper.selectOne(wrapper);

        //判断是否已经存储群状态，若已经存储则判断当前状态是否和要改为的状态是否相同
        if (CommonsUtils.isEmpty(groupState)){
            groupState = new GroupState();
            groupState.setCode(code);
        }
        if (CommonsUtils.isEmpty(groupState.getState()) || stateNum != groupState.getState()) {
            groupState.setState(stateNum);
            this.save(groupState);
        }
    }

    /**
     * 根据id判断是修改还是新增
     * @param groupState
     * @return
     */
    @Override
    public boolean save(GroupState groupState) {
        if (CommonsUtils.isEmpty(groupState)) {
            return false;
        }
        if (CommonsUtils.isNotEmpty(groupState.getId())) {
            //有id表示修改
            groupStateMapper.updateById(groupState);
        } else {
            groupStateMapper.insert(groupState);
        }
        return true;
    }
}
