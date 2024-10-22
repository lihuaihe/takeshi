package com.takeshi.util;


import cn.hutool.core.util.NumberUtil;
import com.takeshi.function.BigDecimalSummaryStatistics;
import com.takeshi.function.ToBigDecimalFunction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * JAVA8 分组求和要用到的
 * <pre>{@code
 * //使用方法
 * list.stream().collect(Collectors.groupingBy(Entry::getValue, CollectorsUtil.summingBigDecimal(Entry::getValue)));
 * list.stream().collect(CollectorsUtil.summingBigDecimal(Entry::getValue));
 * CollectorsUtil
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public final class CollectorsUtil {

    private static final Set<Collector.Characteristics> CHARACTERISTICS = Collections.emptySet();

    private CollectorsUtil() {
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    /**
     * Simple implementation class for {@code Collector}.
     *
     * @param <T> the type of elements to be collected
     * @param <R> the type of the result
     */
    @SuppressWarnings("hiding")
    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {

        private final Supplier<A> supplier;

        private final BiConsumer<A, T> accumulator;

        private final BinaryOperator<A> combiner;

        private final Function<A, R> finisher;

        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher, Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }

    }

    /**
     * Returns a {@code Collector} which applies an {@code BigDecimal}-producing
     * mapping function to each input element, and returns summary statistics
     * for the resulting values.
     *
     * @param <T>    the type of the input elements
     * @param mapper a mapping function to apply to each element
     * @return a {@code Collector} implementing the summary-statistics reduction
     * @see java.util.stream.Collectors#summarizingDouble(ToDoubleFunction)
     * @see java.util.stream.Collectors#summarizingLong(ToLongFunction)
     */
    public static <T> Collector<T, ?, BigDecimalSummaryStatistics> summarizingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(
                BigDecimalSummaryStatistics::new,
                (r, t) -> r.accept(mapper.applyAsBigDecimal(t)),
                (l, r) -> {
                    l.combine(r);
                    return l;
                },
                CHARACTERISTICS);
    }

    /**
     * 求和方法
     *
     * @param mapper a mapping function to apply to each element
     * @param <T>    the type of the input elements
     * @return a {@code Collector} implementing the summary-statistics reduction
     */
    public static <T> Collector<T, ?, BigDecimal> summingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(
                () -> new BigDecimal[]{BigDecimal.ZERO},
                (a, t) -> a[0] = NumberUtil.add(a[0], mapper.applyAsBigDecimal(t)),
                (a, b) -> {
                    a[0] = NumberUtil.add(a[0], b[0]);
                    return a;
                },
                a -> a[0], CHARACTERISTICS);
    }

    /**
     * 求最大,这里的最小MIN值，作为初始条件判断值，如果某些数据范围超过百亿以后，可以根据需求换成Long.MIN_VALUE或者Double.MIN_VALUE
     *
     * @param mapper a mapping function to apply to each element
     * @param <T>    the type of the input elements
     * @return a {@code Collector} implementing the summary-statistics reduction
     */
    public static <T> Collector<T, ?, BigDecimal> maxBy(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(
                () -> new BigDecimal[]{new BigDecimal(Integer.MIN_VALUE)},
                (a, t) -> a[0] = NumberUtil.max(a[0], mapper.applyAsBigDecimal(t)),
                (a, b) -> {
                    a[0] = NumberUtil.max(a[0], b[0]);
                    return a;
                },
                a -> a[0], CHARACTERISTICS);
    }

    /**
     * 求最小，这里的最大MAX值，作为初始条件判断值，如果某些数据范围超过百亿以后，可以根据需求换成Long.MAX_VALUE或者Double.MAX_VALUE
     *
     * @param mapper a mapping function to apply to each element
     * @param <T>    the type of the input elements
     * @return a {@code Collector} implementing the summary-statistics reduction
     */
    public static <T> Collector<T, ?, BigDecimal> minBy(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(
                () -> new BigDecimal[]{new BigDecimal(Integer.MAX_VALUE)},
                (a, t) -> a[0] = NumberUtil.min(a[0], mapper.applyAsBigDecimal(t)),
                (a, b) -> {
                    a[0] = NumberUtil.min(a[0], b[0]);
                    return a;
                },
                a -> a[0], CHARACTERISTICS);
    }

    /**
     * 求平均，并且保留两位小数，返回一个平均值
     *
     * @param mapper a mapping function to apply to each element
     * @param <T>    the type of the input elements
     * @return a {@code Collector} implementing the summary-statistics reduction
     */
    public static <T> Collector<T, ?, BigDecimal> averagingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return averagingBigDecimal(mapper, 2, RoundingMode.HALF_UP);
    }

    /**
     * 求平均，并且保留小数，返回一个平均值
     *
     * @param mapper       a mapping function to apply to each element
     * @param newScale     保留小数位数
     * @param roundingMode 小数处理方式
     *                     #ROUND_UP 进1
     *                     #ROUND_DOWN 退1
     *                     #ROUND_CEILING  进1截取：正数则ROUND_UP，负数则ROUND_DOWN
     *                     #ROUND_FLOOR  退1截取：正数则ROUND_DOWN，负数则ROUND_UP
     *                     #ROUND_HALF_UP >=0.5进1
     *                     #ROUND_HALF_DOWN >0.5进1
     *                     #ROUND_HALF_EVEN
     *                     #ROUND_UNNECESSARY
     * @param <T>          the type of the input elements
     * @return a {@code Collector} implementing the summary-statistics reduction
     */
    public static <T> Collector<T, ?, BigDecimal> averagingBigDecimal(ToBigDecimalFunction<? super T> mapper, int newScale, RoundingMode roundingMode) {
        return new CollectorImpl<>(
                () -> new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO},
                (a, t) -> {
                    a[0] = NumberUtil.add(a[0], mapper.applyAsBigDecimal(t));
                    a[1] = NumberUtil.add(a[1], BigDecimal.ONE);
                },
                (a, b) -> {
                    a[0] = NumberUtil.add(a[0], b[0]);
                    a[1] = NumberUtil.add(a[1], b[1]);
                    return a;
                },
                a -> NumberUtil.equals(a[1], BigDecimal.ZERO) ? BigDecimal.ZERO : NumberUtil.div(a[0], a[1], newScale, roundingMode),
                CHARACTERISTICS);
    }

}
