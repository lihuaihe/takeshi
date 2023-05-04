> 七濑武【Nanase Takeshi】的项目框架说明

**项目中使用到的时间戳统一使用13位的毫秒级时间戳**

header中传递参数：
- 必传参数:
  - `timestamp`：调用接口的时间戳，13位的毫秒级时间戳
  - `User-Agent`
- 国际化消息(接口返回值中的message语言):
    - `Accept-Language`
        - `en-US`(返回英文)
        - `zh-CN`(返回中文)


> 七濑武【Nanase Takeshi】的项目框架说明

**项目中使用到的时间戳统一使用13位的时间戳**

header中传递参数：
- 参数签名：
  - `sign`：签名的值(看系统是否开启了需要参数签名)
    - 对参数做MD5签名
    - 参数签名为对Map参数按照key的顺序排序后拼接为字符串
    - 拼接后的字符串键值对之间无符号，键值对之间无符号，忽略path值和null值，只对最外层的key进行排序，嵌套的对象不用额外处理，保持原本的格式即可
    - 对参数进行排序拼接后在末尾加入`timestamp`字段的值
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

加解密
- 传递参数时，如果请求的接口是POST且是`Content-type: application/json`的参数，需要使用RSA算法进行公钥加密参数
- 获取接口返回值时，如果返回值字段`data`有值，则需要使用RSA算法进行公钥解密`data`值
- 公钥: ``

socket:
- 系统使用socket进行监听APP打开次数和APP使用时长，socket端口 [`10000`]
- APP每次打开时需要连接socket，连接成功后需要再3秒内发送一个消息校验用户token，连接后3秒内没有发送校验token消息或服务端若20秒内没有收到APP发送的心跳则代表APP断开连接
- 连接成功后在3秒内发送校验token消息内容 `{"token": "tokenValue"}`
- 发送心跳消息内容 `{"token": "tokenValue", "recordId": "维持心跳时传记录ID（连接成功且token校验通过后服务端返回的recordId）"}`
- 若token或recordId与服务端匹配不上则会断开socket
- APP每次进入后台时socket会断开，下次重新进入APP需要重新连接socket
- 只要服务端发送了recordId消息给客户端，客户端下次发心跳使用的recordId需要用新的