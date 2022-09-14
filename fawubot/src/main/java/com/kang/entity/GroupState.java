package com.kang.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author K.faWu
 * @program service
 * @description: 启动状态表
 * @create 2022-09-09 14:44
 **/
@Data
public class GroupState implements Serializable {
    private Long id;
    private String code;
    private Integer state;
}
