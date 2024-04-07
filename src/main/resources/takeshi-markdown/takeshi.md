> 七濑武【Nanase Takeshi】的项目框架说明

**项目中使用到的时间戳统一使用13位的毫秒级时间戳**

> 七濑武【Nanase Takeshi】的项目框架说明

**项目中使用到的时间戳统一使用13位的时间戳**

header中传递参数：

- 国际化消息(接口返回值中的message语言):
    - `Accept-Language`
        - `en-US`(返回英文)
        - `zh-CN`(返回中文)
- 必传参数:
    - `x-timestamp`：调用接口的时间戳，13位的毫秒级时间戳
    - `x-timezone`：当前设备所在的时区 (Asia/Shanghai)
    - `User-Agent`：当前项目名/当前APP版本号 (当前设备名 当前设备系统版本; 系统时区)
        - 例如：
            - takeshi/1.0.0 (iPhone 16.0; Asia/Shanghai)
            - takeshi/1.0.0 (iPad 16.0; Asia/Shanghai)
            - takeshi/1.0.0 (Android 8.5; Asia/Shanghai)
            - takeshi/1.0.0 (Android 8.5 Tablet; Asia/Shanghai)
- 参数签名：
    - `x-sign`：签名的值(看系统是否开启了需要参数签名)
        - 对参数做MD5签名
        - 参数签名为对Map参数按照key的顺序排序后拼接为字符串
        - 拼接后的字符串键值对之间无符号，键值对之间无符号，忽略path值和null值，只对最外层的key进行排序，嵌套的对象不用额外处理，保持原本的格式即可
        - 对参数进行排序拼接后在末尾加入`x-timestamp`字段的值
        - 然后根据提供的签名算法生成签名字符串
- 随机字符串：
    - `x-nonce`：仅一次有效的随机字符串，可以使用用户信息+时间戳+随机数等信息做个哈希值，作为nonce值
        - 每次请求接口时，该参数值都得是唯一不重复的
- 经纬度:
    - `x-geo-point`：设备经纬度json字符串
        - {"lon":1.0, "lat":2.0}

参数和接口返回值加解密

- 传递参数时，如果请求的接口是POST且是`Content-type: application/json`的参数，需要使用RSA算法进行公钥加密参数
- 获取接口返回值时，如果返回值字段`data`有值，则需要使用RSA算法进行公钥解密`data`值