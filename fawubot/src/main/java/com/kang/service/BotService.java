package com.kang.service;

import catcode.CatCodeUtil;
import catcode.Neko;
import com.alibaba.fastjson.JSONObject;
import com.kang.Constants;
import com.kang.commons.util.BotUtil;
import com.kang.commons.util.CommonsUtils;
import com.kang.commons.util.GifUtil;
import com.kang.commons.util.HttpClientUtil;
import com.kang.config.BotConfig;
import com.kang.entity.GroupState;
import com.kang.entity.monasticPractice.play2.Event;
import com.kang.entity.monasticPractice.play2.Role;
import com.kang.entity.monasticPractice.play2.vo.BattleRole;
import com.kang.entity.monasticPractice.play2.vo.BossBattleRole;
import com.kang.entity.monasticPractice.play2.vo.EventVo;
import com.kang.game.monasticPractice.listener.BossListener;
import com.kang.game.monasticPractice.service.EventService;
import com.kang.game.monasticPractice.service.RoleService;
import com.kang.listener.GroupListener;
import com.kang.manager.BotAutoManager;
import com.kang.web.service.GroupStateService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import love.forte.common.ioc.annotation.Beans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.awt.SystemColor.text;

/**
 * @author K.faWu
 * @program fawu-bot
 * @description: 服务层
 * @create 2022-09-14 15:54
 **/
@Slf4j
@Beans
@Service
public class BotService {

    @Depend
    @Autowired
    private MessageContentBuilderFactory messageContentBuilderFactory;
    @Autowired
    private BotAutoManager botAutoManager;
    @Autowired
    private TianApiTool tianApiTool;
    @Autowired
    private RoleService roleService;
    @Autowired
    private GroupStateService groupStateService;
    @Autowired
    private EventService eventService;

    /**
     * 群聊中被at后进行的操作
     *
     * @param groupMsg
     * @param sender
     */
    public void roBot(GroupMsg groupMsg, Sender sender) {

        String accountCode = BotUtil.getCode(groupMsg);
        String text = groupMsg.getText();

        //调用api
        String result = tianApiTool.getTianRobot(text, accountCode);

        //@ta,并回复
        MessageContentBuilder contentBuilder = messageContentBuilderFactory.getMessageContentBuilder();
        MessageContent content = contentBuilder.at(accountCode).text(result).build();
        sender.sendGroupMsg(groupMsg, content);
    }

    /**
     * 发送新闻
     */
    public void topnews() {
        //调用接口
        String result = tianApiTool.topnews();
        //组合消息
        MessageContent messageContent = messageContentBuilderFactory.getMessageContentBuilder().text(result).build();
        //发送消息
        sendAllGroup(messageContent);
    }

    /**
     * 调用接口发送舔狗日记
     */
    public void tianGou() {
        //调用接口
        String result = tianApiTool.tianGou();
        //组合消息
        String tianGouUrl = "http://static.fawukang.top/tiangou.png";
        MessageContent messageContent = messageContentBuilderFactory.getMessageContentBuilder().image(tianGouUrl).text(result).build();
        //发送消息
        sendAllGroup(messageContent);
    }

    /**
     * 群发消息，将指定消息发送到该bot存在的所有群
     *
     * @param messageContent 待发送消息体
     */
    private void sendAllGroup(MessageContent messageContent) {
        List<GroupInfo> defaultBotGroups = botAutoManager.getDefaultBotGroups();
        defaultBotGroups.forEach(groupInfo -> botAutoManager.getSender().sendGroupMsg(groupInfo, messageContent));
    }

    @Value("${bot.imag-path}")
    private String seTuPath;

    public void downloadsSeTu(long count) {
        log.debug("开始下载图片：此次下载图片数量-{}", count);
        long successCount = 0;
        for (int i = 0; i<count; i++) {
            String s = HttpClientUtil.doGet("https://ybapi.cn/API/setu_r18.php?type=text");
            String[] split = s.split("/");
            String name = split[split.length-1];
            boolean flag = HttpClientUtil.getImag(s, seTuPath + name);
            if (flag){
                successCount++;
            }
        }
        log.debug("图片下载完毕：本次下载成功数量 - {}/{}", successCount, count);
    }

    /**
     * 生成可达鸭动图
     *
     * @param groupMsg
     * @param sender
     * @throws IOException
     */
    public void duck(GroupMsg groupMsg, Sender sender) throws IOException {
        String text = groupMsg.getMsg();
        String[] split = text.split(" ");
        if (split.length >= 3) {
            String path = setDuck(split[1], split[2]);
            //msgBuilder.image()
            MessageContentBuilder contentBuilder = messageContentBuilderFactory.getMessageContentBuilder();
            MessageContent messageContent = contentBuilder.image(path).build();
            sender.sendGroupMsg(groupMsg, messageContent);
        }

    }

