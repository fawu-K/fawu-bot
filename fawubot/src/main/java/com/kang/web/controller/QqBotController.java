package com.kang.web.controller;

import com.kang.commons.Result;
import com.kang.manager.BotAutoManager;
import com.kang.web.service.ResultByQqService;
import love.forte.simbot.api.message.containers.BotInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.results.GroupMemberInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @program: service
 * @description: 机器人控制类
 * @author: K.faWu
 * @create: 2022-07-08 14:51
 **/
@RestController
@RequestMapping("/qq")
public class QqBotController {
    private final BotAutoManager botAutoManager;
    private final ResultByQqService resultByQqService;

    public QqBotController(BotAutoManager botAutoManager, ResultByQqService resultByQqService) {
        this.botAutoManager = botAutoManager;
        this.resultByQqService = resultByQqService;
    }

    /**
     * 获取当前登录的账号
     * @return 机器人列表
     */
    @GetMapping("/getInfo")
    public ResponseEntity<?> getBotInfo(){
        List<BotInfo> list = botAutoManager.getBotInfo();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/getGroup/{code}")
    public ResponseEntity<?> getGroup(@PathVariable("code") String code){
        List<GroupInfo> groupInfoList = botAutoManager.getGroup(code);
        return ResponseEntity.ok().body(groupInfoList);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam MultipartFile file){
        String path = resultByQqService.uploadPhoto(file);
        return Result.ok(path);
    }

    /**
     * 获取群内成员信息
     * @param code 所属用户
     * @param groupCode 群号
     * @return 群成员信息
     */
    @GetMapping("/groupMemberList/{code}/{groupCode}")
    public ResponseEntity<?> groupMemberList(@PathVariable("code") String code, @PathVariable("groupCode") String groupCode){
        List<GroupMemberInfo> groupMemberList = botAutoManager.getGroupMemberList(code, groupCode);
        return ResponseEntity.ok(groupMemberList);
    }
}
