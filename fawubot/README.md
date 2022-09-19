# 法兀bot
基于[simple-robot](https://github.com/simple-robot) v2.x框架书写的QQ机器人

#启动流程
## 1、在``src/main/resources``文件夹下创建``simbot.yml``文件，格式如下：
```yml
simbot:
  core:
    # 账号:密码，多个用逗号(,)分隔或者用yaml的多项配置
    # bots: 账号:密码
    bots: 1234567:******
```
## 2、创建``application-dev.yml``文件
[七牛云注册流程以及对应key获取方式](http://t.csdn.cn/KStIB)
[天行数据网址](https://www.tianapi.com/) 注册天行数据账号并获取key值，本bot接入天行数据接口有：
    [天行机器人](https://www.tianapi.com/apiview/47) ；
    [今日头条新闻](https://www.tianapi.com/apiview/99) ；
    [舔狗日记](https://www.tianapi.com/apiview/180) ;

内容格式如下：
```yml
# 最高权限角色配置
ROOT_CODE: #这里是用来管理机器人的账号，一些机器人设置只能由该账号进行设置
#天行数据接口
TIANAPI_KEY: #天行数据->控制台->数据管理->我的密钥KEY

#数据库配置
MYSQL_HOST: #mysql数据库地址，例：127.0.0.1
MYSQL_PORT: #数据库端口，例：3306
MYSQL_USERNAME: #数据库账号，例：root
MYSQL_PASSWORD: #账号对应密码
DATABASE: #数据库名称

#七牛云配置
#搜索 七牛云 进行注册账号并获取对应key值
QINIUYUN_ACCESSKEY: #七牛云中获取的accessKey
QINIUYUN_SECRETKEY: #七牛云中获取的secretKey
QINIUYUN_BUCKET: #你在七牛云准备存储图片的空间名称
QINIUYUN_DOMAIN: #七牛云存储空间访问外链接

#腾讯短信配置
#搜索 腾讯云 进行注册并购买或领取短信包
TENCENT_APPID: #短信中的appid
TENCENT_APPKEY: #短信中的appkey
TENCENT_SIGN: #短信的标题，例：腾讯游戏
```
