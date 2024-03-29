# seleniumTest

#### 介绍

基于java的selenium架子，用户可以直接在数据库或者excel上编写自动化的操作步骤，目前默认是使用chrome浏览器

#### 软件架构

testng + selenium + redis + MySQL + springboot(暂时没使用,后续可能集成进来)

#### 软件需要

1. 安装jdk1.8
2. 安装mysql5.6+
3. 安装redis
4. 安装maven
5. 安装lombok插件
6. 安装nodejs(选装)

#### 使用说明

1. 导入src/test/resources/sql/demo_test.sql 这个是demo的模板
2. src/test/resources/demo/index.html 这个是demo的页面,可以放到服务器上启动,或者安装好nodejs,live-server, 直接在根目录下执行npm run live-server,启动demo的页面
3. demo启动好之后到根目录执行mvn clean test
4. 修改相关的配置,打开application.properties,
    - (1) 修改driverPath,这个是自动化浏览器驱动的exe文件目录
    - (2) 修改baseUrl, 这个是selenium初始化后需要打开的网页
    - (3) 修改errorPic, 这个是selenium报错后屏幕截图的保存文件夹路径,debug模式不会保存
    - (4) 修改useDb,如果是true,用例是维护在数据库的表里面,如果是false,用例是维护在excel里面
    - (5) debug模式下默认开启显式和隐式等待,等待时间取最长的那个,开启隐式等待只是为了方便调试代码,driver.findElement()就可以直接使用

#### 用例的表结构

- 详细的参照com.lml.selenium.entity.Selenium这个类,里面有对应的枚举类型说明

|字段|类型|是否允许空|默认|注释|
|----    |-------|--- |---|------|
|id    |int(11)     |否 |  | 编号(必填,相当于步骤,默认从1开始)     |
|description |varchar(255) |否 |    |   相关描述(必填)  |
|model |varchar(255)|否   |    |   步骤模块(必填)    |
|elementAction     |enum |否   |    |    查找这个元素后操作的动作     |
|clickAction     |enum |否   |    |    点击使用的方法     |
|element |varchar(2000)     |是   |   |  要查找的元素 |
|findType |enum     |是   |   | 元素查询的方式 |
|ext |varchar(1000)     |是   |   |  预留字段扩展 |
|valid |enum     |否   | Y  |  是否有效(Y:有效,N:无效) |
|callBack |varchar(255)     |是   |   |  动作完成执行回调 |
|script |varchar(2000)     |是   |   |  要自定义运行的脚本 |
|wait |int(5)     |是   |   |  自定义查询这个dom节点需要等待的时间(单位:秒) |
|retry |int(5)     |是   |   |  自定义查询这个dom节点重试次数 |

#### src/test/resources下相关配置文件的说明

1. case文件夹: 这个是用来存放用例执行顺序的excel模板,如果用例是通过数据库维护的,则不需要管这个#### src/test/resources下相关配置文件的说明
2. config文件夹: redis.setting 这个是用来存放redis的配置的
3. sql文件夹: 这个是用来备份数据里的数据
4. script文件夹: 用来存放对应的js脚本,有两个文件夹:
   biz: 用来存放业务相关的脚本 common: 用来存放通用的一些脚本
5. modular文件夹: 用来存放testng的用例配置文件,可以按照模块的名字划分
6. testNG.xml:  testng的总配置文件
7. db.setting: 这个是数据源的配置文件,支持多数据源
8. application.properties: 项目的通用配置文件

#### 注意事项

1. 测试报告使用了reportng来实现的,如果出现乱码,可以把lib文件夹下的testng包覆盖的本地的maven仓库

#### 改进的地方

1. 暂时没有添加日志去记录错误情况,日后应该会把日志持久化到本地,通过查看日志来定位报错问题
2. 默认driver使用chrome,暂不支持其他浏览器,google上面说新版的firefox双击好像有问题???