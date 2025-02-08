# takeshi

> 工欲其善，必先利其器

## 📚简介

takeshi是为了快速开发一个项目发布的一个库

## 📦安装

### 🍊Maven

在项目的pom.xml的dependencies中加入以下内容:

```xml

<dependency>
    <groupId>life.725</groupId>
    <artifactId>takeshi-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

### 🍐Gradle

```
implementation 'life.725:takeshi-spring-boot-starter:${version}'
```

### 📥下载jar

点击以下链接，下载`takeshi-spring-boot-starter-X.X.X.jar`即可：

- [Maven中央库](https://central.sonatype.com/artifact/life.725/takeshi-spring-boot-starter)

### 🔔️<font color="#FFFF00">注意</font>

使用本库需要Springboot3.0+和JDK17+支持

## 📝教程

### 📌重点

_本库中引入了一堆的依赖，可自行查询使用，下面只列出部分依赖_

**SpringBoot一些依赖已经导入并Enable了**

* spring-boot-starter-aop
* spring-boot-starter-data-redis
* spring-boot-starter-validation
* spring-retry
* redisson-spring-boot-starter

| 依赖                                                                                                                                     | 介绍                                                           |
|:---------------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------|
| [sa-token](https://sa-token.cc/)                                                                                                       | 一个轻量级 java 权限认证框架，让鉴权变得简单、优雅！                                |
| [mybatis-plus-boot-starter](https://baomidou.com/)                                                                                     | 为简化开发而生                                                      |
| [aws-java-sdk-s3](https://docs.aws.amazon.com/AmazonS3/latest/userguide/Welcome.html)                                                  | 对象存储服务，<font color="#FFFF00">需要自行导入包</font>                  |
| [aws-java-sdk-secretsmanager](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/java_secrets-manager_code_examples.html) | 密钥管理器                                                        |
| [firebase-admin](https://firebase.google.com/docs/admin/setup?hl=zh-cn)                                                                | 读写 Realtime Database 数据，<font color="#FFFF00">需要自行导入包</font> |
| [hutool-all](https://www.hutool.cn/docs/#/)                                                                                            | Hutool是一个小而全的Java工具类库                                        |

### 🚪代码

### header中传递参数：

- 国际化消息(接口返回值中的message语言):
    - `Accept-Language`
        - `en-US`(返回英文)
        - `zh-CN`(返回中文)
- 必传参数:
    - `x-timestamp`：调用接口的时间戳，13位的毫秒级时间戳
    - `x-timezone`：当前设备所在的时区 (例如：`Asia/Shanghai`)
    - `User-Agent`：当前项目名/当前APP版本号 (当前设备名 当前设备系统版本; 系统时区)
        - 例如：`takeshi`是应用名称，应用版本号是1.0.0，设备名是iPhone 14 pro，系统版本是16.0，时区是Asia/Shanghai
            - takeshi/1.0.0 (iPhone 14 pro v16.0; Asia/Shanghai)
            - takeshi/1.0.0 (iPad mini v16.0; Asia/Shanghai)
            - takeshi/1.0.0 (Android v8.5; Asia/Shanghai)
            - takeshi/1.0.0 (Android v8.5 Tablet; Asia/Shanghai)
- 随机字符串：参数签名时需要传递的值
    - `x-nonce`：仅一次有效的随机字符串，可以使用用户信息+时间戳+随机数等信息做个哈希值或使用唯一ID，作为nonce值
        - 每次请求接口时，该参数值都得是唯一不重复的
- 参数签名：
    - `x-sign`：签名的值(看系统是否开启了需要参数签名，与后台使用同一个secretKey进行签名)
        - 对参数做MD5签名
        - 将所有query参数及body值及`timestamp`，`noce`值全部放入到map中，对Map参数按照key的顺序排序后拼接为字符串
        - 拼接后的字符串键值对之间使用`&`连接，键值对之间使用`=`连接，忽略path值和null值，只对最外层的key进行排序，嵌套的对象不用额外处理，保持原本的格式即可
        - 例如：[POST]`https://www.baidu.com?a=1&b=2`
          ，且body里传值为`{"c":3,"z":26}`，`timestamp=1715077731701`，`nonce=SJLF223SJl892891JLJL`
          ，则参数拼接后的字符串为`a=1&b=2&c=3&nonce=SJLF223SJl892891JLJL&timestamp=1715077731701&z=26`
          ，secretKey添加到排序后的字符串后面，最终的字符串为
          `a=1&b=2&c=3&nonce=SJLF223SJl892891JLJL&timestamp=1715077731701&z=26&key={secretKey}`
          ，如果body里面不是json字符串，而是其他值（一个普通字符串或数字或数组），假如是个数组（["a","b","c"]
          ），那么使用body作为key，body正文内容作为value，就是`body=["a","b","c"]`
          ，最终排序后且加上key的字符串就是
          `a=1&b=2&body=["a","b","c"]&nonce=SJLF223SJl892891JLJL&timestamp=1715077731701&z=26&key={secretKey}`
          ，对字符串进行MD5加密，得到最终的签名值放到`x-sign`中传到后台
- 经纬度:
    - `x-geo-point`：设备经纬度json字符串
        - {"lon":1.0, "lat":2.0}

### 参数和接口返回值加解密(看后台系统是否开启了加解密)

- 【加密】传递参数时，如果请求的接口是POST且是`Content-type: application/json`的参数，需要使用RSA算法进行公钥加密参数
- 【解密】获取接口返回值时，如果返回值字段`data`有值，则需要使用RSA算法进行公钥解密`data`值

### 🔧工具

* `TakeshiCode.java`
  返回给前端使用的国际化消息，如果项目中需要自定义消息，可继承该接口，例如：
  `public interface SysCode extends TakeshiCode {}`
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

**include: takeshi 后takeshi的默认配置就生效了，详情可自行查看`application-takeshi.yml`**
> sa-token 的配置
> * token名称：${spring.application.name}-satoken
> * token临时有效期30天
> * token风格UUID
> * 从header里读取token，不从cookie和请求体里读取token

> mybatis-plus 的配置
> * 逻辑删除全局属性名 `deleteTime`
> * 逻辑删除全局值（删除时的当前时间戳，表示已删除）
> * 逻辑未删除全局值（0，表示未删除）

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
# 默认会使用已经配置了的i18n/messages
spring:
  messages:
    # ValidationMessages是你自己项目的配置国际化消息的目录
    basename: ValidationMessages
```

### 📃日志

```yaml
# 默认日志文件已经按照不同日志等级存储，sql日志记录需要添加你需要记录的包名
logging:
  level:
    # 例如：com.nanase.takeshi 是我的项目包名，该包下的sql都会记录到debug文件中
    com.nanase.takeshi: debug
```
