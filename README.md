# takeshi

## 📚简介

takeshi是为了快速开发一个项目发布的一个库

## 📦安装

### 🍊Maven

在项目的pom.xml的dependencies中加入以下内容:

```xml

<dependency>
    <groupId>life.725</groupId>
    <artifactId>takeshi</artifactId>
    <version>${version}</version>
</dependency>
```

### 🍐Gradle

```
implementation 'life.725:takeshi:${version}'
```

### 📥下载jar

点击以下链接，下载`takeshi-X.X.X.jar`即可：

- [Maven中央库](https://central.sonatype.com/artifact/life.725/takeshi/)

### 🔔️<font color="#FFFF00">注意</font>

使用本库需要Springboot3.0+和JDK17+支持

## 📝教程

### 📌重点

_本库中引入了一堆的依赖，可自行查询使用，下面只列出部分依赖_

**SpringBoot一些依赖已经导入并Enable了**

* spring-boot-starter-aop
* spring-boot-starter-data-redis
* spring-boot-starter-validation
* spring-boot-starter-thymeleaf
* spring-retry
* redisson-spring-boot-starter

| 依赖                                                                                                                                     | 介绍                            |
|:---------------------------------------------------------------------------------------------------------------------------------------|:------------------------------|
| [sa-token](https://sa-token.cc/)                                                                                                       | 一个轻量级 java 权限认证框架，让鉴权变得简单、优雅！ |
| [mybatis-plus-boot-starter](https://baomidou.com/)                                                                                     | 为简化开发而生                       |
| [knife4j-openapi3-jakarta-spring-boot-starter](https://doc.xiaominfo.com/)                                                             | 帮助开发者快速聚合使用OpenAPI规范.         |
| [aws-java-sdk-s3](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)                                                  | 对象存储服务                        |
| [aws-java-sdk-secretsmanager](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/java_secrets-manager_code_examples.html) | 密钥管理器                         |
| [firebase-admin](https://firebase.google.com/docs/admin/setup?hl=zh-cn)                                                                | 读写 Realtime Database 数据       |
| [hutool-all](https://www.hutool.cn/docs/#/)                                                                                            | Hutool是一个小而全的Java工具类库         |

### 🚪代码

**项目中使用到的时间戳统一使用13位的毫秒级时间戳**
header中传递参数：

- 参数签名：(<font color="#FFFF00">看系统是否开启了需要参数签名</font>)
    - `sign`：签名的值
        - 对参数做MD5签名
        - 参数签名为对Map参数按照key的顺序排序后拼接为字符串
        - 拼接后的字符串键值对之间无符号，键值对之间无符号，忽略path值和null值，只对最外层的key进行排序，嵌套的对象不用额外处理，保持原本的JSON格式即可
        - 对参数进行排序拼接后在末尾加入header中的`timestamp`字段的值
        - 然后根据提供的签名算法生成签名字符串
- 必传参数:
    - `timestamp`：调用接口的时间戳，13位的毫秒级时间戳
    - `User-Agent`：
        - 当前设备名/当前APP版本号 系统时区
        - 例如：
            - iPhone/1.0.0 Asia/Shanghai
            - Android/1.0.0 Asia/Shanghai
            - iPad/1.0.0 Asia/Shanghai
- 国际化消息(接口返回值中的message语言):
    - `Accept-Language`
        - `en-US`(返回英文)
        - `zh-CN`(返回中文)
- 经纬度:
    - `Longitude(设备位置经度)`
    - `Latitude(设备位置纬度)`

加解密(<font color="#FFFF00">看系统是否开启了加解密</font>)

- 传递参数时，如果请求的接口是POST且是`Content-type: application/json`的参数，需要使用RSA算法进行公钥加密参数
- 获取接口返回值时，如果返回值字段`data`有值，则需要使用RSA算法进行公钥解密`data`值

controller

- controller包下的接口都有aop记录入参和返回值
- 书写自己的controller类时应继承一下`BaseController`类，里面有提供一些方法
- 默认已经有写好了两个上传文件的接口，上传至AWS的S3中，需要配置s3相关信息
- 还有一个未展示在文档中的系统接口，`SystemController`中可使用公钥加密数据，使用公钥解密数据，测试生成sign值

### 🔧工具

* `RedisComponent.java` redis的工具类，注入使用
* `TakeshiCode.java`
  返回给前端使用的国际化消息，如果项目中需要自定义消息，可继承该接口，例如：`public interface SysCode extends TakeshiCode {}`
* `TakeshiConstants.java`
  一些常量值，建议使用时也定义一个接口继承该接口，例如：`public interface SysConstants extends TakeshiConstants {}`
* `TakeshiRedisKeyEnum.java` 可以参考该类，创建一个枚举实现`TakeshiRedisKeyFormat`接口，存储一些redis使用的key，调用格式化方法给key加对应前缀
* `TakeshiDatePattern.java` 日期格式常量值，继承自hutool的DatePattern

### 🎍注解

* `BigDecimalFormat` 格式化BigDecimal，解析字符串转BigDecimal，解析前端传过来的数字字符串，可配置指定数字格式放回给前端
* `NumZeroFormat` 去掉前端入参时数字字符串前面多余的零
* `RepeatSubmit` 防止重复提交
* `SystemSecurity` 放弃某些类型的校验

### 🍵格式校验

* `VerifyNumber` 接口参数校验固定值，null值也是有效的，校验数值，校验参数值是否在当前数组中
* `VerifySortColumn` 数据库排序字段校验
* `VerifyString` 接口参数校验固定值，null值也是有效的，校验字符串，校验参数值是否在当前数组中
* `VerifyVersion` 版本号格式校验
* `NumberDigits` 校验数字整数位和小数位的位数，null值也是有效的

**_其他工具类可在util包中查询到_**

### 📒配置

```yaml
# takeshi库已经写了一个基本的[application-takeshi.yml]配置文件，如果需要使用这些使这个配置生效需要添加include
spring:
  profiles:
    include: takeshi
```

**include: takeshi 后takeshi的默认配置就生效了**
> sa-token 的配置
> * token名称：${spring.application.name}-satoken
> * token临时有效期30天
> * token风格UUID
> * 从header里读取token，不从cookie和请求体里读取token

> mybatis-plus 的配置
> * 逻辑删除全局属性名 `deleteTime`
> * 逻辑删除全局值（删除时的当前时间戳，表示已删除）
> * 逻辑未删除全局值（0，表示未删除）

> knife4j 的配置
> * 开启Knife4j增强模式
> * markdown文件路径 `classpath:markdown/*``

> logging 的配置
> * 日志存放路径 `./logs/${spring.application.name}`
> * 日志中输出了一个 `traceId` ，可通过该值进行链路追踪日志，可配合接口返回值中的 `traceId`

```yaml
# takeshi配置
takeshi:
  # 项目名称
  project-name: 'project-name'
  # 参数签名使用的key，随便设定一个与前端约定的值即可，有值则开启参数签名限制
  signature-key: 'signature-key'
  # 是否开启移动端请求工具限制
  app-platform: false
  # Controller方法参数绑定错误时错误信息包含字段名
  include-error-field-name: true
  # AWS凭证
  aws-credentials:
    access-key: 'access-key'
    secret-key: 'secret-key'
    # 存储桶名称，默认使用{takeshi.projectName}-bucket
    bucket-name: 'bucket-name'
    # 设置客户端使用的区域（例如：us-west-2）
    region: 'us-west-2'
  # Mandrill凭证
  mandrill-credentials:
    api-key: 'api-key'
    # 发送人邮箱
    from-email: 'from-email'
    # 发送人名称
    from-name: 'from-name'
  # Firebase凭证
  firebase-credentials:
    # firebase使用的json文件名，默认使用firebase.json
    json-file-name: 'firebase.json'
    database-url: 'database-url'
```

### 🌍国际化消息

```yaml
# 如果需要自己添加一些国际化消息，需要添加如下配置
# 默认[application-takeshi.yml]已经配置了i18n/messages，如果你重新配置，[application-takeshi.yml]中的i18n/messages就被覆盖了，所以需要讲i18n/messages加上
spring:
  messages:
    # i18n/messages是固定的，takeshi库需要用到的国际化消息配置，ValidationMessages是你自己项目的配置
    basename: i18n/messages,ValidationMessages
```

### 📃日志

```yaml
# 默认日志文件已经按照不同日志等级存储，sql日志记录需要添加你需要记录的包名
logging:
  level:
    # 例如：com.nanase.takeshi 是我的项目包名，该包下的sql都会记录到debug文件中
    com.nanase.takeshi: debug
```