    private String setDuck(String leftStr, String rightStr) throws IOException {
        String dirPath = "/www/wwwroot/cloud/image/";
        String path = "/www/wwwroot/cloud/image/res.gif";
        //修改展示的类型
        String left = catToUrl(leftStr);
        boolean leftFlag = left.equals(leftStr);
        String right = catToUrl(rightStr);
        boolean rightFlag = right.equals(rightStr);

        List<BufferedImage> images = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            File outFile = new File(dirPath + i + ".png");
            BufferedImage image = ImageIO.read(outFile);
            switch (i) {
                case 0:
                    draw(image, left, 45, 107, leftFlag);
                    break;
                case 1:
                    draw(image, left, 41, 95, leftFlag);
                    break;
                case 2:
                    draw(image, left, 36, 76, leftFlag);
                    break;
                case 3:
                case 4:
                    draw(image, right, 322, 66, rightFlag);
                    break;
                case 5:
                    draw(image, right, 326, 79, rightFlag);
                    break;
                case 6:
                    draw(image, right, 326, 72, rightFlag);
                    break;
                case 7:
                    draw(image, right, 326, 75, rightFlag);
                    break;
                case 8:
                    draw(image, right, 331, 75, rightFlag);
                    break;
                case 9:
                    draw(image, right, 328, 76, rightFlag);
                    break;
                case 10:
                    draw(image, right, 325, 67, rightFlag);
                    break;
                case 11:
                    draw(image, right, 328, 70, rightFlag);
                    break;
                case 12:
                case 13:
                    draw(image, left, 57, 93, leftFlag);
                    break;
                case 14:
                    draw(image, left, 53, 85, leftFlag);
                    break;
                case 15:
                    draw(image, left, 69, 98, leftFlag);
                    break;
                case 16:
                    draw(image, left, 62, 90, leftFlag);
                    break;
                case 17:
                    draw(image, left, 56, 85, leftFlag);
                    break;
                default:
                    break;
            }
            images.add(image);
        }
        GifUtil.imagesToGif(images, path);
        return path;
    }

    private void draw(BufferedImage image, String str, int x, int y, boolean flag) throws IOException {
        Graphics2D graphics = image.createGraphics();
        if (flag) {
            //文字
            graphics.setColor(Color.black);
            // 旋转
//            if (x>300){
//                graphics.rotate(122, x, y);
//
//            }
            graphics.setFont(new Font("黑体", Font.BOLD, 30));
            graphics.drawString(str, x, y);
        } else {
            //图片
            URL url = new URL(str);
            BufferedImage b = ImageIO.read(url);
            int width = b.getWidth();
            int height = b.getHeight();
//            int[] ints = new int[width * height];
//            ints = b.getRGB(0, 0, width, height, ints, 0, width);
//            image.setRGB(x, y, width, height, ints, 0, width);

            // 缩放画板
            //graphics.scale(0.5, 0.5);
            // 此时的宽和高如果大于画板则无效，自动缩放到充满画板
            graphics.drawImage(b, x, y - 20, 100, 100, null);
        }
    }

    private String catToUrl(String str) {
        if (str.startsWith("[CAT:")) {
            CatCodeUtil codeUtil = CatCodeUtil.getInstance();
            Neko neko = codeUtil.getNeko(str);
            String type = neko.getType();

            if ("face".equals(type)) {
                String id = neko.get("id");
                return "http://emoji.fawukang.top/s" + id + ".png ";
            } else if ("image".equals(type)) {
                return neko.get("url");
            }
        }
        return str;
    }

    /**
     * 根据事件生成Boss
     * @param event boss事件
     */
    public void toBoss(Event event) {
        // 需要将上次击杀Boss的奖励发放
        if (BossListener.boss != null) {
            reward();
        }

        BattleRole boss = roleService.getBattleRole(event.getPlace());
        if (CommonsUtils.isNotEmpty(boss)) {
            //触发Boss的事件
            BossListener.bossEvent = event;
            //赋予Boss
            BossListener.boss = boss;
            //清空伤害列表
            BossListener.KILL_MAP.clear();
            //清空斩杀人员信息
            BossListener.killBossRole = null;

            //将boss出现的信息通知到各个拥有游戏角色的群里
            String msg = String.format("%s\n世界级Boss已经出现！各位豪杰开始你们的讨伐之战吧！\n1、使用[#+技能名称]攻击BOSS\n2、使用查看Boss信息以及对Boss可以使用的指令", event.getInfo());
            sendMsg(msg);
        }
    }

    private void reward(){
        StringBuilder msg = new StringBuilder("[" + BossListener.boss.getName() + "]Boss讨伐奖励已发放:");
        EventVo killBossEvent = eventService.toNext(BossListener.bossEvent.getId(), "BOSS击杀奖励");
        EventVo killRankingBossEvent = eventService.toNext(BossListener.bossEvent.getId(), "BOSS排行奖励");
        List<RewardCoefficient> rewardCoefficient = getRewardCoefficient();
        List<BossBattleRole> list = new ArrayList<>();
        Set<BattleRole> battleRoles = BossListener.KILL_MAP.keySet();
        battleRoles.forEach(battleRole -> list.add(new BossBattleRole(battleRole, BossListener.KILL_MAP.get(battleRole))));
        // 排序
        list.sort((o1, o2) -> o2.getKillNum().compareTo(o1.getKillNum()));

        // 伤害排行奖励
        BigDecimal reward = new BigDecimal(killRankingBossEvent.getPlace());
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            for (RewardCoefficient coefficient : rewardCoefficient) {
                if (coefficient.getRanking() >= i+1) {
                    BossBattleRole bossBattleRole = list.get(i);
                    //获取经验奖励，boss的基础经验 * 奖励系数
                    BigDecimal exp = reward.multiply(coefficient.getCoefficient());
                    msg.append(String.format("\n%s.[%s] --- %sexp", i + 1, bossBattleRole.getName(), exp));
                    exp = bossBattleRole.getExp().add(exp);
                    addExp(bossBattleRole, exp);
                    break;
                }
            }
        }

        // 最终击杀奖励
        BigDecimal reward1 = new BigDecimal(killBossEvent.getPlace());
        BigDecimal exp = BossListener.killBossRole.getExp().add(reward1);
        msg.append("最终击杀奖励：\n").append(String.format("\n[%s] --- %sexp", BossListener.killBossRole.getName(), exp));
        //判断经验是否升级
        addExp(BossListener.killBossRole, exp);

        //将boss出现的信息通知到各个拥有游戏角色的群里
        sendMsg(msg.toString());

    }

    private void sendMsg(String msg){
        for (GroupState groupState : groupStateService.getAll()) {
            if (groupState.getState() == 1) {
                try {
                    botAutoManager.getSender().sendGroupMsg(groupState.getCode(), msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("向{}消息发送失败！{}", groupState.getCode(), msg);
                }
            }
        }
    }

    private void addExp(BattleRole bossBattleRole, BigDecimal exp){
        //判断经验是否升级
        while (exp.compareTo(bossBattleRole.getExpMax()) >= 0) {
            exp = exp.subtract(bossBattleRole.getExpMax());
            String breach = roleService.breach(bossBattleRole);
            if (CommonsUtils.isNotEmpty(breach)) {
                bossBattleRole.setExp(bossBattleRole.getExpMax());
                log.debug("角色[{}]已提升到最高等级", bossBattleRole.getName());
                return;
            }
        }
        bossBattleRole.setExp(exp);
    }

    /**
     * 1    15
     * 2    13
     * 3    12
     * 4    11
     * 5名  10
     * 6-10发放8倍经验
     * 11-25  6倍
     * 26-50  4倍奖励
     * 51-100 双倍奖励
     * 100以后发放普通奖励
     */
    public List<RewardCoefficient> getRewardCoefficient(){
        List<RewardCoefficient> list = new ArrayList<>();
        list.add(new RewardCoefficient(1L, new BigDecimal("15")));
        list.add(new RewardCoefficient(2L, new BigDecimal("13")));
        list.add(new RewardCoefficient(3L, new BigDecimal("12")));
        list.add(new RewardCoefficient(5L, new BigDecimal("10")));
        list.add(new RewardCoefficient(10L, new BigDecimal("8")));
        list.add(new RewardCoefficient(25L, new BigDecimal("6")));
        list.add(new RewardCoefficient(50L, new BigDecimal("4")));
        list.add(new RewardCoefficient(100L, new BigDecimal("2")));
        list.add(new RewardCoefficient(999999L, new BigDecimal("1")));
        return list;
    }

    @Data
    static class RewardCoefficient {
        private Long ranking;
        private BigDecimal coefficient;

        public RewardCoefficient(Long ranking, BigDecimal coefficient) {
            this.ranking = ranking;
            this.coefficient = coefficient;
        }
    }
}
