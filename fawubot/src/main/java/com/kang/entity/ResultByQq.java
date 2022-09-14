package com.kang.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: service
 * @description: 返回到网页的内容信息
 * @author: K.faWu
 * @create: 2022-07-13 17:30
 **/
@Data
public class ResultByQq<T> implements Serializable {
    private String accountCode;
    private String groupCode;
    private String accountNickname;
    private String accountRemark;
    private String accountTitle;
    private String accountAvatar;
    private String email;
    private String age;
    private String level;
    private String text;
    private List<T> msg;

}
