package com.takeshi.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializerBase;
import org.springframework.boot.jackson.JsonComponent;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * TakeshiInstantSerializer
 *
 * @author 七濑武【Nanase Takeshi】
 */
@JsonComponent
public class TakeshiInstantSerializer extends InstantSerializerBase<Instant> {

    /**
     * 实例
     */
    public static final TakeshiInstantSerializer INSTANCE = new TakeshiInstantSerializer();
    /**
     * 格式化，例如：2022-01-01T04:34:56.000Z
     */
    public static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().parseCaseInsensitive().appendInstant(3).toFormatter();

    /**
     * 构造方法
     */
    protected TakeshiInstantSerializer() {
        super(Instant.class, Instant::toEpochMilli, Instant::getEpochSecond, Instant::getNano, FORMATTER);
    }

    /**
     * 构造方法
     *
     * @param base         base
     * @param useTimestamp useTimestamp
     * @param formatter    formatter
     * @param shape        shape
     */
    protected TakeshiInstantSerializer(TakeshiInstantSerializer base, Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        super(base, useTimestamp, base._useNanoseconds, formatter, shape);
    }

    @Override
    protected InstantSerializerBase<Instant> withFormat(Boolean useTimestamp, DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new TakeshiInstantSerializer(this, useTimestamp, formatter, shape);
    }

}
