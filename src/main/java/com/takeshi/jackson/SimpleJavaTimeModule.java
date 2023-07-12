package com.takeshi.jackson;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import com.takeshi.constants.TakeshiDatePattern;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Date;

/**
 * SimpleJavaTimeModule
 *
 * @author 七濑武【Nanase Takeshi】
 */
public final class SimpleJavaTimeModule extends SimpleModule {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     */
    public SimpleJavaTimeModule() {
        super();
        addSerializer(Long.class, ToStringSerializer.instance);
        addSerializer(Long.TYPE, ToStringSerializer.instance);
        addSerializer(BigInteger.class, ToStringSerializer.instance);
        addSerializer(BigDecimal.class, ToStringSerializer.instance);

        addSerializer(Date.class, new DateSerializer(false, DateUtil.newSimpleFormat(TakeshiDatePattern.NORM_DATETIME_PATTERN)));
        addSerializer(Instant.class, InstantSerializer.INSTANCE);
        addSerializer(ZonedDateTime.class, ZonedDateTimeTakeshiSerializer.INSTANCE);
        addSerializer(LocalDate.class, new LocalDateSerializer(TakeshiDatePattern.NORM_DATE_FORMATTER));
        addSerializer(LocalTime.class, new LocalTimeSerializer(TakeshiDatePattern.NORM_TIME_FORMATTER));
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(TakeshiDatePattern.NORM_DATETIME_FORMATTER));
        addSerializer(YearMonth.class, new YearMonthSerializer(TakeshiDatePattern.NORM_MONTH_FORMATTER));
        addSerializer(MonthDay.class, new MonthDaySerializer(TakeshiDatePattern.NORM_MONTH_DAY_FORMATTER));
        addSerializer(OffsetTime.class, OffsetTimeTakeshiSerializer.INSTANCE);
        addSerializer(OffsetDateTime.class, OffsetDateTimeTakeshiSerializer.INSTANCE);

        addDeserializer(Date.class, DateTakeshiDeserializer.INSTANCE);
        addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        addDeserializer(ZonedDateTime.class, ZonedDateTimeTakeshiDeserializer.INSTANCE);
        addDeserializer(LocalDate.class, new LocalDateDeserializer(TakeshiDatePattern.NORM_DATE_FORMATTER));
        addDeserializer(LocalTime.class, new LocalTimeDeserializer(TakeshiDatePattern.NORM_TIME_FORMATTER));
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(TakeshiDatePattern.NORM_DATETIME_FORMATTER));
        addDeserializer(YearMonth.class, new YearMonthDeserializer(TakeshiDatePattern.NORM_MONTH_FORMATTER));
        addDeserializer(MonthDay.class, new MonthDayDeserializer(TakeshiDatePattern.NORM_MONTH_DAY_FORMATTER));
        addDeserializer(OffsetTime.class, OffsetTimeTakeshiDeserializer.INSTANCE);
        addDeserializer(OffsetDateTime.class, OffsetDateTimeTakeshiDeserializer.INSTANCE);
    }

}
