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
   - (6) useBmpProxy如果为true，表示会开启代理去抓取对应的ajax请求，目前只会抓get和post的请求

#### 特殊常用的类

1. WebUtil.java 该类封装了一些selenium操作的通用方法，例如查找元素，点击元素等，当操作步骤太过复杂(excel or 数据库的步骤不适用)的时候可以使用此类来进行相关的操作
2. ChromeDriverProxy.java() 该类继承了ChromeDriver，saveResponse()用来获取所有ajax请求的返回值，但这个方法有缺点，不能获取请求的参数，不建议使用(后续此类可能会废弃，由RequestProxy.java代替)
3. RequestProxy.java 基础的请求代理类，基于bmp的代理，可以获取ajax的请求参数和返回内容，目前只抓取了get和post的请求，使用方法如下:
   - (1)抓取请求前执行 ```RequestProxy requestProxy = WebUtil.getRequestProxy();```
   - (2)开启新的har包```requestProxy.newHar("test");```
   - (3)运行对应的业务代码,等待页面全部加载完,包括所有请求完成
   - (4)结束抓包```Pair<List<BrowserVo>, Har> listHarPair = requestProxy.captureRequest();```
   - (5)此时会返回一个pari对象，左边是解析好的参数响应体对象list，右边是原生抓取的har群文件，用户可以根据自己的需求拿这个list去操作或者拿这个har文件去操作
   - (6)outPutFile()方法接受一个```List<BrowserVo>```的入参，用户输出到对应的文件
4. SeleniumFactory.java selenium的初始化类,用来初始化driver和各种事件handler
5. JsUtil.java 用来执行和加载js脚本的工具类,里面提供了等待页面元素加载和等待ajax请求的方法
6. LoadTestCaseUtil.java 用于加载用例的工具类,目前支持从excel和数据库里加载,推荐优先从数据库加载
7. SeleniumBaseTest.java 基础测试类,测试类可以继承此类，然后重写getCaseTemplate()方法,返回值就是对应要加载用例的表或者excel
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
|wait |int(8)     |是   |   |  自定义查询这个dom节点需要等待的时间(单位:毫秒) |
|retry |int(5)     |是   |   |  自定义查询这个dom节点重试次数 |

#### src/main/resources下相关配置文件的说明

1. config文件夹: redis.setting 这个是用来存放redis的配置的
2. sql文件夹: 这个是用来备份数据里的数据
3. script文件夹: 用来存放通用的js脚本,有两个文件夹:
   biz: 用来存放业务相关的脚本 common: 用来存放通用的一些脚本
4. db.setting: 这个是数据源的配置文件,支持多数据源
5. selenium.properties: 项目的通用配置文件

#### src/test/resources下相关配置文件的说明

1. case文件夹: 这个是用来存放用例执行顺序的excel模板,如果用例是通过数据库维护的,则不需要管这个
2. modular文件夹: 用来存放testng的用例配置文件,可以按照模块的名字划分
3. testNG.xml:  testng的总配置文件

#### 如何使用此项目

##### 项目的使用方法有两种:

1. 直接在这个项目下,在selenium-web模块编写对应的用例,用例类直接继承SeleniumBaseTest.java
2. 通过引入的方式到其他项目使用,利用maven对selenium-core模块打包后,在其他项目直接引入core的依赖,然后需要引入以下依赖

```xml

<dependency>
   <groupId>org.testng</groupId>
   <artifactId>testng</artifactId>
</dependency>
````

```xml

<dependency>
   <groupId>org.uncommons</groupId>
   <artifactId>reportng</artifactId>
   <version>reportng-2.6.4</version>
   <scope>system</scope>
   <systemPath>${project.basedir}/lib/reportng-2.6.4.jar</systemPath>
</dependency>
```

```xml

<dependency>
   <groupId>org.projectlombok</groupId>
   <artifactId>lombok</artifactId>
   <optional>true</optional>
</dependency>
```

3. pom里面指定hutool-all的版本是5.7.19

```xml

<dependency>
   <groupId>cn.hutool</groupId>
   <artifactId>hutool-all</artifactId>
   <version>5.7.19</version>
</dependency>
```

4. pom里面指定selenium-java版本是4.0.0

```xml

<properties>
   <selenium.version>4.0.0</selenium.version>
</properties>
```

#### 注意事项

1. reportng-2.6.4.jar需要放到对应的路径才能使用,这个是改了官方的包,因为官方的包输出测试报告不支持中文
2. 如果不想使用system级的引入,可以把这个包手动安装到本地的仓库中

#### 运行测试

1. 到项目根目录下执行mvn clean test即可,待执行完成会在target文件夹生成对应的测试报告surefire-reports
2. 进入里面的html文件夹，把html文件夹丢到http的服务器中,打开index.html即可
3. index.html如果不放到http服务器,则无法正常加载测试报告

#### 改进的地方

1. 暂时没有添加日志去记录错误情况,日后应该会把日志持久化到本地,通过查看日志来定位报错问题
2. 默认driver使用chrome,暂不支持其他浏览器,google上面说新版的firefox双击好像有问题???
3. SeleniumFactory.closeService()需要注意,全局执行一次就足够(貌似不调用也行？？？也会释放chromeDriver),不然可能会在doubleClick操作的时候报classNotFoundException