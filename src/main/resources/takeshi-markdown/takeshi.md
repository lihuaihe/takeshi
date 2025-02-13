# 项目框架说明

- <span style="color: brown;"> 如果需要给接口增加开发者姓名，可以在方法上使用`@ApiSupport`注解 </span>
- <span style="color: brown;"> 如果需要在打开文档时默认折叠标签下的接口，则需要在对应的`@Tag`接口上将`description`属性值填写一下即可 </span>
- <span style="color: brown;"> 如果需要在文档中展示自定义的一些项目约定信息，可以在`resources/static`目录下创建一个`rdoc-project.md`文件 </span>

### header中传递参数：

- 国际化消息(接口返回值中的message语言):
    - `Accept-Language`
        - `en-US`(返回英文)
        - `zh-CN`(返回中文)
- 必传参数:
    - `timestamp`：调用接口的时间戳，13位的毫秒级时间戳
    - `timezone`：当前设备所在的时区 (例如：`Asia/Shanghai`)
    - `User-Agent`：当前项目名/当前APP版本号 (当前设备名 当前设备系统版本; 系统时区)
        - 例如：`takeshi`是应用名称，应用版本号是1.0.0，设备名是iPhone 14 pro，系统版本是16.0，时区是Asia/Shanghai
            - takeshi/1.0.0 (iPhone 14 pro v16.0; Asia/Shanghai)
            - takeshi/1.0.0 (iPad mini v16.0; Asia/Shanghai)
            - takeshi/1.0.0 (Android v8.5; Asia/Shanghai)
            - takeshi/1.0.0 (Android v8.5 Tablet; Asia/Shanghai)
- 随机字符串：参数签名时需要传递的值
    - `nonce`：仅一次有效的随机字符串，可以使用用户信息+时间戳+随机数等信息做个哈希值或使用唯一ID，作为nonce值
        - 每次请求接口时，该参数值都得是唯一不重复的
- 参数签名：
    - `sign`：签名的值(看系统是否开启了需要参数签名，与后台使用同一个secretKey进行签名)
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
          ，对字符串进行MD5加密，得到最终的签名值放到`sign`中传到后台
- 经纬度:
    - `geo-point`：设备经纬度json字符串
        - {"lon":1.0, "lat":2.0}

### 参数和接口返回值加解密(看后台系统是否开启了加解密)

- 【加密】传递参数时，如果请求的接口是POST且是`Content-type: application/json`的参数，需要使用RSA算法进行公钥加密参数
- 【解密】获取接口返回值时，如果返回值字段`data`有值，则需要使用RSA算法进行公钥解密`data`值