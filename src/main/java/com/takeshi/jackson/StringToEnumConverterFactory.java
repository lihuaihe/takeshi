package com.takeshi.jackson;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * 字符串转枚举，只适用有JsonValue注解的枚举
 */
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    /**
     * 字符串到枚举转换器
     *
     * @param enumType
     * @param <T>
     */
    private record StringToEnumConverter<T extends Enum<?>>(Class<T> enumType) implements Converter<String, T> {

        @Override
        public T convert(String source) {
            try {
                return Arrays.stream(enumType.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(JsonValue.class))
                        .findFirst()
                        .map(field -> Arrays.stream(enumType.getEnumConstants()).filter(enumConstant -> ReflectUtil.getFieldValue(enumConstant, field).equals(source)).findFirst().orElseThrow())
                        .orElseGet(() -> Arrays.stream(enumType.getEnumConstants()).filter(enumConstant -> enumConstant.name().equals(source)).findFirst().orElseThrow());
            } catch (NoSuchElementException e) {
                throw new IllegalArgumentException("No enum constant " + enumType.getTypeName() + "." + source);
            }
        }
    }
}
