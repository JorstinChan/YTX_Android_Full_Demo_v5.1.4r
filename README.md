# YTX Android Full SDK v5.1.4r
In Android to send and receive instant messages

```java
// 发送消息
ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
// 设置消息的属性：发出者，接受者，发送时间等
msg.setForm("$Tony的账号");
// 设置消息接收者
msg.setTo("$John的账号");
// 创建一个文本消息体，并添加到消息对象中
ECTextMessageBody msgBody = new ECTextMessageBody("欢迎来到云通讯");
// 将消息体存放到ECMessage中
msg.setBody(msgBody);
// 调用SDK发送接口发送消息到服务器
ECChatManager manager = ECDevice.getECChatManager();
manager.sendMessage(msg, new ECChatManager.OnSendMessageListener());
```
# ReadMe
为了方便第三方开发者快速集成云通讯IM SDK，我们提供了以下联系方式，协助开发者进行集成：  
**QQ群：416105798（新）**  
**QQ群：427337647（已满）**  

## SDK初始化与注册
5.0版本新增appToken验证方式，开发者只需要根据appKey 和appToken 加上注册账号既可以进行注册SDK
### 1. appToken验证
```java
ECInitParams mInitParams = new ECInitParams();
mInitParams.setUserid("$userid");
mInitParams.setAppKey("$appKey");
mInitParams.setToken("$appToken");
mInitParams.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
// 使用默认验证方式
mInitParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
```
### 2. 账户密码验证（VoIP）
```java
// VoIP方式登陆
ECInitParams mInitParams = new ECInitParams();
mInitParams.setUserid("$VoIP");
// appkey
mInitParams.setAppKey("$appkey");
// appToken
mInitParams.setToken("$apptoken");
// ECInitParams.LoginMode.FORCE_LOGIN
mInitParams.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
// 如果有密码（VoIP密码，对应的登陆验证模式是）
// ECInitParams.LoginAuthType.PASSWORD_AUTH
mInitParams.setPwd("$VoIPToken");
// 使用密码验证方式
mInitParams.setAuthType(ECInitParams.LoginAuthType.PASSWORD_AUTH);
```

# Release Note
### Current Version: 5.1.4
Fixed Bugs:

1. 修复5.0.3 bugs
2. 修复SDK注册失败问题

New Feature:

1. VoIP音视频呼叫
2. 可以进行实时对讲、语音会议了

### Version: 5.0.3
Fixed Bugs:

1. 修复5.0.2 bugs
2. 点对点消息显示成群组消息
3. 离线消息过多时界面卡顿的情况
4. 发送图片显示位置错乱
5. 断网发送图片失败，聊天UI上不显示图片
6. Demo登出后还能收到推送消息
7. 解决小米4收不到消息的问题

New Feature:
 
1. 免打扰功能；
2. 支持GIF图片发送和显示；
3. 离线消息全部获取完成后提示一声；
4. 群组消息会话界面显示最后一条的发送者昵称；
5. 附件下载3次重试；
6. Demo重启检查失败消息；
7. 系统通讯录变化监听同步；
8. 手动点击“网络连接失败”条后重连

#应用混淆配置

```java
-libraryjars YTX_Android_Full_SDK_{$version}.jar
-keep class com.yuntongxun.ecsdk.** {*; }
-keep class org.webrtc.** { *;}
```

License
===

   Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 
   Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
   that can be found in the LICENSE file in the root of the web site.
 
    http://www.yuntongxun.com
 
   An additional intellectual property rights grant can be found
   in the file PATENTS.  All contributing project authors may
   be found in the AUTHORS file in the root of the source tree.
 
