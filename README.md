Android
=======

GracePlayer工程：  
------------------------
### 描述：  
一个音乐播放器  

### 具有功能：  
1. 播放/暂停、上/下一首、后台播放、退出。  
2. 切换主题。  

### 存在的问题：  
1. 点击Home切换到后台时，进度条时间不会更改。  
2. 在不同分辨率的手机上，字体大小固定。  
3. 在Android4.X的平台上运行时程序崩溃。  
4. 上/下一首操作的相关方法在Acitivity中，导致模块间的耦合性提高。  

========

GracePlayer2.0工程：  
----------------------------
### 描述：  
对GracePlayer进行了修改的音乐播放器  

### 功能：  
1. 修复了在Android4.X平台上运行时崩溃的bug。  
2. 优化了程序框架，建立MusicList类，Music类。  
3. 优化程序框架，把上/下一首操作的相关放在了Service中，降低了模块间的耦合性。  
4. 最低版本控制在Android4.0。  

========

Zhbit工程：  
----------------------------
### 描述：  
一个能查看北京理工大学珠海学院图书馆借书信息的软件  

### 功能：  
1. 查看个人当前借书情况。  
2. 查看个人历史借书情况。  
3. 查看个人信息。   

==============


