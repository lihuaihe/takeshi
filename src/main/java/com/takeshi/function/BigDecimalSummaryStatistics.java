package com.takeshi.function;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Like {@code DoubleSummaryStatistics}, {@code IntSummaryStatistics}, and
 * {@code LongSummaryStatistics}, but for {@link BigDecimal}.
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class BigDecimalSummaryStatistics implements Consumer<BigDecimal> {

    private long count;

    private BigDecimal sum = BigDecimal.ZERO, min, max;

    /**
     * Construct an empty instance with zero count, zero sum,
     */
    public BigDecimalSummaryStatistics() {
    }

    /**
     * Records another value into the summary information.
     *
     * @param value the input value
     */
    @Override
    public void accept(BigDecimal value) {
        if (count == 0) {
            Objects.requireNonNull(value);
            min = value;
            max = value;
        }
        ++count;
        sum = sum.add(value);
        min = min.min(value);
        max = max.max(value);
    }

    /**
     * Combines the state of another {@code BigDecimalSummaryStatistics} into this
     * one.
     *
     * @param other another {@code BigDecimalSummaryStatistics}
     * @throws NullPointerException if {@code other} is null
     */
    public void combine(BigDecimalSummaryStatistics other) {
        count += other.count;
        sum = sum.add(other.sum);
        min = min.min(other.min);
        max = max.max(other.max);
    }

    /**
     * Return the count of values recorded.
     *
     * @return the count of values
     */
    public final long getCount() {
        return count;
    }

    /**
     * Returns the sum of values recorded, or zero if no values have been
     * recorded.
     *
     * @return the sum of values, or zero if none
     */
    public final BigDecimal getSum() {
        return sum;
    }

    /**
     * Returns the minimum value recorded
     *
     * @return the minimum value
     */
    public final BigDecimal getMin() {
        return min;
    }

    /**
     * Returns the maximum value recorded
     *
     * @return the maximum value
     */
    public final BigDecimal getMax() {
        return max;
    }

    /**
     * Returns the arithmetic mean of values recorded, or zero if no
     * values have been recorded.
     * <p>
     * If any recorded value is a NaN or the sum is at any point a NaN
     * then the average will be code NaN.
     *
     * <p>The average returned can vary depending upon the order in
     * which values are recorded.
     * <p>
     * This method may be implemented using compensated summation or
     * other technique to reduce the error bound in the {@link #getSum
     * numerical sum} used to compute the average.
     *
     * @return the arithmetic mean of values, or zero if none
     */
    public final BigDecimal getAverage() {
        return getCount() > 0 ? getSum().divide(BigDecimal.valueOf(getCount()), RoundingMode.HALF_UP) : sum;
    }

    /**
     * Returns the arithmetic mean of values recorded, or zero if no
     * values have been recorded.
     * <p>
     * If any recorded value is a NaN or the sum is at any point a NaN
     * then the average will be code NaN.
     *
     * <p>The average returned can vary depending upon the order in
     * which values are recorded.
     * <p>
     * This method may be implemented using compensated summation or
     * other technique to reduce the error bound in the {@link #getSum
     * numerical sum} used to compute the average.
     *
     * @param roundingMode 舍入模式
     * @return the arithmetic mean of values, or zero if none
     */
    public final BigDecimal getAverage(RoundingMode roundingMode) {
        return getCount() > 0 ? getSum().divide(BigDecimal.valueOf(getCount()), roundingMode) : sum;
    }

    /**
     * Returns the arithmetic mean of values recorded, or zero if no
     * values have been recorded.
     * <p>
     * If any recorded value is a NaN or the sum is at any point a NaN
     * then the average will be code NaN.
     *
     * <p>The average returned can vary depending upon the order in
     * which values are recorded.
     * <p>
     * This method may be implemented using compensated summation or
     * other technique to reduce the error bound in the {@link #getSum
     * numerical sum} used to compute the average.
     *
     * @param mc MathContext
     * @return the arithmetic mean of values, or zero if none
     */
    public final BigDecimal getAverage(MathContext mc) {
        return getCount() > 0 ? getSum().divide(BigDecimal.valueOf(getCount()), mc) : sum;
    }

    @Override
    public String toString() {
        return count == 0 ? "empty" : String.format(
                "%s{count=%d, sum=%f, min=%f, average=%f, max=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax());
    }

}