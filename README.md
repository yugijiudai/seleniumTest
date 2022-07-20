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
4. 修改相关的配置,打开selenium.properties,
   - (1) 修改driverPath,这个是自动化浏览器驱动的exe文件目录
   - (2) 修改baseUrl, 这个是selenium初始化后需要打开的网页
   - (3) 修改errorPic, 这个是selenium报错后屏幕截图的保存文件夹路径,debug模式不会保存
   - (4) 修改useDb,如果是true,用例是维护在数据库的表里面,如果是false,用例是维护在excel里面
   - (5) debug模式下默认开启显式和隐式等待,等待时间取最长的那个,开启隐式等待只是为了方便调试代码,driver.findElement()就可以直接使用
   - (6) useBmpProxy如果为true，表示会开启代理去抓取对应的ajax请求，目前只会抓get和post的请求
   - (7) promptForDownload，表示是否开启弹窗下载，默认为false，开启弹窗后win，mac等不同系统在处理上会有不同的机制，例如路径上的保存，win可以直接给一个绝对路径就可以保存，mac不行也无法修改默认的保存目录，虽然robotUtil做了兼容，但仍然不太稳定，不建议开启
   - (8) downloadPath，表示是下载文件的默认保持路径
5. 具体用法和用例步骤格式可以参考下面两个测试类和demo_test.sql里面设置的用例步骤

```
com.lml.selenium.demo.DemoTest
com.lml.selenium.demo.DemoTest2
```

6. RunMethodHandler.java类,这个类是通过反射的方式来调用指定的方法,有以下两种使用姿势:
   - (1) 当执行的步骤太复杂,用户可以自己通过定义一个方法来一次执行复杂的操作
   - (2) 每行用例执行完,用户可以设定callBack字段来进行回调操作,例如把查找到的元素带回,或者进行各种断言<br>

对应的ext字段格式如下:

```json5
{
   // 对应的类路径名字
   "className": "com.lml.selenium.demo.asserts.DemoAssert",
   // 要执行的方法名字
   "methodName": "assertWindowAndCallBack",
   // 传给执行的方法参数,如果没有,这个可以字段可以不出现
   "args": [
      "hello"
   ]
}
```

> 注: 当这个作为callBack使用的时候,如果该步骤的操作属于需要查找元素的话,会根据你的回调方法动态判断是否需要把元素列表作为参数带回,如果需要带回,则元素列表是作为第一个参数出现,使用如下:

```
    // list就是当前步骤操作得到的元素, expectMsg就是args里面的参数
    public void assertCallBack(List<WebElement> list, String expectMsg) {
        Assert.assertEquals(list.size(), 3);
        for (WebElement webElement : list) {
            Assert.assertEquals(webElement.getTagName(), "input");
            Assert.assertEquals(webElement.getAttribute("value"), expectMsg);
        }
    }
```

> 当不需要使用元素时,可以这样直接使用:

```
    // expectMsg就是args里面的参数
    public void assertCallBack(String expectMsg) {
        // doSomething
    }
```

> 当args字段不存在时,可以这样使用

```
    // list就是当前步骤操作得到的元素
    public void assertCallBackWithElement(List<WebElement> list) {
        // doSomething
    }
    
    public void assertCallBackWithoutElement() {
        // doSomething
    }
```

#### 更多使用可以参考```com.lml.selenium.demo.asserts.DemoAssert```类

7. RunScriptHandler.java类,这个类是用来给用户运行自己的js脚本的,当用户需要执行自己的js脚本可以在ext字段按以下格式填写:

```json5
{
   // js运行所需要的参数，非必填
   "args": [
      "123"
   ],
   // 要运行的js脚本
   "script": "console.log(111)",
   // js运行完要执行的回调方法,可以把js的返回值作为参数带给这个回调方法, 格式和上面的RunMethodHandler要求的ext格式一致
   "callFn": {}
}
```

#### 更多骚姿势可以参考```com.lml.selenium.demo.DemoTest#testScriptHandler```

#### 特殊常用的类

1. WebUtil.java 该类封装了一些selenium操作的通用方法，例如查找元素，点击元素等，当操作步骤太过复杂(excel or 数据库的步骤不适用)的时候可以使用此类来进行相关的操作
2. ChromeDriverProxy.java() 该类继承了ChromeDriver，saveResponse()用来获取所有ajax请求的返回值，但这个方法有缺点，不能获取请求的参数，不建议使用(后续此类可能会废弃，由RequestProxy.java代替)
3. RequestProxy.java 基础的请求代理类，基于bmp的代理，可以获取ajax的请求参数和返回内容，目前只抓取了get和post的请求，使用方法如下:
    - (1)开启新的har包```RequestProxy.newHar("test");```
    - (2)运行对应的业务代码,等待页面全部加载完,包括所有请求完成
    - (3)结束抓包```Pair<List<BrowserVo>, Har> listHarPair = requestProxy.captureRequest();```
    - (4)此时会返回一个pari对象，左边是解析好的参数响应体对象list，右边是原生抓取的har群文件，用户可以根据自己的需求拿这个list去操作或者拿这个har文件去操作
    - (5)outPutFile()方法接受一个```List<BrowserVo>```的入参，用户输出到对应的文件
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

4. pom里面指定selenium-java版本是4.3.0,testng使用7.4.0版本

```xml

<properties>
   <selenium.version>4.3.0</selenium.version>
   <testNg.version>7.4.0</testNg.version>
</properties>
```

5. 在自己的模块下的resource文件夹添加selenium.properties和db.setting两个文件,自行对里面的配置进行修改

#### 注意事项

1. reportng-2.6.4.jar需要放到对应的路径才能使用,这个是改了官方的包,因为官方的包输出测试报告不支持中文
2. 如果不想使用system级的引入,可以把这个包手动安装到本地的仓库中

#### 运行测试

1. 到项目根目录下执行mvn clean test即可,待执行完成会在target文件夹生成对应的测试报告surefire-reports
2. 进入里面的html文件夹，把html文件夹丢到http的服务器中,打开index.html即可
3. index.html如果不放到http服务器,则无法正常加载测试报告

#### 待改进的地方

1. 默认driver使用chrome,暂不支持其他浏览器,google上面说新版的firefox双击好像有问题???
2. 每次更新selenium的版本后doubleClick可能都会出现问题，所以目前才用重试机制，原生api不行的时候会采用js的方式来进行双击