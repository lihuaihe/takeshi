package com.takeshi.constants;

/**
 * Firebase的Message枚举使用的接口
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface MessageEnum {

    /**
     * 通知的标题
     *
     * @param params 参数值
     * @return String
     */
    String formatTitle(Object... params);

    /**
     * 通知正文
     *
     * @param params 参数值
     * @return String
     */
    String formatBody(Object... params);

    /**
     * 设置与用户点击通知相关联的操作。如果指定，当用户单击通知时，将启动具有匹配 Intent Filter 的活动
     *
     * @param params 参数值
     * @return String
     */
    String formatClickAction(Object... params);

}
