package com.takeshi.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.takeshi.jackson.CurrencyToCentDeserializer;
import com.takeshi.jackson.CurrencyToYuanSerializer;

import java.lang.annotation.*;

/**
 * 货币转化，前端传过来的金额从元转换为分，后端返回给前端的金额从分转换为元
 * <br/>
 * 同时也会有{@link BigDecimalFormat}注解相同功能
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = CurrencyToYuanSerializer.class)
@JsonDeserialize(using = CurrencyToCentDeserializer.class)
public @interface CurrencyConversion {

    /**
     * 出参格式化格式<br/>
     * 格式 格式中主要以 # 和 0 两种占位符号来指定数字长度。0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置。<br>
     * <ul>
     * <li>0 =》 取一位整数</li>
     * <li>0.00 =》 取一位整数和两位小数</li>
     * <li>00.000 =》 取两位整数和三位小数</li>
     * <li># =》 取所有整数部分</li>
     * <li>#.##% =》 以百分比方式计数，并取两位小数</li>
     * <li>#.#####E0 =》 显示为科学计数法，并取五位小数</li>
     * <li>,### =》 每三位以逗号进行分隔，例如：299,792,458</li>
     * <li>光速大小为每秒,###米 =》 将格式嵌入文本</li>
     * </ul>
     *
     * @return String
     */
    String pattern() default "0.00";

}
